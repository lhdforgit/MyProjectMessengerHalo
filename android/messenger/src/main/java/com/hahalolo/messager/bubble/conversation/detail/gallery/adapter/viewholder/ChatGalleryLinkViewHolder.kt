/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.ChatGalleryListener
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.PreloadHolder
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageGalleryLinkItemBinding
import com.halo.common.utils.DateUtils
import java.util.*

class ChatGalleryLinkViewHolder constructor(
    val binding: ChatMessageGalleryLinkItemBinding,
    val requestManager: RequestManager,
    val listener: ChatGalleryListener
) : RecyclerView.ViewHolder(binding.root), PreloadHolder {

    fun bind(att: AttachmentTable?) {
        att?.run {
            binding.urlTv.text = url
            this.sentAt?.let {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it
                binding.timeTv.text = DateUtils.formatTime(it)
            } ?: run {
                binding.timeTv.visibility = View.GONE
            }
        }
        bindAction(att)
    }

    private fun bindAction(att: AttachmentTable?) {
        binding.menuBt.setOnClickListener { listener.onMenuItemClick(binding.urlTv, att) }
        binding.root.setOnLongClickListener {
            listener.onMenuItemClick(it, att)
            return@setOnLongClickListener true
        }
        binding.urlTv.setOnClickListener { listener.onItemClick(0, att)}
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        requestManager?.clear(binding.thumbnailImg)
        binding.thumbnailImg.setImageDrawable(null)
    }

    override fun getTargets(): MutableList<View> {
        val views = ArrayList<View>()
        views.add(binding.thumbnailImg)
        return views
    }

    companion object {
        fun getHolder(
            parent: ViewGroup,
            requestManager: RequestManager,
            listener: ChatGalleryListener
        ): ChatGalleryLinkViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatMessageGalleryLinkItemBinding>(
                inflater,
                R.layout.chat_message_gallery_link_item,
                parent, false
            )
            return ChatGalleryLinkViewHolder(
                binding,
                requestManager,
                listener
            )
        }
    }
}