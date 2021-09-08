/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.message


import android.os.Handler
import androidx.lifecycle.*
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asFlow
import com.hahalolo.messager.MessengerController
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.hahalolo.messager.utils.Strings
import com.hahalolo.socket.SocketManager
import com.halo.data.entities.message.DeleteMessageBody
import com.halo.data.entities.reaction.ReactionBody
import com.halo.data.entities.reaction.Reactions
import com.halo.data.repository.channel.ChannelRepository
import com.halo.data.repository.invite.InviteRepository
import com.halo.data.repository.member.MemberRepository
import com.halo.data.repository.message.MessagePaging
import com.halo.data.repository.message.MessageRepository
import com.halo.data.repository.reaction.ReactionRepository
import com.halo.data.room.entity.ChannelDetailEntity
import com.halo.data.room.entity.MemberEntity
import com.halo.di.AbsentLiveData
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
class ChatMessageViewModel @Inject
internal constructor(
    private val channelRepository: ChannelRepository,
    private val messageRepository: MessageRepository,
    private val reactionRepository: ReactionRepository,
    private val memberRepository: MemberRepository,
    private val inviteRepository: InviteRepository,
    private val socketManager: SocketManager,
    appController: MessengerController,
) : AbsMessViewModel(appController) {

    fun workspaceId(): String {
        return "0"
    }

    fun channelId(): String {
        return channelId.value?:""
    }

    fun updateChannelId(channelId: String){
        this.channelId.value = channelId
    }

    val friendId = MutableLiveData<String>()
    val createFriendId = MutableLiveData<String>()
    private val channelId = MutableLiveData<String>()
    val msgOfchannelId = MutableLiveData<String>()
    val reactionBody = MutableLiveData<ReactionBody>()

    val deleteBody = MutableLiveData<DeleteMessageBody>()

    val socketState = socketManager.getSocketState()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val friendChannel = flowOf(friendId.asFlow().flatMapLatest { id ->
        channelRepository.findChannel(token(), "0", id?:"")
    }
    ).flattenMerge(2)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val createFriendChannel = flowOf(createFriendId.asFlow().flatMapLatest { id ->
        channelRepository.createChannel(token(), "0", id?:"")
    }
    ).flattenMerge(2)

    val channelDetailEntity : LiveData<ChannelDetailEntity> = Transformations.switchMap(channelId){
        channelRepository.getChannelDetailEntity(token = token(), channelId = it?:"" )
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val reactionResponse = flowOf(reactionBody.asFlow().flatMapLatest { body ->
        reactionRepository.reactionMessage(token = token(),
            body = body
        )
    }
    ).flattenMerge(2)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val deleteMsgResponse = flowOf(deleteBody.asFlow().flatMapLatest { body ->
        messageRepository.deleteMessage(ownerId = userIdToken(), body = body)
    }
    ).flattenMerge(2)

    val messagePaging : LiveData<MessagePaging> = Transformations.switchMap(msgOfchannelId) {
        messageRepository.messagePaging(
            token(),
            workspaceId = "0",
            channelId = it,
            ownerId = userIdToken()
        )
    }

    val messageState = Transformations.switchMap(messagePaging){
        it.networkState
    }

    val messageList : LiveData<MutableList<MessageEntity>> = Transformations.switchMap(messagePaging){
        it.listData
    }

    private val joinCode = MutableLiveData<String>()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val inviteJoinReponse = flowOf(joinCode.asFlow().flatMapLatest { code ->
        inviteRepository.inviteJoin(token = token(), code = code?:"")
    }
    ).flattenMerge(2)

    private val typingToken = MutableLiveData<String>()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val typingResponse = flowOf(typingToken.asFlow().flatMapLatest { token ->
        channelRepository.typing(token = token, channelId =  channelId())
    }
    ).flattenMerge(2)

    val mentionQuery = MutableLiveData<String>()

    val mentionResult : LiveData<MutableList<MemberEntity>> = Transformations.switchMap(mentionQuery){
        it?.run {
            channelRepository.mentionQuery(channelId = channelId(),
                ownerId = userIdToken(),
                query = this)
        }?: AbsentLiveData.create<MutableList<MemberEntity>>()
    }

    init {

    }

    //TODO NEW FUNTION
    fun loadMessageOfChannel(channelId: String) {
        if (!TextUtils.equals(msgOfchannelId.value, channelId)){
            msgOfchannelId.value = channelId
        }
    }

    fun onRemoveMessageError(messageEntity: MessageEntity) {

    }

    //TODO OLD FUNTION

    // update roomId for get list message of this room
    fun updateRoomId(roomId: String?) {

    }

    fun updateCreateRoom(friendId: String?, groupId: String?) {

    }

    fun onSeenMessage(messageId: String?) {

    }

    fun onReactionMessage(messageId: String?, type: String) {
        reactionBody.value = ReactionBody(workspaceId = "0",
            channelId = this.channelId.value?:"",
            messageId = messageId?:"",
            emoji = type
        )
    }

    fun onDeleteReaction(messageId: String?, type: String) {
        reactionBody.value = ReactionBody(workspaceId = "0",
            channelId = this.channelId.value?:"",
            messageId = messageId?:"",
            emoji = type,
            delete = true
        )
    }

    fun onDeleteMessage(messageId: String?) {
        deleteBody.value = DeleteMessageBody(
            token = token(),
            workspaceId = "0",
            channelId = this.channelId.value?:"",
            messageId = messageId
        )
    }

    fun onRevokeMessage(messageId: String?) {
        deleteBody.value = DeleteMessageBody(
            token = token(),
            workspaceId = "0",
            channelId = this.channelId.value?:"",
            messageId = messageId,
            revoke = true
        )
    }

    fun onCancelMessageError(message: MessageEntity?) {

    }

    //xóa list mesage , để lại 20 tin nhắn cuối
    fun clearRangeMessage() {

    }

    // Làm mới danh sách chat
    fun refresh() {
    }

    // Lấy thông tin group chat, nếu trước đó bị lỗi
    fun retry() {

    }

    fun openRoom() {
    }

    fun closeRoom() {
    }

    fun disableReceiverTyping() {
    }

    fun onStartTyping() {
        typingToken.value = token()
    }

    fun onStopTyping() {

    }

    //check to connect
    fun onResumeChatAct(){
    }

    fun onPauseChartAct() {
    }

    fun onFinishChatAct(){
    }


    class CreateRoom {
        var groupId: String? = null
        var friendId: String? = null
    }

   suspend fun convertMemberEntityReaction(reacts : Reactions): List<Pair<String, MutableList<MemberEntity>>>{
       val result = mutableListOf<Pair<String, MutableList<MemberEntity>>>()
       reacts.forEach {
           val members = memberRepository.getMemberList(channelId.value ?: "", it.value).value ?: mutableListOf()
           result.add(it.key to members)
       }
        return result
    }

    fun onInvitedChannel(code: String) {
        joinCode.value = code
    }
}
