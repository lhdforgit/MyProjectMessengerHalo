package com.hahalolo.call.webrtc.callbacks;

import com.hahalolo.call.webrtc.QBPeerConnection;
import com.hahalolo.call.webrtc.QBRTCTypes;

public interface QBPeerChannelCallback extends QBBasePeerChannelCallback {
    void onHangUpSend(final QBPeerConnection connection);

    void onChannelNotAnswer(final QBPeerConnection connection);

    void onChannelConnectionConnecting(final QBPeerConnection connection);

    void onChannelConnectionFailed(final QBPeerConnection connection);

    QBRTCTypes.QBConferenceType conferenceTypeForChannel(final QBPeerConnection connection);
}
