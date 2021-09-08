package com.hahalolo.call.client.activities

import android.annotation.TargetApi
import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.*
import android.preference.PreferenceManager
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.hahalolo.call.R
import com.hahalolo.call.client.CallController
import com.hahalolo.call.client.fragments.*
import com.hahalolo.call.client.services.CallService
import com.hahalolo.call.client.utils.*
import com.hahalolo.call.webrtc.*
import com.hahalolo.call.webrtc.callbacks.QBRTCClientSessionCallbacks
import com.hahalolo.call.webrtc.callbacks.QBRTCSessionEventsCallback
import com.hahalolo.call.webrtc.callbacks.QBRTCSessionStateCallback
import org.webrtc.CameraVideoCapturer
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

private const val INCOME_CALL_FRAGMENT = "income_call_fragment"
private const val REQUEST_PERMISSION_SETTING = 545

class CallActivity : BaseActivity(), IncomeCallFragmentCallbackListener, QBRTCSessionStateCallback<QBRTCSession>,
    QBRTCClientSessionCallbacks, ConversationFragmentCallback, ScreenShareFragment.OnSharingEvents {

    @Inject
    lateinit var controller: CallController

    // Don't attempt to unbind from the service unless the client has received some
    // information about the service's state.
    private var mShouldUnbind = false
    // To invoke the bound service, first make sure that this value
    // is not null.
    lateinit var callService: CallService
    private var callServiceConnection: ServiceConnection = CallServiceConnection()

    private val currentCallStateCallbackList = ArrayList<CurrentCallStateCallback>()
    private var sharedPref: SharedPreferences? = null

    private var isInComingCall: Boolean = false
    private var isVideoCall: Boolean = false
    private lateinit var showIncomingCallWindowTaskHandler: Handler
    private lateinit var showIncomingCallWindowTask: Runnable

    companion object {
        fun start(
            context: Context,
            isIncomingCall: Boolean,
            sharedPreferences: SharedPreferences
        ) {
            val intent = Intent(context, CallActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra(EXTRA_IS_INCOMING_CALL, isIncomingCall)
            SharedPrefsHelper.save(EXTRA_IS_INCOMING_CALL, isIncomingCall, sharedPreferences)
            context.startActivity(intent)
            CallService.start(context)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun initScreen() {
        Timber.i("Init Screen Call")

        callService.setCallTimerCallback(CallTimerCallback())
        isVideoCall = callService.isVideoCall()

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        initSettingsStrategy()
        addListeners()

        if (callService.isCallMode()) {
            checkPermission()
            if (callService.isSharingScreenState()) {
                startScreenSharing(null)
                return
            }
            addConversationFragment(isInComingCall)
        } else {
            isInComingCall = intent?.extras?.run {
                getBoolean(EXTRA_IS_INCOMING_CALL)
            } ?: kotlin.run {
                SharedPrefsHelper[EXTRA_IS_INCOMING_CALL, false, controller.sharedPreferences]
            }

            if (!isInComingCall) {
                callService.playRingtone()
            }
            startSuitableFragment(isInComingCall)
        }
    }

    private fun addListeners() {
        addSessionEventsListener(this)
        addSessionStateListener(this)
    }

    private fun removeListeners() {
        removeSessionEventsListener(this)
        removeSessionStateListener(this)
        callService.removeCallTimerCallback()
    }

    /**
     * {@sample https://developer.android.com/reference/android/app/Service#LocalServiceSample}
     */
    private fun bindCallService() {
        Intent(this, CallService::class.java).also { intent ->
            bindService(intent, callServiceConnection, Context.BIND_AUTO_CREATE)
            mShouldUnbind = true
        }
    }

    private fun doUnbindService() {
        if (mShouldUnbind) {
            // Release information about the service's state.
            unbindService(callServiceConnection)
            mShouldUnbind = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.i( "onActivityResult requestCode=$requestCode, resultCode= $resultCode")
        if (requestCode == QBRTCScreenCapturer.REQUEST_MEDIA_PROJECTION && resultCode == Activity.RESULT_OK) {
            data?.let {
                startScreenSharing(it)
                Timber.i( "Starting screen capture")
            }
        }
    }

    private fun startScreenSharing(data: Intent?) {
        val fragmentByTag = supportFragmentManager.findFragmentByTag(ScreenShareFragment::class.simpleName)
        if (fragmentByTag !is ScreenShareFragment) {
            addFragment(
                supportFragmentManager, R.id.fragment_container,
                ScreenShareFragment.newInstance(), ScreenShareFragment::class.java.simpleName
            )
            data?.let {
                callService.startScreenSharing(it)
            }
        }
    }

    private fun startSuitableFragment(isInComingCall: Boolean) {
        val session = controller.currentSession
        if (session != null) {
            if (isInComingCall) {
                initIncomingCallTask()
                startLoadAbsentUsers()
                addIncomeCallFragment()
                checkPermission()
            } else {
                addConversationFragment(isInComingCall)
                intent.removeExtra(EXTRA_IS_INCOMING_CALL)
                SharedPrefsHelper.save(EXTRA_IS_INCOMING_CALL, false, controller.sharedPreferences)
            }
        } else {
            Timber.e("Current Session is Null")
            finish()
        }
    }

    private fun checkPermission() {
        val cam = SharedPrefsHelper[PERMISSIONS[0], true, controller.sharedPreferences]
        val mic = SharedPrefsHelper[PERMISSIONS[1], true, controller.sharedPreferences]
        Timber.i("CAMERA => $cam; MICROPHONE => $mic")

        if (isVideoCall && checkPermissions(PERMISSIONS)) {
            if (cam) {
                PermissionsActivity.startForResult(this, false, PERMISSIONS)
            } else {
                val rootView = window.decorView.findViewById<View>(android.R.id.content)
                showErrorSnackbar(
                    rootView,
                    getString(R.string.error_permission_video),
                    R.string.dlg_allow
                ) { startPermissionSystemSettings() }
            }
        } else if (checkPermission(PERMISSIONS[1])) {
            if (mic) {
                PermissionsActivity.startForResult(this, true, PERMISSIONS)
            } else {
                val rootView = window.decorView.findViewById<View>(android.R.id.content)
                showErrorSnackbar(
                    rootView,
                    R.string.error_permission_audio,
                    "Allow Permission",
                    R.string.dlg_allow
                ) { startPermissionSystemSettings() }
            }
        }
    }

    private fun startPermissionSystemSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
    }

    private fun startLoadAbsentUsers() {
        val users = controller.callWithUser
        val allParticipantsOfCall = ArrayList<Int>()

        controller.callWithUserIds?.let {
            allParticipantsOfCall.addAll(it)
        }

        if (isInComingCall) {
            val callerId = callService?.getCallerId()
            callerId?.let {
                allParticipantsOfCall.add(it)
            }
        }

        val idsNotLoadedUsers = ArrayList<Int>()

        for (userId in allParticipantsOfCall) {
            /*val user = QBUser(userId)
            user.fullName = userId.toString()
            if (!usersFromDb.contains(user)) {
                idsNotLoadedUsers.add(userId)
            }*/
        }

        if (!idsNotLoadedUsers.isEmpty()) {
            /*loadUsersByIds(idsNotLoadedUsers, object : QBEntityCallbackImpl<ArrayList<QBUser>>() {
                override fun onSuccess(users: ArrayList<QBUser>, params: Bundle) {
                    QbUsersDbManager.saveAllUsers(users, false)
                    notifyCallStateListenersNeedUpdateOpponentsList(users)
                }
            })*/
        }
    }

    private fun initSettingsStrategy() {
        controller.callWithUserIds?.let {
            setSettingsStrategy(it, sharedPref, this)
        }
    }

    private fun initIncomingCallTask() {
        showIncomingCallWindowTaskHandler = Handler(Looper.myLooper()!!)
        showIncomingCallWindowTask = Runnable {
            if (callService.currentSessionExist()) {
                val currentSessionState = callService.getCurrentSessionState()
                if (BaseSession.QBRTCSessionState.QB_RTC_SESSION_NEW == currentSessionState) {
                    callService.rejectCurrentSession(HashMap())
                } else {
                    callService.stopRingtone()
                    hangUpCurrentSession()
                }
                // This is a fix to prevent call stop in case calling to user with more then one device logged in.
                longToast("Call was stopped by UserNoActions timer")
                callService.clearCallState()
                callService.clearButtonsState()
                controller.setCurrentSession(null)
                CallService.stop(this@CallActivity)
                finish()
            }
        }
    }

    private fun hangUpCurrentSession() {
        callService.stopRingtone()
        if (!callService.hangUpCurrentSession(HashMap())) {
            finish()
        }
    }

    private fun startIncomeCallTimer(time: Long) {
        showIncomingCallWindowTaskHandler.postAtTime(
            showIncomingCallWindowTask,
            SystemClock.uptimeMillis() + time
        )
    }

    private fun stopIncomeCallTimer() {
        Timber.i("Stop Income Call Timber")
        showIncomingCallWindowTaskHandler.removeCallbacks(showIncomingCallWindowTask)
    }

    override fun onResume() {
        super.onResume()
        bindCallService()
    }

    override fun onPause() {
        super.onPause()
        doUnbindService()
        removeListeners()
    }

    override fun finish() {
        //Fix bug when user returns to call from service and the backstack doesn't have any screens
        /*OpponentsActivity.start(this)*/
        CallService.stop(this)
        super.finish()
    }

    override fun onBackPressed() {
        // To prevent returning from Call Fragment
    }

    private fun addIncomeCallFragment() {
        if (callService.currentSessionExist()) {
            val fragment = IncomeCallFragment()
            if (supportFragmentManager.findFragmentByTag(INCOME_CALL_FRAGMENT) == null) {
                addFragment(
                    supportFragmentManager,
                    R.id.fragment_container,
                    fragment,
                    INCOME_CALL_FRAGMENT
                )
            }
        }
    }

    private fun addConversationFragment(isIncomingCall: Boolean) {
        val baseConversationFragment: BaseConversationFragment = if (isVideoCall) {
            VideoConversationFragment()
        } else {
            AudioConversationFragment()
        }
        val conversationFragment = BaseConversationFragment.newInstance(baseConversationFragment, isIncomingCall)
        addFragment(
            supportFragmentManager,
            R.id.fragment_container,
            conversationFragment,
            conversationFragment.javaClass.simpleName
        )
    }

    private fun showNotificationPopUp(text: Int, show: Boolean) {
        runOnUiThread {
            val connectionView = View.inflate(this, R.layout.connection_popup, null) as LinearLayout
            if (show) {
                (connectionView.findViewById(R.id.notification) as TextView).setText(text)
                if (connectionView.parent == null) {
                    (this@CallActivity.findViewById<View>(R.id.fragment_container) as ViewGroup).addView(connectionView)
                }
            } else {
                (this@CallActivity.findViewById<View>(R.id.fragment_container) as ViewGroup).removeView(connectionView)
            }
        }
    }

    ////////////////////////////// QBRTCSessionStateCallbackListener ///////////////////////////

    override fun onDisconnectedFromUser(session: QBRTCSession?, userId: Int?) {

    }

    override fun onConnectedToUser(session: QBRTCSession?, userId: Int?) {
        notifyCallStateListenersCallStarted()
        if (isInComingCall) {
            stopIncomeCallTimer()
        }
        Timber.d( "onConnectedToUser() is started")
    }

    override fun onConnectionClosedForUser(session: QBRTCSession?, userId: Int?) {

    }

    override fun onStateChanged(session: QBRTCSession?, sessiontState: BaseSession.QBRTCSessionState?) {

    }

    ////////////////////////////// QBRTCClientSessionCallbacks //////////////////////////////

    override fun onUserNotAnswer(session: QBRTCSession?, userId: Int?) {
        if (callService.isCurrentSession(session)) {
            callService.stopRingtone()
        }
    }

    override fun onSessionStartClose(session: QBRTCSession?) {
        if (callService.isCurrentSession(session)) {
            callService.removeSessionStateListener(this)
            notifyCallStateListenersCallStopped()
        }
    }

    override fun onReceiveHangUpFromUser(session: QBRTCSession?, userId: Int?, map: MutableMap<String, String>?) {
        if (callService.isCurrentSession(session)) {
            if (userId == session?.callerID) {
                hangUpCurrentSession()
                Timber.d("initiator hung up the call")
            }
            /*val participant = QbUsersDbManager.getUserById(userId)
            val participantName = if (participant != null) participant.fullName else userId.toString()
            shortToast("User " + participantName + " " + getString(R.string.text_status_hang_up) + " conversation")*/
        }
    }

    override fun onCallAcceptByUser(session: QBRTCSession?, userId: Int?, map: MutableMap<String, String>?) {
        if (callService.isCurrentSession(session)) {
            callService.stopRingtone()
        }
    }

    override fun onReceiveNewSession(session: QBRTCSession?) {

    }

    override fun onUserNoActions(session: QBRTCSession?, userId: Int?) {
        startIncomeCallTimer(0)
    }

    override fun onSessionClosed(session: QBRTCSession?) {
        if (callService.isCurrentSession(session)) {
            callService.stopForeground(true)
            finish()
        }
    }

    override fun onCallRejectByUser(session: QBRTCSession?, userId: Int?, map: MutableMap<String, String>?) {
        if (callService.isCurrentSession(session)) {
            callService.stopRingtone()
        }
    }

    ////////////////////////////// IncomeCallFragmentCallbackListener ////////////////////////////

    override fun onAcceptCurrentSession() {
        if (callService.currentSessionExist()) {
            addConversationFragment(true)
        }
    }

    override fun onRejectCurrentSession() {
        callService.rejectCurrentSession(HashMap())
    }

    ////////////////////////////// ConversationFragmentCallback ////////////////////////////

    override fun addSessionStateListener(clientConnectionCallbacks: QBRTCSessionStateCallback<*>?) {
        callService.addSessionStateListener(clientConnectionCallbacks)
    }

    override fun addSessionEventsListener(eventsCallback: QBRTCSessionEventsCallback?) {
        callService.addSessionEventsListener(eventsCallback)
    }

    override fun onSetAudioEnabled(isAudioEnabled: Boolean) {
    }

    override fun onHangUpCurrentSession() {
        hangUpCurrentSession()
    }

    @TargetApi(21)
    override fun onStartScreenSharing() {
        QBRTCScreenCapturer.requestPermissions(this)
    }

    override fun onSwitchCamera(cameraSwitchHandler: CameraVideoCapturer.CameraSwitchHandler) {
        callService.switchCamera(cameraSwitchHandler)
    }

    override fun onSetVideoEnabled(isNeedEnableCam: Boolean) {
    }

    override fun onSwitchAudio() {
        callService.switchAudio()
    }

    override fun removeSessionStateListener(clientConnectionCallbacks: QBRTCSessionStateCallback<*>?) {
        callService.removeSessionStateListener(clientConnectionCallbacks)
    }

    override fun removeSessionEventsListener(eventsCallback: QBRTCSessionEventsCallback?) {
        callService.removeSessionEventsListener(eventsCallback)
    }

    override fun addCurrentCallStateListener(currentCallStateCallback: CurrentCallStateCallback?) {
        currentCallStateCallback?.let {
            currentCallStateCallbackList.add(it)
        }
    }

    override fun removeCurrentCallStateListener(currentCallStateCallback: CurrentCallStateCallback?) {
        currentCallStateCallbackList.remove(currentCallStateCallback)
    }

    override fun addOnChangeAudioDeviceListener(onChangeDynamicCallback: OnChangeAudioDevice?) {
    }

    override fun removeOnChangeAudioDeviceListener(onChangeDynamicCallback: OnChangeAudioDevice?) {
    }

    override fun acceptCall(userInfo: Map<String, String>) {
        callService.acceptCall(userInfo)
    }

    override fun startCall(userInfo: Map<String, String>) {
        callService.startCall(userInfo)
    }

    override fun currentSessionExist(): Boolean {
        return callService.currentSessionExist()
    }

    override fun getOpponents(): List<Int>? {
        return callService.getOpponents()
    }

    override fun getCallerId(): Int? {
        return callService.getCallerId()
    }

    override fun getCurrentSessionState(): BaseSession.QBRTCSessionState? {
        return callService.getCurrentSessionState()
    }

    override fun getPeerChannel(userId: Int): QBRTCTypes.QBRTCConnectionState? {
        return callService.getPeerChannel(userId)
    }

    override fun isMediaStreamManagerExist(): Boolean {
        return callService.isMediaStreamManagerExist()
    }

    override fun isCallState(): Boolean {
        return callService.isCallMode()
    }

    override fun onStopPreview() {
        callService.stopScreenSharing()
        addConversationFragment(false)
    }

    private fun notifyCallStateListenersCallStarted() {
        for (callback in currentCallStateCallbackList) {
            callback.onCallStarted()
        }
    }

    private fun notifyCallStateListenersCallStopped() {
        for (callback in currentCallStateCallbackList) {
            callback.onCallStopped()
        }
    }

    private fun notifyCallStateListenersCallTime(callTime: String) {
        for (callback in currentCallStateCallbackList) {
            callback.onCallTimeUpdate(callTime)
        }
    }

    private inner class CallServiceConnection : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.

            val binder = service as CallService.CallServiceBinder
            callService = binder.getService()
            if (callService.currentSessionExist()) {
                //we have already currentSession == null, so it's no reason to do further initialization
                initScreen()
            } else {
                finish()
            }
        }
    }

    private inner class CallTimerCallback : CallService.CallTimerListener {
        override fun onCallTimeUpdate(time: String) {
            runOnUiThread {
                notifyCallStateListenersCallTime(time)
            }
        }
    }

    interface OnChangeAudioDevice {
        fun audioDeviceChanged(newAudioDevice: AppRTCAudioManager.AudioDevice)
    }

    interface CurrentCallStateCallback {
        fun onCallStarted()

        fun onCallStopped()

        fun onCallTimeUpdate(time: String)
    }
}