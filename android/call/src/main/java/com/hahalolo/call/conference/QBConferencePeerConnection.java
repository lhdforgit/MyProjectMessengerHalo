package com.hahalolo.call.conference;

import com.hahalolo.call.webrtc.PeerFactoryManager;
import com.hahalolo.call.webrtc.QBPeerConnection;
import com.hahalolo.call.webrtc.QBRTCTypes.QBConferenceType;
import com.hahalolo.call.webrtc.QBRTCTypes.QBRTCCloseReason;
import com.hahalolo.call.webrtc.callbacks.QBBasePeerChannelCallback;

import org.webrtc.PeerConnection.ContinualGatheringPolicy;
import org.webrtc.SessionDescription.Type;

public class QBConferencePeerConnection extends QBPeerConnection {
   QBConferencePeerConnection(
           PeerFactoryManager peerFactoryManager,
           QBBasePeerChannelCallback callback,
           Integer userID,
           QBConferenceType conferenceType,
           boolean setLocalStream,
           boolean useDialingTimer) {
      super(peerFactoryManager, callback, userID, conferenceType, setLocalStream, useDialingTimer);
      this.initContinualGatheringPolicy(ContinualGatheringPolicy.GATHER_ONCE);
   }

   void setRemoteSDPToConnection(String sdp, Type type) {
      this.setRemoteSDPToConnection(this.convertSdpRawToModel(sdp, type));
   }

   void setRemoteSessionDescription(String remoteSDP, Type type) {
      this.setRemoteSessionDescription(this.convertSdpRawToModel(remoteSDP, type));
   }

   void close() {
      super.close(QBRTCCloseReason.QB_RTC_HANG_UP);
   }

   void startOffer() {
      super.startAsOffer();
   }

   void startAnswer() {
      super.startAsAnswer();
   }
}
