/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.notification

import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.ChatHolderItem
import com.hahalolo.messager.chatkit.holder.HolderListener
import com.hahalolo.messager.chatkit.holder.MessageGroupType
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messager.utils.MsgSystemObject
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatNotificationItemBinding
import com.halo.common.utils.SpanBuilderUtil


class ChatNotificationHolderItem(
    binding: ChatNotificationItemBinding,
    chatListener: ChatListener,
    holderListener: HolderListener,
    imageLoader: ImageLoader,
    style: MessagesListStyle
) : ChatHolderItem<ChatNotificationItemBinding>(
    binding,
    chatListener,
    holderListener,
    imageLoader,
    style
) {

    companion object {
        fun create(
            parent: ViewGroup,
            chatListener: ChatListener,
            holderListener: HolderListener,
            imageLoader: ImageLoader, style: MessagesListStyle
        ): ChatNotificationHolderItem {
            val binding = DataBindingUtil.inflate<ChatNotificationItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.chat_notification_item,
                parent,
                false
            )
            return ChatNotificationHolderItem(
                binding,
                chatListener,
                holderListener,
                imageLoader,
                style
            )
        }
    }

    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
        val content = iMessage.messageContent(chatListener.ownerId())
        val paner = SpanBuilderUtil()
        paner.append(content)
        var clickContent: String? = null
        var clickableSpan: ClickableSpan? = null

        when (iMessage.messageText()) {
            MsgSystemObject.MSG_CHANGED_GROUP_PHOTO,
            MsgSystemObject.MSG_REMOVE_CHANGE_GROUP_AVATAR -> {
                clickContent = itemView.context.getString(R.string.chat_message_holder_action_change_group_avatar)
                clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        chatListener.onChangeGroupAvatar()
                    }
                }
            }
            MsgSystemObject.MSG_SET_NICKNAME,
            MsgSystemObject.MSG_REMOVE_NICK_NAME -> {
                clickContent = itemView.context.getString(R.string.chat_message_holder_action_change_nickname)
                clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        chatListener.onChangeNickName()
                    }
                }
            }
            MsgSystemObject.MSG_RENAME_CONVERSATION,
            MsgSystemObject.MSG_RENAME_GROUP,
            MsgSystemObject.MSG_REMOVE_GROUP_NAME -> {
                clickContent = itemView.context.getString(R.string.chat_message_holder_action_change_group_name)
                clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        chatListener.onChangeGroupName()
                    }
                }
            }
        }
        if (clickContent != null && clickableSpan != null) {
            paner.append(" ")
            paner.append("$clickContent", clickableSpan)
            binding.notificationTv.movementMethod = LinkMovementMethod.getInstance()
        }
        binding.notificationTv.text = paner.build()
    }

    override fun applyStyle(style: MessagesListStyle) {
        super.applyStyle(style)

        binding.notificationTv.apply {
            super.configureLinksBehavior(this)
        }
    }
}