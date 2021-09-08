package com.hahalolo.messager.presentation.message.adapter.holder.message

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.utils.ChatConstant
import com.hahalolo.messager.presentation.message.adapter.ChatMessageGroupType
import com.hahalolo.messager.presentation.message.adapter.ChatMessageModel
import com.hahalolo.messager.presentation.message.adapter.holder.ChatMessageViewHolder
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatStickerIncomingItemBinding
import com.hahalolo.messenger.databinding.ChatStickerOutcomingItemBinding
import com.hahalolo.messenger.databinding.ChatTextIncomingItemBinding
import com.hahalolo.messenger.databinding.ChatTextOutcomingItemBinding
import com.halo.data.entities.message.Message
import com.halo.data.room.entity.MessageEntity
import com.halo.widget.felling.utils.FeelingUtils

abstract class ChatMessageStickerViewHolder(
    binding: ViewDataBinding,
    chatListener: ChatListener
) : ChatMessageItemViewHolder(binding, chatListener) {

    private val messageSticker : ImageView?= itemView.findViewById(R.id.messageSticker)

    override fun onBind(message: MessageEntity) {
        val path = message.attachment().firstOrNull()?.url
        bindStickerSize(path)
        messageSticker?.run {
            Glide.with(itemView.context)
                .load(path)
                .into(messageSticker)
        }
        bindActionReactions(messageSticker, message)
    }

    private fun bindStickerSize(path:String?){
        val WIDTH = (widthScreen() * 2.5f / 4).toInt()
        messageSticker?.scaleType = ImageView.ScaleType.FIT_CENTER
        when {
            FeelingUtils.isStickerCard(path) -> {
                messageSticker?.layoutParams?.width = WIDTH
                messageSticker?.layoutParams?.height = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            path?.contains(ChatConstant.HAHA_GIF) == true -> {
                messageSticker?.layoutParams?.width = widthScreen()  / 8
                messageSticker?.layoutParams?.height = widthScreen()  / 8
            }
            else -> {
                messageSticker?.layoutParams?.width = widthScreen()  / 4
                messageSticker?.layoutParams?.height = widthScreen()  / 4
            }
        }
    }

    class InComing(binding: ChatStickerIncomingItemBinding, chatListener: ChatListener)
        : ChatMessageStickerViewHolder(binding, chatListener)

    class OutComing(binding: ChatStickerOutcomingItemBinding, chatListener: ChatListener)
        : ChatMessageStickerViewHolder(binding, chatListener)
}