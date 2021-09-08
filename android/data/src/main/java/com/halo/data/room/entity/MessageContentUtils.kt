package com.halo.data.room.entity

import com.halo.data.common.utils.Strings
import com.halo.data.entities.message.Message
import com.halo.data.room.type.AttachmentType
import com.halo.data.room.type.MessageContentType

object MessageContentUtils {

    fun messageContent(
        ownerId: String,
        msg: MessageEntity
    ): String {
        return messageContent(
            isOwner = msg.isOwnerMessage(ownerId),
            memberName = msg.userName(),
            msgContent = msg.messageText(),
            attachmentType = msg.attachmentType(),
            attachmentCount = msg.attachment().size
        )
    }

    fun lastMessageContent(
        isOwner: Boolean,
        member: MemberEntity?,
        msg: Message
    ): String {
        val memberName = member?.memberName() ?: msg.actorName()
        return messageContent(
            isOwner = isOwner,
            memberName = memberName,
            msgContent = msg.content ?: "",
            attachmentType = msg.attachmentType,
            attachmentCount = msg.attachments?.size?:0
        )
    }

    fun lastMessageContent(
        ownerId: String,
        member: MemberEntity?,
        msg: Message
    ): String {
        return lastMessageContent(msg.isOwner(ownerId), member, msg )
    }

    fun messageContent(
        isOwner: Boolean,
        memberName: String,
        msgContent: String? = null,
        attachmentType: String? = null,
        attachmentCount: Int = 0
    ): String {
        return when (msgContent) {
            MessageContentType.SAY_HI -> {
                if (isOwner) {
                    "Bạn đã gửi 1 lời chào!"
                } else {
                    "$memberName đã gửi 1 lời chào!"
                }
            }
            MessageContentType.SEND_GIF -> {
                if (isOwner) {
                    "Bạn đã gửi 1 tin nhắn động"
                } else {
                    "$memberName đã gửi 1 tin nhắn động"
                }
            }
            MessageContentType.SEND_STICKER -> {
                if (isOwner) {
                    "Bạn đã gửi 1 nhãn dán"
                } else {
                    "$memberName đã gửi 1 nhãn dán"
                }
            }
            MessageContentType.SEND_MEDIA -> {
                val count = attachmentCount.takeIf { it > 1 } ?: 1
                when (attachmentType) {
                    AttachmentType.IMAGE -> {
                        if (isOwner) {
                            "Bạn đã gửi $count hình ảnh"
                        } else {
                            "$memberName đã gửi $count hình ảnh!"
                        }

                    }
                    AttachmentType.VIDEO -> {
                        if (isOwner) {
                            "Bạn đã gửi $count video"
                        } else {
                            "$memberName đã gửi $count video!"
                        }
                    }
                    else -> {
                        if (isOwner) {
                            "Bạn đã gửi $count tập tin"
                        } else {
                            "$memberName đã gửi $count tập tin"
                        }
                    }
                }
            }
            MessageContentType.CREATE_GROUP -> {
                if (isOwner) {
                    "Bạn đã tạo nhóm!"
                } else {
                    "$memberName đã tạo nhóm!"
                }
            }
            MessageContentType.NEW_INVITATION -> {
                if (isOwner) {
                    "Bạn đã gửi lời mời tham gia nhóm tới $memberName"
                } else {
                    "Bạn có 1 lời mời tham gia nhóm!"
                }
            }

            MessageContentType.WANT_CONNECT -> {
                if (isOwner) {
                    "Bạn đã gửi yêu cầu kết nối"
                } else {
                    "$memberName đã gửi yêu cầu kết nối"
                }
            }

            MessageContentType.JOIN_GROUP -> {
                if (isOwner) {
                    "Bạn đã tham gia nhóm"
                } else {
                    "$memberName đã tham gia nhóm"
                }
            }
            else -> {
                Strings.log("messageContent ", msgContent)
                msgContent?:""
            }
        }
    }
}