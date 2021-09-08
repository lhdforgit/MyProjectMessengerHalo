/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.video

import android.view.LayoutInflater
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
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatVideoIncomingItemBinding

class ChatVideoInComingItem(binding: ChatVideoIncomingItemBinding,
                            chatListener: ChatListener,
                            holderListener: HolderListener,
                            imageLoader: ImageLoader,
                            style: MessagesListStyle
)
    : ChatInComingItem<ChatVideoIncomingItemBinding>(binding, chatListener, holderListener, imageLoader, style) {

    companion object {
        fun create(parent: ViewGroup, chatListener: ChatListener, holderListener: HolderListener, imageLoader: ImageLoader, style: MessagesListStyle): ChatVideoInComingItem {
            val binding = DataBindingUtil.inflate<ChatVideoIncomingItemBinding>(LayoutInflater.from(parent.context),
                    R.layout.chat_video_incoming_item,
                    parent,
                    false)
            return ChatVideoInComingItem(binding, chatListener, holderListener, imageLoader, style)
        }
    }

    override fun initView(binding: ChatVideoIncomingItemBinding) {
        super.initView(binding)
    }

    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
        //todo new update entity
    }

    override fun onMessageGroupType(messageGroupType: MessageGroupType) {
        super.onMessageGroupType(messageGroupType)
        binding.messageOverlay.setBackgroundResource(style.getIncomingImageOverlayResource(messageGroupType))
    }

    override fun applyStyle(style: MessagesListStyle) {
        super.applyStyle(style)
        ViewCompat.setBackground(binding.messageOverlay, style.getOutcomingImageOverlayDrawable())
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        super.invalidateLayout(requestManager)
        binding.image.run {
            requestManager?.clear(this)
            this.setImageDrawable(null)
        }
    }

    override fun getTargets(): MutableList<ImageView> {
        val result = super.getTargets()
        result.add(0, binding.image)
        return  result
    }
}