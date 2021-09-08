package com.hahalolo.messager.presentation.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.hahalolo.messager.worker.download.DownloadMessageMediaWorker
import com.hahalolo.messenger.R
import com.halo.common.utils.HaloFileUtils
import java.util.*
import kotlin.random.Random

class ChatDownloadFileService : LifecycleService() {

    private var builder: NotificationCompat.Builder? = null
    private var notificationId = 0

    override fun onCreate() {
        super.onCreate()
        cancelAllNotification()
        initNotification()
    }

    private fun initNotification() {
        val channelId =
            createNotificationChannel() ?: ""
        builder = NotificationCompat.Builder(this, channelId).apply {
            setContentTitle("Tải xuống")
            setContentText("Đang tải tập tin...")
            setSmallIcon(R.drawable.ic_download_32)
            setAutoCancel(true)
            setCategory(Notification.CATEGORY_PROGRESS)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }
    }

    private fun cancelAllNotification() {
        NotificationManagerCompat.from(this@ChatDownloadFileService).apply {
            cancelAll()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.getStringExtra(ATTACHMENT_DOWNLOAD)?.let { attachment ->
            val workerId = DownloadMessageMediaWorker.downloadMedia(
                this@ChatDownloadFileService,
                attachment
            )
            initHandleDownloadMedia(workerId)
        }
        return START_NOT_STICKY
    }

    private fun initHandleDownloadMedia(workerId: UUID) {
        notificationId = Random.nextInt(10002, 99999)
        WorkManager.getInstance(this@ChatDownloadFileService)
            .getWorkInfoByIdLiveData(workerId)
            .observe(this@ChatDownloadFileService, { workInfo ->
                workInfo?.apply {
                    try {
                        when (state) {
                            WorkInfo.State.FAILED -> {
                                stopSelf()
                            }
                            WorkInfo.State.SUCCEEDED -> {
                                val fileName =
                                    outputData.getString(DownloadMessageMediaWorker.OUTPUT_FILE_NAME)
                                handleDownloadSuccess(fileName ?: "")
                                stopSelf()
                            }
                            WorkInfo.State.ENQUEUED -> {
                                handleDownloadProcess(100, 10)
                            }
                            WorkInfo.State.RUNNING -> {
                                handleDownloadProcess(100, 50)
                            }
                            else -> {

                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
    }

    private fun handleDownloadProcess(max: Int, process: Int) {
        builder?.apply {
            NotificationManagerCompat.from(this@ChatDownloadFileService)
                .apply {
                    setProgress(max, process, false)
                    notify(notificationId, build())
                }
        }
    }

    private fun handleDownloadSuccess(fileName: String) {
        val uri = HaloFileUtils.createUriFromFileName(this@ChatDownloadFileService, fileName)
        val intent = Intent()
        intent.apply {
            action = Intent.ACTION_VIEW
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            if (uri.path?.contains(".pdf") == true) {
                setDataAndType(uri, "application/pdf")
            } else {
                setDataAndType(uri, "vnd.android.document/root")
            }
        }
        val notifyIntent = Intent.createChooser(intent, "Open file")
        val pendingIntent = PendingIntent.getActivity(
            this@ChatDownloadFileService,
            0,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder?.apply {
            NotificationManagerCompat.from(this@ChatDownloadFileService)
                .apply {
                    setContentText(fileName)
                    setContentIntent(pendingIntent)
                    setProgress(0, 0, false)
                    notify(notificationId, build())
                }
        }
    }

    private fun createNotificationChannel(): String? {
        val channelId = "hahalolo_download_file"
        val channelName = "Download file process"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.lightColor = Color.BLUE
            channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(channel)

            return channelId
        }
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    companion object {
        const val ATTACHMENT_DOWNLOAD = "ChatDownloadFileAct_AttachmentTable"
    }
}
