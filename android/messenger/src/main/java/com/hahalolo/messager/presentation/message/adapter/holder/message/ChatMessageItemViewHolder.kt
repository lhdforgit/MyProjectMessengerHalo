package com.hahalolo.messager.presentation.message.adapter.holder.message

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.holder.view.ChatMessageTextView
import com.hahalolo.messager.chatkit.view.ChatReactionView
import com.hahalolo.messager.chatkit.view.MessageLinkView
import com.hahalolo.messager.chatkit.view.MessageReaderItem
import com.hahalolo.messager.chatkit.view.input.MentionUtils
import com.hahalolo.messager.chatkit.view.media.ChatMutilImageView
import com.hahalolo.messager.presentation.message.adapter.ChatMessageGroupType
import com.hahalolo.messager.presentation.message.adapter.ChatMessageModel
import com.hahalolo.messager.presentation.message.adapter.holder.ChatMessageViewHolder
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatTextIncomingItemBinding
import com.hahalolo.messenger.databinding.ChatTextOutcomingItemBinding
import com.hahalolo.pickercolor.util.setVisibility
import com.hahalolo.swipe.SwipeLayout
import com.halo.common.utils.ClipbroadUtils
import com.halo.common.utils.SizeUtils
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.table.MentionTable
import com.halo.data.room.type.MessageType
import com.halo.data.room.type.ReactionType
import com.halo.widget.reactions.MessageMenuListener
import com.halo.widget.reactions.ReactionClickListener
import com.halo.widget.reactions.ReactionSelectedListener
import com.halo.widget.reactions.message.MessageReactionPopup
import com.halo.widget.reactions.message.MessageReactionUtils
import kotlin.math.abs

abstract class ChatMessageItemViewHolder(
    binding: ViewDataBinding,
    chatListener: ChatListener
) : ChatMessageViewHolder(binding.root, chatListener) {

    private var messageGroup: View? = itemView.findViewById(R.id.messageGroup)

    private var messageBubble: View? = itemView.findViewById(R.id.messageBubble)
    private var messageOverlay: View? = itemView.findViewById(R.id.messageOverlay)
    private var messageSwipe: SwipeLayout? = itemView.findViewById(R.id.messageSwipe)
    private var messageSwipeQuote: View? = itemView.findViewById(R.id.messageSwipeQuote)

    private var avatar: ImageView? = itemView.findViewById(R.id.messageUserAvatar)
    private var nameTv: TextView? = itemView.findViewById(R.id.messageUserName)
    private var timeTv: TextView? = itemView.findViewById(R.id.messageTime)

    private var messageLink: MessageLinkView? = itemView.findViewById(R.id.messageLink) as? MessageLinkView
    private var messageText: ChatMessageTextView? = itemView.findViewById(R.id.messageText) as? ChatMessageTextView
    private var messageReader: MessageReaderItem? = itemView.findViewById(R.id.messageReaderItem)as? MessageReaderItem
    private var messageReaction: ChatReactionView? = itemView.findViewById(R.id.messageReactionItem)as? ChatReactionView

    private val reactionPopup: MessageReactionPopup by lazy {
        MessageReactionUtils.initPopup(itemView.context)
    }

    private var messageSending: View? = itemView.findViewById(R.id.messageSending)
    private var messageSendError: View? = itemView.findViewById(R.id.messageSendError)
    private var messageErrorRemove: View? = itemView.findViewById(R.id.messageErrorRemove)
    private var messageEdited: View? = itemView.findViewById(R.id.messageStatusEdit)

    override fun onBind(message: ChatMessageModel) {
        onBindGroup(message.groupType)
        onBind(message.data)
        onBindText(message)
        onBindEmbed(message)
        onBindBubble(message)
        onBindOverlay(message)
        onBindSwipe(message)
        onBindReader(message)
        onBindReaction(message)
        if (message.isOutComing) {
            onBindOutComing(message.data)
        } else {
            onBindInComing(message)
        }
    }

    private fun onBindEmbed(message: ChatMessageModel) {
        messageLink?.setRequestManager(Glide.with(itemView.context))
        messageLink?.setOnClickListener {
            message.data.embedData()?.url?.takeIf { it.isNotEmpty() }?.run {
                chatListener.openWebUrl(this)
            }
        }
        message.data.embedData()?.run {
            messageLink?.setVisibility(true)
            messageLink?.onBind(this)
        }?: kotlin.run {
            messageLink?.setVisibility(false)
        }
    }

    private fun onBindGroup(groupType: ChatMessageGroupType) {
        messageGroup?.run {
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            when (groupType) {
                ChatMessageGroupType.TOP -> params.setMargins(
                    0,
                    SizeUtils.dp2px(MARGIN_NON_GROUP),
                    0,
                    SizeUtils.dp2px(MARGIN_GROUP)
                )
                ChatMessageGroupType.CENTER -> params.setMargins(
                    0,
                    SizeUtils.dp2px(MARGIN_GROUP),
                    0,
                    SizeUtils.dp2px(MARGIN_GROUP)
                )
                ChatMessageGroupType.LAST -> params.setMargins(
                    0,
                    SizeUtils.dp2px(MARGIN_GROUP),
                    0,
                    SizeUtils.dp2px(MARGIN_NON_GROUP)
                )
                ChatMessageGroupType.ONLY -> params.setMargins(
                    0,
                    SizeUtils.dp2px(MARGIN_NON_GROUP),
                    0,
                    SizeUtils.dp2px(MARGIN_NON_GROUP)
                )
            }
            this.layoutParams = params
        }
    }

    private fun onBindBubble(message: ChatMessageModel) {
        messageBubble?.apply {
            this.setPadding(
                itemView.context.resources.getDimension(R.dimen.message_padding_left).toInt(),
                itemView.context.resources.getDimension(R.dimen.message_padding_top).toInt(),
                itemView.context.resources.getDimension(R.dimen.message_padding_right).toInt(),
                itemView.context.resources.getDimension(R.dimen.message_padding_bottom).toInt()
            )
        }

        if (message.isOutComing) {
            val resourceId = when (message.groupType) {
                ChatMessageGroupType.TOP -> R.drawable.shape_outcoming_message_start
                ChatMessageGroupType.CENTER -> R.drawable.shape_outcoming_message_in
                ChatMessageGroupType.LAST -> R.drawable.shape_outcoming_message_last
                else -> R.drawable.shape_outcoming_message
            }
            messageBubble?.setBackgroundResource(resourceId)
        } else {
            val resourceId = when (message.groupType) {
                ChatMessageGroupType.TOP -> R.drawable.shape_incoming_message_start
                ChatMessageGroupType.CENTER -> R.drawable.shape_incoming_message_in
                ChatMessageGroupType.LAST -> R.drawable.shape_incoming_message_last
                else -> R.drawable.shape_incoming_message
            }
            messageBubble?.setBackgroundResource(resourceId)
        }
    }

    private fun onBindOverlay(message: ChatMessageModel) {
        if (message.isOutComing) {
            val resourceId = when (message.groupType) {
                ChatMessageGroupType.TOP -> R.drawable.over_outcoming_message_boder_start
                ChatMessageGroupType.CENTER -> R.drawable.over_outcoming_message_boder_in
                ChatMessageGroupType.LAST -> R.drawable.over_outcoming_message_boder_last
                else -> R.drawable.over_outcoming_message_boder
            }
            messageOverlay?.setBackgroundResource(resourceId)
        } else {
            val resourceId = when (message.groupType) {
                ChatMessageGroupType.TOP -> R.drawable.over_incoming_message_boder_start
                ChatMessageGroupType.CENTER -> R.drawable.over_incoming_message_boder_in
                ChatMessageGroupType.LAST -> R.drawable.over_incoming_message_boder_last
                else -> R.drawable.over_incoming_message_boder
            }
            messageOverlay?.setBackgroundResource(resourceId)
        }
    }

    private fun onBindSwipe(message: ChatMessageModel) {
        messageSwipe?.run {
            this.removeAllSwipeListener()
            this.isClickToClose = false
            this.setOnClickListener(null)
            this.isSwipeEnabled = message.data.isEnableQuote()
            this.addSwipeListener(object : SwipeLayout.SwipeListener {
                private var surFaceLeft = 0
                private val SIZE_SWIPE_QUOTE = SizeUtils.dp2px(60f).toFloat()
                override fun onStartOpen(layout: SwipeLayout?) {
                }

                override fun onOpen(layout: SwipeLayout?) {
                }

                override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
                    val left = abs(leftOffset)
                    val alpha = if (left < SIZE_SWIPE_QUOTE) left / SIZE_SWIPE_QUOTE else 1f
                    messageSwipeQuote?.run {
                        when (layout?.dragEdge) {
                            SwipeLayout.DragEdge.Right -> this.translationX =
                                this.width * (1f - alpha)
                            SwipeLayout.DragEdge.Left -> this.translationX =
                                this.width * (alpha - 1f)
                            else -> {
                            }
                        }
                        this.alpha = alpha
                    }
                }

                override fun onStartClose(layout: SwipeLayout?) {
                }

                override fun onClose(layout: SwipeLayout?) {
                    takeIf {
                        surFaceLeft >= SIZE_SWIPE_QUOTE && message.data.isEnableQuote()
                    }?.run {
                        chatListener.onQuoteMessage(message = message.data)
                    }
                }

                override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
                    surFaceLeft = abs(layout?.surfaceView?.left ?: 0)
                    close()
                }
            })
        }
    }

    private fun onBindText(message: ChatMessageModel) {
        messageText?.updateChatMessageTextListener(object :
            ChatMessageTextView.ChatMessageTextListener {
            override fun onClickSpan(mention: MentionTable) {
//                if (!mention.isMentionAll()) {
//                    clickedMention = true
//                    chatListener.onClickMention(mention)
//                }
            }

            override fun onClickTextView() {
//                onDropLikeAndShowTime(message)
            }

            override fun onDoubleClickTextView() {
//                ClipbroadUtils.copyText(itemView.context, message.messageText())
            }

            override fun onTripleClickTextView() {

            }
        })

        message.data.quoteMessage?.run {
            Strings.log("onBindText quoteMessage 1 ", message.data)
        }?: message.data.quoteMsgId()?.run{
            Strings.log("onBindText quoteMessage 2 ", message.data)
        }

        messageText?.updateMessage( message.isOutComing, message.data)
    }

    open fun onBind(message: MessageEntity) {
        messageEdited?.setVisibility(message.isEdited() && message.isSent())
    }

    private fun onBindInComing(message: ChatMessageModel) {
        nameTv?.setVisibility(message.groupType == ChatMessageGroupType.TOP || message.groupType == ChatMessageGroupType.ONLY)
        avatar?.setVisibility(message.groupType == ChatMessageGroupType.LAST || message.groupType == ChatMessageGroupType.ONLY)
        nameTv?.text = message.data.userName()
        avatar?.run{
            Glide.with(itemView.context)
                .load(message.data.userAvatar())
                .circleCrop()
                .into(this)
        }
    }

    private fun onBindOutComing(message: MessageEntity) {
        messageSending?.setVisibility(message.isSending())
        messageSendError?.setVisibility(message.isSendError())
        messageErrorRemove?.setVisibility(message.isSendError())
        messageErrorRemove?.setOnClickListener {
            chatListener.onRemoveMessageError(message)
        }
    }

    private fun onBindReaction(message: ChatMessageModel) {
        //action Reaction
        bindActionReactions(messageText, message.data)
        //info reaction
        messageReaction?.setOnClickListener {
            message.data.takeIf { it.reactionEntity() != null }?.run {
                chatListener.detailReactionOfMsg(this)
            }
        }
        message.data.reactionEntity()?.run {
            messageReaction?.updateReaction(this)
            messageReaction?.setVisibility(true)
        } ?: run {
            messageReaction?.setVisibility(false)
        }
    }

    private fun onBindReader(message: ChatMessageModel) {

    }

    private fun getTypeReaction(position: Int): String {
        return when (position) {
            0 -> ReactionType.LIKE
            1 -> ReactionType.LOVE
            2 -> ReactionType.HAHA
            3 -> ReactionType.WOW
            4 -> ReactionType.CRY
            5 -> ReactionType.ANGRY
            else -> ""
        }
    }

    protected fun bindActionReactions(view: View?, message: MessageEntity) {
        if (message.isDeleted() || message.isRemoved() || message.isSendError() || message.isSending()) {
            view?.setOnTouchListener(null)
            view?.setOnLongClickListener(null)
            if (view is ChatMutilImageView) {
                view.disableReaction()
            }
            return
        }
        val reactionSelectedListener = object : ReactionSelectedListener {
            override fun invoke(position: Int): Boolean {
                val emoji = getTypeReaction(position)
                takeIf {
                    message.reactionEntity()?.reacted(chatListener.ownerId(),emoji) == true
                }?.run {
                    chatListener.onDeleteReaction(message.messageId(), emoji)
                } ?: kotlin.run {
                    chatListener.onReactionMessage(message.messageId(), emoji)
                }
                return true
            }
        }
        val reactionClickListener = object : ReactionClickListener {
            override fun invoke(): Boolean {
                return false
            }
        }

        val messageMenuListener = object : MessageMenuListener {
            override fun enableEdit(): Boolean {
                return message.isEnableEdit() && message.isOwnerMessage(chatListener.ownerId())
            }

            override fun enableDelete(): Boolean {
                return message.enableDelete(chatListener.ownerId())
            }

            override fun enableRevoke(): Boolean {
                return false
            }

            override fun enableQuote(): Boolean {
                //TODO REACTION Disable quote in menu reaction
                return false
            }

            override fun enableQuoteBtn(): Boolean {
//                return TextUtils.equals(
//                    message.messageType(),
//                    MessageType.TEXT
//                ) && message.messageContent().isNotEmpty()
                //TODO REACTION  tạm thời ẩn action reply message
                return true
            }

            override fun enableDownload(): Boolean {
//                return message.messageType() == MessageType.MEDIA && (message.attachmentTables?.size == 1)
                //TODO REACTION pending download media
                return false
            }

            override fun enableCopy(): Boolean {
                return TextUtils.equals(
                    message.messageType(),
                    MessageType.TEXT
                ) && message.messageContent(chatListener.ownerId()).isNotEmpty()
            }

            override fun enableClearReaction(): Boolean {
                //TODO REACTION Pending clear reaction
                return false
            }


            override fun enableForward(): Boolean {
                return true
            }

            override fun haveReaction(): Boolean {
//                return message.reactions?.find {
//                    TextUtils.equals(it.userId, chatListener.ownerId())
//                } != null
                //TODO REACTION have reaction
                return message.reactionEntity()?.reacted(chatListener.ownerId()) == true
            }

            override fun enableReaction(int: Int): Boolean {
//                return message.reactions?.find {
//                    TextUtils.equals(it.userId, chatListener.ownerId())
//                            && TextUtils.equals(getTypeReaction(int), it.type)
//                } != null
                //TODO REACTION enableReaction
                val emoji = getTypeReaction(int)
                return message.reactionEntity()?.reacted(chatListener.ownerId(),emoji) == true
            }

            override fun onDeleteMessage() {
                chatListener.onDeleteMessage(message)
            }

            override fun onRevokeMessage() {
                chatListener.onRevokeMessage(message)
            }

            override fun onQuoteMessage() {
                chatListener.onQuoteMessage(message)
            }

            override fun onEditMessage() {
                chatListener.onEditMessage(message)
            }

            override fun onDownloadMedia() {
                chatListener.onDownloadMedia(message)
            }

            override fun onCopyMessage() {
                ClipbroadUtils.copyText(itemView.context,
                    MentionUtils.getContentCopy(message))
            }

            override fun onForwardMessage() {
                chatListener.onForwardMessage(message)
            }

            override fun onClearReaction() {
                // TODO REACTION Clear Reaction
            }

            override fun onShowPopup() {
                chatListener.onShowMenuAction()
            }
        }
        reactionPopup.updateListener(
            view,
            reactionSelectedListener,
            reactionClickListener,
            messageMenuListener
        )
        if (view is ChatMutilImageView) {
            view.bindReaction(reactionSelectedListener, reactionClickListener, messageMenuListener)
        } else if (view is ChatMessageTextView) {
            view.bindReaction(reactionSelectedListener, reactionClickListener, messageMenuListener)
        }
    }

    class InComing(binding: ChatTextIncomingItemBinding, chatListener: ChatListener) :
        ChatMessageItemViewHolder(binding, chatListener) {
        init {

        }

        override fun onBind(message: MessageEntity) {
            super.onBind(message)
        }
    }

    class OutComing(binding: ChatTextOutcomingItemBinding, chatListener: ChatListener) :
        ChatMessageItemViewHolder(binding, chatListener) {
        init {
        }
    }
}