/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder

import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.messager.bubble.BubbleService
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.create_group.ChatCreateGroupHolderItem
import com.hahalolo.messager.chatkit.holder.date.ChatDateHolderItem
import com.hahalolo.messager.chatkit.holder.deleted.ChatDeletedInComingItem
import com.hahalolo.messager.chatkit.holder.deleted.ChatDeletedOutComingItem
import com.hahalolo.messager.chatkit.holder.empty.ChatEmptyViewHolder
import com.hahalolo.messager.chatkit.holder.file.ChatFileInComingItem
import com.hahalolo.messager.chatkit.holder.file.ChatFileOutComingItem
import com.hahalolo.messager.chatkit.holder.gif.ChatGifInComingItem
import com.hahalolo.messager.chatkit.holder.gif.ChatGifOutComingItem
import com.hahalolo.messager.chatkit.holder.image.ChatImageInComingItem
import com.hahalolo.messager.chatkit.holder.image.ChatImageOutComingItem
import com.hahalolo.messager.chatkit.holder.link.ChatLinkInComingItem
import com.hahalolo.messager.chatkit.holder.link.ChatLinkOutComingItem
import com.hahalolo.messager.chatkit.holder.mutil_image.ChatMutilImageInComingItem
import com.hahalolo.messager.chatkit.holder.mutil_image.ChatMutilImageOutComingItem
import com.hahalolo.messager.chatkit.holder.notification.ChatNotificationHolderItem
import com.hahalolo.messager.chatkit.holder.replay.ChatReplayInComingItem
import com.hahalolo.messager.chatkit.holder.replay.ChatReplayOutComingItem
import com.hahalolo.messager.chatkit.holder.sticker.ChatStickerInComingItem
import com.hahalolo.messager.chatkit.holder.sticker.ChatStickerOutComingItem
import com.hahalolo.messager.chatkit.holder.text.ChatTextInComingItem
import com.hahalolo.messager.chatkit.holder.text.ChatTextOutComingItem
import com.hahalolo.messager.chatkit.holder.video.ChatVideoInComingItem
import com.hahalolo.messager.chatkit.holder.video.ChatVideoOutComingItem
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.type.SaveType
import com.halo.common.utils.ScreenUtils
import com.halo.common.utils.SizeUtils
import com.halo.data.room.type.AttachmentType
import com.halo.data.room.type.MessageType
import java.util.*

open class ChatMessageHolders(
    var chatListener: ChatListener,
    var imageLoader: ImageLoader
) : HolderListener {

    companion object {
        const val VIEW_TYPE_UNKNOWN: Int = -1
        const val VIEW_TYPE_EMPTY: Int = 0
        const val VIEW_TYPE_DATE_HEADER: Int = 130
        const val VIEW_TYPE_TEXT_MESSAGE: Int = 131
        const val VIEW_TYPE_IMAGE_MESSAGE: Int = 132
        const val VIEW_TYPE_NOTIFICATION: Int = 133
        const val VIEW_TYPE_STICKER_MESSAGE: Int = 134
        const val VIEW_TYPE_VIDEO_MESSAGE: Int = 135
        const val VIEW_TYPE_FILE_MESSAGE: Int = 136
        const val VIEW_TYPE_GIF_MESSAGE: Int = 137
        const val VIEW_TYPE_WEB_MESSAGE: Int = 140
        const val VIEW_TYPE_REPLAY_MESSAGE: Int = 141
        const val VIEW_TYPE_IMAGE_MESSAGE_GALLERY: Int = 142
        const val VIEW_TYPE_REMOVED: Int = 143
        const val VIEW_TYPE_NETWORK_STATUS: Int = -2
        const val VIEW_TYPE_CREATE_GROUP = 144
    }

    private val WIDTH_SCREEN = Math.min(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight())
    private val HEIGHT_SCREEN = Math.max(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight())

    fun getViewType(item: Any?, senderId: String?): Int {
        var isOutcoming = false
        val viewType: Int
        if (item is MessageEntity) {
//            isOutcoming = TextUtils.equals(item.member?.userId(), senderId)
            viewType = getContentViewType(item)
        } else viewType = VIEW_TYPE_DATE_HEADER
        return if (isOutcoming) viewType * -1 else viewType
    }

    @SuppressWarnings("unchecked")
    private fun getContentViewType(message: MessageEntity): Int {
        //TODO GET VIEWTYPE IMAGE
        when (message.messageType()) {
            MessageType.TEXT -> {
                val atmType = ""
                when (atmType) {
                    AttachmentType.DOCUMENT -> return VIEW_TYPE_FILE_MESSAGE
                    AttachmentType.OTHER -> return VIEW_TYPE_UNKNOWN
                    AttachmentType.MEDIA -> return VIEW_TYPE_IMAGE_MESSAGE
                }
                return VIEW_TYPE_TEXT_MESSAGE
            }
            MessageType.STICKER -> {
                return VIEW_TYPE_STICKER_MESSAGE
            }
            MessageType.GIF ->{
                return VIEW_TYPE_GIF_MESSAGE
            }
            MessageType.WAVE -> return VIEW_TYPE_STICKER_MESSAGE
            MessageType.SYSTEM -> {
                if (message.isCreateGroupMsg()) {
                    return VIEW_TYPE_CREATE_GROUP
                }
                return VIEW_TYPE_NOTIFICATION
            }
            MessageType.REVOKED,
            MessageType.DELETED -> return VIEW_TYPE_REMOVED
            else -> return VIEW_TYPE_UNKNOWN
        }
    }

    fun getHolder(
        parent: ViewGroup,
        viewType: Int,
        messagesListStyle: MessagesListStyle
    ): RecyclerView.ViewHolder {
        when (viewType) {
            (-VIEW_TYPE_DATE_HEADER),
            VIEW_TYPE_DATE_HEADER -> return ChatDateHolderItem.create(
                parent,
                chatListener,
                messagesListStyle
            )

            VIEW_TYPE_TEXT_MESSAGE -> return ChatTextInComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            -VIEW_TYPE_TEXT_MESSAGE -> return ChatTextOutComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            VIEW_TYPE_IMAGE_MESSAGE -> return ChatImageInComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            -VIEW_TYPE_IMAGE_MESSAGE -> return ChatImageOutComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            VIEW_TYPE_IMAGE_MESSAGE_GALLERY -> return ChatMutilImageInComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            -VIEW_TYPE_IMAGE_MESSAGE_GALLERY -> return ChatMutilImageOutComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            (-VIEW_TYPE_NOTIFICATION),
            VIEW_TYPE_NOTIFICATION -> return ChatNotificationHolderItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            VIEW_TYPE_STICKER_MESSAGE -> return ChatStickerInComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            -VIEW_TYPE_STICKER_MESSAGE -> return ChatStickerOutComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            VIEW_TYPE_GIF_MESSAGE -> return ChatGifInComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            -VIEW_TYPE_GIF_MESSAGE -> return ChatGifOutComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )

            VIEW_TYPE_VIDEO_MESSAGE -> return ChatVideoInComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            -VIEW_TYPE_VIDEO_MESSAGE -> return ChatVideoOutComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            VIEW_TYPE_FILE_MESSAGE -> return ChatFileInComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            -VIEW_TYPE_FILE_MESSAGE -> return ChatFileOutComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            VIEW_TYPE_WEB_MESSAGE -> return ChatLinkInComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            -VIEW_TYPE_WEB_MESSAGE -> return ChatLinkOutComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            VIEW_TYPE_REPLAY_MESSAGE -> return ChatReplayOutComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            -VIEW_TYPE_REPLAY_MESSAGE -> return ChatReplayInComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            VIEW_TYPE_REMOVED -> return ChatDeletedInComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            -VIEW_TYPE_REMOVED -> return ChatDeletedOutComingItem.create(
                parent,
                chatListener,
                this,
                imageLoader,
                messagesListStyle
            )
            (-VIEW_TYPE_CREATE_GROUP),
            VIEW_TYPE_CREATE_GROUP -> {
                return ChatCreateGroupHolderItem.create(
                    parent,
                    chatListener,
                    imageLoader,
                    messagesListStyle
                )
            }
            (-VIEW_TYPE_EMPTY),
            VIEW_TYPE_EMPTY -> {
                return getHolderEmpty(parent)
            }

        }
        return getHolderEmpty(parent)
    }

    private fun getHolderEmpty(
        parent: ViewGroup
    ): RecyclerView.ViewHolder {
        return ChatEmptyViewHolder.create(parent)
    }

    fun onBind(
        holder: RecyclerView.ViewHolder,
        item: Any?
    ) {
        if (item is Date && holder is ChatDateHolderItem) {
            holder.onBind(item)
        } else if (item is MessageEntity && holder is ChatHolderItem<*>) {
            holder.onBind(item )
            if (BubbleService.DEBUG){
                if (holder.binding.root.context is Activity){
                    when(item.saveType()){
                        SaveType.NEW->{
                            holder.itemView.setBackgroundColor(Color.GREEN)
                        }
                        SaveType.OLD->{
                            holder.itemView.setBackgroundColor(Color.BLUE)
                        }
                    }
                }else{
                    when(item.bubbleSave()){
                        SaveType.NEW->{
                            holder.itemView.setBackgroundColor(Color.GREEN)
                        }
                        SaveType.OLD->{
                            holder.itemView.setBackgroundColor(Color.BLUE)
                        }
                    }
                }
            }
        }
    }

    private var idShowTime: String? = null
    private var viewTime: View? = null

    private var idShowStt: String? = null
    private var viewStt: View? = null


    interface ChangeSizeListeneer : Animator.AnimatorListener {
        override fun onAnimationStart(animator: Animator?) {}
        override fun onAnimationEnd(animator: Animator?)
        override fun onAnimationCancel(animator: Animator?) {}
        override fun onAnimationRepeat(animator: Animator?) {}
    }

    fun clearShowTime(listener: ChangeSizeListeneer): Boolean {
        if (viewTime != null && !TextUtils.isEmpty(idShowTime)) {
            changeSize(viewTime!!, false, object : ChangeSizeListeneer {
                override fun onAnimationEnd(animator: Animator?) {
                    idShowTime = null
                    viewTime = null
                    listener.onAnimationEnd(animator)
                }
            })
            return true
        }
        return false
    }

    private var changSizing = false
    private var changStt = false

    override fun onShowTimeWhenClick(id: String?, timeView: View?) {
        if (changSizing) return
        changSizing = true
        if (TextUtils.equals(idShowTime, id)) {
            val listener: ChangeSizeListeneer = object : ChangeSizeListeneer {
                override fun onAnimationEnd(animator: Animator?) {
                    idShowTime = null
                    viewTime = null
                    changSizing = false
                }
            }
            if (timeView != null) {
                changeSize(timeView, false, listener)
            } else {
                listener.onAnimationEnd(null)
            }
        } else {
            var isGone = false
            if (!TextUtils.isEmpty(idShowTime)) {
                if (viewTime is View
                    && (viewTime as View).tag is String
                    && TextUtils.equals((viewTime as View).tag as String, idShowTime)
                ) {
                    isGone = true
                }
            }
            val listener: ChangeSizeListeneer = object : ChangeSizeListeneer {
                override fun onAnimationEnd(animator: Animator?) {
                    idShowTime = id
                    viewTime = timeView
                    changSizing = false
                }
            }
            if (timeView != null) {
                if (isGone && viewTime is View) {
                    changeSize(timeView, viewTime as View, listener)
                } else {
                    changeSize(timeView, true, listener)
                }
            } else {
                listener.onAnimationEnd(null)
            }
        }
    }

    override fun onShowStatusWhenClick(id: String?, statusView: View?) {
        if (changStt) return
        changStt = true
        if (TextUtils.equals(idShowStt, id)) {
            val listener: ChangeSizeListeneer = object : ChangeSizeListeneer {
                override fun onAnimationEnd(animator: Animator?) {
                    idShowStt = null
                    viewStt = null
                    changStt = false
                }
            }
            if (statusView != null) {
                changeSize(statusView, false, listener)
            } else {
                listener.onAnimationEnd(null)
            }
        } else {
            var isGone = false
            if (!TextUtils.isEmpty(idShowStt)) {
                if (viewStt is View
                    && (viewStt as View).tag is String
                    && TextUtils.equals((viewStt as View).tag as String, idShowStt)
                ) {
                    isGone = true
                }
            }
            val listener: ChangeSizeListeneer = object : ChangeSizeListeneer {
                override fun onAnimationEnd(animator: Animator?) {
                    idShowStt = id
                    viewStt = statusView
                    changStt = false
                }
            }
            if (statusView != null) {
                if (isGone && viewStt is View) {
                    changeSize(statusView, viewStt as View, listener)
                } else {
                    changeSize(statusView, true, listener)
                }
            } else {
                listener.onAnimationEnd(null)
            }
        }
    }

    override fun getWIDTH_SCREEN(): Int {
        return WIDTH_SCREEN
    }

    override fun getHEIGHT_SCREEN(): Int {
        return HEIGHT_SCREEN
    }

    private fun changeSize(show: View, gone: View, iChangeSize: ChangeSizeListeneer) {
        val height = SizeUtils.dp2px(16f)
        show.visibility = View.VISIBLE
        val anim = ValueAnimator.ofInt(0, height)
        anim.addUpdateListener { valueAnimator: ValueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams = show.layoutParams
            layoutParams.height = `val`
            show.layoutParams = layoutParams
            val valGone = height - `val`
            val layoutParamsGone = gone.layoutParams
            layoutParamsGone.height = valGone
            gone.layoutParams = layoutParamsGone
            if (`val` == height) {
                gone.visibility = View.GONE
            }
        }
        anim.addListener(iChangeSize)
        anim.duration = 200
        anim.start()
    }

    private fun changeSize(
        viewToIncreaseHeight: View,
        show: Boolean,
        iChangeSize: ChangeSizeListeneer
    ) {
        val height = SizeUtils.dp2px(16f)

        val start = if (show) 0 else height
        val end = if (show) height else 0
        if (show) {
            viewToIncreaseHeight.visibility = View.VISIBLE
        }
        val anim = ValueAnimator.ofInt(start, end)
        anim.addUpdateListener { valueAnimator: ValueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            val layoutParams = viewToIncreaseHeight.layoutParams
            layoutParams.height = `val`
            viewToIncreaseHeight.layoutParams = layoutParams
            if (`val` == 0 && !show) {
                viewToIncreaseHeight.visibility = View.GONE
            }
        }
        anim.addListener(iChangeSize)
        anim.duration = if (show) 200 else 200.toLong()
        anim.start()
    }

    fun getHolderGroupType(
        befor: MessageEntity?,
        messageEntity: MessageEntity,
        after: MessageEntity?,
        holder: RecyclerView.ViewHolder
    ): MessageGroupType {
        if (holder is ChatHolderItem<*>) {
            val beforIsGr = isOfGroup(befor, messageEntity)
            val afterIsGr = isOfGroup(after, messageEntity)
            if (beforIsGr && afterIsGr) {
                return MessageGroupType.MEDIUM
            } else if (beforIsGr) {
                return MessageGroupType.START
            } else if (afterIsGr) {
                return MessageGroupType.LAST
            } else {
                return MessageGroupType.ONLY
            }
        }
        return MessageGroupType.ONLY
    }

    private fun isOfGroup(m: MessageEntity?, m2: MessageEntity?): Boolean {
        if (m != null && m2 != null     // 2 tin nhanc khac null
            && isOneUserSend(m, m2)     // cung la do  1 nguoi gui
            && isMessageContent(m)      // la tin nhan noi dung
            && isMessageContent(m2)     // tin nhan 2 cung la tin nhan noi dung
            && isIntimeGroup(m, m2)     // 2 tin gửi ko cách nhau 1 phút
        ) {
            return true
        }
        return false
    }

    private fun isOneUserSend(m: MessageEntity?, m2: MessageEntity?): Boolean {
//        return TextUtils.equals(m?.member?.userId(), m2?.member?.userId())
        return false
    }

    private fun isIntimeGroup(m: MessageEntity?, m2: MessageEntity?): Boolean {
        val time = m?.messageTime()
        val time2 = m2?.messageTime()
        if (time != null && time2 != null) {
            return Math.abs(time - time2) <= 60000
        }
        return false
    }

    private fun isMessageContent(m: MessageEntity): Boolean {
        return !TextUtils.equals(m.messageType(), MessageType.SYSTEM)
    }
}