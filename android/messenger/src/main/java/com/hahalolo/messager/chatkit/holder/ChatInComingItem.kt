/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.view.ChatMessageUserAvatar
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messenger.R

open class ChatInComingItem<BINDING : ViewDataBinding>(
    binding: BINDING,
    chatListener: ChatListener,
    holderListener: HolderListener,
    imageLoader: ImageLoader,
    style: MessagesListStyle

) : ChatHolderItem<BINDING>(binding, chatListener, holderListener, imageLoader, style) {

    protected var userAvatar: ChatMessageUserAvatar? = null
    protected var userName: TextView? = null
    protected var edit: View? = null

    override fun initView(binding: BINDING) {
        super.initView(binding)
        userName = itemView.findViewById(R.id.messageUserName)
        userAvatar = itemView.findViewById(R.id.messageUserAvatarGr)
        edit = itemView.findViewById(R.id.messageStatusEdit)
    }

    override fun onBind(iMessage: MessageEntity) {
        super.onBind(iMessage)
//        userName?.text = iMessage.member?.getUserName() ?: ""

        edit?.visibility = if (!iMessage.isEdited() || iMessage.isSending() || iMessage.isSendError()) View.GONE else View.VISIBLE

        // update avatar layout
        userAvatar?.updateImageLoader(imageLoader)
//        userAvatar?.updateMemberEntity(iMessage.member)
        userAvatar?.setOnClickListener { chatListener.onAvatarClick(userAvatar, iMessage) }
    }

    override fun onMessageGroupType(messageGroupType: MessageGroupType) {
        super.onMessageGroupType(messageGroupType)
        when (messageGroupType) {
            MessageGroupType.START -> {
                userName?.visibility = View.VISIBLE
                time?.visibility = View.GONE
                userAvatar?.visibility = View.INVISIBLE
            }
            MessageGroupType.MEDIUM -> {
                userName?.visibility = View.GONE
                time?.visibility = View.GONE
                userAvatar?.visibility = View.INVISIBLE
            }
            MessageGroupType.LAST -> {
                userName?.visibility = View.GONE
                time?.visibility = View.GONE
                userAvatar?.visibility = View.VISIBLE
            }
            MessageGroupType.ONLY -> {
                userName?.visibility = View.VISIBLE
                time?.visibility = View.GONE
                userAvatar?.visibility = View.VISIBLE
            }
        }
    }

    override fun applyStyle(style: MessagesListStyle) {
        super.applyStyle(style)
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        super.invalidateLayout(requestManager)
        userAvatar?.invalidateLayout(requestManager)

    }

    override fun getTargets(): MutableList<ImageView> {
        val result = super.getTargets()
        userAvatar?.getTargets()?.run{
            result.addAll(this)
        }
        return result
    }
}



















