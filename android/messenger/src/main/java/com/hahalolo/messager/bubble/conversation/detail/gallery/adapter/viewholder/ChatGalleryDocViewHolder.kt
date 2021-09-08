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
import com.hahalolo.messenger.databinding.ChatMessageGalleryDocItemBinding
import com.halo.common.utils.DateUtils
import com.halo.data.room.type.DocumentType
import java.util.*

class ChatGalleryDocViewHolder constructor(
    val binding: ChatMessageGalleryDocItemBinding,
    val requestManager: RequestManager,
    val listener: ChatGalleryListener
) : RecyclerView.ViewHolder(binding.root), PreloadHolder {

    fun bind(att: AttachmentTable?) {
        att?.run {
            binding.fileNameTv.text = this.fileName
            binding.dateTv.text = DateUtils.formatTime(sentAt ?: 0)
            binding.fileSizeTv.text = this.getFileSizeText()
            var typeFileRes = R.drawable.ic_chat_file_doc
            getDocumentType().let { type ->
                when (type) {
                    DocumentType.PDF -> typeFileRes = R.drawable.ic_chat_file_pdf
                    DocumentType.EXCEl -> typeFileRes = R.drawable.ic_chat_file_excel
                }
            }
            binding.fileImg.setImageResource(typeFileRes)
        }
        bindAction(att)
    }

    private fun bindAction(att: AttachmentTable?) {
        binding.menuBt.setOnClickListener { listener.onMenuItemClick(binding.fileNameTv, att) }
        binding.root.setOnLongClickListener {
            listener.onMenuItemClick(it, att)
            return@setOnLongClickListener true
        }
    }

    override fun invalidateLayout(requestManager: RequestManager?) {
        requestManager?.clear(binding.fileImg)
        binding.fileImg.setImageDrawable(null)
    }

    override fun getTargets(): MutableList<View> {
        val views = ArrayList<View>()
        views.add(binding.fileImg)
        return views
    }

    companion object {
        fun getHolder(
            parent: ViewGroup,
            requestManager: RequestManager,
            listener: ChatGalleryListener
        ): ChatGalleryDocViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatMessageGalleryDocItemBinding>(
                inflater,
                R.layout.chat_message_gallery_doc_item,
                parent, false
            )
            return ChatGalleryDocViewHolder(
                binding,
                requestManager,
                listener
            )
        }
    }
}