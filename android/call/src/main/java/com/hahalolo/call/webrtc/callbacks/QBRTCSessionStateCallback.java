package com.hahalolo.call.webrtc.callbacks;

import com.hahalolo.call.webrtc.BaseSession;

public interface QBRTCSessionStateCallback<T extends BaseSession> {
    /**
     * A call session connection state has been changed in real-time. View all available call session states in the Call session states section.
     */
    void onStateChanged(final T t, final BaseSession.QBRTCSessionState sessionState);

    /**
     * A peer connection has been established.
     */
    void onConnectedToUser(final T t, final Integer userId);

    /**
     * A connection was terminated.
     */
    void onDisconnectedFromUser(final T t, final Integer userId);

    /**
     * A connection has been closed for the user.
     */
    void onConnectionClosedForUser(final T t, final Integer userId);
}
