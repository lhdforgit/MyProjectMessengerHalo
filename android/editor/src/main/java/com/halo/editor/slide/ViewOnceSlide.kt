/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.slide

import android.content.Context
import com.halo.editor.attachments.Attachment

/**
 * Slide used for attachments with contentType [MediaUtil.VIEW_ONCE].
 * Attachments will only get this type *after* they've been viewed, or if they were synced from a
 * linked device. Incoming unviewed messages will have the appropriate image/video contentType.
 */
class ViewOnceSlide(
    context: Context,
    attachment: Attachment
) : Slide(context, attachment) {
    override fun hasViewOnce(): Boolean {
        return true
    }
}