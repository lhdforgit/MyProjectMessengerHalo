package com.hahalolo.call.webrtc;

import com.hahalolo.call.webrtc.callbacks.QBBasePeerChannelCallback;
import com.hahalolo.call.webrtc.callbacks.QBPeerChannelCallback;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.MediaStreamTrack;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.StatsObserver;
import org.webrtc.StatsReport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class QBPeerConnection {

    private Executor executor;
    private final PeerChannelLifeCycleTimers peerChannelTimers;
    private QBRTCTypes.QBConferenceType conferenceType;
    private QBBasePeerChannelCallback pcEventsCallback;
    private Integer userID;
    private MediaConstraints sdpConstraints;
    private QBRTCTypes.QBRTCConnectionState state;
    private List<IceCandidate> iceCandidates;
    private MediaStream localStream;
    private MediaStream remoteStream;
    private PeerConnection.Observer pcObserver;
    private SdpObserver sdpObserver;
    private SessionDescription remoteSDP;
    private SessionDescription localSdp;
    private final Timer statsTimer;
    private boolean isError;
    private QBRTCTypes.QBRTCCloseReason disconnectReason;
    private long answerTime;
    private volatile PeerConnection peerConnection;
    private volatile boolean isCloseStarted;
    private PeerFactoryManager peerFactoryManager;
    private boolean setLocalStream;
    private boolean useDialingTimer;
    private PeerConnection.ContinualGatheringPolicy continualGatheringPolicy;

    protected QBPeerConnection(
            final PeerFactoryManager peerFactoryManager,
            final QBBasePeerChannelCallback callback,
            final Integer userID,
            final QBRTCTypes.QBConferenceType conferenceType,
            final boolean setLocalStream,
            final boolean useDialingTimer) {

        this.pcObserver = new PCObserver();
        this.sdpObserver = new SDPObserver();
        this.statsTimer = new Timer();
        this.peerFactoryManager = peerFactoryManager;
        this.pcEventsCallback = callback;
        this.userID = userID;
        this.sdpConstraints = RTCMediaUtils.createConferenceConstraints(conferenceType);
        this.state = QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_NEW;
        this.iceCandidates = new LinkedList<>();
        this.conferenceType = conferenceType;
        this.setLocalStream = setLocalStream;
        this.useDialingTimer = useDialingTimer;
        this.peerChannelTimers = new PeerChannelLifeCycleTimers();
        this.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY;
        this.peerChannelTimers.initSchedledTasks();
        this.disconnectReason = QBRTCTypes.QBRTCCloseReason.QB_RTC_UNKNOWN;
        this.initExecutor(peerFactoryManager.getExecutor());
    }

    public synchronized QBRTCTypes.QBRTCConnectionState getState() {
        return this.state;
    }

    public QBRTCTypes.QBRTCCloseReason getDisconnectReason() {
        return this.disconnectReason;
    }

    private void initExecutor(final Executor executor) {
        this.executor = executor;
    }

    private void createConnection() {
        Timber.d("createConnection for opponent %s", this.userID);

        this.executor.execute(() -> {
            if (!QBPeerConnection.this.isDestroyed()) {
                final PeerConnectionFactory peerConnectionFactory = peerFactoryManager.getPeerConnectionFactory();
                final PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(QBRTCUtils.getIceServersList());
                rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
                rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.BALANCED;
                rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
                rtcConfig.continualGatheringPolicy = continualGatheringPolicy;
                rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
                Timber.d("createConnection rtcConfig.bundlePolicy = %s", rtcConfig.bundlePolicy);
                final MediaConstraints pcConstraints = RTCMediaUtils.createPeerConnectionConstraints();
                peerConnection = peerConnectionFactory.createPeerConnection(rtcConfig, pcConstraints, pcObserver);
                if (setLocalStream) {
                    setLocalMediaStream();
                }
            }
        });
    }

    private void setLocalMediaStream() {
        this.localStream = this.pcEventsCallback.onLocalStreamNeedAdd(this);
        this.peerConnection.addStream(this.localStream);
    }

    void createOffer() {
        Timber.d("createOffer for opponent %s", userID);
        this.executor.execute(() -> {
            if (isPeerConnectionValid()) {
                peerConnection.createOffer(sdpObserver, sdpConstraints);
            }
        });
    }

    void createAnswer() {
        Timber.d("createAnswer for opponent %s", this.userID);

        this.executor.execute(() -> {
            if (isPeerConnectionValid()) {
                peerConnection.createAnswer(sdpObserver, sdpConstraints);
            }
        });
    }

    protected void startAsAnswer() {
        Timber.d("startAsAnswer for opponent: %s", this.userID);

        if (this.checkDestroyed()) {
            return;
        }
        this.setState(QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_CONNECTING);
        this.peerChannelTimers.stopWaitTimer();
        this.createConnection();
        this.setRemoteSDPToConnection(this.remoteSDP);
    }

    protected void startAsOffer() {
        Timber.d("startAsOffer for opponent: %s", this.userID);

        if (this.checkDestroyed()) {
            return;
        }
        this.setState(QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_CONNECTING);
        this.createConnection();
        this.createOffer();
    }

    void startWaitOffer() {
        Timber.d("startWaitOffer for opponent: %s", this.userID);

        if (this.checkDestroyed()) {
            return;
        }
        this.setState(QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_PENDING);
        this.peerChannelTimers.startWaitTask();
    }

    void enableStatsEvents(final boolean enable, final long periodSec) {
        if (enable) {
            this.statsTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    QBPeerConnection.this.executor.execute(QBPeerConnection.this::getStats);
                }
            }, TimeUnit.SECONDS.toMillis(periodSec), TimeUnit.SECONDS.toMillis(periodSec));
        } else {
            this.statsTimer.cancel();
        }
    }

    private void getStats() {
        if (this.peerConnection == null || this.isError) {
            return;
        }
        final boolean success = this.peerConnection.getStats((StatsObserver) reports -> {
            for (final StatsReport report : reports) {
                Timber.d("report : id " + report.id + ", type : " + report.type);
            }
        }, (MediaStreamTrack) null);
        if (!success) {
            Timber.e( "getStats() returns false!");
        }
    }

    protected void setRemoteSDPToConnection(final SessionDescription remoteSessionDP) {
        Timber.d("setRemoteSDPToConnection");

        if (remoteSessionDP == null) {
            return;
        }
        this.peerChannelTimers.stopDialingTimer();
        this.remoteSDP = this.prepareDescription(remoteSessionDP);
        this.executor.execute(() -> {
            if (isPeerConnectionValid() && peerConnection.getRemoteDescription() == null) {
                Timber.d(" peerConnection.setRemoteDescription");
                peerConnection.setRemoteDescription(sdpObserver, remoteSDP);
            }
        });
    }

    private SessionDescription prepareDescription(final SessionDescription sdp) {
        final String description = RTCMediaUtils.generateRemoteDescription(sdp, new QBRTCMediaConfig(), this.conferenceType);
        return new SessionDescription(sdp.type, description);
    }

    protected SessionDescription convertSdpRawToModel(final String sdp, final SessionDescription.Type type) {
        return new SessionDescription(type, sdp);
    }

    private boolean checkDestroyed() {
        if (this.isDestroyed()) {
            Timber.d("Peer channel for user=" + this.userID + " have already been destroyed");
            return true;
        }
        return false;
    }

    private synchronized boolean isDestroyed() {
        return this.disconnectReason != QBRTCTypes.QBRTCCloseReason.QB_RTC_UNKNOWN;
    }

    void procReject() {
        Timber.d("Call Reject to opponent %s", this.userID);
        this.close(QBRTCTypes.QBRTCCloseReason.QB_RTC_RECEIVE_REJECT);
    }

    void procHungUp() {
        Timber.d("Call procHungUp to opponent %s", this.userID);
        this.close(QBRTCTypes.QBRTCCloseReason.QB_RTC_RECEIVE_HANG_UP);
    }

    protected void close(final QBRTCTypes.QBRTCCloseReason disconnectReason) {
        Timber.d("close by reason:%s", disconnectReason);
        if (this.checkDestroyed()) {
            return;
        }
        this.disconnectReason = disconnectReason;
        this.peerChannelTimers.stopAllTasks();
        this.executor.execute(QBPeerConnection.this::closeInternal);
    }

    private void noOffer() {
        Timber.d("noOffer for opponent %s", this.userID);
        this.peerChannelTimers.stopDisconnectTimer();
        if (this.pcEventsCallback instanceof QBPeerChannelCallback) {
            ((QBPeerChannelCallback) this.pcEventsCallback).onHangUpSend(this);
        }
        this.close(QBRTCTypes.QBRTCCloseReason.QB_RTC_ANSWER_TIMEOUT);
    }

    private void dialing() {
        Timber.d("dialing for opponent %s", this.userID);
        this.answerTime += QBRTCConfig.getDialingTimeInterval();
        if (this.answerTime >= QBRTCConfig.getAnswerTimeInterval()) {
            this.peerChannelTimers.stopDisconnectTimer();
            if (this.pcEventsCallback instanceof QBPeerChannelCallback) {
                ((QBPeerChannelCallback) this.pcEventsCallback).onChannelNotAnswer(this);
                ((QBPeerChannelCallback) this.pcEventsCallback).onHangUpSend(this);
            }
            this.close(QBRTCTypes.QBRTCCloseReason.QB_RTC_ANSWER_TIMEOUT);
        } else {
            this.sendSessionDescription();
        }
    }

    private void beginDialing(final boolean useDialing) {
        if (useDialing) {
            this.peerChannelTimers.startDialingTimer();
        }
        this.sendSessionDescription();
    }

    private void sendSessionDescription() {
        this.pcEventsCallback.onSessionDescriptionSend(this, this.peerConnection.getLocalDescription());
    }

    private void closeInternal() {
        this.isCloseStarted = true;
        Timber.d("Closing peer connection start.");
        if (this.peerConnection != null) {
            if (this.localStream != null) {
                this.peerConnection.removeStream(this.localStream);
            }
            this.peerConnection.dispose();
            this.peerConnection = null;
        }
        this.setState(QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_CLOSED);
        this.pcEventsCallback.onChannelConnectionClosed(this);
    }

    private boolean isPeerConnectionValid() {
        return this.peerConnection != null && !this.isDestroyed();
    }

    protected void setRemoteSessionDescription(final SessionDescription remoteSDP) {
        this.remoteSDP = remoteSDP;
    }

    SessionDescription getRemoteSDP() {
        return this.remoteSDP;
    }

    synchronized void setState(final QBRTCTypes.QBRTCConnectionState state) {
        this.state = state;
    }

    void setRemoteIceCandidates(final List<IceCandidate> candidates) {
        Timber.d("Set iceCandidates in count of: %s", candidates.size());
        this.executor.execute(() -> {
            if (isPeerConnectionValid()) {
                for (final IceCandidate candidate : candidates) {
                    peerConnection.addIceCandidate(candidate);
                }
            }
        });
    }

    void setLocalMediaStream(final MediaStream localMediaStream) {
        this.localStream = localMediaStream;
    }

    public Integer getUserID() {
        return this.userID;
    }

    protected void initContinualGatheringPolicy(final PeerConnection.ContinualGatheringPolicy continualGatheringPolicy) {
        this.continualGatheringPolicy = continualGatheringPolicy;
    }

    private void clearIceCandidates() {
        this.iceCandidates.clear();
    }

    private class SDPObserver implements SdpObserver {
        private SDPObserver() {
        }

        public void onCreateSuccess(final SessionDescription sessionDescription) {
            this.log("SDP successfully created \n" + sessionDescription.description);
            QBRTCUtils.abortUnless(QBPeerConnection.this.localSdp == null, "multiple SDP create?!?");
            final String sdpDescription = RTCMediaUtils.generateLocalDescription(sessionDescription, new QBRTCMediaConfig(), QBPeerConnection.this.conferenceType);
            final SessionDescription sdp = new SessionDescription(sessionDescription.type, sdpDescription);
            QBPeerConnection.this.localSdp = sdp;
            QBPeerConnection.this.executor.execute(() -> {
                if (!QBPeerConnection.this.isPeerConnectionValid()) {
                    return;
                }
                QBPeerConnection.this.peerConnection.setLocalDescription(QBPeerConnection.this.sdpObserver, sdp);
            });
        }

        public void onSetSuccess() {
            this.log("onSetSuccess");
            QBPeerConnection.this.executor.execute(() -> {
                if (!QBPeerConnection.this.isPeerConnectionValid()) {
                    return;
                }
                if (QBPeerConnection.this.localSdp != null && QBPeerConnection.this.localSdp.type.equals((Object) SessionDescription.Type.OFFER)) {
                    if (QBPeerConnection.this.peerConnection.getRemoteDescription() == null) {
                        SDPObserver.this.log("Local SDP set successfully");
                        QBPeerConnection.this.beginDialing(QBPeerConnection.this.useDialingTimer);
                    } else {
                        SDPObserver.this.log("Remote SDP set successfully");
                        SDPObserver.this.drainIceCandidates();
                    }
                } else if (QBPeerConnection.this.peerConnection.getLocalDescription() != null) {
                    SDPObserver.this.log("Local SDP set successfully");
                    QBPeerConnection.this.pcEventsCallback.onSessionDescriptionSend(QBPeerConnection.this, QBPeerConnection.this.localSdp);
                    SDPObserver.this.drainIceCandidates();
                } else {
                    SDPObserver.this.log("Remote SDP set successfully");
                    QBPeerConnection.this.createAnswer();
                }
            });
        }

        public void onCreateFailure(final String s) {
            this.log("onCreateFailure: " + s);
        }

        public void onSetFailure(final String s) {
            this.log("onSetFailure: " + s);
        }

        private void log(final String onCreateSuccess) {
            Timber.d( onCreateSuccess);
        }

        private void drainIceCandidates() {
            if (QBPeerConnection.this.iceCandidates != null) {
                this.log("Add " + QBPeerConnection.this.iceCandidates.size() + " remote candidates");
                if (QBPeerConnection.this.iceCandidates.size() > 0) {
                    QBPeerConnection.this.pcEventsCallback.onIceCandidatesSend(QBPeerConnection.this, QBPeerConnection.this.iceCandidates);
                }
                QBPeerConnection.this.iceCandidates = null;
            }
        }
    }

    private class PCObserver implements PeerConnection.Observer {
        private PCObserver() {
        }

        public void onSignalingChange(final PeerConnection.SignalingState signalingState) {
            this.log("onSignalingChange to " + signalingState.toString());
        }

        public void onIceConnectionChange(final PeerConnection.IceConnectionState iceConnectionState) {
            this.log("onIceConnectionChange to " + iceConnectionState.toString());
            QBPeerConnection.this.executor.execute(() -> {
                if (!QBPeerConnection.this.isPeerConnectionValid()) {
                    return;
                }
                if (iceConnectionState == PeerConnection.IceConnectionState.CHECKING) {
                    QBPeerConnection.this.setState(QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_CHECKING);
                    if (QBPeerConnection.this.pcEventsCallback instanceof QBPeerChannelCallback) {
                        ((QBPeerChannelCallback) QBPeerConnection.this.pcEventsCallback).onChannelConnectionConnecting(QBPeerConnection.this);
                    }
                } else if (iceConnectionState == PeerConnection.IceConnectionState.CONNECTED) {
                    QBPeerConnection.this.peerChannelTimers.stopDisconnectTimer();
                    QBPeerConnection.this.setState(QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_CONNECTED);
                    QBPeerConnection.this.pcEventsCallback.onChannelConnectionConnected(QBPeerConnection.this);
                } else if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED) {
                    QBPeerConnection.this.setState(QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_DISCONNECTED);
                    QBPeerConnection.this.pcEventsCallback.onChannelConnectionDisconnected(QBPeerConnection.this);
                } else if (iceConnectionState == PeerConnection.IceConnectionState.FAILED) {
                    if (QBPeerConnection.this.pcEventsCallback instanceof QBPeerChannelCallback) {
                        ((QBPeerChannelCallback) QBPeerConnection.this.pcEventsCallback).onChannelConnectionFailed(QBPeerConnection.this);
                    }
                    PCObserver.this.loge("ICE connection failed.");
                    QBPeerConnection.this.close(QBRTCTypes.QBRTCCloseReason.QB_RTC_FAILED);
                } else if (iceConnectionState == PeerConnection.IceConnectionState.CLOSED) {
                    PCObserver.this.log("onChannelConnectionClosed called on " + QBPeerConnection.this.pcEventsCallback);
                }
            });
        }

        public void onIceConnectionReceivingChange(final boolean b) {
            this.log("onIceConnectionReceivingChange to " + b);
        }

        public void onIceGatheringChange(final PeerConnection.IceGatheringState iceGatheringState) {
            this.log("onIceGatheringChange to " + iceGatheringState.toString());
            QBPeerConnection.this.pcEventsCallback.onIceGatheringChange(iceGatheringState, QBPeerConnection.this.getUserID());
        }

        public void onIceCandidate(final IceCandidate candidate) {
            this.log("onIceCandidate: " + candidate.sdpMLineIndex + " " + candidate.sdpMid);
            QBPeerConnection.this.executor.execute(() -> {
                if (!QBPeerConnection.this.isPeerConnectionValid()) {
                    return;
                }
                if (QBPeerConnection.this.iceCandidates != null) {
                    QBPeerConnection.this.iceCandidates.add(candidate);
                } else {
                    final List<IceCandidate> iceCandidate = new ArrayList<>();
                    iceCandidate.add(candidate);
                    QBPeerConnection.this.pcEventsCallback.onIceCandidatesSend(QBPeerConnection.this, iceCandidate);
                }
            });
        }

        public void onIceCandidatesRemoved(final IceCandidate[] iceCandidates) {
            this.log("onIceCandidatesRemoved: ");
        }

        public void onAddStream(final MediaStream stream) {
            this.log("onAddStream");
            QBPeerConnection.this.remoteStream = stream;
            QBPeerConnection.this.executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (!QBPeerConnection.this.isPeerConnectionValid()) {
                        return;
                    }
                    QBRTCUtils.abortUnless(QBPeerConnection.this.remoteStream.audioTracks.size() <= 1 && QBPeerConnection.this.remoteStream.videoTracks.size() <= 1, "Weird-looking stream: " + QBPeerConnection.this.remoteStream);
                    if (QBPeerConnection.this.remoteStream.videoTracks.size() == 1) {
                        PCObserver.this.log("set remote stream TO remote renderer ");
                    }
                }
            });
        }

        public void onRemoveStream(final MediaStream stream) {
            this.log("onRemoveStream");
            QBPeerConnection.this.executor.execute(new Runnable() {
                @Override
                public void run() {
                    QBPeerConnection.this.remoteStream = null;
                }
            });
        }

        public void onDataChannel(final DataChannel dataChannel) {
            this.log("onDataChannel");
        }

        public void onRenegotiationNeeded() {
            this.log("onRenegotiationNeeded");
        }

        public void onAddTrack(final RtpReceiver rtpReceiver, final MediaStream[] mediaStreams) {
            this.log("onAddTrack, mediaStreams: " + Arrays.toString(mediaStreams));
        }

        private void log(final String onSignalingChange) {
            Timber.d(onSignalingChange);
        }

        private void loge(final String onSignalingChange) {
            Timber.e(onSignalingChange);
        }
    }

    class PeerChannelLifeCycleTimers {
        private ScheduledExecutorService scheduledTasksService;
        private Runnable waitOfferTask;
        private Runnable dialingTask;
        private ScheduledFuture<?> futureWaitTask;
        private ScheduledFuture<?> futureDisconnectTask;
        private ScheduledFuture<?> futureDialingTask;

        PeerChannelLifeCycleTimers() {
        }

        public void initSchedledTasks() {
            this.scheduledTasksService = Executors.newScheduledThreadPool(3);
            this.waitOfferTask = QBPeerConnection.this::noOffer;
            this.dialingTask = QBPeerConnection.this::dialing;
        }

        private void startWaitTask() {
            if (!this.scheduledTasksService.isShutdown()) {
                this.futureWaitTask = this.scheduledTasksService.schedule(this.waitOfferTask, QBRTCConfig.getAnswerTimeInterval(), TimeUnit.SECONDS);
            }
        }

        private void startDialingTimer() {
            if (!this.scheduledTasksService.isShutdown()) {
                final long interval = Math.max(QBRTCConfig.getDialingTimeInterval(), 3L);
                this.futureDialingTask = this.scheduledTasksService.scheduleAtFixedRate(this.dialingTask, interval, interval, TimeUnit.SECONDS);
            }
        }

        private void stopAllTasks() {
            this.stopDialingTimer();
            this.stopWaitTimer();
            this.stopDisconnectTimer();
            this.scheduledTasksService.shutdownNow();
        }

        private void stopDialingTimer() {
            Timber.d("Stop DialingTimer");
            if (this.futureDialingTask != null) {
                this.futureDialingTask.cancel(true);
                this.futureDialingTask = null;
            }
        }

        private void stopDisconnectTimer() {
            Timber.d("Stop DisconnectTimer");
            if (this.futureDisconnectTask != null) {
                this.futureDisconnectTask.cancel(true);
                this.futureDisconnectTask = null;
            }
        }

        private void stopWaitTimer() {
            Timber.d("Stop WaitTimer");
            if (this.futureWaitTask != null) {
                this.futureWaitTask.cancel(true);
                this.futureWaitTask = null;
            }
        }
    }
}
