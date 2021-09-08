/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.worker.sendMessage

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.work.*
import com.google.gson.Gson
import com.halo.data.cache.serializer.Serializer
import com.halo.data.common.resource.Resource
import com.halo.data.common.utils.Strings
import com.halo.data.entities.attachment.AttachmentBody
import com.halo.data.entities.message.Message
import com.halo.data.entities.message.MessageSendBody
import com.halo.data.repository.message.MessageRepository
import com.halo.di.ChildWorkerFactory
import javax.inject.Inject

class SendMessageWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val serializer: Serializer,
    private val messageRepository: MessageRepository,
    private val attachmentRepository: MessageRepository
) : CoroutineWorker(context, workerParams) {

    private var messageModel: MessageSendBody? = null

    override suspend fun doWork(): Result {
        return try {
            if (runAttemptCount > 2) {
                resultFailure()
            }
            messageModel = serializer.deserialize(
                inputData.getString(MESSAGE_BODY_PARAMS),
                MessageSendBody::class.java
            )

            return messageModel?.run {
                sendMessageData(this)
            }?: resultFailure()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            resultFailure()
        }
    }

    private suspend fun sendMessageData(body: MessageSendBody) : Result{
        kotlin.runCatching {
            val response = messageRepository.createMessage(body = body).run {

            }
        }.getOrElse {
            it.printStackTrace()
        }
        return Result.success()
    }

    private fun resultSuscess(): Result {
        return Result.success()
    }

    private fun resultFailure(): Result {
        return Result.failure()
    }

    class Factory @Inject constructor(
        private val serializer: Serializer,
        private val messageRepository: MessageRepository,
        private val attachmentRepository: MessageRepository
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): CoroutineWorker {
            return SendMessageWorker(
                appContext,
                params,
                serializer,
                messageRepository,
                attachmentRepository
            )
        }
    }

    companion object {

        const val MESSAGE_BODY_PARAMS = "SendMessageWorker-MESSAGE_BODY_PARAMS"

        fun sendMessage(
            context: Context,
            messageSendBody: MessageSendBody
        ): LiveData<Resource<Message>> {
            val sendMessageWokerRequest = OneTimeWorkRequest.Builder(SendMessageWorker::class.java)
            val data = Data.Builder()

            data.putString(MESSAGE_BODY_PARAMS, serialize(messageSendBody, MessageSendBody::class.java))

            sendMessageWokerRequest.setInputData(data.build())
            val request = sendMessageWokerRequest.build()
            WorkManager.getInstance(context).enqueue(request)
            val result: LiveData<Resource<Message>> = Transformations.switchMap(
                WorkManager.getInstance(context)
                    .getWorkInfoByIdLiveData(request.id)
            ) {
                val resource: Resource<Message> = when (it.state) {
                    WorkInfo.State.ENQUEUED,
                    WorkInfo.State.RUNNING -> {
                        Resource.loading(null)
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        Resource.success(null)
                    }
                    else -> {
                        Resource.error(500, "errors", null)
                    }
                }
                MutableLiveData(resource)
            }
            return result
        }

        private fun serialize(`object`: Any?, clazz: Class<*>): String {
            return if (`object` == null) {
                ""
            } else Gson().toJson(`object`, clazz)
        }
    }
}

