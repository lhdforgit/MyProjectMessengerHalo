package com.hahalolo.messager.chatkit.holder.view

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView
import com.hahalolo.messager.chatkit.view.input.MentionTextView
import com.hahalolo.messenger.R
import com.halo.common.utils.DateUtils
import com.halo.common.utils.SizeUtils
import com.halo.common.utils.Strings
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.entity.MentionEntity
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.entity.MessageQuoteEntity
import com.halo.data.room.table.MentionTable
import com.halo.data.room.type.AttachmentType
import com.halo.data.room.type.MessageType
import com.halo.widget.reactions.MessageMenuListener
import com.halo.widget.reactions.ReactionClickListener
import com.halo.widget.reactions.ReactionSelectedListener
import com.halo.widget.reactions.message.MessageReactionPopup
import com.halo.widget.reactions.message.MessageReactionUtils
import java.text.DateFormat
import java.util.*

class ChatMessageTextView : LinearLayout {

    companion object {
        const val EDIT_QUOTE_LENGTH = 128
        const val EDIT_QUOTE_VIEW_MORE = "…"
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private var chatMessageTextListener: ChatMessageTextListener? = null

    private var isOutComing = false

    //    private var messageContent: String? = null
    private var messageEntity: MessageEntity? = null
    private var messageQuoteEntity: MessageQuoteEntity? = null

//    private var mentions: MutableList<MentionEntity>? = null

    private var popups = mutableListOf<MessageReactionPopup>()

    private fun initView() {
        this.orientation = VERTICAL
    }

    fun bindReaction(
        reactionSelectedListener: ReactionSelectedListener,
        reactionClickListener: ReactionClickListener,
        messageMenuListener: MessageMenuListener,
    ) {
        this.takeIf { it.childCount > 0 }?.run {
            for (i in 0 until this.childCount) {
                this.getChildAt(i)?.let { view ->
                    when (view) {
                        is TextView -> {
                            popups.add(MessageReactionUtils.initPopup(context).apply {
                                this.updateListener(
                                    view,
                                    reactionSelectedListener,
                                    reactionClickListener,
                                    messageMenuListener
                                )
                            })
                        }
                        is LinearLayout -> {
                            // container qoute
                            for (j in 0 until view.childCount) {
                                (view.getChildAt(j) as? TextView)?.let { qouteTextView ->
                                    // container qoute TextView
                                    popups.add(MessageReactionUtils.initPopup(context).apply {
                                        this.updateListener(
                                            qouteTextView,
                                            reactionSelectedListener,
                                            reactionClickListener,
                                            messageMenuListener
                                        )
                                    })
                                }
                            }
                        }
                        else -> {

                        }
                    }
                }
            }
        }
    }

    fun updateChatMessageTextListener(listener: ChatMessageTextListener?) {
        this.chatMessageTextListener = listener
    }

    fun updateMessage(outComing: Boolean, data: MessageEntity) {
        this@ChatMessageTextView.isOutComing = outComing
        this@ChatMessageTextView.messageEntity = data
        this.removeAllViews()
        requestUpdateLayout()
    }

    fun updateMessageQuote(data: MessageEntity) {
        this@ChatMessageTextView.messageEntity = data
        this.removeAllViews()
        requestUpdateLayout()
    }

    fun updateMessageQuote(data: MessageQuoteEntity) {
        this@ChatMessageTextView.messageQuoteEntity = data
        this.removeAllViews()
        requestUpdateLayout()
    }

    private fun requestUpdateLayout() {
        this.background = null
        messageQuoteEntity?.run {
            addTextQuote(this.messageText(), this.mentions)
        }
        messageEntity?.run {
            addMessageQuote(this.quoteMessage)
            addTextContent(this.messageText(), this.memberMentions())
        }
    }

    private var typeClick = TypeClick.NONE_CLICK

    private val runnable = Runnable {
        when (typeClick) {
            TypeClick.TRIPLE_CLICK -> chatMessageTextListener?.onTripleClickTextView()
            TypeClick.DOUBLE_CLICK -> chatMessageTextListener?.onDoubleClickTextView()
            else -> chatMessageTextListener?.onClickTextView()
        }
        typeClick = TypeClick.NONE_CLICK
    }

    private fun onClickTextView() {
        typeClick = when (typeClick) {
            TypeClick.DOUBLE_CLICK -> TypeClick.TRIPLE_CLICK
            TypeClick.ONE_CLICK -> TypeClick.DOUBLE_CLICK
            else -> TypeClick.ONE_CLICK
        }
        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 250)
    }

    //Context Text View
    private fun addTextContent(textContent: String, memberMentions: MutableList<MemberEntity>) {
        val textView = MentionTextView(context)
        textView.setTextContent(
            textContent.trim(),
            memberMentions
        )
        try {
            @StyleRes val style = if (isOutComing) R.style.Messenger_ChatKit_OutcomingText
            else R.style.Messenger_ChatKit_IncomingText
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(style)
            } else {
                textView.setTextAppearance(context, style)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        this.addView(
            textView,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    private fun addMessageQuote(quoteMessage: MessageQuoteEntity?) {

        quoteMessage?.run {
            val quoteGr = LinearLayout(this@ChatMessageTextView.context)
            quoteGr.orientation = VERTICAL

            val textView = MentionTextView(context)
            try {
                @StyleRes val style = if (isOutComing) R.style.Messenger_ChatKit_OutcomingText_Quote
                else R.style.Messenger_ChatKit_IncomingText_Quote
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    textView.setTextAppearance(style)
                } else {
                    textView.setTextAppearance(context, style)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            when (this.messageType()) {
                MessageType.TEXT -> {
                    when (this.attachmentType()) {
                        AttachmentType.IMAGE,
                        AttachmentType.VIDEO -> {
                            textView.setTextContent(textContent = "Tin nhắn media")
                        }
                        AttachmentType.GIF -> {
                            textView.setTextContent(textContent = "Tin nhắn media")
                        }
                        AttachmentType.STICKER -> {
                            textView.setTextContent(textContent = "Tin nhắn media")
                        }
                        else -> {
                            //text
                            textView.setTextContent(this.messageText(),
                                mentions?.mapNotNull { it.memberEntity }?.toMutableList()
                            )
                        }
                    }
                }
                MessageType.DELETED,
                MessageType.REVOKED -> {
                    textView.setTextContent("Tin nhắn đã bị xóa")
                }
                else -> {

                }
            }

            textView.setBackgroundColor(Color.GREEN)
            quoteGr.addView(textView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT)

            val subTextView = AppCompatTextView(context)
            try {
                @StyleRes val subTtyle = if (isOutComing) R.style.Messenger_ChatKit_Date
                else R.style.Messenger_ChatKit_Date
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    subTextView.setTextAppearance(subTtyle)
                } else {
                    subTextView.setTextAppearance(context, subTtyle)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val time :String = this.messageTime()?.run {
                val smsTime = Calendar.getInstance()
                smsTime.timeInMillis = this
                val now = Calendar.getInstance()
                val date = DateUtils.formatTime(DateUtils.DATE_FORMAT_EE_DD_MM_YY, this)
                val time = DateUtils.formatTime(DateUtils.DATE_FORMAT_HH_MM, this)
                if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
                     "Today at ${time}"
                } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
                    "Yesterday at ${time}"
                } else {
                    "${date} at ${time}"
                }
            }?:""

            subTextView.text = "${this.userName()}, $time"
            subTextView.setBackgroundColor(Color.RED)

            quoteGr.addView(subTextView,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT)

            quoteGr.setBackgroundResource(
                if (isOutComing) R.drawable.bg_chat_message_text_quote_outcoming else
                    R.drawable.bg_chat_message_text_quote_incoming
            )
            val pading = SizeUtils.dp2px(8f)
            quoteGr.setPadding(pading,pading,pading,pading)
            this@ChatMessageTextView.addView(
                quoteGr,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
        }
    }

    //Context Text View
    private fun addTextQuote(textContent: String, mentions: MutableList<MentionEntity>?) {
        val textView = MentionTextView(context)
        textView.setTextContent(
            textContent,
            mentions?.mapNotNull { it.memberEntity }?.toMutableList()
        )
        try {
            @StyleRes val style = R.style.Messenger_ChatKit_EditQuote
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                textView.setTextAppearance(style)
            } else {
                textView.setTextAppearance(context, style)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        this.addView(
            textView,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    fun invalidateLayout() {
        try {
            popups.forEach { it.dismiss() }
            this.takeIf { it.childCount > 0 }?.removeAllViews()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface ChatMessageTextListener {
        fun onClickSpan(mention: MentionTable)
        fun onClickTextView()
        fun onDoubleClickTextView()
        fun onTripleClickTextView()
    }

    enum class TypeClick {
        NONE_CLICK, ONE_CLICK, DOUBLE_CLICK, TRIPLE_CLICK
    }
}