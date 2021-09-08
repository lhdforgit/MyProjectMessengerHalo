/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.home.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.annotation.StyleRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.BubbleService
import com.halo.data.room.entity.ChannelEntity
import com.halo.data.room.type.SaveType
import com.hahalolo.messager.utils.MessageUtil
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.MessageConversationItemBinding
import com.hahalolo.swipe.SimpleSwipeListener
import com.hahalolo.swipe.SwipeLayout
import com.hahalolo.swipe.interfaces.SwipeItemMangerInterface
import com.halo.common.utils.SpanBuilderUtil
import com.halo.common.utils.ktx.executeAfter
import com.halo.data.room.type.ChannelType
import java.util.*


class ConversationHolder(
    val binding: MessageConversationItemBinding,
    val requestManager: RequestManager,
    val listener: ChatAdapterListener<ChannelEntity>,
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

    fun bind(entity: ChannelEntity, context: Context) {
        binding.executeAfter {
            updateRoomName(entity)
            updateRoomIsOnline(entity)
            updateAvatar(entity)
            updateRoomLastMessage(entity, context)
            updateNewMessageCount(entity.countNewMessage())
            updateRoomTime(entity.roomTime())
            bindSwipe(entity)
            if (BubbleService.DEBUG) {
                when (entity.roomSave()) {
                    SaveType.CACHE -> {
                        avatarIv.setBackgroundColor(Color.RED)
                    }
                    SaveType.OLD -> {
                        avatarIv.setBackgroundColor(Color.BLUE)
                    }
                    SaveType.NEW -> {
                        avatarIv.setBackgroundColor(Color.GREEN)
                    }
                }

                when (entity.bubbleSave()) {
                    SaveType.CACHE -> {
                        conversationNameTv.setBackgroundColor(Color.RED)
                    }
                    SaveType.OLD -> {
                        conversationNameTv.setBackgroundColor(Color.BLUE)
                    }
                    SaveType.NEW -> {
                        conversationNameTv.setBackgroundColor(Color.GREEN)
                    }
                }
            }
        }
    }

    private fun updateRoomTime(roomTime: Long?) {
        roomTime?.run {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = this
            binding.timeMsg = MessageUtil.getTimeString(calendar.time, appLang ?: "")
        }
    }

    private fun updateAvatar(room: ChannelEntity) {
        val listAvatar = mutableListOf<String>()
        when (room.roomType()) {
            ChannelType.PRIVATE -> {
                room.recipient()?.run {
                    listAvatar.add(this.avatar?:"")
                }
            }
            ChannelType.GROUP,
            ChannelType.PUBLIC -> {
                room.roomAvatars().takeIf { it.isNotEmpty() }?.run {
                    listAvatar.addAll(this)
                }
            }
        }
        binding.avatarIv.setRequestManager(requestManager)
        binding.avatarIv.setImageList(listAvatar)
    }

    private fun updateRoomIsOnline(room: ChannelEntity) {
        room.run {
            if (this.isGroup()) {
                binding.onlineIndicator.visibility = View.GONE
            } else {
                binding.onlineIndicator.visibility = View.VISIBLE
                takeIf { !this.isGroup() }
                    ?.recipient()
                    ?.takeIf { it.isOnline() }?.run {
                        binding.onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_online)
                    }
                    ?: run {
                        binding.onlineIndicator.setBackgroundResource(R.drawable.shape_bubble_offline)
                    }
            }
        }
    }

    private fun updateRoomName(room: ChannelEntity) {
        binding.conversationNameTv.text = room.channelName()
    }

    private fun updateRoomLastMessage(room: ChannelEntity, context: Context) {
        binding.conversationMessTv.text = ""
        val sp = SpanBuilderUtil()
        room.run {
            MessageUtil.getLastMessage(this, context, userIdToken).takeIf { it.isNotEmpty() }?.let {
                sp.append(it)
            } ?: kotlin.run {
                if (isGroup()) {
                    sp.append(itemView.context?.getString(R.string.chat_message_send_wave_group))
                } else {
                    sp.appendWithSpace(itemView.context?.getString(R.string.chat_message_send_wave_friend))
                    sp.append(room.recipient()?.userName())
                }
            }
            binding.conversationMessTv.text = sp.build()
        }
    }

    private fun updateNewMessageCount(count: Int) {
        binding.newMessengerBadge.setText(if (count > 99) "99+" else count.toString(), true)
        val typeface = ResourcesCompat.getFont(itemView.context, R.font.muli_bold)
        binding.newMessengerBadge.badgeTextView?.typeface = typeface
        try {
            @StyleRes val style = if (count > 0) R.style.Messenger_TextView_ConversationMess_Unread
            else R.style.Messenger_TextView_ConversationMess

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.conversationMessTv.setTextAppearance(style)
            } else {
                binding.conversationMessTv.setTextAppearance(itemView.context, style)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindSwipe(room: ChannelEntity) {
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
                    listener.itemClick(room)
                    swipeListener.closeAllItems()
                }
            }
            binding.menuMore.setOnClickListener {
                swipeListener.closeAllItems()
                listener.onClickMenuMore(room)
            }
            binding.menuRemove.setOnClickListener {
                swipeListener.closeAllItems()
                listener.onClickMenuRemove(room)
            }
        }
    }
}