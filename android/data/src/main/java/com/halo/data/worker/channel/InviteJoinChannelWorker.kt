package com.halo.data.worker.channel

import android.content.Context
import androidx.work.*
import com.halo.data.common.utils.Strings
import com.halo.data.entities.invite.CreateInviteBody
import com.halo.data.entities.invite.SendInviteBody
import com.halo.data.repository.invite.InviteRepository
import com.halo.di.ChildWorkerFactory
import java.util.*
import javax.inject.Inject

class InviteJoinChannelWorker(
    private val context: Context,
    workerParams: WorkerParameters,
    private val inviteRepository: InviteRepository
) : CoroutineWorker(context, workerParams) {

    private var token: String = ""
    private var workspaceId: String = "0"
    private var channelId: String? = null
    private var listInvite: MutableList<String> = mutableListOf()
    private var listInviteError: MutableList<String> = mutableListOf()

    override suspend fun doWork(): Result {
        kotlin.runCatching {
            if (runAttemptCount > 2) {
                return Result.failure()
            }
            token = inputData.getString(TOKEN_INVITE_CHANNEL_WORKER) ?: ""
            workspaceId = inputData.getString(WORKSPACE_ID_INVITE_CHANNEL_WORKER) ?: ""
            channelId = inputData.getString(CHANNEL_ID_INVITE_CHANNEL_WORKER) ?: ""
            listInvite = inputData.getStringArray(LIST_USER_CREATE_CHANNEL_WORKER)?.toMutableList()
                ?: mutableListOf()

            if (token.isEmpty() || listInvite.isEmpty() || channelId == null) {
                return Result.failure()
            }
            return handleInviteUser(channelId ?: "")
        }.getOrElse {
            return Result.failure()
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
        if (urlInvite.isEmpty()) return Result.failure()
        // Sau khi lấy đc url tiền hành mời user
        return sendInvite(urlInvite)
    }

    private suspend fun sendInvite(url: String): Result {
        // Kiểm tra nếu listInviteError có data thì lấy data đó send invite
        listInviteError.takeIf { it.isNotEmpty() }?.run {
            listInvite.clear()
            listInvite.addAll(this)
        }

        listInvite.forEach { userId ->
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
            return Result.success()
        } ?: kotlin.run {
            return Result.retry()
        }
    }


    class Factory @Inject constructor(
        private val inviteRepository: InviteRepository
    ) : ChildWorkerFactory {

        override fun create(appContext: Context, params: WorkerParameters): CoroutineWorker {
            return InviteJoinChannelWorker(
                appContext,
                params,
                inviteRepository
            )
        }
    }

    companion object {

        const val TOKEN_INVITE_CHANNEL_WORKER = "InviteJoinChannelWorker_token"
        const val WORKSPACE_ID_INVITE_CHANNEL_WORKER = "InviteJoinChannelWorker_workspaceId"
        const val CHANNEL_ID_INVITE_CHANNEL_WORKER = "InviteJoinChannelWorker_channel_id"
        const val LIST_USER_CREATE_CHANNEL_WORKER = "InviteJoinChannelWorker_list_user_id"

        fun inviteChannel(
            context: Context,
            token: String,
            workspaceId: String,
            channelId: String,
            userIds: Array<String>
        ): UUID {
            val createChannelWorkerRequest =
                OneTimeWorkRequest.Builder(InviteJoinChannelWorker::class.java)
            val data = Data.Builder()

            data.putString(TOKEN_INVITE_CHANNEL_WORKER, token)
            data.putString(WORKSPACE_ID_INVITE_CHANNEL_WORKER, workspaceId)
            data.putString(CHANNEL_ID_INVITE_CHANNEL_WORKER, channelId)
            data.putStringArray(LIST_USER_CREATE_CHANNEL_WORKER, userIds)
            createChannelWorkerRequest.setInputData(data.build())
            val oneTimeWorkRequest = createChannelWorkerRequest.build()
            WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
            return oneTimeWorkRequest.id
        }
    }
}