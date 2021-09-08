package com.hahalolo.call.webrtc.callbacks;

import com.hahalolo.call.webrtc.QBRTCSession;

import java.util.Map;

public interface QBRTCSessionEventsCallback {

    /**
     * A remote peer did not respond to your call within the timeout period.
     * @param session {@link QBRTCSession}
     * @param userId Opponent Id
     */
    void onUserNotAnswer(final QBRTCSession session, final Integer userId);

    /**
     * A call session has been rejected.
     * @param session {@link QBRTCSession}
     * @param userId Opponent Id
     */
    void onCallRejectByUser(final QBRTCSession session, final Integer userId, final Map<String, String> map);

    /**
     * A call session has been accepted.
     * @param session {@link QBRTCSession}
     * @param userId Opponent Id
     */
    void onCallAcceptByUser(final QBRTCSession session, final Integer userId, final Map<String, String> map);

    /**
     * An accepted call session has been ended by the peer by pressing the hang-up button.
     * @param session {@link QBRTCSession}
     * @param userId Opponent Id
     */
    void onReceiveHangUpFromUser(final QBRTCSession session, final Integer userId, final Map<String, String> map);

    /**
     * A call session has been closed.
     * @param session {@link QBRTCSession}
     */
    void onSessionClosed(final QBRTCSession session);
}