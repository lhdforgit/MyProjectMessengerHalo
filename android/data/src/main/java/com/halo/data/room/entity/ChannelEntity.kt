/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.room.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.google.gson.Gson
import com.halo.data.entities.message.Message
import com.halo.data.entities.user.User
import com.halo.data.room.table.ChannelTable
import com.halo.data.room.table.ReadStateTable

class ChannelEntity(
    @Embedded
    private var channelTable: ChannelTable,

    @Relation(parentColumn = "channelId", entityColumn = "channelId", entity = ReadStateTable::class)
    private var readStateTable: ReadStateTable? = null
) {

    fun recipient():User? {
        return  kotlin.runCatching {
            channelTable.recipientJson?.takeIf { it.isNotEmpty() }?.run {
                Gson().fromJson(channelTable.recipientJson, User::class.java)
            }
        }.getOrNull()
    }

    //TODO NEW FUNTION
    fun channelId(): String {
        return channelTable.id
    }

    fun isPrivateChannel(): Boolean {
        return channelTable.dmId?.isNotEmpty() == true
    }

    fun channelName(): String {
        return recipient()?.takeIf { isPrivateChannel() }?.run {
            this.userName()
        }?: kotlin.run {
            channelTable.name?.takeIf { it.isNotEmpty() } ?:""
        }
    }

    fun lastMessageContent(ownerId: String): String {
        return kotlin.runCatching {
            Gson().fromJson(channelTable.lastMessageJson, Message::class.java)?.run {
                return MessageContentUtils.lastMessageContent(
                    ownerId,
                    null,
                    this
                )
            } ?: ""
        }.getOrElse { "" }
    }

    fun channelAvatar():String{
        return channelTable.avatar?:""
    }

    fun memberCount(): Int {
        return 0
    }

    fun isFriendOnline(): Boolean {
        return false
    }

    //TODO LAST FUNTION

    fun contentLastMsg(): String {
        return ""
    }

    fun isGroup(): Boolean {
        return false
    }

    fun roomType(): String {
        return ""
    }

    fun roomNameSetting(): String {

        return ""
    }

    fun countNewMessage(): Int {
        return readStateTable?.newMsg?:0
    }

    fun roomAvatars(): MutableList<String> {
        val listAvatars = mutableListOf<String>()

        return listAvatars
    }

    fun roomTime(): Long? {
        return null
    }

    fun saveType(): String {
        return ""
    }

    fun isHaveAvatarGroup(): Boolean {

        return false
    }

    fun getAvatarGroup(): String {
        return ""
    }

    fun roomSave(): String {
        return channelTable.saveType?:""
    }

    fun bubbleSave(): String {
        return channelTable.bubbleSave?:""
    }
}