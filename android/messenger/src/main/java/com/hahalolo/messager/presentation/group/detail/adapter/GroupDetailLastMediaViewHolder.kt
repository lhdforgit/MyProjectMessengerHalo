/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.detail.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.ChatGalleryListener
import com.hahalolo.messager.presentation.group.gallery.AttachmentListener
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileModel
import com.hahalolo.messager.presentation.utils.MediaLoaderUtils
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageLastMediaBinding
import com.halo.common.utils.ScreenUtils
import com.halo.common.utils.ThumbImageUtils

class GroupDetailLastMediaViewHolder(
    private val binding: ChatMessageLastMediaBinding,
    private val requestManager: RequestManager,
    private val listener: AttachmentListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(entity: MessengerManagerFileModel) {
        entity.url?.takeIf { it.isNotEmpty() }?.let { path ->
            loadImage(path)
            binding.typeVideo.visibility = View.GONE
        } ?: kotlin.run {
            binding.photoIv.setImageResource(R.drawable.img_holder)
            binding.typeVideo.visibility = View.GONE
        }
        binding.root.setOnClickListener { listener.onClickLastMedia() }
        updateSize()
    }

    private fun loadImage(path: String) {
        val uri = ThumbImageUtils.thumb(
            ThumbImageUtils.Size.MEDIA_WIDTH_360,
            path,
            ThumbImageUtils.TypeSize._AUTO
        )
        MediaLoaderUtils.loadMediaFace(binding.photoIv, uri, requestManager)
    }

    private fun updateSize() {

        val WIDTH_SCREEN = ScreenUtils.getScreenWidth()
        itemView.layoutParams.width = WIDTH_SCREEN / 3
        itemView.layoutParams.height = WIDTH_SCREEN / 3
    }

    companion object {

        fun getHolder(
            parent: ViewGroup,
            requestManager: RequestManager,
            listener: AttachmentListener
        ): GroupDetailLastMediaViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatMessageLastMediaBinding>(
                inflater,
                R.layout.chat_message_last_media,
                parent, false
            )
            return GroupDetailLastMediaViewHolder(
                binding,
                requestManager,
                listener
            )
        }
    }
}