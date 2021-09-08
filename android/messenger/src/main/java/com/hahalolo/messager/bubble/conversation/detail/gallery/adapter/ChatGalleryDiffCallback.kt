/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.detail.gallery.adapter

import android.text.TextUtils
import androidx.recyclerview.widget.DiffUtil
import com.halo.data.room.table.AttachmentTable

class ChatGalleryDiffCallback : DiffUtil.ItemCallback<AttachmentTable>() {
    override fun areItemsTheSame(oldAttm: AttachmentTable, newAttm: AttachmentTable): Boolean {
        return TextUtils.equals(oldAttm.id, newAttm.id)
    }

    override fun areContentsTheSame(oldAttm: AttachmentTable, newAttm: AttachmentTable): Boolean {
        return TextUtils.equals(oldAttm.id, newAttm.id)
                && TextUtils.equals(oldAttm.type, newAttm.type)
                && TextUtils.equals(oldAttm.fileUrl, newAttm.fileUrl)
                && TextUtils.equals(oldAttm.fileName, newAttm.fileName)
                && TextUtils.equals(oldAttm.fileType, newAttm.fileType)
    }
}
