package com.hahalolo.call.conference;

import android.os.Bundle;

import com.hahalolo.call.conference.callbacks.ConferenceEntityCallback;
import com.hahalolo.call.conference.callbacks.ConferenceSessionCallbacks;
import com.hahalolo.call.utils.ThreadTaskEx;
import com.hahalolo.call.webrtc.BaseSession;
import com.hahalolo.call.webrtc.QBPeerConnection;
import com.hahalolo.call.webrtc.QBRTCConfig;
import com.hahalolo.call.webrtc.QBRTCSessionDescription;
import com.hahalolo.call.webrtc.QBRTCTypes.QBConferenceType;
import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.CameraVideoCapturer.CameraEventsHandler;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection.IceGatheringState;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConferenceSession extends BaseSession<ConferenceClient, QBConferencePeerConnection> implements JanusResponseEventCallback {
   private static final String TAG = ConferenceSession.class.getSimpleName();
   private final String CLASS_TAG;
   private static final Logger LOGGER;
   private static final String JANUS_ROLE = "publisher";
   private static final int JOIN_ERROR_CODE = 436;
   private static final int MAX_PUBLISHERS_ERROR_CODE = 432;
   private static final int NO_SUCH_FEED_ERROR = 428;
   private Set<ConferenceEntityCallback<ArrayList<Integer>>> joinCallbacks;
   private Set<Integer> allActivePublishers = new CopyOnWriteArraySet();
   private Set<Integer> joinEventPublishers = new CopyOnWriteArraySet();
   private JanusSignaler signaler;
   private ConferenceSessionDescription sessionDescription;
   private List<ConferenceSessionCallbacks> packetListeners = new CopyOnWriteArrayList();
   private ConferenceEntityCallback<Map<Integer, Boolean>> participantListener;
   protected Executor signalerExecutor;
   private BigInteger joinSenderID;

   ConferenceSession(ConferenceClient client, CameraEventsHandler cameraErrorHandler, JanusSignaler signaler, QBConferenceType conferenceType) {
      super(client, (QBRTCSessionDescription)null, cameraErrorHandler);
      this.sessionDescription = new ConferenceSessionDescription(conferenceType);
      this.joinCallbacks = new CopyOnWriteArraySet();
      this.signaler = signaler;
      this.signaler.setJanusResponseEventCallback(this);
      this.CLASS_TAG = TAG + "(" + client.getCurrentUserId() + ")";
      this.signalerExecutor = Executors.newSingleThreadScheduledExecutor();
   }

   private void makeAndAddNewChannelForOpponent(Integer opponentID, QBConferenceType qbConferenceType, boolean setLocalStream) {
      if (!this.channels.containsKey(opponentID)) {
         QBConferencePeerConnection newChannel = new QBConferencePeerConnection(this.factoryManager, this, opponentID, qbConferenceType, setLocalStream, false);
         this.channels.put(opponentID, newChannel);
         this.activeChannels.add(newChannel);
         LOGGER.d(this.CLASS_TAG, "Make new channel for opponent:" + opponentID + ", " + newChannel.toString());
      } else {
         LOGGER.d(this.CLASS_TAG, "Channel with this opponent " + opponentID + " already exists");
      }

   }

   private void createOffer(Integer opponentID) {
      ((QBConferencePeerConnection)this.getPeerConnection(opponentID)).startOffer();
      if (QBRTCConfig.getStatsReportTimeInterval() > 0L) {
         this.startFetchStatsReport();
      }

   }

   private void createAnswer(Integer opponentID, String sdp) {
      QBConferencePeerConnection channel = (QBConferencePeerConnection)this.getPeerConnection(opponentID);
      if (channel != null) {
         channel.setRemoteSessionDescription(sdp, Type.OFFER);
         channel.startAnswer();
         if (QBRTCConfig.getStatsReportTimeInterval() > 0L) {
            this.startFetchStatsReport();
         }
      }

   }

   private void cleanUpPeerConnection(Integer userId) {
      QBConferencePeerConnection channel = (QBConferencePeerConnection)this.channels.get(userId);
      if (channel != null) {
         channel.close();
      }

   }

   private boolean isPublisherEvent(BigInteger senderID) {
      return this.joinSenderID.equals(senderID);
   }

   private void procRemoteOfferSDP(String sdp, Integer opponentID, Type type) {
      LOGGER.d(this.CLASS_TAG, "Process accept from " + opponentID);
      QBConferencePeerConnection channel = (QBConferencePeerConnection)this.channels.get(opponentID);
      if (channel != null) {
         channel.setRemoteSDPToConnection(sdp, type);
      }

   }

   protected void callHangUpOnChannels(Map<String, String> userInfo) {
      LOGGER.d(this.CLASS_TAG, "callHangUpOnChannels");
      if (this.channels.size() != 0) {
         Iterator var2 = this.channels.keySet().iterator();

         while(var2.hasNext()) {
            Integer opponentID = (Integer)var2.next();
            QBConferencePeerConnection channel = (QBConferencePeerConnection)this.channels.get(opponentID);
            channel.close();
         }

      }
   }

   public void addConferenceSessionListener(ConferenceSessionCallbacks listener) {
      if (listener != null) {
         this.packetListeners.add(listener);
         LOGGER.d(this.CLASS_TAG, "ADD ConferenceSessionCallbacks " + listener);
      } else {
         LOGGER.e(this.CLASS_TAG, "Try to add null ConferenceSessionCallbacks");
      }

   }

   public void removeConferenceSessionListener(ConferenceSessionCallbacks listener) {
      this.packetListeners.remove(listener);
      LOGGER.d(this.CLASS_TAG, "REMOVE ConferenceSessionCallbacks " + listener);
   }

   public Set<Integer> getActivePublishers() {
      return this.allActivePublishers;
   }

   private void notifyJoinedSuccessListeners(ArrayList<Integer> publishers) {
      Iterator var2 = this.joinCallbacks.iterator();

      while(var2.hasNext()) {
         ConferenceEntityCallback<ArrayList<Integer>> listener = (ConferenceEntityCallback)var2.next();
         listener.onSuccess(publishers);
      }

   }

   private void notifyJoinedErrorListeners(WsException exception) {
      Iterator var2 = this.joinCallbacks.iterator();

      while(var2.hasNext()) {
         ConferenceEntityCallback<ArrayList<Integer>> listener = (ConferenceEntityCallback)var2.next();
         listener.onError(exception);
      }

   }

   private void notifyOnlineParticipantsReceived(Map<Integer, Boolean> participants) {
      if (this.participantListener != null) {
         this.participantListener.onSuccess(participants);
      }

   }

   private void notifyOnlineParticipantsReceivedError(WsException e) {
      if (this.participantListener != null) {
         this.participantListener.onError(e);
      }

   }

   private void notifyPublishersReceived(ArrayList<Integer> publishers) {
      Iterator var2 = this.packetListeners.iterator();

      while(var2.hasNext()) {
         ConferenceSessionCallbacks listener = (ConferenceSessionCallbacks)var2.next();
         listener.onPublishersReceived(publishers);
      }

   }

   private void notifyPublisherLeave(Integer userID) {
      Iterator var2 = this.packetListeners.iterator();

      while(var2.hasNext()) {
         ConferenceSessionCallbacks listener = (ConferenceSessionCallbacks)var2.next();
         listener.onPublisherLeft(userID);
      }

   }

   private void notifySessionClosed(ConferenceSession session) {
      Iterator var2 = this.packetListeners.iterator();

      while(var2.hasNext()) {
         ConferenceSessionCallbacks listener = (ConferenceSessionCallbacks)var2.next();
         listener.onSessionClosed(session);
      }

   }

   private void notifyMediaReceived(String type, boolean success) {
      Iterator var3 = this.packetListeners.iterator();

      while(var3.hasNext()) {
         ConferenceSessionCallbacks listener = (ConferenceSessionCallbacks)var3.next();
         listener.onMediaReceived(type, success);
      }

   }

   private void notifySlowLinkReceived(boolean uplink, int nacks) {
      Iterator var3 = this.packetListeners.iterator();

      while(var3.hasNext()) {
         ConferenceSessionCallbacks listener = (ConferenceSessionCallbacks)var3.next();
         listener.onSlowLinkReceived(uplink, nacks);
      }

   }

   private void notifySessionError(WsException ex) {
      Iterator var2 = this.packetListeners.iterator();

      while(var2.hasNext()) {
         ConferenceSessionCallbacks listener = (ConferenceSessionCallbacks)var2.next();
         listener.onError(ex);
      }

   }

   private void autoSubscribeToPublisher(boolean autoSubscribe) {
      if (autoSubscribe) {
         LOGGER.d(this.CLASS_TAG, "autoSubscribeToPublisher enabled");
         Iterator var2 = this.joinEventPublishers.iterator();

         while(var2.hasNext()) {
            Integer publisher = (Integer)var2.next();
            this.subscribeToPublisher(publisher);
         }

         this.joinEventPublishers.clear();
      }

   }

   public void subscribeToPublisher(final Integer publisherID) {
      LOGGER.d(this.CLASS_TAG, "subscribeToPublisher signalerExecutor.execute");
      this.signalerExecutor.execute(new Runnable() {
         public void run() {
            try {
               ConferenceSession.this.signaler.attachPlugin(ConferenceConfig.getPlugin(), publisherID);
               ConferenceSession.this.signaler.subscribe(ConferenceSession.this.sessionDescription.getDialogID(), publisherID);
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "subscribeToPublisher success publisher= " + publisherID);
            } catch (WsException var2) {
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "subscribeToPublisher error: " + var2.getMessage());
               ConferenceSession.this.notifySessionError(var2);
            }

         }
      });
   }

   public void unsubscribeFromPublisher(Integer publisherID) {
      this.cleanUpPeerConnection(publisherID);
      this.detachHandleId(publisherID);
   }

   public void getOnlineParticipants(ConferenceEntityCallback<Map<Integer, Boolean>> callback) {
      this.participantListener = callback;
      this.signalerExecutor.execute(new Runnable() {
         public void run() {
            try {
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "signaler getOnlineParticipants");
               ConferenceSession.this.signaler.sendGetOnlineParticipants(ConferenceSession.this.sessionDescription.getDialogID(), ConferenceSession.this.getCurrentUserID());
            } catch (WsException var2) {
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "getOnlineParticipants error: " + var2.getMessage());
               ConferenceSession.this.notifyOnlineParticipantsReceivedError(var2);
            }

         }
      });
   }

   public void joinDialog(String dialogID, QBConferenceRole conferenceRole, ConferenceEntityCallback<ArrayList<Integer>> callback) {
      if (callback != null) {
         this.joinCallbacks.add(callback);
      }

      this.sessionDescription.setDialogID(dialogID);
      this.sessionDescription.setConferenceRole(conferenceRole);
      new JoinRoomTask(dialogID, this.getCurrentUserID(), "publisher");
   }

   private void selectJoinStrategy() {
      QBConferenceRole conferenceRole = this.sessionDescription.getConferenceRole();
      if (QBConferenceRole.PUBLISHER.equals(conferenceRole)) {
         this.performSendOffer();
      } else if (QBConferenceRole.LISTENER.equals(conferenceRole)) {
         this.proceedAsListener();
      }

   }

   private void proceedAsListener() {
      this.setState(QBRTCSessionState.QB_RTC_SESSION_CONNECTING);
      this.autoSubscribeToPublisher(((ConferenceClient)this.client).isAutoSubscribeAfterJoin());
   }

   private void performSendOffer() {
      this.setState(QBRTCSessionState.QB_RTC_SESSION_CONNECTING);
      this.makeAndAddNewChannelForOpponent(this.getCurrentUserID(), this.getConferenceType(), true);
      this.createOffer(this.getCurrentUserID());
   }

   private void performSendAnswer(Integer opponentID, String sdp) {
      this.makeAndAddNewChannelForOpponent(opponentID, QBConferenceType.QB_CONFERENCE_TYPE_VIDEO, false);
      this.createAnswer(opponentID, sdp);
   }

   private void leaveSession() {
      this.signalerExecutor.execute(new Runnable() {
         public void run() {
            try {
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "signaler leave");
               ConferenceSession.this.signaler.leave(ConferenceSession.this.getCurrentUserID());
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "signaler detachHandleIdAll");
               ConferenceSession.this.signaler.detachHandleIdAll();
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "signaler destroySession");
               ConferenceSession.this.signaler.destroySession();
            } catch (WsException var2) {
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "leave error: " + var2.getMessage());
               ConferenceSession.this.notifySessionError(var2);
            }

         }
      });
   }

   private void detachHandleId(final Integer userId) {
      this.signalerExecutor.execute(new Runnable() {
         public void run() {
            try {
               ConferenceSession.this.signaler.detachHandleId(userId);
            } catch (WsException var2) {
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "detachHandleId error: " + var2.getMessage());
               ConferenceSession.this.notifySessionError(var2);
            }

         }
      });
   }

   public Integer getCurrentUserID() {
      return ((ConferenceClient)this.client).getCurrentUserId();
   }

   protected void noUserAction() {
      LOGGER.d(this.CLASS_TAG, "no User Actions");
   }

   public void onIceGatheringChange(IceGatheringState iceGatheringState, int userId) {
      if (iceGatheringState.equals(IceGatheringState.COMPLETE)) {
         LOGGER.d(this.CLASS_TAG, "onIceGatheringChange userId= " + userId);
         this.sendIceCandidateComplete(userId);
      }

   }

   protected void closeInternal() {
      this.connectionCallbacksList.clear();
      this.closeMediaStreamManager();
      ((ConferenceClient)this.client).postOnMainThread(new Runnable() {
         public void run() {
            ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "close session Internal");
            ConferenceSession.this.notifySessionClosed(ConferenceSession.this);
         }
      });
   }

   public void onChannelConnectionConnected(QBPeerConnection channel) {
      if (this.isConnectedWithOpponent(channel.getUserID())) {
         super.onChannelConnectionConnected(channel);
      } else {
         this.setState(QBRTCSessionState.QB_RTC_SESSION_CONNECTED);
      }

   }

   public void onChannelConnectionClosed(QBPeerConnection channel) {
      super.onChannelConnectionClosed(channel);
      this.channels.remove(channel.getUserID());
   }

   private boolean isConnectedWithOpponent(Integer userID) {
      return !this.getCurrentUserID().equals(userID);
   }

   public void onSessionDescriptionSend(QBPeerConnection channel, SessionDescription sdp) {
      LOGGER.d(this.CLASS_TAG, "onSessionDescriptionSend sdp.type: " + sdp.type);
      if (sdp.type == Type.OFFER) {
         this.sendOfferSignaler(sdp, channel.getUserID());
      } else if (sdp.type == Type.ANSWER) {
         this.sendAnswerSignaler(sdp, channel.getUserID());
      }

   }

   public QBConferenceType getConferenceType() {
      return this.sessionDescription.getConferenceType();
   }

   public QBConferenceRole getConferenceRole() {
      return this.sessionDescription.getConferenceRole();
   }

   public String getSessionID() {
      return this.sessionDescription.getSessionID();
   }

   public String getDialogID() {
      return this.sessionDescription.getDialogID();
   }

   private void sendAnswerSignaler(final SessionDescription sdp, final int userId) {
      this.signalerExecutor.execute(new Runnable() {
         public void run() {
            try {
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "signaler sendAnswer");
               ConferenceSession.this.signaler.sendAnswer(sdp, userId, ConferenceSession.this.getConferenceType(), (Map)null);
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "signaler sendAnswer success");
            } catch (WsException var2) {
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "sendAnswerSignaler error: " + var2.getMessage());
               ConferenceSession.this.notifySessionError(var2);
            }

         }
      });
   }

   private void sendOfferSignaler(final SessionDescription sdp, final int userID) {
      this.signalerExecutor.execute(new Runnable() {
         public void run() {
            try {
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "signaler sendOffer");
               ConferenceSession.this.signaler.sendOffer(sdp, userID, ConferenceSession.this.getConferenceType(), (Map)null);
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "signaler sendOffer success");
            } catch (WsException var2) {
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "sendOfferSignaler error: " + var2.getMessage());
               ConferenceSession.this.notifySessionError(var2);
            }

         }
      });
   }

   public void onIceCandidatesSend(QBPeerConnection channel, List<IceCandidate> candidates) {
      LOGGER.d(this.CLASS_TAG, "onIceCandidatesSend for channel opponent " + channel.getUserID());
      if (this.isConnectionActive()) {
         if (candidates.size() > 0) {
            this.sendIceCandidates(candidates, channel.getUserID());
         }
      } else {
         LOGGER.d(this.CLASS_TAG, "Store candidates");
      }

   }

   private void sendIceCandidates(final List<IceCandidate> candidates, final int userId) {
      this.signalerExecutor.execute(new Runnable() {
         public void run() {
            ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "signaler sendIceCandidates");
            Map<String, String> params = new HashMap();
            Iterator var2 = candidates.iterator();

            while(var2.hasNext()) {
               IceCandidate iceCandidate = (IceCandidate)var2.next();
               ConferenceSession.this.sendIceCandidate(iceCandidate, userId, params);
            }

         }
      });
   }

   private void sendIceCandidate(IceCandidate iceCandidate, int userId, Map<String, String> customParameters) {
      try {
         LOGGER.d(this.CLASS_TAG, "signaler sendIceCandidate for userId= " + userId);
         this.signaler.sendIceCandidate(iceCandidate, userId, customParameters);
      } catch (WsException var5) {
         LOGGER.d(this.CLASS_TAG, "sendIceCandidate error: " + var5.getMessage());
         this.notifySessionError(var5);
      }

   }

   private void sendIceCandidateComplete(final int userId) {
      this.signalerExecutor.execute(new Runnable() {
         public void run() {
            ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "signaler sendIceCandidateComplete for userId= " + userId);

            try {
               ConferenceSession.this.signaler.sendIceCandidateComplete(userId);
            } catch (WsException var2) {
               ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "sendIceCandidateComplete error: " + var2.getMessage());
               ConferenceSession.this.notifySessionError(var2);
            }

         }
      });
   }

   public void onRemoteSDPEventAnswer(String sdp) {
      LOGGER.d(this.CLASS_TAG, "onRemoteSDPEventAnswer");
      this.procRemoteOfferSDP(sdp, this.getCurrentUserID(), Type.ANSWER);
   }

   public void onRemoteSDPEventOffer(int opponentId, String sdp) {
      LOGGER.d(this.CLASS_TAG, "onRemoteSDPEventOffer" + opponentId);
      this.performSendAnswer(opponentId, sdp);
   }

   public void onJoinEvent(ArrayList<Integer> publishersList, BigInteger senderID) {
      LOGGER.d(this.CLASS_TAG, "onJoinEvent");
      this.joinSenderID = senderID;
      this.joinEventPublishers.addAll(publishersList);
      this.allActivePublishers.addAll(publishersList);
      this.selectJoinStrategy();
      this.notifyJoinedSuccessListeners(publishersList);
   }

   public void onPublishedEvent(ArrayList<Integer> publishersList) {
      LOGGER.d(this.CLASS_TAG, "onPublishedEvent");
      this.allActivePublishers.addAll(publishersList);
      this.notifyPublishersReceived(publishersList);
   }

   public void onUnPublishedEvent(int publisherID) {
      LOGGER.d(this.CLASS_TAG, "onUnPublishedEvent publisherID= " + publisherID);
   }

   public void onStartedEvent(String started) {
      LOGGER.d(this.CLASS_TAG, "onStartedEvent");
   }

   public void onLeavePublisherEvent(int publisherID) {
      if (this.allActivePublishers.contains(publisherID)) {
         this.allActivePublishers.remove(publisherID);
         this.notifyPublisherLeave(publisherID);
         this.detachHandleId(publisherID);
         this.cleanUpPeerConnection(publisherID);
         LOGGER.d(this.CLASS_TAG, "onLeavePublisherEvent publisherID= " + publisherID + " cleaning all stuff");
      } else {
         LOGGER.d(this.CLASS_TAG, "onLeavePublisherEvent publisherID= " + publisherID + " already left");
      }

   }

   public void onLeaveCurrentUserEvent(boolean success) {
      LOGGER.d(this.CLASS_TAG, "onLeaveCurrentUserEvent");
   }

   public void onWebRTCUpReceived(BigInteger senderID) {
      LOGGER.d(this.CLASS_TAG, "WsWebRTCUp onWebRTCUpReceived");
      if (this.isPublisherEvent(senderID)) {
         LOGGER.d(this.CLASS_TAG, "became a publisher");
         this.autoSubscribeToPublisher(((ConferenceClient)this.client).isAutoSubscribeAfterJoin());
      }

   }

   public void onMediaReceived(String type, boolean success) {
      LOGGER.d(this.CLASS_TAG, "onMediaReceived");
      this.notifyMediaReceived(type, success);
   }

   public void onSlowLinkReceived(boolean uplink, int nacks) {
      LOGGER.d(this.CLASS_TAG, "onSlowLinkReceived");
      this.notifySlowLinkReceived(uplink, nacks);
   }

   public void onHangUp(String reason) {
      LOGGER.d(this.CLASS_TAG, "onHangUp reason= " + reason);
      this.notifySessionError(new WsHangUpException(reason));
   }

   public void onEventError(String error, int code) {
      LOGGER.d(this.CLASS_TAG, "onEventError error= " + error + ", code= " + code);
      if (code == 436) {
         this.notifyJoinedErrorListeners(new WsException(error));
      } else if (code == 432) {
         this.notifySessionError(new WsEventException(error));
      } else if (code == 428) {
         this.notifySessionError(new WsEventException(error));
      }

   }

   public void onPacketError(String error) {
      LOGGER.d(this.CLASS_TAG, "onPacketError: " + error);
   }

   public void onGetOnlineParticipants(Map<Integer, Boolean> participants) {
      LOGGER.d(this.CLASS_TAG, "onGetOnlineParticipants: " + participants);
      this.notifyOnlineParticipantsReceived(participants);
   }

   public void onVideoRoomEvent(String event) {
      LOGGER.d(this.CLASS_TAG, "onVideoRoomEvent: " + event);
      this.notifyOnlineParticipantsReceivedError(new WsException(event));
   }

   public void leave() {
      if (!this.getState().equals(QBRTCSessionState.QB_RTC_SESSION_GOING_TO_CLOSE) && !this.getState().equals(QBRTCSessionState.QB_RTC_SESSION_CLOSED)) {
         this.closeMediaStreamManager();
         this.setState(QBRTCSessionState.QB_RTC_SESSION_GOING_TO_CLOSE);
         this.signaler.stopAutoSendPresence();
         this.leaveSession();
         this.disposeSession();
      } else {
         LOGGER.d(this.CLASS_TAG, "Trying to leave from chat dialog, while session has been already closed");
      }

   }

   private void disposeSession() {
      if (this.sessionDescription.getConferenceRole().equals(QBConferenceRole.LISTENER) && this.channels.size() == 0) {
         this.closeSession();
      } else {
         this.hangUpChannels(new HashMap());
      }

   }

   protected boolean isNeedToClose() {
      if (!this.sessionDescription.getConferenceRole().equals(QBConferenceRole.LISTENER)) {
         return super.isNeedToClose();
      } else {
         return this.getState().equals(QBRTCSessionState.QB_RTC_SESSION_GOING_TO_CLOSE) && this.activeChannels.size() == 0;
      }
   }

   private boolean isConnectionActive() {
      return this.signaler.isActive();
   }

   public void setDialogId(String dialogId) {
      this.sessionDescription.setDialogID(dialogId);
   }

   static {
      LOGGER = Logger.getInstance(ConferenceClient.TAG);
   }

   private class JoinRoomTask extends ThreadTaskEx {
      String dialogId;
      int userId;
      String role;

      JoinRoomTask(String dialogId, int userId, String role) {
         super((Bundle)null, ConferenceSession.this.signalerExecutor);
         this.dialogId = dialogId;
         this.userId = userId;
         this.role = role;
         this.execute();
      }

      public void performInAsync() {
         try {
            ConferenceSession.this.signaler.joinDialog(this.dialogId, this.userId, this.role);
         } catch (WsException var2) {
            ConferenceSession.LOGGER.d(ConferenceSession.this.CLASS_TAG, "joinDialog error: " + var2.getMessage());
            ConferenceSession.this.notifyJoinedErrorListeners(var2);
         }

      }
   }
}
