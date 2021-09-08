package com.hahalolo.call.webrtc.callbacks;

import com.hahalolo.call.webrtc.QBRTCSession;

public interface QBRTCClientSessionCallbacks extends QBRTCSessionEventsCallback {
    /**
     * A new call session has been received.
     * @param session {@link QBRTCSession}
     */
    void onReceiveNewSession(final QBRTCSession session);

    /**
     * A user didn't take any actions on the received call session.
     * @param session {@link QBRTCSession}
     * @param userId Opponent Id
     */
    void onUserNoActions(final QBRTCSession session, final Integer userId);

    /**
     * A call session is going to be closed.
     * @param session {@link QBRTCSession}
     */
    void onSessionStartClose(final QBRTCSession session);
}
