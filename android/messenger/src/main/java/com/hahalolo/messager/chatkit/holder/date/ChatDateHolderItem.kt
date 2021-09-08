/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder.date

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatDateHolderItemBinding
import java.text.DateFormat
import java.util.*

class ChatDateHolderItem(var binding: ChatDateHolderItemBinding,
                         var chatListener: ChatListener,
                         var style: MessagesListStyle
)
    : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun create(parent: ViewGroup, chatListener: ChatListener,
                   style: MessagesListStyle)
                : ChatDateHolderItem {

            val binding = DataBindingUtil.inflate<ChatDateHolderItemBinding>(
                LayoutInflater.from(parent.context),
                R.layout.chat_date_holder_item,
                parent,
                false
            )
            return ChatDateHolderItem(binding!!, chatListener, style)
        }
    }

    fun onBind(date: Date) {
        val code = chatListener.getLanguageCode()
        val dateFM: DateFormat
        if (TextUtils.isEmpty(code)) {
            dateFM = DateFormat.getDateInstance(DateFormat.LONG)
        } else {
            dateFM = DateFormat.getDateInstance(DateFormat.LONG, Locale(code))
        }
        val smsTime = Calendar.getInstance()
        smsTime.time = date
        val now = Calendar.getInstance()

        val content: String
        val time = dateFM.format(date.time)?:""
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
            content = itemView.context.getString(R.string.chat_message_holder_time_today, time)
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
            content = itemView.context.getString(R.string.chat_message_holder_time_yesterday, time)
        } else {
            content = time
        }
        binding.messageText.text = content
    }
}