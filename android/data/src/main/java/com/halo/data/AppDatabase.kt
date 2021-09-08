/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data

import android.text.TextUtils
import androidx.room.Database
import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.halo.constant.HaloConfig
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.message.Message
import com.halo.data.entities.message.MessageSendBody
import com.halo.data.room.dao.*
import com.halo.data.room.table.*
import com.halo.data.room.type.MessageStatus
import com.halo.data.room.type.MessageType
import com.halo.data.room.type.SaveType
import java.util.*

/**
 * Create by sbv
 * Create on 5/14/20
 * com.hahalolo.messenger.database.room
 */
@Database(
    entities = [
        ChannelTable::class,
        ReadStateTable::class,
        MemberTable::class,
        MessageTable::class,
        UserTable::class,
        AttachmentTable::class,
        SeenMessageTable::class,
        ParameterTable::class,
        MessageBlockTable::class,
        MentionTable::class],
    version = HaloConfig.MESSENGER_DATABASE_VERSION
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun readStateDao(): ReadStateDao
    abstract fun messageDao(): MessageDao
    abstract fun memberDao(): MemberDao
    abstract fun userDao(): UserDao
    abstract fun mentionDao(): MentionDao

    suspend fun insertChannels(workspaceId: String, channels: MutableList<Channel>) {
        val memberList = mutableListOf<MemberTable>()
        val userTables = mutableListOf<UserTable>()
        val channelList = channels.mapNotNull { channel ->
            channel.members?.forEach { member ->
                memberList.add(MemberTable(
                    memberId = member.memberId ?: "",
                    userId = member.userId ?: "",
                    channelId = member.channelId ?: "",
                    workspaceId = member.workspaceId ?: "",

                    ).apply {
                    nickName = member.nickName ?: ""
                    status = member.status ?: ""
                    createdAt = member.createTime()
                    updatedAt = member.updateTime()
                    deletedAt = member.deleteTime()
                })
                userTables.find { TextUtils.equals(it.userId, member.userId) }?.run {
                    //exited
                } ?: member.user?.let { user ->
                    userTables.add(UserTable(
                        userId = member.userId ?: "",
                        providerId = user.providerId ?: "",
                        userName = user.userName()
                    ).apply {
                        avatar = user.avatar
                        status = user.userStatus()
                    })
                }
            }
            ChannelTable(
                id = channel.channelId ?: "",
                workspaceId = channel.workspaceId ?: "",
                name = channel.name ?: "",
                createAt = channel.timeCreate(),
                updateAt = channel.timeUpdate(),
            ).apply {
                avatar = channel.avatar
                lastMessageId = channel.lastMessageId
                lastMessageJson = channel.lastMessageJson()

                recipientJson = channel.recipientJson()

                ownerId = channel.owner
                recipientId = channel.recipientId
                dmId = channel.dmId
                membersCount = channel.membersCount
                deleteAt = channel.timeDelete()
                saveType = SaveType.NEW
                bubbleSave = SaveType.NEW
            }.takeIf { it.id.isNotEmpty() }
        }.toMutableList()
        runInTransaction {
            userDao().insertUsers(userTables)
            memberDao().insertMembers(memberList)
            channelDao().insertChannels(workspaceId, channelList)
        }
    }

    fun insertMessages(channelId: String, ownerId: String, newList: MutableList<Message>) {
        //clear old cache
        messageDao().deleteMessageCache(newList.mapNotNull { it.ref?.takeIf { it.isNotEmpty() } }
            .toMutableList())
        val mentionTables = mutableListOf<MentionTable>()
        newList.mapNotNull { message ->

            message.mentions?.takeIf { it.isNotEmpty() }?.forEach {
                mentionTables.add(
                    MentionTable(
                        channelId = message.channelId?:"",
                        msgId = message.messageId ?: "",
                        userId = it
                    )
                )
            }

            MessageTable(
                msgId = message.messageId ?: "",
                userId = message.userId ?: "",
                workspaceId = message.workspaceId ?: "",
                channelId = message.channelId ?: "",
                clientId = message.ref ?: "",
                timeCreate = message.timeCreate(),
                timeUpdate = message.timeUpdate()
            ).apply {
                // TYPE
                if (message.isRevoked()) {
                    this.type = MessageType.REVOKED
                } else if (message.isHided(ownerId)) {
                    this.type = MessageType.DELETED
                } else {
                    this.type = message.messageType
                }

                //CONTENT
                this.content = message.content?.trim()

                this.attachmentType = message.attachmentType
                this.attachmentJson = Gson().toJson(message.attachments)

                this.reactionJson = Gson().toJson(message.reactions)

                this.embedJson = Gson().toJson(message.embed)
                this.inviteJson = message.inviteInfo?.run {
                    Gson().toJson(this)
                }

                this.quoteMsgId = message.originMessageId

                //STATUS
                this.status = MessageStatus.SENDED
                this.msgSave = SaveType.NEW
                this.msgBubbleSave = messageDao().bubbleSaveType(message.messageId ?: "")
            }.takeIf { it.msgId.isNotEmpty() }
        }.toMutableList().run {
            runInTransaction {
                mentionDao().inserts(mentionTables)
                messageDao().insertMessages(channelId, this)
            }
        }
    }


    fun refreshMessage(ownerId: String, quoteTable: MessageTable?, message: Message) {
        val mentionTables = mutableListOf<MentionTable>()

        message.mentions?.takeIf { it.isNotEmpty() }?.forEach {
            mentionTables.add(
                MentionTable(
                    channelId = message.channelId?:"",
                    msgId = message.messageId ?: "",
                    userId = it
                )
            )
        }

        MessageTable(
            msgId = message.messageId ?: "",
            userId = message.userId ?: "",
            workspaceId = message.workspaceId ?: "",
            channelId = message.channelId ?: "",
            clientId = message.ref ?: "",
            timeCreate = message.timeCreate(),
            timeUpdate = message.timeUpdate()
        ).apply {

            //TYPE
            if (message.isRevoked()) {
                this.type = MessageType.REVOKED
            } else if (message.isHided(ownerId)) {
                this.type = MessageType.DELETED
            } else {
                this.type = message.messageType
            }

            //CONTENT
            this.content = message.content

            this.attachmentType = message.attachmentType
            this.attachmentJson = Gson().toJson(message.attachments)

            this.embedJson = Gson().toJson(message.embed)
            this.inviteJson = message.inviteInfo?.run {
                Gson().toJson(this)
            }

            this.quoteMsgId = message.originMessageId

            //STATUS
            this.status = MessageStatus.SENDED
            this.msgSave = quoteTable?.msgSave
            this.msgBubbleSave = quoteTable?.msgBubbleSave
        }.run {
            runInTransaction {
                mentionDao().inserts(mentionTables)
                messageDao().insertMessage(this)
            }
        }
    }

    fun insertNewMessage(ownerId: String, message: Message) {
        message.ref?.takeIf { it.isNotEmpty() }?.run {
            messageDao().deleteMessageCache(this)
        }
        removeUserTyping(message.channelId ?: "", message.userId ?: "")

        val mentionTables = mutableListOf<MentionTable>()

        message.mentions?.takeIf { it.isNotEmpty() }?.forEach {
            mentionTables.add(
                MentionTable(
                    channelId = message.channelId?:"",
                    msgId = message.messageId ?: "",
                    userId = it
                )
            )
        }

        MessageTable(
            msgId = message.messageId ?: "",
            userId = message.userId ?: "",
            workspaceId = message.workspaceId ?: "",
            channelId = message.channelId ?: "",
            clientId = message.ref ?: "",
            timeCreate = message.timeCreate(),
            timeUpdate = message.timeUpdate()
        ).apply {

            //TYPE
            if (message.isRevoked()) {
                this.type = MessageType.REVOKED
            } else if (message.isHided(ownerId)) {
                this.type = MessageType.DELETED
            } else {
                this.type = message.messageType
            }

            //CONTENT
            this.content = message.content

            this.attachmentType = message.attachmentType
            this.attachmentJson = Gson().toJson(message.attachments)

            this.embedJson = Gson().toJson(message.embed)
            this.inviteJson = message.inviteInfo?.run {
                Gson().toJson(this)
            }

            this.quoteMsgId = message.originMessageId

            //STATUS
            this.status = MessageStatus.SENDED
            this.msgSave = SaveType.NEW
            this.msgBubbleSave = SaveType.NEW
        }.run {
            runInTransaction {
                mentionDao().inserts(mentionTables)
                messageDao().insertMessage(this)
            }
        }
    }

    fun insertMessageSending(body: MessageSendBody) {
        val lastTime =
            Math.max(((messageDao().lastTime(body.channelId) ?: 0) + 1), System.currentTimeMillis())

        val mentionTables = mutableListOf<MentionTable>()

        val messageId:String = UUID.randomUUID().toString()

        body.mentions?.takeIf { it.isNotEmpty() }?.forEach {
            mentionTables.add(
                MentionTable(
                    channelId = body.channelId,
                    msgId = messageId,
                    userId = it
                )
            )
        }

        MessageTable(
            msgId = messageId,
            userId = body.ownerId,
            workspaceId = body.workspaceId,
            channelId = body.channelId,
            clientId = body.ref,
            timeCreate = lastTime,
            timeUpdate = lastTime,
            timeSend = lastTime
        ).apply {
            this.type = MessageType.TEXT

            this.content = body.content

            body.attachmentModels()?.takeIf { it.isNotEmpty() }?.let { attachmentModels ->
                this.attachmentType = body.attachmentType()
                this.attachmentJson = Gson().toJson(attachmentModels)
            }

            this.status = MessageStatus.SENDDING

            this.quoteMsgId = body.quoteMessageId

            this.msgSave = SaveType.NEW
            this.msgBubbleSave = SaveType.NEW
        }.run {
            runInTransaction {
                mentionDao().inserts(mentionTables)
                messageDao().insertMessage(this)
            }
        }
    }

    fun insertUserTyping(workspaceId: String, channelId: String, userId: String) {
        val lastTime =
            Math.max(((messageDao().lastTime(channelId) ?: 0) + 1), System.currentTimeMillis())
        MessageTable(
            msgId = channelId + userId,
            userId = userId,
            workspaceId = workspaceId,
            channelId = channelId,
            clientId = channelId + userId,
            timeCreate = lastTime,
            timeUpdate = lastTime,
            timeSend = lastTime
        ).apply {
            this.type = MessageType.TEXT
            this.status = MessageStatus.TYPING
            this.msgSave = SaveType.NEW
            this.msgBubbleSave = SaveType.NEW
        }.run {
            messageDao().insertMessage(this)
        }
    }

    fun removeUserTyping(channelId: String, userId: String) {
        messageDao().deleteMessageCache(channelId + userId)
    }

}