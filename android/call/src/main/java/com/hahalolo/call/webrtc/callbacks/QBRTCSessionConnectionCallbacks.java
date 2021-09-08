package com.hahalolo.call.webrtc.callbacks;

import com.hahalolo.call.webrtc.QBRTCSession;

/**
 * Interface that allows for implementing classes to listen for main session connection events.
 */
public interface QBRTCSessionConnectionCallbacks extends QBRTCSessionStateCallback<QBRTCSession> {

    /**
     * Calls each time when channel initialization was started for some user.
     * @param session QBRTCSession instance
     * @param userID   ID of opponent
     */
    void onStartConnectToUser(final QBRTCSession session, final Integer userID);

    /**
     * Called in case when disconnected by timeout
     * @param session QBRTCSession instance
     * @param userID   ID of opponent
     */
    void onDisconnectedTimeoutFromUser(final QBRTCSession session, final Integer userID);

    /**
     * Called in case when connection failed with user
     * @param session QBRTCSession instance
     * @param userID   ID of opponent
     */
    void onConnectionFailedWithUser(final QBRTCSession session, final Integer userID);
}
