package com.hahalolo.messager.presentation.message.adapter

import android.text.TextUtils
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.adapter.SeenMessageListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_DATE
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_FILE_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_FILE_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_GIF_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_GIF_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_IMAGE_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_IMAGE_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_LINK_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_LINK_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_NOTIFICATION
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_REMOVED_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_REMOVED_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_REPLAY_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_REPLAY_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_STICKER_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_STICKER_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_TEXT_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_TEXT_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_TYPING_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_TYPING_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_UNKNOWN
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_VIDEO_INCOMING
import com.hahalolo.messager.presentation.message.adapter.ChatMessageViewType.Companion.VIEW_TYPE_VIDEO_OUTCOMING
import com.hahalolo.messager.presentation.message.adapter.holder.ChatMessageViewHolder
import com.hahalolo.messenger.R
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.type.AttachmentType
import com.halo.data.room.type.MessageStatus
import com.halo.data.room.type.MessageType

class ChatMessageAdapter(
    ownerId: String,
    chatListener: ChatListener,
    val imageLoader: ImageLoader
) : AbsChatMessageAdapter(ownerId, imageLoader.getRequestManager(), chatListener) {

    private var seenMessageListener: SeenMessageListener? = null

    fun setSeenMessageSListener(seenMessageListener: SeenMessageListener?) {
        this.seenMessageListener = seenMessageListener
    }

    override fun areContentsTheSame(oldItem: MessageEntity, newItem: MessageEntity): Boolean {
        return TextUtils.equals(oldItem.messageId(), newItem.messageId())
                && TextUtils.equals(oldItem.messageType(), newItem.messageType())
                && TextUtils.equals(oldItem.userId(), newItem.userId())
                && TextUtils.equals(oldItem.reactionEntity().toString(), newItem.reactionEntity().toString())
                && TextUtils.equals(oldItem.messageText(), newItem.messageText())
                && TextUtils.equals(oldItem.quoteMessage?.messageId(), newItem.quoteMessage?.messageId())
                && TextUtils.equals(oldItem.quoteMessage?.messageType(), newItem.quoteMessage?.messageType())
                && TextUtils.equals(oldItem.quoteMessage?.userId(), newItem.quoteMessage?.userId())
                && TextUtils.equals(oldItem.quoteMessage?.messageText(), newItem.quoteMessage?.messageText())
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        getItem(position)?.run {
            seenMessageListener?.onSeenMessage(this.id())
        }
    }

    override fun holderLayout(viewType: Int): Int {
        when (viewType) {
            VIEW_TYPE_TEXT_INCOMING -> {
                return R.layout.chat_text_incoming_item
            }
            VIEW_TYPE_IMAGE_INCOMING -> {
                return R.layout.chat_image_incoming_item
            }
            VIEW_TYPE_VIDEO_INCOMING -> {
                return R.layout.chat_video_incoming_item
            }
            VIEW_TYPE_STICKER_INCOMING -> {
                return R.layout.chat_sticker_incoming_item
            }
            VIEW_TYPE_GIF_INCOMING -> {
                return R.layout.chat_gif_incoming_item
            }
            VIEW_TYPE_LINK_INCOMING -> {
                return R.layout.chat_link_incomming_item
            }
            VIEW_TYPE_FILE_INCOMING -> {
                return R.layout.chat_file_incoming_item
            }
            VIEW_TYPE_REMOVED_INCOMING -> {
                return R.layout.chat_delete_incoming_item
            }
            VIEW_TYPE_REPLAY_INCOMING -> {
                return R.layout.chat_replay_incoming_item
            }
            VIEW_TYPE_TEXT_OUTCOMING -> {
                return R.layout.chat_text_outcoming_item
            }
            VIEW_TYPE_IMAGE_OUTCOMING -> {
                return R.layout.chat_image_outcoming_item
            }
            VIEW_TYPE_VIDEO_OUTCOMING -> {
                return R.layout.chat_video_outcoming_item
            }
            VIEW_TYPE_STICKER_OUTCOMING -> {
                return R.layout.chat_sticker_outcoming_item
            }
            VIEW_TYPE_GIF_OUTCOMING -> {
                return R.layout.chat_gif_outcoming_item
            }
            VIEW_TYPE_LINK_OUTCOMING -> {
                return R.layout.chat_link_outcomming_item
            }
            VIEW_TYPE_FILE_OUTCOMING -> {
                return R.layout.chat_file_outcoming_item
            }
            VIEW_TYPE_REMOVED_OUTCOMING -> {
                return R.layout.chat_delete_outcoming_item
            }
            VIEW_TYPE_REPLAY_OUTCOMING -> {
                return R.layout.chat_text_outcoming_item
            }
            VIEW_TYPE_TYPING_INCOMING -> {
                return R.layout.chat_typing_incoming_item
            }
            VIEW_TYPE_TYPING_OUTCOMING -> {
                return R.layout.chat_typing_outcoming_item
            }
            VIEW_TYPE_DATE -> {
                return R.layout.chat_date_holder_item
            }
            VIEW_TYPE_NOTIFICATION -> {
                return R.layout.chat_notification_item
            }
            VIEW_TYPE_UNKNOWN -> {
                return R.layout.chat_empty_holder_item
            }
        }
        return R.layout.chat_empty_holder_item
    }

    override fun emptyLayout(): Int {
        return R.layout.chat_empty_holder_item
    }

    override fun getItemViewType(position: Int): Int {
        val data = getItem(position)
        val isOutcoming = TextUtils.equals(
            data?.data?.userId(),
            ownerId
        )
        when (data?.data?.messageType()) {
            MessageType.TEXT -> {
                when (data.data.attachmentType()) {
                    AttachmentType.IMAGE,
                    AttachmentType.VIDEO -> {
                        return if (isOutcoming) VIEW_TYPE_IMAGE_OUTCOMING else VIEW_TYPE_IMAGE_INCOMING
                    }
                    AttachmentType.GIF->{
                        return if (isOutcoming) VIEW_TYPE_GIF_OUTCOMING else VIEW_TYPE_GIF_OUTCOMING
                    }
                    AttachmentType.STICKER->{
                        return if (isOutcoming) VIEW_TYPE_STICKER_OUTCOMING else VIEW_TYPE_STICKER_INCOMING
                    }
                }
                when(data.data.messageStatus()){
                    MessageStatus.TYPING->{
                        return if (isOutcoming) VIEW_TYPE_TYPING_OUTCOMING else VIEW_TYPE_TYPING_INCOMING
                    }
                }
                return if (isOutcoming) VIEW_TYPE_TEXT_OUTCOMING else VIEW_TYPE_TEXT_INCOMING
            }
            MessageType.SYSTEM -> return VIEW_TYPE_NOTIFICATION
            MessageType.DELETED,
            MessageType.REVOKED -> return if (isOutcoming) VIEW_TYPE_REMOVED_OUTCOMING else VIEW_TYPE_REMOVED_INCOMING
        }
        return -1
    }
}