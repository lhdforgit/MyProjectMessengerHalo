/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.sticker

import android.text.TextUtils
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
import com.hahalolo.messager.utils.MsgSystemObject
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatStickerIncomingItemBinding
import com.halo.data.room.type.MessageType

class ChatStickerInComingItem(binding: ChatStickerIncomingItemBinding,
                              chatListener: ChatListener,
                              holderListener: HolderListener,
                              imageLoader: ImageLoader,
                              style: MessagesListStyle
)
    : ChatInComingItem<ChatStickerIncomingItemBinding>(binding, chatListener,holderListener, imageLoader, style) {

    companion object {
        fun create(parent: ViewGroup, chatListener: ChatListener, holderListener: HolderListener, imageLoader: ImageLoader, style: MessagesListStyle): ChatStickerInComingItem {
            val binding = DataBindingUtil.inflate<ChatStickerIncomingItemBinding>(LayoutInflater.from(parent.context),
                    R.layout.chat_sticker_incoming_item,
                    parent,
                    false)
            return ChatStickerInComingItem(binding, chatListener,holderListener, imageLoader, style)
        }
    }

    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
//        iMessage.attachmentTables?.firstOrNull()?.run {
//            if (this.isGiphy()){
//                setGiphySize(this.getStickerUrl(), binding.image, holderListener.getWIDTH_SCREEN())
//            }else {
//                setStickerSize(this.getStickerUrl(), binding.image, holderListener.getWIDTH_SCREEN())
//            }
//            bindStickerMedia(binding.image, this.getStickerUrl())
//        }?: takeIf { TextUtils.equals(iMessage.messageType(), MessageType.WAVE) }?.run{
//            setWaveSize(binding.image, holderListener.getWIDTH_SCREEN())
//            bindWaveMedia( binding.image)
//        } ?: takeIf { TextUtils.equals(iMessage.messageContent(), MsgSystemObject.MSG_HAHA) }?.run{
//            setHahaSize(binding.image, holderListener.getWIDTH_SCREEN())
//            bindHahaMedia( binding.image)
//        }?: run {
//            binding.image.setImageResource(R.color.img_holder)
//        }
        bindActionReactions(binding.messageSticker, iMessage)
        bindDropLikeAndShowTime(iMessage, binding.messageSticker)
    }

    override fun onMessageGroupType(messageGroupType: MessageGroupType) {
        super.onMessageGroupType(messageGroupType)
        binding.messageOverlay.setBackgroundResource(style.getInComingStickerOverlayResource(messageGroupType))
    }

    override fun applyStyle(style: MessagesListStyle) {
        super.applyStyle(style)
        ViewCompat.setBackground(binding.messageOverlay, style.getIncomingImageOverlayDrawable())
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        super.invalidateLayout(requestManager)
        binding.messageSticker.run {
            requestManager?.clear(this)
            this.setImageDrawable(null)
        }
    }

    override fun getTargets(): MutableList<ImageView> {
        val result = super.getTargets()
        result.add(0, binding.messageSticker)
        return  result
    }
}