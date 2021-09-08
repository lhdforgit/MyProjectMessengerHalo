package com.hahalolo.messager.presentation.message.adapter.holder.message

import android.widget.ImageView
import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatGifIncomingItemBinding
import com.hahalolo.messenger.databinding.ChatGifOutcomingItemBinding
import com.halo.data.room.entity.MessageEntity
import com.halo.widget.felling.utils.FeelingUtils

abstract class ChatMessageGifViewHolder(
    binding: ViewDataBinding,
      chatListener: ChatListener
) : ChatMessageItemViewHolder(binding, chatListener) {

    private val messageGif :ImageView?= itemView.findViewById(R.id.messageGif)

    override fun onBind(message: MessageEntity) {
        val path = message.attachment().firstOrNull()?.url
        bindGifSize(path)
        messageGif?.run {
            Glide.with(itemView.context)
                .load(path)
                .into(messageGif)
        }
        bindActionReactions(messageGif, message)
    }

    private fun bindGifSize(path:String?){
        if(FeelingUtils.isStickerCard(path)){
            val WIDTH = (widthScreen() * 2.5f / 4).toInt()
            messageGif?.layoutParams?.width = WIDTH
            messageGif?.layoutParams?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        }else{
            val WIDTH = (widthScreen() * 3f / 8).toInt()
            messageGif?.scaleType = ImageView.ScaleType.FIT_CENTER
            messageGif?.layoutParams?.width = WIDTH
            messageGif?.layoutParams?.height = LinearLayout.LayoutParams.WRAP_CONTENT
        }
    }

    class InComing(binding: ChatGifIncomingItemBinding, chatListener: ChatListener)
        : ChatMessageGifViewHolder(binding, chatListener)

    class OutComing(binding: ChatGifOutcomingItemBinding, chatListener: ChatListener)
        : ChatMessageGifViewHolder(binding, chatListener)
}