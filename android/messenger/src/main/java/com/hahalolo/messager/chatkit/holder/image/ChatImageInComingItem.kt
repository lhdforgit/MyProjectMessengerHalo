/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.image

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.ChatInComingItem
import com.hahalolo.messager.chatkit.holder.HolderListener
import com.hahalolo.messager.chatkit.holder.MessageGroupType
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.hahalolo.messager.chatkit.view.media.MutilMediaListener
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatImageIncomingItemBinding

class ChatImageInComingItem(
    binding: ChatImageIncomingItemBinding,
    chatListener: ChatListener,
    holderListener: HolderListener,
    imageLoader: ImageLoader,
    style: MessagesListStyle
) : ChatInComingItem<ChatImageIncomingItemBinding>(
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
        ): ChatImageInComingItem {
            val binding = DataBindingUtil.inflate<ChatImageIncomingItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.chat_image_incoming_item,
                parent,
                false
            )
            return ChatImageInComingItem(
                binding!!,
                chatListener,
                holderListener,
                imageLoader,
                style
            )
        }
    }

    override fun initView(binding: ChatImageIncomingItemBinding) {
        super.initView(binding)
    }

    private val listener = object : MutilMediaListener {
        override fun onClickViewMedia(position: Int, listImage: MutableList<AttachmentTable>?) {
            chatListener.onImageClickListener(listImage, position)
        }
    }

    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
        binding.downloadBt.visibility = View.GONE
        binding.progressDownload.visibility = if (iMessage.isDownloading()) View.VISIBLE else View.GONE
        binding.messageMedia.updateRequestManager(imageLoader.getRequestManager())
//        binding.messageMedia.updateListMedia(iMessage.attachmentTables ?: mutableListOf(), listener)
        bindActionReactions(binding.messageMedia, iMessage)
    }

    override fun onMessageGroupType(messageGroupType: MessageGroupType) {
        super.onMessageGroupType(messageGroupType)
        binding.messageOverlay.setBackgroundResource(
            style.getIncomingImageOverlayResource(
                messageGroupType
            )
        )
    }

    override fun applyStyle(style: MessagesListStyle) {
        super.applyStyle(style)
        ViewCompat.setBackground(binding.messageOverlay, style.getOutcomingImageOverlayDrawable())
    }

    override fun getTargets(): MutableList<ImageView> {
        val result = super.getTargets()
        result.addAll( binding.messageMedia.getTargets())
        return result
    }
    override fun invalidateLayout(requestManager: RequestManager?) {
        super.invalidateLayout(requestManager)
        binding.messageMedia.invalidateLayout(requestManager)
    }
}