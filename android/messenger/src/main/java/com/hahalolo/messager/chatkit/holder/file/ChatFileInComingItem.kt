/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.file

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.ChatInComingItem
import com.hahalolo.messager.chatkit.holder.HolderListener
import com.hahalolo.messager.chatkit.holder.MessageGroupType
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatFileIncomingItemBinding

class ChatFileInComingItem(binding: ChatFileIncomingItemBinding,
                           chatListener: ChatListener,
                           holderListener: HolderListener,
                           imageLoader: ImageLoader,
                           style: MessagesListStyle
)
    : ChatInComingItem<ChatFileIncomingItemBinding>(binding, chatListener, holderListener, imageLoader, style) {

    companion object {
        fun create(parent: ViewGroup,
                   chatListener: ChatListener,
                   holderListener: HolderListener,
                   imageLoader: ImageLoader,
                   style: MessagesListStyle): ChatFileInComingItem {
            val binding = DataBindingUtil.inflate<ChatFileIncomingItemBinding>(LayoutInflater.from(parent.context),
                    R.layout.chat_file_incoming_item,
                    parent,
                    false)
            return ChatFileInComingItem(binding!!, chatListener,holderListener, imageLoader, style)
        }
    }

    override fun initView(binding: ChatFileIncomingItemBinding) {
        super.initView(binding)
    }

    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
        //todo new update entity
        binding.downloadBt.visibility = View.GONE
        binding.progressDownload.visibility = if(iMessage.isDownloading()) View.VISIBLE else View.GONE
        bindContentFile(iMessage)
        bindDropLikeAndShowTime(iMessage, binding.messageBubble)
    }

    override fun onMessageGroupType(messageGroupType: MessageGroupType) {
        super.onMessageGroupType(messageGroupType)
        binding.messageBubble.apply {
            ViewCompat.setBackground(this, style.getIncomingBubbleDrawable(messageGroupType))
        }
    }

    override fun applyStyle(style: MessagesListStyle) {
        super.applyStyle(style)
        binding.messageBubble.apply {
            this.setPadding(style.incomingDefaultBubblePaddingLeft,
                    style.incomingDefaultBubblePaddingTop,
                    style.incomingDefaultBubblePaddingRight,
                    style.incomingDefaultBubblePaddingBottom)
            ViewCompat.setBackground(this, style.getIncomingBubbleDrawable())
        }
        binding.messageText.apply {
            super.configureLinksBehavior(this)
        }
    }

    private fun bindContentFile(message: MessageEntity) {

//        message.attachmentTables?.firstOrNull()?.takeIf{ it.isDoccument()}?.run {
//
//            binding.messageText.text = message.messageContent(itemView.context)
//            val fileName = this.fileName
//            val fileType = this.fileMimeType?.takeIf { it.isNotEmpty() }
//                ?: itemView.context.getString(R.string.chat_message_file_unknow)
//            binding.fileNameTv.text = fileName
//            binding.fileTypeTv.text = fileType
//            binding.fileSizeTv.text = this.getFileSizeText()
//        } ?: run {
//            binding.fileNameTv.text = itemView.context.getString(R.string.chat_message_file_not_exit)
//            binding.fileSizeTv.text = ""
//            binding.messageText.text = ""
//        }
    }
}


