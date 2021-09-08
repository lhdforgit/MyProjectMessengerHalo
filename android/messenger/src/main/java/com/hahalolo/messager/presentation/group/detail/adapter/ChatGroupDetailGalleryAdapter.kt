/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.ChatGalleryListener
import com.hahalolo.messager.presentation.adapter.holder.EmptyUndefineErrorHolder
import com.hahalolo.messager.presentation.group.gallery.AttachmentListener
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerMediaViewHolder
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileModel
import com.hahalolo.messenger.R

class ChatGroupDetailGalleryAdapter(
    val requestManager: RequestManager,
    val listener: AttachmentListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listData = mutableListOf<MessengerManagerFileModel>()
    private val maxPhoto = 9

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.chat_message_gallery_media_item -> {
                MessengerMediaViewHolder.createHolder(parent, requestManager, listener)
            }
            R.layout.chat_message_last_media -> {
                GroupDetailLastMediaViewHolder.getHolder(parent, requestManager, listener)
            }
            else -> {
                EmptyUndefineErrorHolder.createHolder(parent)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size < maxPhoto) listData.size else maxPhoto
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MessengerMediaViewHolder -> {
                holder.bind(listData[position])
            }
            is GroupDetailLastMediaViewHolder -> {
                holder.bind(listData[position])
            }
            is EmptyUndefineErrorHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        listData.takeIf { it.isNotEmpty() }?.run {
            get(position).run {
                url?.takeIf { it.isNotEmpty() }?.run {
                    return if (position < maxPhoto - 1) {
                        R.layout.chat_message_gallery_media_item
                    } else {
                        R.layout.chat_message_last_media
                    }
                }
            }
        }
        return R.layout.message_empty_undefine_error_item
    }

    fun updateData(listData: MutableList<MessengerManagerFileModel>) {
        this.listData = listData
        notifyDataSetChanged()
    }
}