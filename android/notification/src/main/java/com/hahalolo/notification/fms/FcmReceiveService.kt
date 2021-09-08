package com.hahalolo.notification.fms

import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hahalolo.notification.controller.NotificationController
import com.hahalolo.notification.worker.FcmReceiveWorker
import com.hahalolo.notification.worker.RegisterFcmTokenWorker
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 6/11/21
 * com.hahalolo.notification.gms
 */
class FcmReceiveService
@Inject constructor() : FirebaseMessagingService() {

    @Inject
    lateinit var notificationController: NotificationController

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.i(
            "FcmReceiveService", "onMessageReceived() " +
                    "MessageID: ${remoteMessage.messageId}, " +
                    "Delay: ${System.currentTimeMillis() - remoteMessage.sentTime}, " +
                    "Priority: ${remoteMessage.priority}, " +
                    "Original Priority: ${remoteMessage.originalPriority}"
        )
        scheduleJob(remoteMessage.data[""])
    }

    /**
     * onNewToken chỉ được gọi khi người dùng xóa app, và cài đặt lại, lúc này nếu ứng dụng gọi
     * lệnh tạo token, thì hàm onNewToken được thực thi
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.i("FcmReceiveService", "onNewToken: $token")
        val work = OneTimeWorkRequest.Builder(RegisterFcmTokenWorker::class.java).build()
        WorkManager.getInstance(this).beginWith(work).enqueue()
    }

    override fun onMessageSent(s: String) {
        Log.i("FcmReceiveService", "onMessageSent: $s")
    }

    override fun onSendError(s: String, e: Exception) {
        Log.w("FcmReceiveService", "onSendError $e")
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.w("FcmReceiveService", "onDeletedMessages")
    }

    private fun scheduleJob(message: String?) {
        Log.w("FcmReceiveService", "scheduleJob: $message")
        FcmReceiveWorker.scheduleJob(message, applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        Log.w("FcmReceiveService", "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w("FcmReceiveService", "onDestroy")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.w("FcmReceiveService", "onLowMemory")
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.w("FcmReceiveService", "onRebind")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.w("FcmReceiveService", "onUnbind")
        return super.onUnbind(intent)
    }
}