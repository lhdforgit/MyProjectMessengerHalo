/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.detail.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.halo.widget.ErrorHideEmptyHolder

class ConversationDetailMediaAdapter(
    private val widthScreen : Int,
    private val requestManager: RequestManager,
    private val listener: ConversationDetailMediaListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listData = mutableListOf<AttachmentTable>()
    private val maxPhoto = 9

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.conversation_detail_media_item -> {
                ConversationDetailMediaHolder.getHolder(parent, requestManager, listener, widthScreen)
            }
            R.layout.conversation_detail_last_media_item -> {
                ConversationLastMediaViewHolder.getHolder(parent, requestManager, listener, widthScreen)
            }
            else -> {
                ErrorHideEmptyHolder.build(parent)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (listData.size < maxPhoto) listData.size else maxPhoto
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ConversationDetailMediaHolder -> {
                holder.bind(listData[position], position)
            }
            is ConversationLastMediaViewHolder -> {
                holder.bind(listData[position], position)
            }
            is ErrorHideEmptyHolder -> {
                holder.bind()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        listData.takeIf { it.isNotEmpty() }?.run {
            get(position).run {
                getAttachmentUrl().takeIf { it.isNotEmpty() }?.run {
                    return if (position < maxPhoto - 1) {
                        R.layout.conversation_detail_media_item
                    } else {
                        R.layout.conversation_detail_last_media_item
                    }
                }
            }
        }
        return -1
    }

    fun updateData(listData: MutableList<AttachmentTable>) {
        this.listData = listData
        notifyDataSetChanged()
    }
}