package com.hahalolo.messager.bubble.conversation.detail.gallery.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.viewholder.ChatGalleryDocViewHolder
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.viewholder.ChatGalleryLinkViewHolder
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.viewholder.ChatGalleryMediaHolder
import com.hahalolo.messenger.R
import com.halo.data.room.table.AttachmentTable
import com.halo.data.room.type.AttachmentType
import com.halo.widget.ErrorHideEmptyHolder

class ChatAttachmentAdapter(
    diffCallback: ChatGalleryDiffCallback,
    preloadSizeProvider: ViewPreloadSizeProvider<AttachmentTable>,
    requestManager: RequestManager,
    private val listener: ChatGalleryListener
) : AbsPreloadAdapter<AttachmentTable>(diffCallback, preloadSizeProvider, requestManager) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder = when (viewType) {
            R.layout.chat_message_gallery_media_item -> {
                ChatGalleryMediaHolder.getHolder(parent, requestManager, listener)
            }
            R.layout.chat_message_gallery_link_item -> {
                ChatGalleryLinkViewHolder.getHolder(parent, requestManager, listener)
            }
            R.layout.chat_message_gallery_doc_item -> {
                ChatGalleryDocViewHolder.getHolder(parent, requestManager, listener)
            }
            else -> {
                ErrorHideEmptyHolder.build(parent)
            }
        }
        checkPreloadHolder(viewHolder)
        return viewHolder
    }

    override fun getItemCount(): Int {
        return listDatas.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChatGalleryMediaHolder -> {
                holder.bind(getItem(position), position)
            }
            is ChatGalleryLinkViewHolder -> {
                holder.bind(getItem(position))
            }
            is ChatGalleryDocViewHolder -> {
                holder.bind(getItem(position))
            }
            is ErrorHideEmptyHolder -> {
                holder.bind()
            }
        }
        checkPreloadHolder(holder)
    }

    override fun getItemViewType(position: Int): Int {
        getItem(position)?.run {
            when (type) {
                AttachmentType.MEDIA -> {
                    getAttachmentUrl().takeIf { it.isNotEmpty() }?.run {
                        return R.layout.chat_message_gallery_media_item
                    }
                }
                AttachmentType.LINK -> {
                    url?.takeIf { it.isNotEmpty() }?.run {
                        return R.layout.chat_message_gallery_link_item
                    }
                }
                AttachmentType.DOCUMENT -> {
                    fileName?.takeIf { it.isNotEmpty() }?.run {
                        return R.layout.chat_message_gallery_doc_item
                    }
                }
                else -> {
                    return R.layout.error_hide_empty_holder
                }
            }
        }
        return R.layout.error_hide_empty_holder
    }

    private fun getItem(position: Int): AttachmentTable? {
        return listDatas.takeIf { position >= 0 && position < it.size }?.get(position)
    }

    fun getListData(): List<AttachmentTable>? {
        return listDatas
    }

    override fun getCountPreload(): Int {
        return 3
    }
}