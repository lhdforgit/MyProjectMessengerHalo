package com.hahalolo.notification.worker

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.work.*
import com.halo.di.ChildWorkerFactory
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 6/11/21
 * com.hahalolo.notification.worker
 */
class FcmReceiveWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(
    appContext, params
) {

    override suspend fun doWork(): Result {
        val message = inputData.getString(DATA_MESSAGE)
        Log.w("FcmReceiveWorker", "doWork: $message")
        /*message?.apply {
            val intent = FetchMessageService.getIntent(applicationContext, message)
            applicationContext.startService(intent)
        }*/
        return Result.success()
    }

    companion object {
        private const val DATA_MESSAGE = "FcmReceiveWorker-Data-Message"

        @SuppressLint("RestrictedApi")
        fun scheduleJob(message: String?, context: Context) {
            val workBuilder = OneTimeWorkRequest.Builder(FcmReceiveWorker::class.java)
            val data = Data.Builder()
            data.put(DATA_MESSAGE, message ?: "")
            workBuilder.setInputData(data.build())
            val oneTimeWorkRequest = workBuilder.build()
            WorkManager.getInstance(context).beginWith(oneTimeWorkRequest).enqueue()
        }
    }
}

class FcmReceiveWorkerFactory
@Inject constructor() : ChildWorkerFactory {

    override fun create(
        appContext: Context,
        params: WorkerParameters
    ): CoroutineWorker {
        return FcmReceiveWorker(appContext, params)
    }
}