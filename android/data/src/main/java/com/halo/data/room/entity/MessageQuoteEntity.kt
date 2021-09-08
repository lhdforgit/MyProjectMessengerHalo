/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.entity

import android.content.Context
import android.text.TextUtils
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.halo.data.common.utils.Strings
import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.invite.InviteInfo
import com.halo.data.entities.message.Metadata
import com.halo.data.room.table.MemberTable
import com.halo.data.room.table.MentionTable
import com.halo.data.room.table.MessageTable
import com.halo.data.room.type.MessageStatus
import com.halo.data.room.type.MessageType

class MessageQuoteEntity(
    @Embedded
    private var messageTable: MessageTable,

    @Relation(parentColumn = "msgId", entityColumn = "msgId", entity = MentionTable::class)
    var mentions: MutableList<MentionEntity>? = null,

    @Relation(
        parentColumn = "userInRoomId",
        entityColumn = "userInRoomId",
        entity = MemberTable::class,
        associateBy = Junction(MemberTable::class)
    )
    private var memberEntity: MemberEntity? = null

    ) {

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

    fun messageContent(context: Context?): String {
        return messageTable.content ?: ""
    }

    fun messageText(): String {
        return messageTable.content?.trim() ?: ""
    }

    fun messageBlockIds(): String {
        return ""
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

    fun lastUserIds(): String {
        return ""
    }

    fun reactionTypes(): String {
        return ""
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

    fun enableRevoke(ownerId: String): Boolean {
        return isSent() && isOwnerMessage(ownerId)
    }


    fun userName(): String {
        Strings.log("MessageQuoteEntity ", this)
        return memberEntity?.memberName() ?: ""
//        return ""
    }

    fun memberMentions(): MutableList<MemberEntity> {
        return mentions?.mapNotNull { it.memberEntity }?.toMutableList()?: mutableListOf()
    }
}