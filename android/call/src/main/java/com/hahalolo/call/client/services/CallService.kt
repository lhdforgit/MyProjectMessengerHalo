package com.hahalolo.call.client.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.text.TextUtils
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.hahalolo.call.R
import com.hahalolo.call.client.CallController
import com.hahalolo.call.client.activities.CallActivity
import com.hahalolo.call.client.fragments.CAMERA_ENABLED
import com.hahalolo.call.client.fragments.IS_CURRENT_CAMERA_FRONT
import com.hahalolo.call.client.fragments.MIC_ENABLED
import com.hahalolo.call.client.fragments.SPEAKER_ENABLED
import com.hahalolo.call.client.utils.*
import com.hahalolo.call.webrtc.*
import com.hahalolo.call.webrtc.callbacks.*
import dagger.android.AndroidInjection
import org.webrtc.CameraVideoCapturer
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap


const val SERVICE_ID = 787
const val CHANNEL_ID = "Quickblox channel"
const val CHANNEL_NAME = "Quickblox background service"

class CallService : LifecycleService() {

    @Inject
    lateinit var controller: CallController

    private val callServiceBinder: IBinder = CallServiceBinder()

    private lateinit var networkConnectionListener: NetworkConnectionListener
    private lateinit var networkConnectionChecker: NetworkConnectionChecker
    private var sessionEventsListener: SessionEventsListener? = null
    private var sessionStateListener: SessionStateListener? = null
    private var appRTCAudioManager: AppRTCAudioManager? = null
    private var callTimerListener: CallTimerListener? = null
    private var ringtonePlayer: RingtonePlayer? = null
    private var currentSession: QBRTCSession? = null
    private var sharingScreenState: Boolean = false
    private var isCallState: Boolean = false
    private var rtcClient: QBRTCClient? = null

    private val callTimerTask: CallTimerTask = CallTimerTask()
    private var callTime: Long? = null
    private val callTimer = Timer()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, CallService::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, CallService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        Timber.i("On Create Call Service")

        AndroidInjection.inject(this)
        currentSession = controller.currentSession
        clearButtonsState()
        initNetworkChecker()
        initRTCClient()
        initListeners()
        initAudioManager()
        ringtonePlayer = RingtonePlayer(this, R.raw.beep)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = initNotification()
        startForeground(SERVICE_ID, notification)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("On Destroy Call Service")

        networkConnectionChecker?.unregisterListener(networkConnectionListener!!)

        releaseCurrentSession()
        releaseAudioManager()

        stopCallTimer()
        clearButtonsState()
        clearCallState()
        stopForeground(true)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    /**
     * {@see https://developer.android.com/reference/android/app/Service#LocalServiceSample}
     */
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return callServiceBinder
    }

    private fun initNotification(): Notification {
        val notifyIntent = Intent(this, CallActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val notifyPendingIntent = PendingIntent.getActivity(
            this, 0, notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationTitle = getString(R.string.notification_title)
        var notificationText = getString(R.string.notification_text, "")

        val callTime = getCallTime()
        if (!TextUtils.isEmpty(callTime)) {
            notificationText = getString(R.string.notification_text, callTime)
        }

        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle(notificationTitle)
        bigTextStyle.bigText(notificationText)

        val channelId: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(CHANNEL_ID, CHANNEL_NAME)
        } else {
            getString(R.string.app_name)
        }

        val builder = NotificationCompat.Builder(this, channelId)
        builder.setStyle(bigTextStyle)
        builder.setContentTitle(notificationTitle)
        builder.setContentText(notificationText)
        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.drawable.ic_call)
        val bitmapIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_call)
        builder.setLargeIcon(bitmapIcon)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.priority = NotificationManager.IMPORTANCE_LOW
        } else {
            builder.priority = Notification.PRIORITY_LOW
        }
        builder.apply {
            setContentIntent(notifyPendingIntent)
        }
        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        channel.lightColor = getColor(R.color.green)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(channel)
        return channelId
    }

    private fun getCallTime(): String {
        var time = ""
        callTime?.let {
            val format = String.format("%%0%dd", 2)
            val elapsedTime = it / 1000
            val seconds = String.format(format, elapsedTime % 60)
            val minutes = String.format(format, elapsedTime % 3600 / 60)
            val hours = String.format(format, elapsedTime / 3600)
            time = "$minutes:$seconds"
            if (!TextUtils.isEmpty(hours) && hours != "00") {
                time = "$hours:$minutes:$seconds"
            }
        }
        return time
    }

    fun playRingtone() {
        ringtonePlayer?.play(true)
    }

    fun stopRingtone() {
        ringtonePlayer?.stop()
    }

    private fun initNetworkChecker() {
        networkConnectionChecker = NetworkConnectionChecker(application)
        networkConnectionListener = NetworkConnectionListener()
        networkConnectionChecker.registerListener(networkConnectionListener)
    }

    private fun initRTCClient() {
        rtcClient = QBRTCClient.getInstance(this)
        rtcClient?.setCameraErrorHandler(CameraEventsListener())

        QBRTCConfig.setMaxOpponentsCount(MAX_OPPONENTS_COUNT)
        QBRTCConfig.setDebugEnabled(true)

        configRTCTimers(this)
    }

    private fun initListeners() {
        sessionStateListener = SessionStateListener()
        addSessionStateListener(sessionStateListener)

        sessionEventsListener = SessionEventsListener()
        addSessionEventsListener(sessionEventsListener)
    }

    private fun initAudioManager() {
        appRTCAudioManager = AppRTCAudioManager.create(this)

        appRTCAudioManager?.setOnWiredHeadsetStateListener { plugged, hasMicrophone ->
            Timber.i(
                "Headset %s - Has Microphone: $hasMicrophone",
                if (plugged) "plugged" else "unplugged"
            )
        }

        appRTCAudioManager?.setBluetoothAudioDeviceStateListener { connected ->
            Timber.i("Bluetooth %s", if (connected) "connected" else "disconnected")
        }

        appRTCAudioManager?.start { selectedAudioDevice, availableAudioDevices ->
            Timber.i("Audio device switched to  $selectedAudioDevice - Available Audio Devices: $availableAudioDevices")
        }

        if (currentSessionExist() && currentSession!!.conferenceType == QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO) {
            appRTCAudioManager?.selectAudioDevice(AppRTCAudioManager.AudioDevice.EARPIECE)
        }
    }

    fun releaseAudioManager() {
        appRTCAudioManager?.stop()
    }

    fun currentSessionExist(): Boolean {
        return currentSession != null
    }

    private fun releaseCurrentSession() {
        Timber.d("Release current session")
        removeSessionStateListener(sessionStateListener)
        removeSessionEventsListener(sessionEventsListener)
        currentSession = null
    }

    //Listeners

    fun addSessionStateListener(callback: QBRTCSessionStateCallback<*>?) {
        currentSession?.addSessionCallbacksListener(callback)
    }

    fun removeSessionStateListener(callback: QBRTCSessionStateCallback<*>?) {
        currentSession?.removeSessionCallbacksListener(callback)
    }

    fun addSessionEventsListener(callback: QBRTCSessionEventsCallback?) {
        rtcClient?.addSessionCallbacksListener(callback)
    }

    fun removeSessionEventsListener(callback: QBRTCSessionEventsCallback?) {
        rtcClient?.removeSessionsCallbacksListener(callback)
    }

    //Common methods
    fun acceptCall(userInfo: Map<String, String>) {
        currentSession?.acceptCall(userInfo)
    }

    fun startCall(userInfo: Map<String, String>) {
        currentSession?.startCall(userInfo)
    }

    fun rejectCurrentSession(userInfo: Map<String, String>) {
        currentSession?.rejectCall(userInfo)
    }

    fun hangUpCurrentSession(userInfo: Map<String, String>): Boolean {
        stopRingtone()
        var result = false
        currentSession?.let {
            it.hangUp(userInfo)
            result = true
        }
        return result
    }

    fun startScreenSharing(data: Intent) {
        sharingScreenState = true
        currentSession?.mediaStreamManager?.videoCapturer = QBRTCScreenCapturer(data, null)
    }

    fun stopScreenSharing() {
        sharingScreenState = false
        try {
            currentSession?.mediaStreamManager?.videoCapturer = QBRTCCameraVideoCapturer(this, null)
        } catch (e: QBRTCCameraVideoCapturer.QBRTCCameraCapturerException) {
            Timber.i("Error: device doesn't have camera")
        }
    }

    fun getCallerId(): Int? {
        return currentSession?.callerID
    }

    fun getOpponents(): List<Int>? {
        return currentSession?.opponents
    }

    fun isVideoCall(): Boolean {
        return QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO == currentSession?.conferenceType
    }

    fun getCurrentSessionState(): BaseSession.QBRTCSessionState? {
        return currentSession?.state
    }

    fun isMediaStreamManagerExist(): Boolean {
        return currentSession?.mediaStreamManager != null
    }

    fun getPeerChannel(userId: Int): QBRTCTypes.QBRTCConnectionState? {
        return currentSession?.getPeerConnection(userId)?.state
    }

    fun isCurrentSession(session: QBRTCSession?): Boolean {
        var isCurrentSession = false
        session?.let {
            isCurrentSession = currentSession?.sessionID == it.sessionID
        }
        return isCurrentSession
    }

    fun switchCamera(cameraSwitchHandler: CameraVideoCapturer.CameraSwitchHandler) {
        val videoCapturer =
            currentSession?.mediaStreamManager?.videoCapturer as QBRTCCameraVideoCapturer
        videoCapturer.switchCamera(cameraSwitchHandler)
    }

    fun switchAudio() {
        Timber.v(
            "onSwitchAudio(), SelectedAudioDevice() = %s",
            appRTCAudioManager?.selectedAudioDevice
        )
        if (appRTCAudioManager?.selectedAudioDevice != AppRTCAudioManager.AudioDevice.SPEAKER_PHONE) {
            appRTCAudioManager?.selectAudioDevice(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE)
        } else {
            when {
                appRTCAudioManager?.audioDevices?.contains(AppRTCAudioManager.AudioDevice.BLUETOOTH) == true -> {
                    appRTCAudioManager?.selectAudioDevice(AppRTCAudioManager.AudioDevice.BLUETOOTH)
                }
                appRTCAudioManager?.audioDevices?.contains(AppRTCAudioManager.AudioDevice.WIRED_HEADSET) == true -> {
                    appRTCAudioManager?.selectAudioDevice(AppRTCAudioManager.AudioDevice.WIRED_HEADSET)
                }
                else -> {
                    appRTCAudioManager?.selectAudioDevice(AppRTCAudioManager.AudioDevice.EARPIECE)
                }
            }
        }
    }

    fun isSharingScreenState(): Boolean {
        return sharingScreenState
    }

    fun isCallMode(): Boolean {
        return isCallState
    }

    fun setCallTimerCallback(callback: CallTimerListener) {
        callTimerListener = callback
    }

    fun removeCallTimerCallback() {
        callTimerListener = null
    }

    private fun startCallTimer() {
        if (callTime == null) {
            callTime = 1000
        }
        if (!callTimerTask.isRunning) {
            callTimer.scheduleAtFixedRate(callTimerTask, 0, 1000)
        }
    }

    private fun stopCallTimer() {
        callTimerListener = null
        callTimer.cancel()
        callTimer.purge()
    }

    fun clearButtonsState() {
        controller.apply {
            SharedPrefsHelper.delete(MIC_ENABLED, sharedPreferences)
            SharedPrefsHelper.delete(SPEAKER_ENABLED, sharedPreferences)
            SharedPrefsHelper.delete(CAMERA_ENABLED, sharedPreferences)
            SharedPrefsHelper.delete(IS_CURRENT_CAMERA_FRONT, sharedPreferences)
        }
    }

    fun clearCallState() {
        controller.apply {
            SharedPrefsHelper.delete(EXTRA_IS_INCOMING_CALL, sharedPreferences)
        }
    }

    private inner class CallTimerTask : TimerTask() {
        var isRunning: Boolean = false

        override fun run() {
            isRunning = true

            callTime = callTime?.plus(1000L)
            val notification = initNotification()
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(SERVICE_ID, notification)

            callTimerListener?.let {
                val callTime = getCallTime()
                if (!TextUtils.isEmpty(callTime)) {
                    it.onCallTimeUpdate(callTime)
                }
            }
        }
    }

    /**
     * https://developer.android.com/reference/android/app/Service#LocalServiceSample
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    inner class CallServiceBinder : Binder() {
        fun getService(): CallService = this@CallService
    }

    private inner class SessionEventsListener : QBRTCClientSessionCallbacks {
        override fun onUserNotAnswer(session: QBRTCSession?, userId: Int?) {
            stopRingtone()
        }

        override fun onSessionStartClose(session: QBRTCSession?) {
            controller.apply {
                if (session == currentSession) {
                    stop(applicationContext)
                }
            }
        }

        override fun onReceiveHangUpFromUser(
            session: QBRTCSession?,
            userID: Int?,
            p2: MutableMap<String, String>?
        ) {
            stopRingtone()
            controller.apply {
                if (session == currentSession) {
                    if (userID == session?.callerID) {
                        currentSession?.let {
                            it.hangUp(HashMap<String, String>())
                            stop(this@CallService)
                        }
                    }
                    Timber.d("initiator hung up the call")
                } else {
                    stop(this@CallService)
                }
            }
        }

        override fun onCallAcceptByUser(
            session: QBRTCSession?,
            p1: Int?,
            p2: MutableMap<String, String>?
        ) {
            stopRingtone()
            controller.apply {
                if (session != currentSession) {
                    return
                }
            }
        }

        override fun onReceiveNewSession(session: QBRTCSession?) {
            Timber.d("Session %s are income", session?.sessionID)
            controller.currentSession?.let {
                Timber.d("Stop new session. Device now is busy")
                session?.rejectCall(null)
            }
        }

        override fun onUserNoActions(p0: QBRTCSession?, p1: Int?) {
            longToast("Call was stopped by UserNoActions timer")
            clearCallState()
            clearButtonsState()
            controller.setCurrentSession(null)
            stop(this@CallService)
        }

        override fun onSessionClosed(session: QBRTCSession?) {
            Timber.d("Session %s start stop session", session?.sessionID)
            stopRingtone()
            if (session == currentSession) {
                Timber.d("Stop session")
                stop(this@CallService)
            }
        }

        override fun onCallRejectByUser(
            session: QBRTCSession?,
            p1: Int?,
            p2: MutableMap<String, String>?
        ) {
            stopRingtone()
            controller.apply {
                if (session != currentSession) {
                    return
                }
            }
        }
    }

    private inner class SessionStateListener : QBRTCSessionStateCallback<QBRTCSession> {
        override fun onDisconnectedFromUser(session: QBRTCSession?, userId: Int?) {
            Timber.d("Disconnected from user: $userId")
        }

        override fun onConnectedToUser(session: QBRTCSession?, userId: Int?) {
            stopRingtone()
            isCallState = true
            Timber.d("onConnectedToUser() is started")
            startCallTimer()
        }

        override fun onConnectionClosedForUser(session: QBRTCSession?, userID: Int?) {
            Timber.d("Connection closed for user: $userID")
            shortToast("The user: " + userID + "has left the call")
        }

        override fun onStateChanged(
            session: QBRTCSession?,
            sessionState: BaseSession.QBRTCSessionState?
        ) {

        }
    }

    private inner class NetworkConnectionListener :
        NetworkConnectionChecker.OnConnectivityChangedListener {
        override fun connectivityChanged(availableNow: Boolean) {
            Timber.i("Internet connection %s", if (availableNow) "available" else " unavailable")
        }
    }

    private inner class CameraEventsListener : CameraVideoCapturer.CameraEventsHandler {
        override fun onCameraError(s: String) {
            shortToast("Camera error: $s")
        }

        override fun onCameraDisconnected() {
            shortToast("Camera Disconnected: ")
        }

        override fun onCameraFreezed(s: String) {
            shortToast("Camera freezed: $s")
            hangUpCurrentSession(HashMap())
        }

        override fun onCameraOpening(s: String) {
            shortToast("Camera opening: $s")
        }

        override fun onFirstFrameAvailable() {
            shortToast("onFirstFrameAvailable: ")
        }

        override fun onCameraClosed() {
            shortToast("Camera closed: ")
        }
    }

    interface CallTimerListener {
        fun onCallTimeUpdate(time: String)
    }
}