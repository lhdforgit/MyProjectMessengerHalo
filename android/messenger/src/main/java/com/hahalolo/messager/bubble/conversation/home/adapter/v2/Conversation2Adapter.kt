/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.home.adapter.v2

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.bubble.conversation.home.adapter.*
import com.hahalolo.messager.presentation.adapter.holder.NetworkStateListener
import com.halo.data.room.entity.ChannelEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.MessageConversationItemBinding
import com.hahalolo.messenger.databinding.MessageSearchInputItemBinding
import com.hahalolo.messenger.databinding.MessageSearchItemBinding
import com.halo.data.room.type.ChannelType

class Conversation2Adapter constructor(
    listener: ChatAdapterListener<ChannelEntity>,
    requestManager: RequestManager,
    val context: Context,
    viewPreloadSizeProvider: ViewPreloadSizeProvider<ChannelEntity>,
    @NonNull diffCallback: RoomDiffCallback,
    networkStateListener: NetworkStateListener,
    val userIdToken: String?,
    val appLang: String?
) : ChatAbsSwipe2Adapter<ChannelEntity>(
    diffCallback = diffCallback,
    requestManager = requestManager,
    listener = listener,
    preloadSizeProvider = viewPreloadSizeProvider,
    networkStateListener = networkStateListener
) {

    var searchListener: SearchInputListener? = null



    override fun getSwipeLayoutResourceId(position: Int): Int {
        return R.id.swipe
    }

    override fun onCreateChatHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ConversationHolder(
            binding = MessageConversationItemBinding.inflate(inflater, parent, false),
            requestManager = requestManager,
            listener = listener,
            swipeListener = this,
            userIdToken = userIdToken,
            appLang = appLang
        )
    }

    override fun onCreateHeaderHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return searchListener?.run {
            SearchInputViewHolder(MessageSearchInputItemBinding.inflate(inflater, parent, false), this)
        } ?: SearchViewHolder(
            MessageSearchItemBinding.inflate(inflater, parent, false)
        ).also {
            it.binding.searchTv.setOnClickListener {
                listener.search()
            }
        }
    }

    override fun onBindChatHolder(holder: RecyclerView.ViewHolder, f: ChannelEntity) {
        if (holder is ConversationHolder) {
            holder.bind(f, context)
        }
    }

    override fun onBindHeaderHolder(holder: RecyclerView.ViewHolder) {
        if (holder is SearchViewHolder) {
            holder.bind()
        } else if (holder is SearchInputViewHolder) {
            holder.bind()
        }
    }

    override fun getChatViewType(position: Int): Int {
       return getItem(position)?.run {
            if (roomType() == ChannelType.PRIVATE ) {
                R.layout.message_empty_undefine_error_item
            } else {
                R.layout.message_conversation_item
            }
        }?: R.layout.message_empty_undefine_error_item
    }

    override fun getHeaderViewType(): Int {
        return searchListener?.run {
             R.layout.message_search_input_item
        } ?: R.layout.message_search_item
    }
}