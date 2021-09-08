/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ConversationDetailLastMediaItemBinding
import com.halo.common.utils.ThumbImageUtils

class ConversationLastMediaViewHolder(
    private val binding: ConversationDetailLastMediaItemBinding,
    private val requestManager: RequestManager,
    private val listener: ConversationDetailMediaListener,
    private val widthScreen : Int
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(entity: AttachmentTable?, position: Int) {
        entity?.run {
            val path = this.getAttachmentUrl()
            loadImage(path)
            if (this.isVideo()) {
                binding.typeVideo.visibility = View.VISIBLE
            } else {
                binding.typeVideo.visibility = View.GONE
            }

        } ?: run {
            //set image error
            binding.photoIv.setImageResource(R.drawable.img_holder)
            binding.typeVideo.visibility = View.GONE
        }
        itemView.setOnClickListener { v ->
            entity?.run {
                listener.onLastItemClick()
            }
        }
        updateSize()
    }

    private fun loadImage(path: String) {
        val uri = ThumbImageUtils.thumb(
            ThumbImageUtils.Size.MEDIA_WIDTH_360,
            path,
            ThumbImageUtils.TypeSize._AUTO
        )
        requestManager.apply {
            load(uri)
                .into(binding.photoIv)

        }
    }

    private fun updateSize() {
        if(widthScreen > 0){
            itemView.layoutParams.width = widthScreen / 3
            itemView.layoutParams.height = widthScreen / 3
        }
    }

    companion object {

        fun getHolder(
            parent: ViewGroup,
            requestManager: RequestManager,
            listener: ConversationDetailMediaListener,
            widthScreen : Int
        ): ConversationLastMediaViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ConversationDetailLastMediaItemBinding>(
                inflater,
                R.layout.conversation_detail_last_media_item,
                parent, false
            )
            return ConversationLastMediaViewHolder(
                binding,
                requestManager,
                listener,
                widthScreen
            )
        }
    }
}