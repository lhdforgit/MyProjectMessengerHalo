/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.detail.gallery.adapter

import android.view.View
import com.halo.data.room.table.AttachmentTable

interface ChatGalleryListener {
    fun onItemClick(position: Int, attachmentTable: AttachmentTable?)
    fun onMenuItemClick(view: View, attachmentTable: AttachmentTable?)
    fun onLastItemClick()
}
