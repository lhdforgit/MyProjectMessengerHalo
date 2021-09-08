package com.hahalolo.messager.presentation.main.conversation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.MessageConversationItemBinding
import com.hahalolo.pickercolor.util.setVisibility
import com.hahalolo.swipe.SimpleSwipeListener
import com.hahalolo.swipe.SwipeLayout
import com.hahalolo.swipe.interfaces.SwipeItemMangerInterface
import com.halo.data.common.utils.Strings
import com.halo.data.room.entity.ChannelEntity

class ConversationViewHolder (
    val binding: MessageConversationItemBinding,
    val listener: ConversationListener,
    val swipeListener: SwipeItemMangerInterface,
    val userIdToken: String?,
    val appLang: String?
) : RecyclerView.ViewHolder(binding.root) {

    private val simpleSwipeListener: SimpleSwipeListener

    init {
        simpleSwipeListener = object : SimpleSwipeListener() {
            override fun onOpen(layout: SwipeLayout?) {
                super.onOpen(layout)
                binding.overLay.alpha = 1f
            }

            override fun onClose(layout: SwipeLayout?) {
                super.onClose(layout)
                binding.overLay.alpha = 0f
            }

            override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
                super.onUpdate(layout, leftOffset, topOffset)
                val with = binding.menuGr.width
                if (with > 0) {
                    binding.overLay.alpha = Math.abs(leftOffset / with.toFloat())
                }
            }
        }
    }


    fun bind(entity: ChannelEntity) {
        onBindOnlineIndicator(entity)
        binding.conversationNameTv.text = entity.channelName()
        binding.conversationMessTv.text = entity.lastMessageContent(userIdToken?:"")
        binding.avatarIv.setRequestManager(Glide.with(itemView.context))
        binding.avatarIv.setImage(entity.channelAvatar())
        val count = entity.countNewMessage()
        binding.newMessengerBadge.setText(if (count > 99) "99+" else count.toString(), true)
        binding.newMessengerBadge.setVisibility(count>0)
        bindSwipe(entity)
    }

    private fun onBindOnlineIndicator(entity: ChannelEntity) {
        binding.onlineIndicator.setVisibility(entity.isPrivateChannel())
        if (entity.isPrivateChannel()){
            binding.onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_chat_online)
        }else{
            binding.onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_chat_online)
        }
    }

    private fun bindSwipe(channelEntity: ChannelEntity) {
        binding.apply {
            binding.swipe.showMode = SwipeLayout.ShowMode.LayDown
            if (swipeListener.isOpen(bindingAdapterPosition)) {
                binding.overLay.alpha = 1f
            } else {
                binding.overLay.alpha = 0f
            }
            binding.swipe.removeSwipeListener(simpleSwipeListener)
            binding.swipe.addSwipeListener(simpleSwipeListener)
            container.setOnClickListener {
                if (swipeListener.isOpen(bindingAdapterPosition)) {
                    swipeListener.closeAllItems()
                } else {
                    listener.onClick(channelEntity)
                    swipeListener.closeAllItems()
                }
            }
            binding.menuMore.setOnClickListener {
                swipeListener.closeAllItems()
                listener.onClickMenuMore(channelEntity)
            }
            binding.menuRemove.setOnClickListener {
                swipeListener.closeAllItems()
                listener.onClickMenuRemove(channelEntity)
            }
        }
    }

    companion object{

        fun create(parent:ViewGroup,
                   listener: ConversationListener,
                   swipeListener: SwipeItemMangerInterface,
                   userIdToken: String,
                   appLang: String
        ): ConversationViewHolder{
            val binding = DataBindingUtil.inflate<MessageConversationItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.message_conversation_item,
                parent,
                false)
            return ConversationViewHolder(binding, listener, swipeListener, userIdToken, appLang)
        }
    }
}