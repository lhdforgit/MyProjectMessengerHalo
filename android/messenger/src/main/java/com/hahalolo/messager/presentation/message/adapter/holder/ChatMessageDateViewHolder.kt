package com.hahalolo.messager.presentation.message.adapter.holder

import android.text.TextUtils
import android.text.format.DateUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.presentation.message.adapter.ChatMessageModel
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatDateHolderItemBinding
import java.text.DateFormat
import java.util.*

class ChatMessageDateViewHolder (private val binding: ChatDateHolderItemBinding,
                                 chatListener: ChatListener)
    : ChatMessageViewHolder(binding.root, chatListener){
    override fun onBind(message: ChatMessageModel) {

        message.data.messageTime()?.run {
            val code = chatListener.getLanguageCode()
            val dateFM: DateFormat
            if (TextUtils.isEmpty(code)) {
                dateFM = DateFormat.getDateInstance(DateFormat.LONG)
            } else {
                dateFM = DateFormat.getDateInstance(DateFormat.LONG, Locale(code))
            }
            val smsTime = Calendar.getInstance()
            val date = Date(this)
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
}