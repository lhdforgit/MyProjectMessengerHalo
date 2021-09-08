package com.hahalolo.call.conference;

import android.text.TextUtils;

import com.hahalolo.call.conference.utils.ConferenceUtils;
import com.hahalolo.call.webrtc.QBRTCMediaConfig;
import com.hahalolo.call.webrtc.QBRTCTypes.QBConferenceType;
import com.hahalolo.call.webrtc.RTCMediaUtils;
import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class JanusSignaler implements JanusSignaling, WebSocketConnection.WsPacketListener {
   private static final String CLASS_TAG = JanusSignaler.class.getSimpleName();
   private static final Logger LOGGER;
   private int keepAliveValueSec = 30;
   private int socketTimeOutMs = 10000;
   protected JanusResponseEventCallback janusResponseCallback;
   private WebSocketConnection socketConnection;
   private int userId;
   private String dialogId;
   private ConcurrentHashMap<Integer, BigInteger> handleIDs;
   private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
   private final Runnable keepAlivePacket = new KeepAlivePacket();
   private ScheduledFuture senderHandler;

   public JanusSignaler(String url, String protocol, int socketTimeOutMs, int keepAliveValueSec) {
      this.socketConnection = new WebSocketConnection(url, protocol);
      this.handleIDs = new ConcurrentHashMap();
      this.socketTimeOutMs = socketTimeOutMs;
      this.keepAliveValueSec = keepAliveValueSec;
      this.socketConnection.setTimeOut((long)this.socketTimeOutMs);
      this.socketConnection.addPacketListener(this);
   }

   public void startSession() throws WsException {
      this.socketConnection.connect();
      this.socketConnection.authenticate();
   }

   public void attachPlugin(String pluginId, Integer userID) throws WsException {
      WsPluginPacket packet = new WsPluginPacket();
      packet.setMessageType(WsPacket.Type.attach);
      packet.setPlugin(pluginId);
      WsDataPacket wsDataPacket = (WsDataPacket)this.socketConnection.sendSynchronous(packet, WsPacket.Type.success);
      BigInteger handleId = wsDataPacket.getData().getId();
      this.handleIDs.put(userID, handleId);
   }

   public void detachPlugin(BigInteger handleId) throws WsException {
      WsDetach packet = new WsDetach();
      packet.setMessageType(WsPacket.Type.detach);
      packet.handleId = handleId;
      this.socketConnection.send(packet);
   }

   void detachHandleId(Integer userId) throws WsException {
      if (this.handleIDs.containsKey(userId)) {
         BigInteger handleId = (BigInteger)this.handleIDs.remove(userId);
         this.detachPlugin(handleId);
      }

   }

   void detachHandleIdAll() throws WsException {
      Iterator var1 = this.handleIDs.entrySet().iterator();

      while(var1.hasNext()) {
         Entry<Integer, BigInteger> usersIDEntry = (Entry)var1.next();
         this.detachPlugin((BigInteger)usersIDEntry.getValue());
      }

   }

   public void joinDialog(String dialogId, int userId, String role) throws WsException {
      this.userId = userId;
      this.dialogId = dialogId;
      WsRoomPacket requestPacket = new WsRoomPacket();
      requestPacket.setMessageType(WsPacket.Type.message);
      requestPacket.handleId = (BigInteger)this.handleIDs.get(userId);
      requestPacket.body = new WsRoomPacket.Body();
      requestPacket.body.room = dialogId;
      requestPacket.body.ptype = role;
      requestPacket.body.request = WsRoomPacket.Type.join;
      requestPacket.body.userId = userId;
      this.socketConnection.sendSynchronous(requestPacket, WsPacket.Type.ack);
   }

   public void subscribe(String dialogId, int userId) throws WsException {
      this.dialogId = dialogId;
      WsRoomPacket requestPacket = new WsRoomPacket();
      requestPacket.setMessageType(WsPacket.Type.message);
      requestPacket.handleId = (BigInteger)this.handleIDs.get(userId);
      requestPacket.body = new WsRoomPacket.Body();
      requestPacket.body.room = dialogId;
      requestPacket.body.ptype = "listener";
      requestPacket.body.request = WsRoomPacket.Type.join;
      requestPacket.body.feed = userId;
      this.socketConnection.sendSynchronous(requestPacket, WsPacket.Type.ack);
   }

   public void leave(int userId) throws WsException {
      WsRoomPacket requestPacket = new WsRoomPacket();
      requestPacket.setMessageType(WsPacket.Type.message);
      requestPacket.handleId = (BigInteger)this.handleIDs.get(userId);
      requestPacket.body = new WsRoomPacket.Body();
      requestPacket.body.room = this.dialogId;
      requestPacket.body.userId = userId;
      requestPacket.body.request = WsRoomPacket.Type.leave;
      this.socketConnection.send(requestPacket);
   }

   public void sendKeepAlive() throws WsException {
      WsKeepAlive requestPacket = new WsKeepAlive();
      requestPacket.setMessageType(WsPacket.Type.keepalive);
      this.socketConnection.sendSynchronous(requestPacket, WsPacket.Type.ack);
   }

   public void sendOffer(SessionDescription sdp, Integer opponentID, QBConferenceType conferenceType, Map<String, String> customParameters) throws WsException {
      String sdpRaw = RTCMediaUtils.generateLocalDescription(sdp, (QBRTCMediaConfig)null, conferenceType);
      boolean isVideoType = QBConferenceType.QB_CONFERENCE_TYPE_VIDEO.equals(conferenceType);
      LOGGER.d(CLASS_TAG, "sendOffer isVideoType= " + isVideoType);
      sdpRaw = this.setSDPWithoutCVO(sdpRaw);
      WsOffer requestPacket = new WsOffer();
      requestPacket.setMessageType(WsPacket.Type.message);
      requestPacket.handleId = (BigInteger)this.handleIDs.get(opponentID);
      requestPacket.body = new WsOffer.Body();
      requestPacket.body.audio = true;
      requestPacket.body.video = isVideoType;
      requestPacket.body.request = WsOfferAnswer.Type.configure;
      requestPacket.jsep = new WsOfferAnswer.Jsep();
      requestPacket.jsep.type = "offer";
      requestPacket.jsep.sdp = sdpRaw;
      this.socketConnection.sendSynchronous(requestPacket, WsPacket.Type.ack);
   }

   public void sendGetOnlineParticipants(String dialogId, int userId) throws WsException {
      WsListOnlineParticipants requestPacket = new WsListOnlineParticipants();
      requestPacket.setMessageType(WsPacket.Type.message);
      requestPacket.handleId = (BigInteger)this.handleIDs.get(userId);
      requestPacket.body = new WsListOnlineParticipants.Body();
      requestPacket.body.room = dialogId;
      requestPacket.body.request = WsPacket.Type.listparticipants;
      this.socketConnection.sendSynchronous(requestPacket, WsPacket.Type.success);
   }

   public void sendAnswer(SessionDescription sdp, Integer opponentID, QBConferenceType conferenceType, Map<String, String> customParameters) throws WsException {
      String sdpRaw = RTCMediaUtils.generateLocalDescription(sdp, (QBRTCMediaConfig)null, conferenceType);
      WsAnswer requestPacket = new WsAnswer();
      requestPacket.setMessageType(WsPacket.Type.message);
      requestPacket.handleId = (BigInteger)this.handleIDs.get(opponentID);
      requestPacket.body = new WsAnswer.Body();
      requestPacket.body.room = this.dialogId;
      requestPacket.body.request = WsOfferAnswer.Type.start;
      requestPacket.jsep = new WsOfferAnswer.Jsep();
      requestPacket.jsep.type = "answer";
      requestPacket.jsep.sdp = sdpRaw;
      this.socketConnection.sendSynchronous(requestPacket, WsPacket.Type.ack);
   }

   public void sendIceCandidate(IceCandidate iceCandidate, int userId, Map<String, String> customParameters) throws WsException {
      WsCandidate requestPacket = new WsCandidate();
      requestPacket.setMessageType(WsPacket.Type.trickle);
      requestPacket.handleId = (BigInteger)this.handleIDs.get(userId);
      requestPacket.candidate = new WsCandidate.Candidate();
      requestPacket.candidate.candidate = iceCandidate.toString();
      requestPacket.candidate.sdpMLineIndex = 0;
      requestPacket.candidate.sdpMid = "audio";
      this.socketConnection.sendSynchronous(requestPacket, WsPacket.Type.ack);
   }

   public void sendIceCandidateComplete(int userId) throws WsException {
      WsCandidate requestPacket = new WsCandidate();
      requestPacket.setMessageType(WsPacket.Type.trickle);
      requestPacket.handleId = (BigInteger)this.handleIDs.get(userId);
      requestPacket.candidate = new WsCandidate.Candidate();
      requestPacket.candidate.completed = Boolean.TRUE;
      this.socketConnection.sendSynchronous(requestPacket, WsPacket.Type.ack);
   }

   void setJanusResponseEventCallback(JanusResponseEventCallback callback) {
      this.janusResponseCallback = callback;
   }

   public void destroySession() throws WsException {
      this.socketConnection.closeSession();
   }

   public void onPacketReceived(WsPacket packet) {
      this.packetParser(packet);
   }

   public void onPacketError(WsPacket packet, String error) {
      LOGGER.d(CLASS_TAG, "onPacketError");
      this.janusResponseCallback.onPacketError(error);
   }

   private void packetParser(WsPacket packet) {
      if (WsEvent.class.isInstance(packet)) {
         this.eventParser((WsEvent)packet);
      } else if (WsWebRTCUp.class.isInstance(packet)) {
         WsWebRTCUp webRTCUp = (WsWebRTCUp)packet;
         LOGGER.d(CLASS_TAG, "WsWebRTCUp packet sender= " + webRTCUp.sender);
         this.janusResponseCallback.onWebRTCUpReceived(webRTCUp.sender);
      } else if (WsMedia.class.isInstance(packet)) {
         WsMedia wsMedia = (WsMedia)packet;
         LOGGER.d(CLASS_TAG, "WsMedia packet type= " + ((WsMedia)packet).type + ", receiving= " + ((WsMedia)packet).receiving);
         this.janusResponseCallback.onMediaReceived(wsMedia.type, wsMedia.receiving);
      } else if (WsHangUp.class.isInstance(packet)) {
         WsHangUp wsHangUp = (WsHangUp)packet;
         LOGGER.d(CLASS_TAG, "WsHangUp packet reason= " + ((WsHangUp)packet).reason);
         this.janusResponseCallback.onHangUp(wsHangUp.reason);
      } else if (WsSlowLink.class.isInstance(packet)) {
         WsSlowLink wsSlowLink = (WsSlowLink)packet;
         LOGGER.d(CLASS_TAG, "WsSlowLink packet uplink= " + ((WsSlowLink)packet).uplink);
         this.janusResponseCallback.onSlowLinkReceived(wsSlowLink.uplink, wsSlowLink.nacks);
      } else if (WsDataPacket.class.isInstance(packet)) {
         WsDataPacket wsDataPacket = (WsDataPacket)packet;
         if (wsDataPacket.isGetParticipant()) {
            List<Map<String, Object>> participants = wsDataPacket.plugindata.data.participants;
            this.janusResponseCallback.onGetOnlineParticipants(ConferenceUtils.convertParticipantListToArray(participants));
         } else if (wsDataPacket.isVideoRoomEvent()) {
            this.janusResponseCallback.onVideoRoomEvent(wsDataPacket.plugindata.data.error);
         }
      }

   }

   private void eventParser(WsEvent wsEvent) {
      if (wsEvent.isRemoteSDPEventAnswer()) {
         LOGGER.d(CLASS_TAG, "RemoteSDPEventAnswer wsEvent with sdp type= " + wsEvent.jsep.type);
         this.janusResponseCallback.onRemoteSDPEventAnswer(wsEvent.jsep.sdp);
      } else if (wsEvent.isRemoteSDPEventOffer()) {
         int opponentId = wsEvent.plugindata.data.id;
         LOGGER.d(CLASS_TAG, "RemoteSDPEventOffer wsEvent with sdp type= " + wsEvent.jsep.type + ", opponentId= " + opponentId);
         this.janusResponseCallback.onRemoteSDPEventOffer(opponentId, wsEvent.jsep.sdp);
      } else {
         List publishers;
         if (wsEvent.isJoinEvent()) {
            publishers = wsEvent.plugindata.data.publishers;
            LOGGER.d(CLASS_TAG, "JoinEvent publishers= " + publishers);
            ArrayList<Integer> publishersList = ConferenceUtils.convertPublishersToArray(publishers);
            this.janusResponseCallback.onJoinEvent(publishersList, wsEvent.sender);
         } else if (wsEvent.isEventError()) {
            this.janusResponseCallback.onEventError(wsEvent.plugindata.data.error, wsEvent.plugindata.data.errorCode);
         } else if (wsEvent.isPublisherEvent()) {
            publishers = wsEvent.plugindata.data.publishers;
            LOGGER.d(CLASS_TAG, "PublisherEvent wsEvent publishers= " + publishers);
            this.janusResponseCallback.onPublishedEvent(ConferenceUtils.convertPublishersToArray(publishers));
         } else {
            Integer userId;
            if (wsEvent.isUnPublishedEvent()) {
               userId = wsEvent.plugindata.data.unpublished;
               LOGGER.d(CLASS_TAG, "UnPublishedEvent  unpublished userID= " + userId);
               this.janusResponseCallback.onUnPublishedEvent(userId);
            } else if (wsEvent.isStartedEvent()) {
               LOGGER.d(CLASS_TAG, "StartedEvent subscribed started= " + wsEvent.plugindata.data.started);
               this.janusResponseCallback.onStartedEvent(wsEvent.plugindata.data.started);
            } else if (wsEvent.isLeavePublisherEvent()) {
               userId = Integer.valueOf(wsEvent.plugindata.data.leaving);
               LOGGER.d(CLASS_TAG, "LeavePublisherEvent left userId= " + userId);
               this.janusResponseCallback.onLeavePublisherEvent(userId);
            } else if (wsEvent.isLeaveCurrentUserEvent() || wsEvent.isLeftCurrentUserEvent()) {
               boolean leavingOk = TextUtils.equals(wsEvent.plugindata.data.leaving, "ok") || TextUtils.equals(wsEvent.plugindata.data.left, "ok");
               LOGGER.d(CLASS_TAG, "isLeaveCurrentUserEvent leavingOk? " + leavingOk);
               this.janusResponseCallback.onLeaveCurrentUserEvent(leavingOk);
            }
         }
      }

   }

   private String setSDPWithoutCVO(String sdpRaw) {
      String CVO = "urn:3gpp:video-orientation";
      String sdpWithoutCVO = sdpRaw;
      if (sdpRaw.contains(CVO)) {
         int indexEndOfCVO = sdpRaw.indexOf(CVO) + CVO.length();
         String sdpWithoutCVOLineBreaker = sdpRaw.substring(0, indexEndOfCVO) + "" + sdpRaw.substring(indexEndOfCVO + 2);
         sdpWithoutCVO = sdpWithoutCVOLineBreaker.replaceAll("(.*)urn:3gpp:video-orientation", "");
      }

      return sdpWithoutCVO;
   }

   public boolean isActive() {
      return this.socketConnection.isActiveSession();
   }

   void startAutoSendPresence() {
      if (this.keepAlivePacket != null) {
         LOGGER.d(CLASS_TAG, "startAutoSendPresence");
         this.senderHandler = this.scheduler.scheduleAtFixedRate(this.keepAlivePacket, (long)this.keepAliveValueSec, (long)this.keepAliveValueSec, TimeUnit.SECONDS);
      }

   }

   void stopAutoSendPresence() {
      if (this.senderHandler != null && !this.senderHandler.isCancelled()) {
         this.senderHandler.cancel(true);
         this.senderHandler = null;
      }

   }

   static {
      LOGGER = Logger.getInstance(ConferenceClient.TAG);
   }

   private class KeepAlivePacket implements Runnable {
      private KeepAlivePacket() {
      }

      public void run() {
         try {
            if (JanusSignaler.this.isActive()) {
               JanusSignaler.LOGGER.d(JanusSignaler.CLASS_TAG, "sendKeepAlive");
               JanusSignaler.this.sendKeepAlive();
            }
         } catch (WsException var2) {
            JanusSignaler.LOGGER.d(JanusSignaler.CLASS_TAG, "sendKeepAlive error: " + var2.getMessage());
         }

      }

      // $FF: synthetic method
      KeepAlivePacket(Object x1) {
         this();
      }
   }
}
