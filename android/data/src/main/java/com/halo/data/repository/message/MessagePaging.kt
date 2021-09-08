package com.halo.data.repository.message

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.halo.data.AppDatabase
import com.halo.data.api.message.MessageRestApi
import com.halo.data.common.paging.NetworkState
import com.halo.data.common.utils.Strings
import com.halo.data.entities.message.Message
import com.halo.data.room.entity.MessageEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessagePaging(
    private val api: MessageRestApi,
    private val messageInfo: suspend (message: Message) -> Unit,
    private val refreshMessage: suspend (token: String, ownerId: String, workspaceId:String, channelId:String, messageId: String) -> Unit,
    private val appDataBase: AppDatabase,
    private val token: String,
    private val ownerId: String,
    private val workspaceId: String,
    private val channelId: String,
    private val pagingSize: Int
) {
    val listData: LiveData<MutableList<MessageEntity>> =
        appDataBase.messageDao().messageList(workspaceId, channelId)

    val networkState = MutableLiveData<Pair<NetworkState?, NetworkState?>>()

    var isEmpty: Boolean = false

    private var sinceId: String? = null
    private var end = false
    private var running = false
    private var refreshId: String? = null
    private var nextId: String? = null

    init {
        Strings.log("MessagePaging INIT ")
        onLoadData()
    }

    private fun onLoadData(since: String? = null) {

        running = true
        CoroutineScope(Dispatchers.Default).launch {
            updateState(since, NetworkState.LOADING)
            kotlin.runCatching {
                loadData(since)?.run {
                    this@MessagePaging.sinceId = since
                    if (since == null) {
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
            }.getOrElse {
                updateState(since, NetworkState.ERROR)
                refreshComplete()
            }
            running = false
        }
    }

    private fun updateState(since: String?, state: NetworkState) {
        if (since == null) {
            networkState.postValue(Pair(state, null))
        } else {
            networkState.postValue(Pair(networkState.value?.first, state))
        }
    }

    private fun addList(newList: MutableList<Message>) {
        Strings.log("MessagePaging addList ", newList)
        CoroutineScope(Dispatchers.IO).launch {
            appDataBase.runInTransaction {
                appDataBase.insertMessages(channelId, ownerId, newList)
            }
        }
    }

    private suspend fun loadData(since: String?): MutableList<Message>? {
        Strings.log("MessagePaging loadData " + since)
        return kotlin.runCatching {
            api.messages(token, workspaceId, channelId, pagingSize, since)?.run {
                Strings.log("insertNewMessage loadData ", this)
                nextId = this.docs?.lastOrNull()?.messageId?.takeIf {
                    it.isNotEmpty()
                            && this.hasNextPage == true
                }
                end = this.hasNextPage == false

                (this.docs?.takeIf { it.isNotEmpty() }
                    ?: mutableListOf<Message>()).onEach { message ->
                    //get message info
                    messageInfo.invoke(message)
                    Strings.log("insertNewMessage loadData 1 ")
                    message.originMessageId?.takeIf { quoteMsgId ->
                        quoteMsgId.isNotEmpty()
                                && docs?.find { TextUtils.equals(it.messageId, quoteMsgId) } == null
                    }?.run {
                        Strings.log("insertNewMessage loadData 2 ")
                        //quote messageId, and this msg not in docs - to refresh this msg
                        refreshMessage.invoke( token, ownerId, workspaceId, channelId, this)
                        Strings.log("insertNewMessage loadData 3 ")
                    }
                }
            }
        }.getOrNull()
    }

    fun refresh() {
        Strings.log("MessagePaging refresh ")
        CoroutineScope(Dispatchers.Default).launch {
            while (running) {
                delay(100)
            }
            refreshId = sinceId
            onLoadData()
        }
    }

    private fun checkContinuteRefresh(mutableList: MutableList<Message>) {
        refreshId?.takeIf { it.isNotEmpty() }?.run {
            Strings.log("MessagePaging checkContinuteRefresh ")
            mutableList.find { TextUtils.equals(this, it.messageId) }?.run {
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
        Strings.log("MessagePaging refreshComplete ")
        refreshId = null
    }

    fun retry() {
        //todo onRetry
        onItemAtEndLoaded()
    }

    fun onItemAtEndLoaded() {
        Strings.log("MessagePaging onItemAtEndLoaded running "+ running)
        Strings.log("MessagePaging onItemAtEndLoaded end "+ end)
        Strings.log("MessagePaging onItemAtEndLoaded nextId "+ nextId)
        CoroutineScope(Dispatchers.Default).launch {
            while (running) {
                Strings.log("MessagePaging onItemAtEndLoaded running...... ")
                delay(100)
            }
            if (!running && !end && !nextId.isNullOrEmpty()) {
                onLoadData(nextId)
            }
        }
    }
}