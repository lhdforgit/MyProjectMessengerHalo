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
import com.hahalolo.messenger.databinding.ChatMessageGalleryMediaItemBinding
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.ThumbImageUtils
import java.util.*

class ChatGalleryMediaHolder private constructor(
    private val binding: ChatMessageGalleryMediaItemBinding,
    private val requestManager: RequestManager,
    private val listener: ChatGalleryListener,
    private val width : Int
) : RecyclerView.ViewHolder(binding.root), PreloadHolder {

    fun bind(entity: AttachmentTable?, position: Int) {
        entity?.run {
            val path = this.getAttachmentUrl()
            binding.avLoading.visibility = View.VISIBLE
            loadImage(path)
            if (this.isVideo()) {
                binding.typeVideo.visibility = View.VISIBLE
            } else {
                binding.typeVideo.visibility = View.GONE
            }

        } ?: run {
            //set image error
            binding.photoIv.setImageResource(R.drawable.img_holder)
            binding.avLoading.visibility = View.GONE
            binding.typeVideo.visibility = View.GONE
        }
        itemView.setOnClickListener { v ->
            entity?.run {
                listener.onItemClick(position, this)
            }
        }
        updateSize()
    }

    private fun loadImage(path: String) {
        binding.avLoading.visibility = View.GONE
        val uri = ThumbImageUtils.thumb(
            ThumbImageUtils.Size.MEDIA_WIDTH_360,
            path,
            ThumbImageUtils.TypeSize._AUTO
        )
        GlideRequestBuilder.getFaceCenterCrop(requestManager).load(uri).into(binding.photoIv)
    }

    private fun updateSize() {
        itemView.layoutParams.width = width / 3
        itemView.layoutParams.height = width / 3
    }

    override fun getTargets(): MutableList<View> {
        val views = ArrayList<View>()
        views.add(binding.photoIv)
        return views
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        requestManager?.clear(binding.photoIv)
        binding.photoIv.setImageDrawable(null)
    }

    companion object {
        fun getHolder(
            parent: ViewGroup,
            requestManager: RequestManager,
            listener: ChatGalleryListener
        ): ChatGalleryMediaHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatMessageGalleryMediaItemBinding>(
                inflater,
                R.layout.chat_message_gallery_media_item,
                parent, false
            )
            return ChatGalleryMediaHolder(
                binding,
                requestManager,
                listener,
                parent.measuredWidth
            )
        }
    }
}