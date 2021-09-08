package com.hahalolo.notification.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.hahalolo.notification.controller.NotificationController
import com.halo.common.utils.DeviceUtils
import com.halo.data.entities.notification.NotificationRegisterBody
import com.halo.data.entities.notification.WorkspaceChannel
import com.halo.data.repository.notification.NotificationRepository
import com.halo.di.ChildWorkerFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 6/11/21
 * com.hahalolo.notification.worker
 */
class RegisterFcmTokenWorker(
    appContext: Context,
    params: WorkerParameters,
    val controller: NotificationController,
    val repository: NotificationRepository
) : CoroutineWorker(
    appContext, params
) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            val task = Firebase.messaging.token
            Tasks.await(task)
            val token = task.result
            Log.w("FcmTokenWorker", "Fetching FCM registration token: $token")
            val workspaceChannel = WorkspaceChannel(
                mutableListOf("")
            )
            val registerBody = NotificationRegisterBody(
                userId = controller.userId,
                deviceId = DeviceUtils.getAndroidID(),
                deviceToken = token,
                workspaceChannels = workspaceChannel
            )
            repository.register(registerBody).collect {
                Log.i("RegisterFcmTokenWorker", "$it")
            }
            return@withContext Result.success()
        }
    }
}

class FcmTokenWorkerFactory
@Inject constructor(
    val controller: NotificationController,
    val repository: NotificationRepository
) : ChildWorkerFactory {

    override fun create(
        appContext: Context,
        params: WorkerParameters
    ): CoroutineWorker {
        return RegisterFcmTokenWorker(appContext, params, controller, repository)
    }
}