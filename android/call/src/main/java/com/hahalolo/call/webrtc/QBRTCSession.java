package com.hahalolo.call.webrtc;

import com.hahalolo.call.webrtc.callbacks.QBPeerChannelCallback;
import com.hahalolo.call.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.hahalolo.call.webrtc.callbacks.QBRTCSessionConnectionCallbacks;
import com.hahalolo.call.webrtc.callbacks.QBRTCSessionEventsCallback;
import com.hahalolo.call.webrtc.callbacks.QBRTCSessionStateCallback;

import org.jetbrains.annotations.NotNull;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.util.List;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;

/**
 * Stores main information about webrtc conversation. Handles main signaling events.
 * Allows to listen for main events using {@link QBRTCSessionConnectionCallbacks} to listen for signaling events
 * Use {@link QBRTCMediaConfig} to set audio and video constraints before creating {@link QBRTCSession}.
 */
public class QBRTCSession extends BaseSession<QBRTCClient, QBPeerConnection> implements QBPeerChannelCallback {

    protected QBRTCSessionDescription sessionDescription;
    protected SessionWaitingTimers sessionWaitingTimers;
    private Set<QBRTCSessionEventsCallback> eventsCallbacks;

    QBRTCSession(final QBRTCClient client,
                 final QBRTCSessionDescription sessionDescription,
                 final Set<QBRTCSessionEventsCallback> chatCallbackList,
                 final CameraVideoCapturer.CameraEventsHandler cameraErrorHandler) {
        super(client, sessionDescription, cameraErrorHandler);
        this.sessionDescription = sessionDescription;
        this.eventsCallbacks = chatCallbackList;
        this.startWaitingAcceptOrRejectTimer();
        this.initSignallingWithOpponents(sessionDescription.getOpponents());
        this.makeChannelsWithOpponents(sessionDescription.getOpponents());
    }

    protected void makeChannelsWithOpponents(final List<Integer> opponents) {
        if (opponents != null) {
            for (final Integer opponentID : opponents) {
                if (!opponentID.equals(0/*QBRTCUtils.getCurrentChatUser()*/)) {
                    this.makeAndAddNewChannelForOpponent(opponentID);
                } else {
                    this.makeAndAddNewChannelForOpponent(this.getCallerID());
                }
            }
        }
    }

    private void makeAndAddNewChannelForOpponent(final Integer opponentID) {
        if (!this.channels.containsKey(opponentID)) {
            final QBPeerConnection newChannel = new QBPeerConnection(this.factoryManager,
                    this, opponentID, this.getConferenceType(), true, true);
            this.channels.put(opponentID, newChannel);
            this.activeChannels.add(newChannel);
            Timber.i("Make new channel for opponents:" + opponentID + newChannel.toString());
        } else {
            Timber.d("Channel with this opponent " + opponentID + " already exists");
        }
    }

    @Override
    protected void closeInternal() {
        this.client.postOnMainThread(() -> {
            for (final QBRTCSessionEventsCallback callback : QBRTCSession.this.eventsCallbacks) {
                if (callback instanceof QBRTCClientSessionCallbacks) {
                    ((QBRTCClientSessionCallbacks) callback).onSessionStartClose(QBRTCSession.this);
                }
            }
        });
        this.connectionCallbacksList.clear();
        this.closeMediaStreamManager();
        this.sessionWaitingTimers.shutDown();
        this.client.postOnMainThread(() -> {
            Timber.d("Notify sessions callbacks in count of: %s", QBRTCSession.this.eventsCallbacks.size());
            for (final QBRTCSessionEventsCallback callback : QBRTCSession.this.eventsCallbacks) {
                callback.onSessionClosed(QBRTCSession.this);
            }
        });
    }

    @Override
    public boolean isActive() {
        return super.isActive() || QBRTCSessionState.QB_RTC_SESSION_CONNECTING.equals(this.getState());
    }

    protected void startWaitingAcceptOrRejectTimer() {
        final boolean isInitiator = this.getCallerID().equals(0/*QBRTCUtils.getCurrentChatUser()*/);
        Timber.d("isInitiator= %s", isInitiator);
        this.sessionWaitingTimers = new SessionWaitingTimers();
        if (!isInitiator) {
            this.sessionWaitingTimers.startWaitForUserActionsTask();
        }
    }

    @Override
    public String getSessionID() {
        return this.sessionDescription.getSessionId();
    }

    public Integer getCallerID() {
        return this.sessionDescription.getCallerID();
    }

    public List<Integer> getOpponents() {
        return this.sessionDescription.getOpponents();
    }

    @Override
    public QBRTCTypes.QBConferenceType getConferenceType() {
        return this.sessionDescription.getConferenceType();
    }

    public Map<String, String> getUserInfo() {
        return this.sessionDescription.getUserInfo();
    }

    public QBRTCSessionDescription getSessionDescription() {
        return this.sessionDescription;
    }

    @NotNull
    @Override
    public String toString() {
        return "QBRTCSession{sessionDescription=" + this.sessionDescription
                + ", MediaStreamManager=" + ((this.mediaStreamManager != null) ? this.mediaStreamManager : "null")
                + ", channels=" + this.channels
                + ", eventsCallbacks=" + this.eventsCallbacks
                + ", state=" + this.state + '}';
    }

    @Override
    public boolean equals(final Object session) {
        return session instanceof QBRTCSession
                && (this == session || this.getSessionID().equals(((QBRTCSession) session).getSessionID()));
    }

    @Override
    public int hashCode() {
        int result = this.sessionDescription.hashCode();
        result = 31 * result + this.mediaStreamManager.hashCode();
        result = 31 * result + this.channels.hashCode();
        result = 31 * result + this.eventsCallbacks.hashCode();
        result = 31 * result + this.state.hashCode();
        return result;
    }

    /**
     * Start call with user info
     *
     * @param userInfo The user information map for the accept call. May be null.
     */
    public void startCall(final Map<String, String> userInfo) {
        Timber.d("Start Call");
        this.executor.execute(() -> {
            if (QBRTCSession.this.isDestroyed()) {
                Timber.d("session destroyed");
            } else {
                QBRTCSession.this.sessionDescription.setUserInfo(userInfo);
                QBRTCSession.this.setState(QBRTCSessionState.QB_RTC_SESSION_CONNECTING);
                QBRTCSession.this.stopWaitingUserTimer();

                for (Integer opponentID : QBRTCSession.this.getOpponents()) {
                    QBPeerConnection channel = QBRTCSession.this.channels.get(opponentID);
                    if (channel == null) {
                        return;
                    }

                    if (channel.getState() != QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_NEW) {
                        return;
                    }

                    channel.startAsOffer();
                }

                if (QBRTCConfig.getStatsReportTimeInterval() > 0L) {
                    QBRTCSession.this.startFetchStatsReport();
                }

            }
        });
    }

    /**
     * Accept call with userInfo.
     *
     * @param userInfo The user information map for the accept call. May be null.
     */
    public void acceptCall(final Map<String, String> userInfo) {
        Timber.d("Accept Call");
        this.executor.execute(() -> {
            if (QBRTCSession.this.isDestroyed() || QBRTCSession.this.getState().ordinal() == QBRTCSessionState.QB_RTC_SESSION_CONNECTED.ordinal()) {
                return;
            }
            QBRTCSession.this.stopWaitingUserTimer();
            QBRTCSession.this.sessionDescription.setUserInfo(userInfo);
            QBRTCSession.this.setState(QBRTCSessionState.QB_RTC_SESSION_CONNECTING);
            for (final Map.Entry<Integer, QBPeerConnection> entry : QBRTCSession.this.channels.entrySet()) {
                final QBPeerConnection channel = entry.getValue();
                final Integer opponentID = entry.getKey();
                if (channel != null && channel.getState() == QBRTCTypes.QBRTCConnectionState.QB_RTC_CONNECTION_NEW) {
                    if (opponentID.equals(QBRTCSession.this.getCallerID())) {
                        channel.startAsAnswer();
                    } else if (opponentID < 0/*QBRTCUtils.getCurrentChatUser()*/) {
                        channel.startAsOffer();
                    } else if (channel.getRemoteSDP() != null) {
                        channel.startAsAnswer();
                    } else {
                        channel.startWaitOffer();
                    }
                }
            }
            if (QBRTCConfig.getStatsReportTimeInterval() > 0L) {
                QBRTCSession.this.startFetchStatsReport();
            }
        });
    }

    /**
     * Reject call.
     *
     * @param userInfo The user information map for the accept call. May be null.
     */
    public void rejectCall(final Map<String, String> userInfo) {
        Timber.d("Reject Call");
        if (this.isDestroyed()) {
            return;
        }
        this.setState(QBRTCSessionState.QB_RTC_SESSION_GOING_TO_CLOSE);
        this.executor.execute(() -> {
            QBRTCSession.this.stopWaitingUserTimer();
            Timber.d("Set session state rejected");
            QBRTCSession.this.closeAllChannels(userInfo, QBRTCTypes.QBRTCCloseReason.QB_RTC_REJECTED);
        });
    }

    private void closeAllChannels(final Map<String, String> userInfo, final QBRTCTypes.QBRTCCloseReason closeReason) {
        for (final Map.Entry<Integer, QBPeerConnection> entry : this.channels.entrySet()) {
            if (QBRTCTypes.QBRTCCloseReason.QB_RTC_REJECTED.equals(closeReason)) {
                // TODO: Send Rejected Event to Server
                /*final QBChatMessage rejectCallMessage = this.signalChannel.sendRejectCallToOpponent(entry.getKey(), userInfo);
                this.sendMessage(QBSignalingSpec.QBSignalCMD.REJECT_CALL, rejectCallMessage, entry.getKey());*/
            } else {
                // TODO: Send Hang Up Event to Server
                /*final QBChatMessage hangUpMessage = this.signalChannel.sendHandUpCallWithStatus(entry.getKey(), userInfo);
                this.sendMessage(QBSignalingSpec.QBSignalCMD.HANG_UP, hangUpMessage, entry.getKey());*/
            }
            entry.getValue().close(closeReason);
        }
    }

    @Override
    protected void hangUpChannels(final Map<String, String> userInfo) {
        super.hangUpChannels(userInfo);
        this.stopWaitingUserTimer();
    }

    /**
     * Finish call with all users
     *
     * @param userInfo userInfo The user information map for the accept call. May be null.
     */
    public void hangUp(final Map<String, String> userInfo) {
        Timber.d("Hang Up");
        if (this.isDestroyed()) {
            return;
        }
        this.setState(QBRTCSessionState.QB_RTC_SESSION_GOING_TO_CLOSE);
        this.hangUpChannels(userInfo);
    }

    @Override
    protected void callHangUpOnChannels(final Map<String, String> userInfo) {
        Timber.d("Call Hang Up On Channels");
        this.closeAllChannels(userInfo, QBRTCTypes.QBRTCCloseReason.QB_RTC_HANG_UP);
    }

    private void sendHangUpOnChannel(final int opponentID, final Map<String, String> userInfo) {
        /*final QBChatMessage hangUpMessage = this.signalChannel.sendHandUpCallWithStatus(opponentID, userInfo);
        this.sendMessage(QBSignalingSpec.QBSignalCMD.HANG_UP, hangUpMessage, opponentID);*/
        // TODO: Send hang up channel to Server
    }

    /**
     * Init signalling channel between two opponents
     *
     * @param opponentIDs ID of opponents
     */
    protected void initSignallingWithOpponents(final List<Integer> opponentIDs) {
        // TODO: Use socket Messenger
    }

    private void stopWaitingUserTimer() {
        this.sessionWaitingTimers.stopWaitForUserActionsTimer();
    }

    private void startWaitingUserTimer() {
        this.sessionWaitingTimers.stopWaitForUserActionsTimer();
    }

    @Override
    public void onSessionDescriptionSend(final QBPeerConnection channel, final SessionDescription sdp) {
        Timber.d("onSessionDescriptionSend for channel opponent %s", channel.getUserID());
        if (sdp != null) {
            this.executor.execute(new Runnable() {
                @Override
                public void run() {
                    // TODO: Send Session Description

                    /*if (sdp.type.equals((Object) SessionDescription.Type.ANSWER)) {
                        final QBChatMessage answerMessage = QBRTCSession.this.signalChannel.sendAcceptCallMessage(sdp, channel.getUserID(), QBRTCSession.this.getUserInfo());
                        QBRTCSession.this.sendMessage(QBSignalingSpec.QBSignalCMD.ACCEPT_CALL, answerMessage, channel.getUserID());
                    } else {
                        final QBChatMessage offerMessage = QBRTCSession.this.signalChannel.sendCallRequestMessage(sdp, channel.getUserID(), QBRTCSession.this.getUserInfo());
                        QBRTCSession.this.sendMessage(QBSignalingSpec.QBSignalCMD.CALL, offerMessage, channel.getUserID());
                    }*/
                }
            });
        }
    }

    @Override
    public void onHangUpSend(final QBPeerConnection channel) {
        this.executor.execute(() -> QBRTCSession.this.sendHangUpOnChannel(channel.getUserID(), QBRTCSession.this.getUserInfo()));
    }

    @Override
    public void onIceCandidatesSend(final QBPeerConnection channel, final List<IceCandidate> candidates) {
        Timber.d("onIceCandidatesSend for channel opponent %s", channel.getUserID());
        if (this.isActive()) {
            if (candidates.size() > 0) {
                this.executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        /*final QBChatMessage iceCandidatesMessage = QBRTCSession.this.signalChannel.sendIceCandidates(channel.getUserID(), candidates, QBRTCSession.this.getUserInfo());
                        QBRTCSession.this.sendMessage(QBSignalingSpec.QBSignalCMD.CANDITATES, iceCandidatesMessage, channel.getUserID());*/
                        Timber.d("Candidates in count of " + candidates.size() + " was sent");
                    }
                });
            }
        } else {
            Timber.d("Store candidates");
        }
    }

    @Override
    public void onIceGatheringChange(final PeerConnection.IceGatheringState iceGatheringState, final int userId) {
    }

    @Override
    public void onChannelNotAnswer(final QBPeerConnection channel) {
        Timber.d("onChannelNotAnswer for opponent %s", channel.getUserID());
        this.client.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                for (final QBRTCSessionEventsCallback callback : QBRTCSession.this.eventsCallbacks) {
                    callback.onUserNotAnswer(QBRTCSession.this, channel.getUserID());
                }
            }
        });
    }

    @Override
    public void onChannelConnectionFailed(final QBPeerConnection channel) {
        Timber.d("onChannelConnectionFailed for opponent %s", channel.getUserID());
        this.client.postOnMainThread(() -> {
            for (final QBRTCSessionStateCallback callback : QBRTCSession.this.connectionCallbacksList) {
                if (callback instanceof QBRTCSessionConnectionCallbacks) {
                    ((QBRTCSessionConnectionCallbacks) callback).onConnectionFailedWithUser(QBRTCSession.this, channel.getUserID());
                }
            }
        });
    }

    @Override
    public void onChannelConnectionConnecting(final QBPeerConnection channel) {
        Timber.d("onChannelConnectionConnecting for opponent %s", channel.getUserID());
        this.client.postOnMainThread(() -> {
            for (final QBRTCSessionStateCallback callback : QBRTCSession.this.connectionCallbacksList) {
                if (callback instanceof QBRTCSessionConnectionCallbacks) {
                    ((QBRTCSessionConnectionCallbacks) callback).onStartConnectToUser(QBRTCSession.this, channel.getUserID());
                }
            }
        });
    }

    @Override
    public QBRTCTypes.QBConferenceType conferenceTypeForChannel(final QBPeerConnection channel) {
        return this.getConferenceType();
    }

    @Override
    protected void noUserAction() {
        Timber.d("no User Actions");
        this.stopWaitingUserTimer();
        this.client.postOnMainThread(() -> {
            for (final QBRTCSessionEventsCallback sessionCallback : QBRTCSession.this.eventsCallbacks) {
                if (sessionCallback instanceof QBRTCClientSessionCallbacks) {
                    ((QBRTCClientSessionCallbacks) sessionCallback).onUserNoActions(QBRTCSession.this, 0/*QBRTCUtils.getCurrentChatUser()*/);
                }
            }
        });
    }
}
