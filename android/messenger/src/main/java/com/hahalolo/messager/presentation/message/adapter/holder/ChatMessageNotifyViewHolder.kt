package com.hahalolo.messager.presentation.message.adapter.holder

import android.view.View
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.presentation.message.adapter.ChatMessageModel
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.databinding.ChatNotificationItemBinding
import com.hahalolo.pickercolor.util.setVisibility

class ChatMessageNotifyViewHolder(
    private val binding: ChatNotificationItemBinding,
    chatListener: ChatListener
) : ChatMessageViewHolder(binding.root, chatListener) {
    override fun onBind(message: ChatMessageModel) {
        binding.notificationTv.text = message.data.messageContent(chatListener.ownerId())
        message.data.inviteInfo()?.run {
            binding.notificationTv.setVisibility(false)
            binding.inviteView.visibility = View.VISIBLE
            binding.inviteView.bind(this) {
                chatListener.onInvitedChannel(this)
            }
        }?: kotlin.run {
            binding.notificationTv.setVisibility(true)
            binding.inviteView.visibility = View.GONE
        }
    }
}