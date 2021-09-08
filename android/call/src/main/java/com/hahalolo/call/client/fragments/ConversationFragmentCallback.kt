package com.hahalolo.call.client.fragments

import com.hahalolo.call.client.activities.CallActivity
import com.hahalolo.call.webrtc.BaseSession
import com.hahalolo.call.webrtc.QBRTCTypes
import com.hahalolo.call.webrtc.callbacks.QBRTCSessionEventsCallback
import com.hahalolo.call.webrtc.callbacks.QBRTCSessionStateCallback
import org.webrtc.CameraVideoCapturer


interface ConversationFragmentCallback {

    fun addSessionStateListener(clientConnectionCallbacks: QBRTCSessionStateCallback<*>?)

    fun removeSessionStateListener(clientConnectionCallbacks: QBRTCSessionStateCallback<*>?)

    fun addSessionEventsListener(eventsCallback: QBRTCSessionEventsCallback?)

    fun removeSessionEventsListener(eventsCallback: QBRTCSessionEventsCallback?)

    fun addCurrentCallStateListener(currentCallStateCallback: CallActivity.CurrentCallStateCallback?)

    fun removeCurrentCallStateListener(currentCallStateCallback: CallActivity.CurrentCallStateCallback?)

    fun addOnChangeAudioDeviceListener(onChangeDynamicCallback: CallActivity.OnChangeAudioDevice?)

    fun removeOnChangeAudioDeviceListener(onChangeDynamicCallback: CallActivity.OnChangeAudioDevice?)

    fun onSetAudioEnabled(isAudioEnabled: Boolean)

    fun onSetVideoEnabled(isNeedEnableCam: Boolean)

    fun onSwitchAudio()

    fun onHangUpCurrentSession()

    fun onStartScreenSharing()

    fun onSwitchCamera(cameraSwitchHandler: CameraVideoCapturer.CameraSwitchHandler)

    fun acceptCall(userInfo: Map<String, String>)

    fun startCall(userInfo: Map<String, String>)

    fun currentSessionExist(): Boolean

    fun getOpponents(): List<Int>?

    fun getCallerId(): Int?

    fun getCurrentSessionState(): BaseSession.QBRTCSessionState?

    fun getPeerChannel(userId: Int): QBRTCTypes.QBRTCConnectionState?

    fun isMediaStreamManagerExist(): Boolean

    fun isCallState(): Boolean
}