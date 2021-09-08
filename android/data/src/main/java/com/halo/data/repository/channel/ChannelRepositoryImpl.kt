package com.halo.data.repository.channel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.halo.data.AppDatabase
import com.halo.data.HalomeConfig
import com.halo.data.api.channel.ChannelRestApi
import com.halo.data.api.member.MemberRestApi
import com.halo.data.api.readstate.ReadStateRestApi
import com.halo.data.common.resource.Resource
import com.halo.data.common.utils.Strings
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.channel.ChannelBody
import com.halo.data.entities.channel.ChannelCreateResponse
import com.halo.data.entities.channel.ChannelFriendResponse
import com.halo.data.entities.member.Member
import com.halo.data.repository.message.MessageRepositoryImpl
import com.halo.data.room.dao.ChannelDao
import com.halo.data.room.entity.ChannelDetailEntity
import com.halo.data.room.entity.ChannelEntity
import com.halo.data.room.entity.MemberEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepositoryImpl @Inject constructor(
    private val channelRestApi: ChannelRestApi,
    private val readStateRestApi: ReadStateRestApi,
    private val memberRestApi: MemberRestApi,
    private val appDataBase: AppDatabase,
    private val channelDao: ChannelDao

) : ChannelRepository {

    override suspend fun channelPaging(
        token: String?,
        workspaceId: String
    ): Flow<PagingData<Channel>> =
        Pager(PagingConfig(pageSize = 20)) {
            ChannelPagingSource(
                api = channelRestApi,
                token = token ?: "",
                workspaceId = workspaceId
            )
        }.flow

    override fun channelPaging2(
        token: String?,
        workspaceId: String,
        ownerId: String
    ): LiveData<ChannelPaging> {
        return MutableLiveData<ChannelPaging>().apply {
            CoroutineScope(Dispatchers.IO).launch {
                //clear old data
                channelDao.clearCache(workspaceId, MessageRepositoryImpl.PAGING_SIZE)
                postValue(
                    ChannelPaging(
                        api = channelRestApi,
                        readStateApi = readStateRestApi,
                        memberApi = memberRestApi,
                        appDataBase = appDataBase,
                        token = token ?: "",
                        workspaceId = workspaceId,
                        ownerId = ownerId,
                        pagingSize = MessageRepositoryImpl.PAGING_SIZE
                    )
                )
            }
        }
    }

    override suspend fun createChannel(
        token: String,
        workspaceId: String,
        body: ChannelBody
    ): Flow<Resource<ChannelCreateResponse>> = flow {
        emit(Resource.loading<ChannelCreateResponse>(null))
        val resource = kotlin.runCatching {
            val isHaveAvatar = body.avatar?.isNotEmpty() ?: false
            var channelAvatar: MultipartBody.Part? = null

            /* Create channel name */
            val channelName = MultipartBody.Part.createFormData("name", body.name ?: "")

            if (isHaveAvatar) {
                /* Create channel avatar */
                val file = File(body.avatar ?: "")
                val requestFile: RequestBody =
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                channelAvatar = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
            }

            /* Create channel list member */
//            val channelUserIds: MutableList<MultipartBody.Part> = mutableListOf()
//            body.userIds?.forEach { id ->
//                val part: MultipartBody.Part = MultipartBody.Part.createFormData("userIds", id)
//                channelUserIds.add(part)
//            }
            channelRestApi.createChannel(
                token = token,
                workspaceId = workspaceId,
                name = channelName,
                avatar = channelAvatar
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<ChannelCreateResponse>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<ChannelCreateResponse>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun getChannelDetail(
        token: String,
        workspaceId: String,
        channelId: String?
    ): Flow<Resource<Channel>> = flow {
        emit(Resource.loading<Channel>(null))
        val resource = kotlin.runCatching {
            channelRestApi.channelById(
                token = token,
                workspaceId = workspaceId,
                channelId = channelId
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<Channel>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<Channel>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun getChannelEntity(
        token: String,
        workspaceId: String,
        channelId: String?
    ): Flow<ChannelEntity?> = flow {
        emit(appDataBase.channelDao().getChannelEntity(channelId ?: ""))
        kotlin.runCatching {
            channelRestApi.channelById(
                token = token,
                workspaceId = workspaceId,
                channelId = channelId ?: ""
            )?.apply {
                this.members = loadMemberOfChannel(token, this)
                appDataBase.insertChannels(workspaceId, mutableListOf(this))
                emit(appDataBase.channelDao().getChannelEntity(channelId ?: ""))
            }
        }.getOrElse {
            emit(null)
        }
    }.catch {
        emit(null)
    }.flowOn(Dispatchers.IO)

    override fun getChannelDetailEntity(
        token: String,
        workspaceId: String,
        channelId: String?
    ): LiveData<ChannelDetailEntity> =
        appDataBase.channelDao().getChannelDetailEntity(channelId ?: "").apply {
            CoroutineScope(Dispatchers.IO).launch {
                kotlin.runCatching {
                    channelId?.takeIf { it.isNotEmpty() }?.run {
                        // group channel
                        channelRestApi.channelById(
                            token = token,
                            workspaceId = workspaceId,
                            channelId = channelId ?: ""
                        )?.apply {
                            this.members = loadMemberOfChannel(token, this)
                            appDataBase.insertChannels(workspaceId, mutableListOf(this))
                        }
                    }
                }
            }
        }

    override suspend fun updateChannel(
        token: String,
        workspaceId: String,
        channelId: String?,
        body: ChannelBody?
    ): Flow<Resource<Channel>> = flow {
        emit(Resource.loading<Channel>(null))
        val resource = kotlin.runCatching {
            val isHaveAvatar = body?.avatar?.isNotEmpty() ?: false
            if (isHaveAvatar) {
                /* Create channel name */
                val channelName = MultipartBody.Part.createFormData("name", body?.name ?: "")

                /* Create channel avatar */
                val channelAvatar: MultipartBody.Part?
                val file = File(body?.avatar ?: "")
                val requestFile: RequestBody =
                    file.asRequestBody("multipart/form-data".toMediaTypeOrNull())

                channelAvatar = MultipartBody.Part.createFormData("avatar", file.name, requestFile)
                channelRestApi.updateChannel(
                    token = token,
                    workspaceId = workspaceId,
                    channelId = channelId,
                    name = channelName,
                    avatar = channelAvatar
                )?.run {
                    Resource.success(this)
                } ?: kotlin.run {
                    Resource.error<Channel>(500, "errors", null)
                }
            } else {
                channelRestApi.updateChannel(
                    token = token,
                    workspaceId = workspaceId,
                    channelId = channelId,
                    body
                )?.run {
                    Resource.success(this)
                } ?: kotlin.run {
                    Resource.error<Channel>(500, "errors", null)
                }
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<Channel>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun deleteChannel(
        token: String,
        workspaceId: String,
        channelId: String?
    ): Flow<Resource<Channel>> = flow {
        emit(Resource.loading<Channel>(null))
        val resource = kotlin.runCatching {
            channelRestApi.deleteChannel(
                token = token,
                workspaceId = workspaceId,
                channelId = channelId
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<Channel>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<Channel>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun findChannel(
        token: String,
        workspaceId: String,
        friendId: String?
    ): Flow<Resource<Channel>> = flow {
        emit(Resource.loading<Channel>(null))
        val resource = kotlin.runCatching {
            channelRestApi.findChannel(
                token = token,
                friendId = friendId
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<Channel>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<Channel>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun createChannel(
        token: String,
        workspaceId: String,
        friendId: String?
    ): Flow<Resource<ChannelFriendResponse>> = flow {
        emit(Resource.loading<ChannelFriendResponse>(null))
        val resource = kotlin.runCatching {
            channelRestApi.createChannel(
                token = token,
                friendId = friendId
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<ChannelFriendResponse>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<ChannelFriendResponse>(it)
    }.flowOn(Dispatchers.IO)

    override suspend fun typing(
        token: String,
        workspaceId: String?,
        channelId: String?
    ): Flow<Resource<Any>> = flow {
        emit(Resource.loading<Any>(null))
        val resource = kotlin.runCatching {
            channelRestApi.typing(
                token = token,
                workspaceId = workspaceId,
                channelId = channelId
            )?.run {
                Resource.success(this)
            } ?: kotlin.run {
                Resource.error<Any>(500, "errors", null)
            }
        }.getOrElse {
            Resource.error(it)
        }
        emit(resource)
    }.catch {
        Resource.error<Any>(it)
    }.flowOn(Dispatchers.IO)

    private suspend fun loadMemberOfChannel(token: String, channel: Channel): MutableList<Member> {
        return kotlin.runCatching {
            val result = mutableListOf<Member>()
            var page: Int? = 1
            while (page != null) {
                val response = memberRestApi.getMemberPaging2(
                    token = token,
                    workspaceId = channel.workspaceId ?: "",
                    channelId = channel.channelId ?: "",
                    page = page,
                    limit = 20
                )
                //todo next page
                result.addAll(response?.docs ?: mutableListOf<Member>())
                response?.takeIf { it.isHasNextPage }?.run {
                    page = (page ?: 1) + 1
                } ?: run {
                    page = null
                }
            }
            result
        }.getOrElse {
            mutableListOf<Member>()
        }
    }

    override fun mentionQuery(
        workspaceId: String,
        channelId: String?,
        ownerId: String,
        query: String
    ): LiveData<MutableList<MemberEntity>> {
        Strings.log("mentionResult channelId "+channelId)
        Strings.log("mentionResult ownerId "+ownerId)
        Strings.log("mentionResult query "+"'%${query}%'")
        return appDataBase.memberDao().mentionQuery(channelId = channelId ?: "",
            ownerId = ownerId,
            query = "%${query}%")
    }

    /*Receiver data from socket*/
    override fun onNewChannel(token: String, channel: Channel) {
        CoroutineScope(Dispatchers.IO).launch {
            var channelDetail: Channel? = null
            kotlin.runCatching {
                channelDetail = channelRestApi.channelById(
                    token = token,
                    workspaceId = channel.workspaceId ?: "",
                    channelId = channel.channelId ?: ""
                )
            }.getOrElse {

            }
            val members = loadMemberOfChannel(token, channel)
            channelDetail?.members = members
            channelDetail?.run {
                appDataBase.insertChannels(this.workspaceId ?: "", mutableListOf(this))
            }
        }
    }

    private val typingJobs = HashMap<String, Job>()

    override fun onUserTyping(workspaceId: String, channelId: String?, userId: String?) {
        takeIf { !channelId.isNullOrEmpty() && !userId.isNullOrEmpty() }?.run {
            val key = channelId + userId
            typingJobs[key]?.cancel()
            val job = CoroutineScope(Dispatchers.IO).launch {
                appDataBase.insertUserTyping(workspaceId, channelId!!, userId!!)
                delay(HalomeConfig.TYPING_TIME_STOP)
                appDataBase.removeUserTyping(channelId, userId)
                typingJobs.remove(key)
            }
            typingJobs.put(key, job)
        }
    }
    /*Receiver data from socket END*/
}
