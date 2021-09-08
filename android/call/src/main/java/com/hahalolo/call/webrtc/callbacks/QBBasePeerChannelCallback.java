package com.hahalolo.call.webrtc.callbacks;

import com.hahalolo.call.webrtc.QBPeerConnection;

import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.util.List;

public interface QBBasePeerChannelCallback {
    void onChannelConnectionDisconnected(final QBPeerConnection connection);

    void onChannelConnectionClosed(final QBPeerConnection connection);

    void onChannelConnectionConnected(final QBPeerConnection connection);

    MediaStream onLocalStreamNeedAdd(final QBPeerConnection connection);

    void onSessionDescriptionSend(final QBPeerConnection connection, final SessionDescription description);
    
    void onIceCandidatesSend(final QBPeerConnection connection, final List<IceCandidate> iceCandidates);
    
    void onIceGatheringChange(final PeerConnection.IceGatheringState connection, final int userId);
}
