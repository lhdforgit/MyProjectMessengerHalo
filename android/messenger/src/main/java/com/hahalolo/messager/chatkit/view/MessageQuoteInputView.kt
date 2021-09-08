/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messenger.databinding.MessageQuoteInputViewBinding
import com.halo.data.room.entity.MessageQuoteEntity

class MessageQuoteInputView : RelativeLayout {
    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        initView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private lateinit var binding: MessageQuoteInputViewBinding

    private fun initView() {
        binding = MessageQuoteInputViewBinding.inflate(LayoutInflater.from(context))
        addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        bindAction()
    }

    private var listener: Listener? = null

    //quote this message
    private var messageEntity: MessageEntity? = null
    //with this quote messsage
    private var messageQuoteEntity: MessageQuoteEntity? = null

    private fun bindAction() {
        binding.closeBt.setOnClickListener {
            listener?.onClose()
        }
    }

    fun updateListener(listener: Listener) {
        this.listener = listener
    }

    fun setQuoteMessage(messageEntity: MessageEntity?) {
        this.messageEntity = messageEntity
        updateRequestLayout()
    }


    fun setMessageQuoteEntity(editQuote: MessageQuoteEntity?) {
        messageQuoteEntity = editQuote
        updateRequestLayout()
    }

    fun getQuoteMessage(): MessageEntity? {
        // nếu đang quote 1 message return messageQuote
        // nếu đang edit 1 message có chưa quote return messageEditQuote
        return messageEntity
    }

    private fun updateRequestLayout() {
        messageEntity?.run {
            binding.subContentTv.visibility = View.VISIBLE
            binding.subContentTv.text = "Trả lời ${this.userName()}"
            updateQuoteMsgLayout()
        } ?: messageQuoteEntity?.run {
            binding.subContentTv.text = "Trả lời ${this.userName()}"
            binding.subContentTv.visibility = View.VISIBLE
            updateEditQuoteMsgLayout()
        } ?: run {
            binding.subContentTv.visibility = View.GONE
        }
    }

    private fun updateEditQuoteMsgLayout() {
        messageQuoteEntity?.run {
            binding.chatQuoteTv.setTextContent(this.messageText(), this.memberMentions(), true)
            listener?.onShow()
        }
    }

    private fun updateQuoteMsgLayout() {
        messageEntity?.run {
            binding.chatQuoteTv.setTextContent(this.messageText(), this.memberMentions(), true )
            listener?.onShow()
        }
    }

    interface Listener {
        fun onClose()
        fun onShow()
    }
}