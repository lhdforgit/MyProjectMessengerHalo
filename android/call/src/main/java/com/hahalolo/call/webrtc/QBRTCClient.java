package com.hahalolo.call.webrtc;

import android.content.Context;

import com.hahalolo.call.webrtc.callbacks.QBRTCClientSessionCallbacksImpl;
import com.hahalolo.call.webrtc.callbacks.QBRTCSessionEventsCallback;
import com.hahalolo.call.webrtc.util.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebRTC singletone class. For getting it instance use static generation method getInstance(), manages {@link QBRTCSession}
 * Also this class responsible for storing listeners which implements interfaces below:
 * {@link com.hahalolo.call.webrtc.callbacks.QBRTCClientSessionCallbacks} - interface for listening session lifecycle
 * Next steps should be executed before users start conversation:
 * The {@link QBRTCClient} should be setted up
 * You must start call or receive it from opponent
 * You must handle incoming {@link QBRTCSession} in case of opponent call to you
 * You must handle incoming of new sessions
 */
public final class QBRTCClient extends BaseClient {

    public static final String TAG = "RTCClient";

    private static volatile QBRTCClient INSTANCE;
    private static final Logger LOGGER;

    private final Set<QBRTCSessionEventsCallback> sessionCallbacksList;
    protected final Map<String, QBRTCSession> sessions;

    private QBRTCClient(final Context context) {
        super(context);
        this.sessionCallbacksList = new CopyOnWriteArraySet<>();
        this.sessionCallbacksList.add(new SessionClosedListener());
        this.sessions = Collections.synchronizedMap(new HashMap<>());
    }

    public static QBRTCClient getInstance(final Context context) {
        if (QBRTCClient.INSTANCE == null) {
            synchronized (QBRTCClient.class) {
                if (QBRTCClient.INSTANCE == null) {
                    QBRTCClient.LOGGER.d("", "Create QBRTCClient INSTANCE");
                    QBRTCClient.INSTANCE = new QBRTCClient(context);
                }
            }
        }
        return QBRTCClient.INSTANCE;
    }

    private QBRTCSession createSessionWithDescription(final QBRTCSessionDescription sessionDescription) {
        QBRTCClient.LOGGER.d("", "createSessionWithDescription" + sessionDescription);
        final QBRTCSession session = new QBRTCSession(this, sessionDescription, this.sessionCallbacksList, this.cameraErrorHandler);
        this.sessions.put(sessionDescription.getSessionId(), session);
        return session;
    }

    public synchronized boolean isLastActiveSession() {
        synchronized (this.sessions) {
            Iterator var2 = this.sessions.keySet().iterator();

            QBRTCSession session;
            do {
                if (!var2.hasNext()) {
                    return true;
                }

                String key = (String) var2.next();
                session = this.sessions.get(key);
            } while (session.isDestroyed());

            return false;
        }
    }

    public QBRTCSession getSession(final String sessionId) {
        return this.sessions.get(sessionId);
    }

    public void addSessionCallbacksListener(final QBRTCSessionEventsCallback callback) {
        if (callback != null) {
            this.sessionCallbacksList.add(callback);
            QBRTCClient.LOGGER.d("", " Added session CALLBACK listener" + callback);
        } else {
            QBRTCClient.LOGGER.e("", "Try to add null SessionCallbacksListener");
        }
    }

    public void removeSessionsCallbacksListener(final QBRTCSessionEventsCallback callback) {
        this.sessionCallbacksList.remove(callback);
        QBRTCClient.LOGGER.d("", " REMOVE SessionsCallbacksListene " + callback);
    }

    private QBRTCSession getOrCreateSession(final QBRTCSessionDescription sessionDescription) {
        synchronized (this.sessions) {
            QBRTCSession session = this.sessions.get(sessionDescription.getSessionId());
            if (session == null) {
                session = this.createSessionWithDescription(sessionDescription);
            }
            return session;
        }
    }

    public void destroy() {
        QBRTCClient.LOGGER.d("", "destroy");
        this.sessionCallbacksList.clear();
    }

    static {
        LOGGER = Logger.getInstance("RTCClient");
    }

    class SessionClosedListener extends QBRTCClientSessionCallbacksImpl {
        @Override
        public void onSessionClosed(final QBRTCSession session) {
            QBRTCClient.LOGGER.d("", "onSessionClosed start");
            if (QBRTCClient.this.isLastActiveSession()) {
                QBRTCClient.this.clear();
            }
        }
    }
}
