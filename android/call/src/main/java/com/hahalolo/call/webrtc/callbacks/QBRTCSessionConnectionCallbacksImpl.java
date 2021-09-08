package com.hahalolo.call.webrtc.callbacks;

import com.hahalolo.call.webrtc.BaseSession;
import com.hahalolo.call.webrtc.QBRTCSession;

import timber.log.Timber;

public class QBRTCSessionConnectionCallbacksImpl implements QBRTCSessionConnectionCallbacks {
    public QBRTCSessionConnectionCallbacksImpl() {
    }

    @Override
    public void onStateChanged(final QBRTCSession session, final BaseSession.QBRTCSessionState state) {
        this.log("onStateChanged");
    }

    @Override
    public void onStartConnectToUser(final QBRTCSession session, final Integer userID) {
        this.log("onStartConnectToUser");
    }
    
    @Override
    public void onConnectedToUser(final QBRTCSession session, final Integer userID) {
        this.log("onConnectedToUser");
    }
    
    @Override
    public void onConnectionClosedForUser(final QBRTCSession session, final Integer userID) {
    }
    
    @Override
    public void onDisconnectedFromUser(final QBRTCSession session, final Integer userID) {
        this.log("onDisconnectedFromUser");
    }
    
    @Override
    public void onDisconnectedTimeoutFromUser(final QBRTCSession session, final Integer userID) {
        this.log("onDisconnectedTimeoutFromUser");
    }
    
    @Override
    public void onConnectionFailedWithUser(final QBRTCSession session, final Integer userID) {
        this.log("onConnectionFaildWithUser");
    }
    
    private void log(final String message) {
        Timber.d(message);
    }
}
