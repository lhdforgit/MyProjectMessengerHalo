package com.halo.data.worker.channel

import android.content.Context
import androidx.work.*
import com.google.gson.Gson
import com.halo.data.cache.serializer.Serializer
import com.halo.data.entities.channel.ChannelBody
import com.halo.data.entities.invite.CreateInviteBody
import com.halo.data.entities.invite.SendInviteBody
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.repository.invite.InviteRepository
import com.halo.di.ChildWorkerFactory
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import javax.inject.Inject

class CreateChannelWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val serializer: Serializer,
    private val channelRepository: ChannelRepository,
    private val inviteRepository: InviteRepository
) : CoroutineWorker(context, workerParams) {

    private var channelBody: ChannelBody? = null
    private var token: String = ""
    private var workspaceId: String = "0"
    private var channelId: String? = null
    private var listInviteError: MutableList<String> = mutableListOf()

    override suspend fun doWork(): Result {
        kotlin.runCatching {
            if (runAttemptCount > 2) {
                return Result.failure()
            }
            token = inputData.getString(TOKEN_CREATE_CHANNEL_WORKER) ?: ""
            workspaceId = inputData.getString(WORKSPACE_ID_CREATE_CHANNEL_WORKER) ?: ""
            channelBody = serializer.deserialize(
                inputData.getString(CHANNEL_BODY_CREATE_CHANNEL_WORKER),
                ChannelBody::class.java
            )

            // Nếu token và body null return error
            if (token.isEmpty() || channelBody == null) {
                return Result.failure()
            }
            // Nếu channelId == null (case create group chạy lần lần đầu)
            if (channelId == null) {
                handleCreateChannel()
            }
            // Sau khi đã tạo thành công channel tiền hành mời user
            return handleInviteUser(channelId ?: "")
        }.getOrElse {
            return Result.failure()
        }
    }

    private suspend fun handleCreateChannel() {
        channelBody?.let { body ->
            val channelResponse = channelRepository.createChannel(token, workspaceId, body)
            channelResponse.collectLatest {
                when {
                    it.isSuccess -> {
                        it.data?.channelId?.takeIf { it.isNotEmpty() }?.let { channelId ->
                            this.channelId = channelId
                        }
                    }
                }
            }
        }
    }

    private suspend fun handleInviteUser(channelId: String): Result {
        // Kiểm tra lại channelId nếu rỗng thì return error
        if (channelId.isEmpty()) return Result.failure()
        // Call api lấy invite url của channel
        val body = CreateInviteBody(channelId, 100, 10, workspaceId)
        val response = inviteRepository.createUrlInvite(token, body)
        val urlInvite = response?.shortLink ?: ""
        // Nếu url rỗng thì retry
        if (urlInvite.isEmpty()) return Result.failure() // todo return Result.retry()
        // Sau khi lấy đc url tiền hành mời user
        return sendInvite(urlInvite)
    }

    private suspend fun sendInvite(url: String): Result {
        // Kiểm tra nếu listInviteError có data thì lấy data đó send invite
        listInviteError.takeIf { it.isNotEmpty() }?.run {
            channelBody?.userIds?.clear()
            channelBody?.userIds?.addAll(this)
        }

        channelBody?.userIds?.forEach { userId ->
            val bodySend = SendInviteBody(url, userId)
            val response = inviteRepository.sendInviteUser(token, bodySend)
            if (response != null && response.attachments.isNotEmpty()) {
                // todo success
            } else {
                // Lưu lại list user mời bị lỗi
                listInviteError.add(userId)
            }
        }
        // Sau khi mời user kiểm tra xem đã mời đủ
        listInviteError.takeIf { it.isEmpty() }?.run {
            return Result.success(
                workDataOf(
                    CHANNEL_ID_RESULT to channelId
                )
            )
        } ?: kotlin.run {
            return Result.failure()
        }
    }


    class Factory @Inject constructor(
        private val serializer: Serializer,
        private val channelRepository: ChannelRepository,
        private val inviteRepository: InviteRepository
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): CoroutineWorker {
            return CreateChannelWorker(
                appContext,
                params,
                serializer,
                channelRepository,
                inviteRepository
            )
        }
    }


    companion object {

        const val TOKEN_CREATE_CHANNEL_WORKER = "CreateChannelWorker_token"
        const val WORKSPACE_ID_CREATE_CHANNEL_WORKER = "CreateChannelWorker_workspaceId"
        const val CHANNEL_BODY_CREATE_CHANNEL_WORKER = "CreateChannelWorker_channelBody"
        const val CHANNEL_ID_RESULT = "CreateChannelWorker_channel_id"

        fun createChannel(
            context: Context,
            token: String,
            workspaceId: String,
            channelBody: ChannelBody
        ): UUID {
            val createChannelWorkerRequest =
                OneTimeWorkRequest.Builder(CreateChannelWorker::class.java)
            val data = Data.Builder()

            data.putString(TOKEN_CREATE_CHANNEL_WORKER, token)
            data.putString(WORKSPACE_ID_CREATE_CHANNEL_WORKER, workspaceId)
            data.putString(
                CHANNEL_BODY_CREATE_CHANNEL_WORKER, serialize(channelBody, ChannelBody::class.java)
            )
            createChannelWorkerRequest.setInputData(data.build())
            val oneTimeWorkRequest = createChannelWorkerRequest.build()
            WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
            return oneTimeWorkRequest.id
        }

        private fun serialize(`object`: Any?, clazz: Class<*>): String {
            return if (`object` == null) {
                ""
            } else Gson().toJson(`object`, clazz)
        }
    }

}