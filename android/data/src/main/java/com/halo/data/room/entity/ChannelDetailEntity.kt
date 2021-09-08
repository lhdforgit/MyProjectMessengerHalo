package com.halo.data.room.entity

import android.text.TextUtils
import androidx.room.Embedded
import androidx.room.Relation
import com.google.gson.Gson
import com.halo.data.entities.user.User
import com.halo.data.room.table.ChannelTable
import com.halo.data.room.table.MemberTable

class ChannelDetailEntity(
    @Embedded
    private var channelTable: ChannelTable,

    @Relation(parentColumn = "channelId", entityColumn = "channelId", entity = MemberTable::class)
    private var members: MutableList<MemberEntity>? = null

){

    private fun recipient(): User? {
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

    fun channelAvatar():String{
        return channelTable.avatar?:""
    }

    fun channelName(): String {
        return recipient()?.takeIf { isPrivateChannel() }?.run {
            this.userName()
        }?: kotlin.run {
            channelTable.name?.takeIf { it.isNotEmpty() } ?:""
        }
    }

    fun memberCount(): Int {
        return members?.size?:0
    }

    fun isFriendOnline(): Boolean {
        return members?.find { TextUtils.equals(it.userId(), channelTable.recipientId) }?.isOnline()?:false
    }
}
