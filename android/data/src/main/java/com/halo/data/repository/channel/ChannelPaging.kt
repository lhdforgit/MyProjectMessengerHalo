package com.halo.data.repository.channel

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.halo.data.AppDatabase
import com.halo.data.api.channel.ChannelRestApi
import com.halo.data.api.member.MemberRestApi
import com.halo.data.api.readstate.ReadStateRestApi
import com.halo.data.common.paging.NetworkState
import com.halo.data.common.utils.Strings
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.member.Member
import com.halo.data.room.entity.ChannelEntity
import com.halo.data.room.table.ReadStateTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChannelPaging(
    private val api: ChannelRestApi,
    private val readStateApi: ReadStateRestApi,
    private val memberApi: MemberRestApi,
    private val appDataBase: AppDatabase,
    private val token: String,
    private val workspaceId: String,
    private val ownerId: String,
    private val pagingSize: Int
) {
    val listData: LiveData<MutableList<ChannelEntity>> = appDataBase.channelDao()
        .channelList(workspaceId )

    val networkState = MutableLiveData<Pair<NetworkState?, NetworkState?>>()

    var isEmpty: Boolean = false

    private var sinceId: String? = null
    private var end = false
    private var running = false
    private var refreshId: String? = null
    private var nextId: String? = null

    init {
        onLoadData()
    }

    private fun onLoadData(since:String?=null ) {
        running = true
        CoroutineScope(Dispatchers.Default).launch {
            updateState(since, NetworkState.LOADING)
            try {
                loadData(since)?.run {
                    this@ChannelPaging.sinceId = since
                    if (since==null) {
                        isEmpty = this.isNullOrEmpty()
                    }
                    this.takeIf { it.isNotEmpty() }?.run {
                        addList(this)
                        checkContinuteRefresh(this)
                    } ?: run {
                        end = true
                        refreshComplete()
                    }
                    updateState(since, NetworkState.LOADED)
                } ?: run {
                    //error
                    updateState(since, NetworkState.ERROR)
                    refreshComplete()
                }
            } catch (e: Exception) {
                updateState(since, NetworkState.ERROR)
                refreshComplete()
            }
            running = false
        }
    }

    private fun updateState(sinceId: String? , state:NetworkState ){
        if (sinceId==null) {
            networkState.postValue(Pair(state, null))
        }else{
            networkState.postValue(Pair(networkState.value?.first, state))
        }
    }

    private fun addList(channels: MutableList<Channel>) {
        CoroutineScope(Dispatchers.IO).launch {
            appDataBase.insertChannels(workspaceId,channels )
        }
    }

    private suspend fun loadData(sinceId:String?): MutableList<Channel>? {
        return kotlin.runCatching {
            if(sinceId==null){
                loadSeenState()
            }
            api.channels(token, workspaceId, pagingSize, sinceId)?.run {
                nextId =  this.docs?.lastOrNull()?.channelId?.takeIf { it.isNotEmpty() && this.hasNextPage==true }
                end = this.hasNextPage==false
                (this.docs?.takeIf { it.isNotEmpty() }
                    ?: mutableListOf()).onEach { channel ->
//                    channel.members = loadMemberOfChannel(token = token, channel)
                }
            }
        }.getOrNull()
    }

    private suspend fun loadSeenState() {
        kotlin.runCatching {
            readStateApi.lastSeen(token, workspaceId)?.run{
                Strings.log("loadSeenState ", this)
                this.mapNotNull {
                    ReadStateTable(
                        channelId = it.channelId?:"",
                        userId = it.userId?:"",
                        newMsg = it.totalNewMessages
                    ).takeIf { it.channelId.isNotEmpty() }
                }.takeIf { it.isNotEmpty() }?.toMutableList()?.run {
                    appDataBase.readStateDao().inserts(this)
                }
            }
        }
    }

    private suspend fun loadMemberOfChannel(token: String, channel: Channel): MutableList<Member> {
        return kotlin.runCatching {
            val result = mutableListOf<Member>()
            var page: Int? = 1
            while (page != null) {
                val response = memberApi.getMemberPaging2(
                    token = token,
                    workspaceId = channel.workspaceId ?: "",
                    channelId = channel.channelId ?: "",
                    page = page,
                    limit = 20
                )
                //todo next page
                result.addAll(response?.docs ?: mutableListOf<Member>())
                response?.takeIf { it.isHasNextPage }?.run {
                    page = (page?:1) + 1
                } ?: run {
                    page = null
                }
            }
            result
        }.getOrElse {
            mutableListOf<Member>()
        }
    }

    fun refresh() {
        CoroutineScope(Dispatchers.Default).launch {
            while (running) {
                delay(100)
            }
            refreshId = sinceId
            onLoadData()
        }
    }

    private fun checkContinuteRefresh(mutableList: MutableList<Channel>) {
        refreshId?.takeIf { it.isNotEmpty() }?.run {
            mutableList.find { TextUtils.equals(this, it.channelId) }?.run {
                //complete
                refreshComplete()
            } ?: run {
                //not complete next page
                nextId?.takeIf { it.isNotEmpty() }?.run {
                    onLoadData(this)
                }
            }
        }
    }

    private fun refreshComplete() {
        //todo refresh complete
        refreshId = null
    }

    fun retry() {
        //todo onRetry
        onItemAtEndLoaded()
    }

    fun onItemAtEndLoaded() {
        CoroutineScope(Dispatchers.Default).launch {
            if (!running && !end && !nextId.isNullOrEmpty()) {
                onLoadData(nextId)
            }
        }
    }
}