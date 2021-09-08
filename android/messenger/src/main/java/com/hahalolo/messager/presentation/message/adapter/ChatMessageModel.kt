package com.hahalolo.messager.presentation.message.adapter

import android.text.TextUtils
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.type.MessageStatus
import com.halo.data.room.type.MessageType
import java.util.*
import kotlin.math.abs

class ChatMessageModel(
    var data: MessageEntity,
    before: MessageEntity?,
    after: MessageEntity?,
    ownerId: String
) {
    var groupType: ChatMessageGroupType = ChatMessageGroupType.ONLY
    val isOutComing: Boolean

    init {
        isOutComing = TextUtils.equals(data.userId(), ownerId)
        val beforIsGr = isOfGroup(before, data)
        val afterIsGr = isOfGroup(after, data)
        if (beforIsGr && afterIsGr) {
            groupType = ChatMessageGroupType.CENTER
        } else if (beforIsGr) {
            groupType = ChatMessageGroupType.TOP
        } else if (afterIsGr) {
            groupType = ChatMessageGroupType.LAST
        } else {
            groupType = ChatMessageGroupType.ONLY
        }
    }

    private fun isOfGroup(m: MessageEntity?, m2: MessageEntity?): Boolean {
        if (m != null && m2 != null     // 2 tin nhanc khac null
            && isOneUserSend(m, m2)     // cung la do  1 nguoi gui
            && isMessageContent(m)     // tin nhan 2 cung la tin nhan noi dung
            && isMessageContent(m2)     // tin nhan 2 cung la tin nhan noi dung
            && isIntimeGroup(m, m2)     // 2 tin gửi ko cách nhau 1 phút
        ) {
            return true
        }
        return false
    }

    private fun isOneUserSend(m: MessageEntity?, m2: MessageEntity?): Boolean {
        return TextUtils.equals(m?.userId(), m2?.userId())
    }

    private fun isIntimeGroup(m: MessageEntity?, m2: MessageEntity?): Boolean {
        // trong cùng 1 khoảng thời gian
        return abs((m?.messageTime() ?: 0) - (m2?.messageTime() ?: 0)) < 50000
    }

    private fun isMessageContent(msg: MessageEntity): Boolean {
        //Cùng là tin nhắn nội dung
        return !TextUtils.equals(msg.messageType(), MessageType.SYSTEM)
                && msg.messageStatus() != MessageStatus.TYPING
    }

    /*DATA CONTENT */
    fun id(): String {
        return data.messageId()
    }

    fun headerTimeId(): Long {
        val calender = Calendar.getInstance()
        calender.timeInMillis = (data.messageTime() ?: 0)
        val day = calender.get(Calendar.DAY_OF_MONTH)
        val month = calender.get(Calendar.MONTH)
        val year = calender.get(Calendar.YEAR)
        return (day * 1000000 + month * 10000 + year).toLong()
    }
}