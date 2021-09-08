package com.halo.data.room.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.halo.data.room.table.MentionTable
import com.halo.data.room.table.MessageBlockTable

class MessageBlockEntity(
    @Embedded
    private var messageBlockTable: MessageBlockTable,
    @Relation(parentColumn = "bloId", entityColumn = "bloId")
    var mentions: MutableList<MentionTable>? = null
) {

    fun msgId(): String {
        return messageBlockTable.msgId
    }

    fun blockType(): String {
        return messageBlockTable.type ?: ""
    }

    fun blockText(): String {
        return messageBlockTable.text ?: ""
    }

    fun blockKey(): String {
        return messageBlockTable.key ?: ""
    }

    fun blockId(): String {
        return messageBlockTable.id
    }

    fun mentionIds(): String {
        var result = ""
        mentions?.forEach { result += it.userId }
        return result
    }
}