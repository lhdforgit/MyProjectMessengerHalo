/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.entity

import android.content.Context
import android.text.TextUtils
import androidx.room.Embedded
import androidx.room.Relation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.halo.data.common.utils.Strings
import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.invite.InviteInfo
import com.halo.data.entities.message.Message
import com.halo.data.entities.message.Metadata
import com.halo.data.entities.reaction.Reactions
import com.halo.data.room.table.MemberTable
import com.halo.data.room.table.MentionTable
import com.halo.data.room.table.MessageTable
import com.halo.data.room.type.MessageStatus
import com.halo.data.room.type.MessageType
import com.halo.data.room.type.SystemType

class MessageEntity(
    @Embedded
    private var messageTable: MessageTable,

    @Relation(
        parentColumn = "userInRoomId",
        entityColumn = "userInRoomId",
        entity = MemberTable::class
    )
    private var memberEntity: MemberEntity? = null,

    @Relation(parentColumn = "msgId",
        entityColumn = "msgId",
        entity = MentionTable::class)
    private var mentions: MutableList<MentionEntity>? = null,

    @Relation(
        parentColumn = "quoteMsgId",
        entityColumn = "msgId",
        entity = MessageTable::class
    )
    var quoteMessage: MessageQuoteEntity? = null

    ) {

    init {

    }

    fun memberMentions():MutableList<MemberEntity>{
        return mentions?.mapNotNull { it.memberEntity }?.toMutableList()?: mutableListOf()
    }

    fun mentionIds():MutableList<String>{
        return mentions?.mapNotNull { it.memberEntity?.userId() }?.toMutableList()?: mutableListOf()
    }

    fun quoteMsgId():String?{
        return messageTable.quoteMsgId
    }

    fun userId(): String {
        return messageTable.userId ?: ""
    }

    fun messageType(): String {
        return messageTable.type ?: ""
    }

    fun messageTime(): Long? {
        return messageTable.msgTime
    }

    fun messageSentAt(): Long? {
        return messageTable.timeSend
    }

    fun messageCreateAt(): Long? {
        return messageTable.timeCreate
    }

    fun isOwnerMessage(userId: String): Boolean {
        return TextUtils.equals(userId, messageTable.userId)
    }

    fun messageContent(ownerId: String): String {
        Strings.log("MessageContentUtils messsageContent ", this)
        return MessageContentUtils.messageContent(ownerId, this)
    }

    fun messageText(): String {
        return messageTable.content?.trim() ?: ""
    }

    fun messageStatus(): Int {
        return messageTable.status ?: -1
    }

    fun messageId(): String {
        return messageTable.msgId
    }

    fun isSent(): Boolean {
        return messageTable.status == MessageStatus.SENDED
    }

    fun isSending(): Boolean {
        return messageTable.status == MessageStatus.SENDDING
    }

    fun isSendError(): Boolean {
        return messageTable.status == MessageStatus.ERROR
    }

    fun isDownloading(): Boolean {
        return messageTable.isDownloading
    }

    fun reactionTypes(): String {
        return reactionEntity()?.toString()?:""
    }

    fun isEdit(): Boolean {
        return messageTable.timeUpdate != null && messageTable.timeUpdate != messageTable.timeCreate
    }

    fun isEdited(): Boolean {
        return messageTable.timeUpdate != null && messageTable.timeUpdate != messageTable.timeCreate
    }

    fun isDeleted(): Boolean {
        return messageType() == MessageType.DELETED
    }

    fun isRemoved(): Boolean {
        return messageType() == MessageType.REVOKED
    }

    fun saveType(): String {
        return messageTable.msgSave ?: ""
    }

    fun bubbleSave(): String {
        return messageTable.msgBubbleSave ?: ""
    }

    fun isCreateGroupMsg(): Boolean {
        return TextUtils.equals(messageTable.content, SystemType.CREATE_CHANNEL)
    }

    fun isSystem(): Boolean {
        return TextUtils.equals(messageTable.type, MessageType.SYSTEM)
    }

    private fun countQuoteBlock(): Int {
        return 0
    }

    // cho phép edit
    fun isEnableEdit(): Boolean {
        // nếu có ít nhât 1 quote block , cho phép edit message
        return TextUtils.equals(
            messageType(),
            MessageType.TEXT
        ) && countQuoteBlock() <= 1 && isSent()
    }

    fun isEnableQuote(): Boolean {
        // nếu có ít nhất 1 quote block, cho phép quote thí message
        return isSent()
    }

    fun canDropLike(userId: String): Boolean {
        return false
    }

    fun reactionEntity(): Reactions? {
        val entity = Gson().fromJson(messageTable.reactionJson, Reactions::class.java)
        return entity?.takeIf { it.haveReaction() }
    }

    fun attachmentType(): String {
        return messageTable.attachmentType ?: ""
    }

    fun attachment(): MutableList<Attachment> {
        kotlin.runCatching {
            val listType = object :
                TypeToken<MutableList<Attachment>>() {}.type
            return Gson().fromJson(messageTable.attachmentJson, listType)
        }.getOrElse { return mutableListOf() }
    }

    private fun embeds(): MutableList<Message.Embed> {
        kotlin.runCatching {
            val listType = object :
                TypeToken<MutableList<Message.Embed>>() {}.type
            return Gson().fromJson(messageTable.embedJson, listType)
        }.getOrElse { return mutableListOf() }
    }

    fun embedData(): Message.Embed? {
        return embeds().firstOrNull()?.takeIf { it.meta() != null }
    }

    fun metadata(): Metadata? {
        return kotlin.runCatching {
            messageTable.metadataJson?.takeIf { it.isNotEmpty() }?.run {
                Gson().fromJson(messageTable.metadataJson, Metadata::class.java)
            }
        }.getOrNull()
    }

    fun inviteInfo(): InviteInfo? {
        return kotlin.runCatching {
            messageTable.inviteJson?.takeIf { it.isNotEmpty() }?.run {
                Gson().fromJson(messageTable.inviteJson, InviteInfo::class.java)
            }
        }.getOrNull()
    }

    fun userName(): String {
        return memberEntity?.memberName() ?: ""
    }

    fun userAvatar(): String {
        return memberEntity?.memberAvatar() ?: ""
    }

    fun enableRevoke(ownerId: String): Boolean {
        return isSent() && isOwnerMessage(ownerId)
    }

    fun enableDelete(ownerId: String): Boolean {
        return isSent()
    }
}