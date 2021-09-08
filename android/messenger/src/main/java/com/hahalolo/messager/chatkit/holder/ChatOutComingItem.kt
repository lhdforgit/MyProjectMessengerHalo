/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder

import android.graphics.Color
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messenger.R
import com.halo.common.utils.SpanBuilderUtil

abstract class ChatOutComingItem<BINDING : ViewDataBinding>(
    binding: BINDING,
    chatListener: ChatListener,
    holderListener: HolderListener,
    imageLoader: ImageLoader,
    style: MessagesListStyle
) : ChatHolderItem<BINDING>(binding, chatListener, holderListener, imageLoader, style) {

    protected var sending: View? = null
    protected var sendMsgError: View? = null
    protected var sendMsgEdit: View? = null
    protected var sendMsgRetry: TextView? = null

    override fun initView(binding: BINDING) {
        super.initView(binding)
        sending = itemView.findViewById(R.id.messageSending)
        sendMsgRetry = itemView.findViewById(R.id.messageSendError)
        sendMsgEdit = itemView.findViewById(R.id.messageStatusEdit)
        sendMsgError = itemView.findViewById(R.id.messageErrorRemove)
    }

    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
        sending?.visibility = if (iMessage.isSending()) View.VISIBLE else View.GONE
        sendMsgRetry?.visibility = if (iMessage.isSendError()) View.VISIBLE else View.GONE
        sendMsgError?.visibility = if (iMessage.isSendError()) View.VISIBLE else View.GONE
        sendMsgEdit?.visibility = if (!iMessage.isEdited() || iMessage.isSending() || iMessage.isSendError()) View.GONE else View.VISIBLE
        handleChatError(iMessage)
    }

    private fun handleChatError(iMessage: MessageEntity) {
        val sb = SpanBuilderUtil()
        sb.appendWithSpace(itemView.context.getString(R.string.chat_message_resend), object : ClickableSpan() {
            override fun onClick(widget: View) {
                chatListener.onRetrySendMessage(iMessage)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(itemView.context, R.color.badges)
                ds.isUnderlineText = false
            }
        })

        sendMsgRetry?.text = sb.build()
        // this step is mandated for the url and clickable styles
        sendMsgRetry?.movementMethod = LinkMovementMethod.getInstance()
        sendMsgRetry?.highlightColor = Color.TRANSPARENT
        sendMsgError?.setOnClickListener { chatListener.onCancelMessageError(iMessage) }
    }

    override fun onMessageGroupType(messageGroupType: MessageGroupType) {
        super.onMessageGroupType(messageGroupType)
        when (messageGroupType) {
            MessageGroupType.START -> if (time != null) time!!.visibility = View.GONE
            MessageGroupType.MEDIUM -> if (time != null) time!!.visibility = View.GONE
            MessageGroupType.LAST -> if (time != null) time!!.visibility = View.GONE
            MessageGroupType.ONLY -> if (time != null) time!!.visibility = View.GONE
        }
    }
}