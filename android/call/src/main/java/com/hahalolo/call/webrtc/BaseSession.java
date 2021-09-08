package com.hahalolo.call.webrtc;

import com.hahalolo.call.webrtc.callbacks.QBBasePeerChannelCallback;
import com.hahalolo.call.webrtc.callbacks.QBMediaStreamManagerCallback;
import com.hahalolo.call.webrtc.callbacks.QBRTCMediaCapturerCallback;
import com.hahalolo.call.webrtc.callbacks.QBRTCSessionStateCallback;
import com.hahalolo.call.webrtc.util.Logger;

import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.MediaStream;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class BaseSession<T extends BaseClient, P extends QBPeerConnection>
        implements QBMediaStreamManagerCallback, QBBasePeerChannelCallback {

    protected static final String CLASS_TAG;
    private static final Logger LOGGER;
    protected final CameraVideoCapturer.CameraEventsHandler cameraErrorHandler;
    protected Executor executor;
    protected Set<QBRTCSessionStateCallback<BaseSession>> connectionCallbacksList;
    protected volatile QBMediaStreamManager mediaStreamManager;
    protected final Map<Integer, P> channels;
    protected final Set<QBPeerConnection> activeChannels;
    protected QBRTCSessionState state;
    protected SessionOptions sessionOptions;
    protected PeerFactoryManager factoryManager;
    protected T client;
    protected QBRTCMediaCapturerCallback mediaCapturerCallback;

    public BaseSession(final T client,
                       final QBRTCSessionDescription sessionDescription,
                       final CameraVideoCapturer.CameraEventsHandler cameraErrorHandler) {
        BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "Create new BaseSession");
        this.client = client;
        this.factoryManager = client.getPeerFactoryManager();
        this.channels = new ConcurrentHashMap<>();
        this.activeChannels = new HashSet<>();
        this.cameraErrorHandler = cameraErrorHandler;
        this.connectionCallbacksList = new CopyOnWriteArraySet<>();
        this.initExecutor(this.factoryManager.getExecutor());
        this.initSessionState(sessionDescription);
        this.setOptions(new SessionOptions());
        this.mediaCapturerCallback = new DefaultRTCMediaCapturerCallback();
    }
    
    public void setMediaCapturerCallback(final QBRTCMediaCapturerCallback mediaCapturerCallback) {
        this.mediaCapturerCallback = mediaCapturerCallback;
    }
    
    public void addSessionCallbacksListener(final QBRTCSessionStateCallback callback) {
        if (callback != null) {
            this.connectionCallbacksList.add(callback);
        }
    }
    
    public void removeSessionCallbacksListener(final QBRTCSessionStateCallback callback) {
        this.connectionCallbacksList.remove(callback);
    }
    
    public QBMediaStreamManager getMediaStreamManager() {
        return this.mediaStreamManager;
    }
    
    public P getPeerConnection(final Integer userId) {
        return this.channels.get(userId);
    }
    
    @Deprecated
    public P getPeerChannel(final Integer userId) {
        return this.channels.get(userId);
    }
    
    public void setOptions(final SessionOptions sessionOptions) {
        if (sessionOptions == null) {
            return;
        }
        this.sessionOptions = sessionOptions;
    }
    
    private void initExecutor(final Executor executor) {
        if (executor != null) {
            this.executor = executor;
        }
    }
    
    private void initSessionState(final QBRTCSessionDescription sessionDescription) {
        this.setState(QBRTCSessionState.QB_RTC_SESSION_NEW);
    }
    
    protected synchronized void setState(final QBRTCSessionState state) {
        if (this.state != state) {
            this.onStateChanged(this.state = state);
        }
    }
    
    private void onStateChanged(final QBRTCSessionState state) {
        BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "onStateChanged to:" + state);
        if (QBRTCSessionState.QB_RTC_SESSION_CONNECTING == state) {
            this.factoryManager.createFactory();
        }
        this.client.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                for (final QBRTCSessionStateCallback<BaseSession> callback : BaseSession.this.connectionCallbacksList) {
                    callback.onStateChanged(BaseSession.this, state);
                }
            }
        });
    }

    public synchronized EglBase getEglContext() {
        return this.client.getEglContext();
    }

    protected QBMediaStreamManager obtainMediaStreamManager() {
        if (this.mediaStreamManager == null) {
            this.mediaStreamManager = new QBMediaStreamManager(this.factoryManager, this.client.getContext(), this);
        }
        return this.mediaStreamManager;
    }

    public abstract String getSessionID();

    public abstract QBRTCTypes.QBConferenceType getConferenceType();
    
    public synchronized QBRTCSessionState getState() {
        return this.state;
    }
    
    protected abstract void noUserAction();

    @Override
    public void onChannelConnectionDisconnected(final QBPeerConnection channel) {
        BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "onChannelConnectionDisconnected for opponent " + channel.getUserID());
        this.client.postOnMainThread(() -> {
            for (final QBRTCSessionStateCallback<BaseSession> callback : BaseSession.this.connectionCallbacksList) {
                callback.onDisconnectedFromUser(BaseSession.this, channel.getUserID());
            }
        });
    }
    
    @Override
    public void onChannelConnectionClosed(final QBPeerConnection channel) {
        BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "onChannelConnectionClosed for opponent " + channel.getUserID());
        this.client.postOnMainThread(() -> {
            for (final QBRTCSessionStateCallback<BaseSession> callback : BaseSession.this.connectionCallbacksList) {
                callback.onConnectionClosedForUser(BaseSession.this, channel.getUserID());
            }
        });
        this.checkAllChannelsClosed(channel);
    }
    
    protected synchronized void checkAllChannelsClosed(final QBPeerConnection peerChannel) {
        BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "Check is session need close");
        BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "removing peer channel " + peerChannel);
        this.activeChannels.remove(peerChannel);
        if (this.isNeedToClose()) {
            BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "Session isNeedToClose true");
            this.closeSession();
        }
        else {
            BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "Session isNeedToClose false");
        }
    }
    
    protected void closeSession() {
        BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "closeSession");
        if (QBRTCSessionState.QB_RTC_SESSION_CLOSED == this.getState()) {
            BaseSession.LOGGER.e(BaseSession.CLASS_TAG, "Session has already been closed");
            return;
        }
        this.setState(QBRTCSessionState.QB_RTC_SESSION_CLOSED);
        if (this.mediaStreamManager != null) {
            this.mediaStreamManager.setClosed();
        }
        this.stopFetchStatsReport();
        this.executor.execute(() -> BaseSession.this.closeInternal());
    }
    
    protected void closeMediaStreamManager() {
        if (this.mediaStreamManager != null) {
            this.mediaStreamManager.close();
            this.mediaStreamManager = null;
        }
    }
    
    protected void hangUpChannels(final Map<String, String> userInfo) {
        this.callHangUpOnChannels(userInfo);
    }
    
    protected abstract void closeInternal();
    
    protected abstract void callHangUpOnChannels(final Map<String, String> p0);
    
    @Override
    public void onChannelConnectionConnected(final QBPeerConnection channel) {
        BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "onChannelConnectionConnected for opponent " + channel.getUserID());
        this.setState(QBRTCSessionState.QB_RTC_SESSION_CONNECTED);
        this.client.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                for (final QBRTCSessionStateCallback<BaseSession> callback : BaseSession.this.connectionCallbacksList) {
                    callback.onConnectedToUser(BaseSession.this, channel.getUserID());
                }
            }
        });
    }
    
    @Override
    public synchronized MediaStream onLocalStreamNeedAdd(final QBPeerConnection channel) {
        BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "onLocalStreamNeedAdd for opponent " + channel.getUserID());
        final QBMediaStreamManager mediaStreamManager = this.obtainMediaStreamManager();
        final MediaStream mediaStream = mediaStreamManager.initLocalMediaStream(this.getConferenceType(), this.getEglContext().getEglBaseContext(), this.mediaCapturerCallback);
        return mediaStream;
    }

    @Override
    public void setAudioCategoryError(final Exception error) {
        BaseSession.LOGGER.d(BaseSession.CLASS_TAG, "setAudioCategoryError. " + error);
    }
    
    @Override
    public void onMediaStreamChange(final QBMediaStreamManager mediaStreamManager) {
        this.executor.execute(() -> {
            for (final QBPeerConnection channel : BaseSession.this.channels.values()) {
                channel.setLocalMediaStream(mediaStreamManager.getLocalMediaStream());
            }
        });
    }
    
    protected boolean isNeedToClose() {
        return this.activeChannels.size() == 0;
    }
    
    protected void startFetchStatsReport() {
        for (final Map.Entry<Integer, P> entry : this.channels.entrySet()) {
            entry.getValue().enableStatsEvents(true, QBRTCConfig.getStatsReportTimeInterval());
        }
    }
    
    protected void stopFetchStatsReport() {
        for (final Map.Entry<Integer, P> entry : this.channels.entrySet()) {
            entry.getValue().enableStatsEvents(false, 0L);
        }
    }
    
    public boolean isActive() {
        return this.getState().ordinal() == QBRTCSessionState.QB_RTC_SESSION_CONNECTED.ordinal();
    }

    public boolean isDestroyed() {
        return this.state.ordinal() > QBRTCSessionState.QB_RTC_SESSION_CONNECTED.ordinal();
    }

    static {
        CLASS_TAG = BaseSession.class.getSimpleName();
        LOGGER = Logger.getInstance(BaseClient.TAG);
    }

    /**
     * Call session connection states.
     */
    public enum QBRTCSessionState {
        QB_RTC_SESSION_PENDING, // A call session is in a pending state for other actions to occur.
        QB_RTC_SESSION_NEW, // A call session was successfully created and ready for the next step.
        QB_RTC_SESSION_CONNECTING, // The call session is in the progress of establishing a connection.
        QB_RTC_SESSION_CONNECTED, // // The call session is connected.
        QB_RTC_SESSION_GOING_TO_CLOSE, // A call session is going to be closed.
        QB_RTC_SESSION_CLOSED // A call session has been closed.
    }

    public static class SessionOptions {
        public boolean leaveSessionIfInitiatorHangUp;

        public SessionOptions() {
            this.leaveSessionIfInitiatorHangUp = false;
        }
    }

    private class DefaultRTCMediaCapturerCallback implements QBRTCMediaCapturerCallback {
        @Override
        public void onInitLocalMediaStream(final QBRTCMediaStream mediaStream) {
            BaseSession.LOGGER.e(DefaultRTCMediaCapturerCallback.class.getSimpleName(), " onInitLocalMediaStream ");
            try {
                BaseSession.this.obtainMediaStreamManager().setVideoCapturer(new QBRTCCameraVideoCapturer(BaseSession.this.client.getContext(), BaseSession.this.cameraErrorHandler));
            } catch (QBRTCCameraVideoCapturer.QBRTCCameraCapturerException e) {
                BaseSession.LOGGER.e(BaseSession.CLASS_TAG, "error capturing camera" + e.getLocalizedMessage());
            }
        }
    }
    
    class SessionWaitingTimers {
        private final String TAG;
        private ScheduledExecutorService scheduledTasksService;
        private Runnable waitUserActionsTask;
        private ScheduledFuture<?> futureWaitTask;
        
        public SessionWaitingTimers() {
            this.TAG = BaseSession.CLASS_TAG + "." + SessionWaitingTimers.class.getSimpleName();
            this.scheduledTasksService = Executors.newScheduledThreadPool(3);
            this.waitUserActionsTask = BaseSession.this::noUserAction;
        }
        
        protected void startWaitForUserActionsTask() {
            BaseSession.LOGGER.d(this.TAG, "startWaitForUserActionsTask for " + QBRTCConfig.getAnswerTimeInterval());
            if (!this.scheduledTasksService.isShutdown()) {
                this.futureWaitTask = this.scheduledTasksService.schedule(this.waitUserActionsTask, QBRTCConfig.getAnswerTimeInterval(), TimeUnit.SECONDS);
            }
        }
        
        protected void shutDown() {
            this.stopWaitForUserActionsTimer();
            this.scheduledTasksService.shutdownNow();
        }
        
        protected void stopWaitForUserActionsTimer() {
            BaseSession.LOGGER.d(this.TAG, "Stop WaitTimer");
            if (this.futureWaitTask != null && !this.futureWaitTask.isCancelled()) {
                this.futureWaitTask.cancel(true);
                this.futureWaitTask = null;
            }
        }
    }
}
