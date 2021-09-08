package com.hahalolo.call.webrtc.callbacks;

import com.hahalolo.call.webrtc.QBRTCSession;

import java.util.Map;

import timber.log.Timber;

public class QBRTCClientSessionCallbacksImpl implements QBRTCClientSessionCallbacks {

    public QBRTCClientSessionCallbacksImpl() {
    }

    @Override
    public void onReceiveNewSession(final QBRTCSession session) {
        this.log("onReceiveNewSession");
    }

    @Override
    public void onCallRejectByUser(final QBRTCSession session, final Integer userID, final Map<String, String> userInfo) {
        this.log("onCallRejectByUser");
    }
    
    @Override
    public void onCallAcceptByUser(final QBRTCSession session, final Integer userID, final Map<String, String> userInfo) {
        this.log("onCallAcceptByUser");
    }
    
    @Override
    public void onUserNotAnswer(final QBRTCSession session, final Integer userID) {
        this.log("onUserNotAnswer");
    }
    
    @Override
    public void onUserNoActions(final QBRTCSession session, final Integer userID) {
        this.log("onUserNoActions");
    }
    
    @Override
    public void onSessionClosed(final QBRTCSession session) {
        this.log("onSessionClosed");
    }
    
    @Override
    public void onSessionStartClose(final QBRTCSession session) {
        this.log("onSessionStartClose");
    }
    
    @Override
    public void onReceiveHangUpFromUser(final QBRTCSession session, final Integer userID, final Map<String, String> userInfo) {
        this.log("onReceiveHangUpFromUser");
    }
    
    private void log(final String message) {
        Timber.i(message);
    }
}
