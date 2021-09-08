package com.hahalolo.incognito.presentation.conversation.adapter.viewholder

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.*
import com.hahalolo.incognito.presentation.conversation.adapter.IncognitoConversationListener
import com.hahalolo.incognito.presentation.conversation.adapter.model.IncognitoMsgGroupType
import com.hahalolo.incognito.presentation.conversation.adapter.model.IncognitoMsgModel
import com.hahalolo.incognito.presentation.view.avatar.ConversationAvatar
import com.halo.common.utils.SizeUtils
import java.text.DateFormat
import java.util.*

sealed class IncognitoMsgViewHolder(
    itemView: View,
    protected val listener: IncognitoConversationListener
) : RecyclerView.ViewHolder(itemView) {
    open fun onBind(incognitoMsgModel: IncognitoMsgModel) {

    }

    class Notify(
        private val binding: IncognitoMessageNotifyItemBinding,
        listener: IncognitoConversationListener
    ) :
        IncognitoMsgViewHolder(binding.root, listener) {
        override fun onBind(incognitoMsgModel: IncognitoMsgModel) {
            super.onBind(incognitoMsgModel)
        }
    }

    class Date(
        private val binding: IncognitoMessageDateItemBinding,
        listener: IncognitoConversationListener
    ) : IncognitoMsgViewHolder(binding.root, listener) {
        override fun onBind(incognitoMsgModel: IncognitoMsgModel) {
            super.onBind(incognitoMsgModel)
            val date = Date(System.currentTimeMillis())
            val code = listener.getLanguageCode()
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
            val time = dateFM.format(date.time) ?: ""
            if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE)) {
                content = itemView.context.getString(R.string.chat_message_holder_time_today, time)
            } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1) {
                content =
                    itemView.context.getString(R.string.chat_message_holder_time_yesterday, time)
            } else {
                content = time
            }
            binding.timeTv.text = content
        }
    }

    class Unknown(
        private val binding: IncognitoMessageUnknownItemBinding,
        listener: IncognitoConversationListener
    ) :
        IncognitoMsgViewHolder(binding.root, listener) {
        override fun onBind(incognitoMsgModel: IncognitoMsgModel) {
            super.onBind(incognitoMsgModel)
        }
    }

    class Empty(
        private val binding: IncognitoMessageEmptyItemBinding,
        listener: IncognitoConversationListener
    ) :
        IncognitoMsgViewHolder(binding.root, listener) {
        override fun onBind(incognitoMsgModel: IncognitoMsgModel) {
            super.onBind(incognitoMsgModel)
        }
    }

    sealed class Message(itemView: View, listener: IncognitoConversationListener) :
        IncognitoMsgViewHolder(itemView, listener) {

        private var messageGroup: View? = itemView.findViewById(R.id.messageGroup)

        private var messageBuble: View? = itemView.findViewById(R.id.messageBubble)
        private var messageOverlay: View? = itemView.findViewById(R.id.messageOverlay)

        private var avatar: ConversationAvatar? = itemView.findViewById(R.id.messageUserAvatar)
        private var nameTv: TextView? = itemView.findViewById(R.id.messageUserName)
        private var timeTv: TextView? = itemView.findViewById(R.id.messageTime)

        override fun onBind(incognitoMsgModel: IncognitoMsgModel) {
            super.onBind(incognitoMsgModel)
            onBindGroupType(incognitoMsgModel.groupType)
            if (incognitoMsgModel.isOutComing()){
                onBindMessageBubbleOutcoming(incognitoMsgModel.groupType)
                onBindMessageOverlayOutcoming(incognitoMsgModel.groupType)
            }else{
                onBindMessageBubbleIncoming(incognitoMsgModel.groupType)
                onBindMessageOverlayIncoming(incognitoMsgModel.groupType)
            }

            itemView.setOnClickListener {
                listener.onClickMessage(incognitoMsgModel)
            }
        }

        private fun onBindMessageBubbleIncoming(groupType: IncognitoMsgGroupType) {
            val resourceId = when (groupType) {
                IncognitoMsgGroupType.TOP -> R.drawable.bg_incognito_msg_incoming_top
                IncognitoMsgGroupType.CENTER -> R.drawable.bg_incognito_msg_incoming_center
                IncognitoMsgGroupType.LAST -> R.drawable.bg_incognito_msg_incoming_last
                else -> R.drawable.bg_incognito_msg_incoming
            }
            messageBuble?.setBackgroundResource(resourceId)
        }

        private fun onBindMessageBubbleOutcoming(groupType: IncognitoMsgGroupType) {
            val resourceId = when (groupType) {
                IncognitoMsgGroupType.TOP -> R.drawable.bg_incognito_msg_outcoming_top
                IncognitoMsgGroupType.CENTER -> R.drawable.bg_incognito_msg_outcoming_center
                IncognitoMsgGroupType.LAST -> R.drawable.bg_incognito_msg_outcoming_last
                else -> R.drawable.bg_incognito_msg_outcoming
            }
            messageBuble?.setBackgroundResource(resourceId)
        }

        private fun onBindMessageOverlayIncoming(groupType: IncognitoMsgGroupType) {
            val resourceId = when (groupType) {
                IncognitoMsgGroupType.TOP -> R.drawable.bg_incognito_msg_overlay_incoming_top
                IncognitoMsgGroupType.CENTER -> R.drawable.bg_incognito_msg_overlay_incoming_center
                IncognitoMsgGroupType.LAST -> R.drawable.bg_incognito_msg_overlay_incoming_last
                else -> R.drawable.bg_incognito_msg_overlay_incoming
            }
            messageOverlay?.setBackgroundResource(resourceId)
        }

        private fun onBindMessageOverlayOutcoming(groupType: IncognitoMsgGroupType) {
            val resourceId = when (groupType) {
                IncognitoMsgGroupType.TOP -> R.drawable.bg_incognito_msg_overlay_outcoming_top
                IncognitoMsgGroupType.CENTER -> R.drawable.bg_incognito_msg_overlay_outcoming_center
                IncognitoMsgGroupType.LAST -> R.drawable.bg_incognito_msg_overlay_outcoming_last
                else -> R.drawable.bg_incognito_msg_overlay_outcoming
            }
            messageOverlay?.setBackgroundResource(resourceId)
        }

        open fun onBindGroupType(groupType: IncognitoMsgGroupType) {
            messageGroup?.run {
                val params = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                when (groupType) {
                    IncognitoMsgGroupType.TOP -> params.setMargins(
                        0,
                        SizeUtils.dp2px(MARGIN_NON_GROUP),
                        0,
                        SizeUtils.dp2px(MARGIN_GROUP)
                    )
                    IncognitoMsgGroupType.CENTER -> params.setMargins(
                        0,
                        SizeUtils.dp2px(MARGIN_GROUP),
                        0,
                        SizeUtils.dp2px(MARGIN_GROUP)
                    )
                    IncognitoMsgGroupType.LAST -> params.setMargins(
                        0,
                        SizeUtils.dp2px(MARGIN_GROUP),
                        0,
                        SizeUtils.dp2px(MARGIN_NON_GROUP)
                    )
                    IncognitoMsgGroupType.ONLY -> params.setMargins(
                        0,
                        SizeUtils.dp2px(MARGIN_NON_GROUP),
                        0,
                        SizeUtils.dp2px(MARGIN_NON_GROUP)
                    )
                }
                this.layoutParams = params
            }
        }

        sealed class Text(itemView: View, listener: IncognitoConversationListener) :
            Message(itemView, listener) {

            override fun onBind(incognitoMsgModel: IncognitoMsgModel) {
                super.onBind(incognitoMsgModel)
            }

            class InComing(
                binding: IncognitoMessageTextIncomingItemBinding,
                listener: IncognitoConversationListener
            ) : Text(binding.root, listener) {

                override fun onBindGroupType(groupType: IncognitoMsgGroupType) {
                    super.onBindGroupType(groupType)
                }
            }

            class OutComing(
                binding: IncognitoMessageTextOutcomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                Text(binding.root, listener) {

                override fun onBindGroupType(groupType: IncognitoMsgGroupType) {
                    super.onBindGroupType(groupType)
                }
            }
        }

        sealed class Image(itemView: View, listener: IncognitoConversationListener) :
            Message(itemView, listener) {
            class InComing(
                binding: IncognitoMessageImageIncomingItemBinding,
                listener: IncognitoConversationListener
            ) : Image(binding.root, listener) {

                override fun onBindGroupType(groupType: IncognitoMsgGroupType) {
                    super.onBindGroupType(groupType)
                }
            }

            class OutComing(
                binding: IncognitoMessageImageOutcomingItemBinding,
                listener: IncognitoConversationListener
            ) : Image(binding.root, listener) {

                override fun onBindGroupType(groupType: IncognitoMsgGroupType) {
                    super.onBindGroupType(groupType)
                }
            }
        }

        sealed class Video(itemView: View, listener: IncognitoConversationListener) :
            Message(itemView, listener) {
            class InComing(
                binding: IncognitoMessageVideoIncomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                Video(binding.root, listener) {

            }

            class OutComing(
                binding: IncognitoMessageVideoOutcomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                Video(binding.root, listener) {

            }
        }

        sealed class Sticker(itemView: View, listener: IncognitoConversationListener) :
            Message(itemView, listener) {
            class InComing(
                binding: IncognitoMessageStickerIncomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                Sticker(binding.root, listener) {

            }

            class OutComing(
                binding: IncognitoMessageStickerOutcomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                Sticker(binding.root, listener) {

            }
        }

        sealed class Gif(itemView: View, listener: IncognitoConversationListener) :
            Message(itemView, listener) {
            class InComing(
                binding: IncognitoMessageGifIncomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                Gif(binding.root, listener) {

            }

            class OutComing(
                binding: IncognitoMessageGifOutcomingItemBinding,
                listener: IncognitoConversationListener
            ) : Gif(binding.root, listener) {

            }
        }

        sealed class Link(itemView: View, listener: IncognitoConversationListener) :
            Message(itemView, listener) {
            class InComing(
                binding: IncognitoMessageLinkIncomingItemBinding,
                listener: IncognitoConversationListener
            ) : Link(binding.root, listener) {

            }

            class OutComing(
                binding: IncognitoMessageLinkOutcomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                Link(binding.root, listener) {

            }
        }

        sealed class File(itemView: View, listener: IncognitoConversationListener) :
            Message(itemView, listener) {
            class InComing(
                binding: IncognitoMessageFileIncomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                File(binding.root, listener) {

            }

            class OutComing(
                binding: IncognitoMessageFileOutcomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                File(binding.root, listener) {

            }
        }

        sealed class Removed(itemView: View, listener: IncognitoConversationListener) :
            Message(itemView, listener) {
            class InComing(
                binding: IncognitoMessageRemovedIncomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                Removed(binding.root, listener) {

            }

            class OutComing(
                binding: IncognitoMessageRemovedOutcomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                Removed(binding.root, listener) {

            }
        }

        sealed class Replay(itemView: View, listener: IncognitoConversationListener) :
            Message(itemView, listener) {
            class InComing(
                binding: IncognitoMessageReplayIncomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                Replay(binding.root, listener) {

            }

            class OutComing(
                binding: IncognitoMessageReplayOutcomingItemBinding,
                listener: IncognitoConversationListener
            ) :
                Replay(binding.root, listener) {

            }
        }

        class Typing(
            binding: IncognitoMessageTypingItemBinding,
            listener: IncognitoConversationListener
        ) : Message(binding.root, listener) {

        }
    }

    companion object {
        const val MARGIN_GROUP = 1f
        const val MARGIN_NON_GROUP = 8f

        fun create(
            parent: ViewGroup,
            viewType: Int,
            listener: IncognitoConversationListener
        ): IncognitoMsgViewHolder {

            return when (viewType) {
                IncognitoMsgViewType.VIEW_TYPE_EMPTY -> {
                    val binding: IncognitoMessageEmptyItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_empty_item, parent, false
                    )
                    Empty(binding, listener)
                }

                IncognitoMsgViewType.VIEW_TYPE_TEXT_INCOMING -> {
                    val binding: IncognitoMessageTextIncomingItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_text_incoming_item, parent, false
                    )
                    Message.Text.InComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_TEXT_OUTCOMING -> {
                    val binding: IncognitoMessageTextOutcomingItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_text_incoming_item, parent, false
                    )
                    Message.Text.OutComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_IMAGE_INCOMING -> {
                    val binding: IncognitoMessageImageIncomingItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_image_incoming_item, parent, false
                    )
                    Message.Image.InComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_IMAGE_OUTCOMING -> {
                    val binding: IncognitoMessageImageOutcomingItemBinding =
                        DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.incognito_message_image_incoming_item, parent, false
                        )
                    Message.Image.OutComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_VIDEO_INCOMING -> {
                    val binding: IncognitoMessageVideoIncomingItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_video_incoming_item, parent, false
                    )
                    Message.Video.InComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_VIDEO_OUTCOMING -> {
                    val binding: IncognitoMessageVideoOutcomingItemBinding =
                        DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.incognito_message_video_incoming_item, parent, false
                        )
                    Message.Video.OutComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_STICKER_INCOMING -> {
                    val binding: IncognitoMessageStickerIncomingItemBinding =
                        DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.incognito_message_sticker_incoming_item, parent, false
                        )
                    Message.Sticker.InComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_STICKER_OUTCOMING -> {
                    val binding: IncognitoMessageStickerOutcomingItemBinding =
                        DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.incognito_message_sticker_incoming_item, parent, false
                        )
                    Message.Sticker.OutComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_GIF_INCOMING -> {
                    val binding: IncognitoMessageGifIncomingItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_gif_incoming_item, parent, false
                    )
                    Message.Gif.InComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_GIF_OUTCOMING -> {
                    val binding: IncognitoMessageGifOutcomingItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_gif_incoming_item, parent, false
                    )
                    Message.Gif.OutComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_LINK_INCOMING -> {
                    val binding: IncognitoMessageLinkIncomingItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_link_incoming_item, parent, false
                    )
                    Message.Link.InComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_LINK_OUTCOMING -> {
                    val binding: IncognitoMessageLinkOutcomingItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_link_incoming_item, parent, false
                    )
                    Message.Link.OutComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_FILE_INCOMING -> {
                    val binding: IncognitoMessageFileIncomingItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_file_incoming_item, parent, false
                    )
                    Message.File.InComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_FILE_OUTCOMING -> {
                    val binding: IncognitoMessageFileOutcomingItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_file_incoming_item, parent, false
                    )
                    Message.File.OutComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_REMOVED_INCOMING -> {
                    val binding: IncognitoMessageRemovedIncomingItemBinding =
                        DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.incognito_message_removed_incoming_item, parent, false
                        )
                    Message.Removed.InComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_REMOVED_OUTCOMING -> {
                    val binding: IncognitoMessageRemovedOutcomingItemBinding =
                        DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.incognito_message_removed_incoming_item, parent, false
                        )
                    Message.Removed.OutComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_REPLAY_INCOMING -> {
                    val binding: IncognitoMessageReplayIncomingItemBinding =
                        DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.incognito_message_replay_incoming_item, parent, false
                        )
                    Message.Replay.InComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_REPLAY_OUTCOMING -> {
                    val binding: IncognitoMessageReplayOutcomingItemBinding =
                        DataBindingUtil.inflate(
                            LayoutInflater.from(parent.context),
                            R.layout.incognito_message_replay_incoming_item, parent, false
                        )
                    Message.Replay.OutComing(binding, listener)
                }
                IncognitoMsgViewType.VIEW_TYPE_TYPING -> {
                    val binding: IncognitoMessageTypingItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_typing_item, parent, false
                    )
                    Message.Typing(binding, listener)
                }

                IncognitoMsgViewType.VIEW_TYPE_DATE -> {
                    val binding: IncognitoMessageDateItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_date_item, parent, false
                    )
                    Date(binding, listener)
                }

                IncognitoMsgViewType.VIEW_TYPE_NOTIFICATION -> {
                    val binding: IncognitoMessageNotifyItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_notify_item, parent, false
                    )
                    Notify(binding, listener)
                }

                else -> {
                    val binding: IncognitoMessageUnknownItemBinding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.incognito_message_unknown_item, parent, false
                    )
                    Unknown(binding, listener)
                }
            }
        }
    }
}