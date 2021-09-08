/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.slide

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.annotation.DrawableRes
import com.halo.editor.R
import com.halo.editor.attachments.Attachment
import com.halo.editor.util.MediaUtil

open class ImageSlide : Slide {
    constructor(
        context: Context,
        attachment: Attachment
    ) : super(context, attachment)

    constructor(
        context: Context,
        uri: Uri,
        size: Long,
        width: Int,
        height: Int,
        caption: String
    ) : super(
        context,
        constructAttachmentFromUri(
            context,
            uri,
            MediaUtil.IMAGE_JPEG,
            size,
            width,
            height,
            true,
            null,
            caption
        )
    )

    @DrawableRes
    override fun getPlaceholderRes(theme: Resources.Theme?): Int {
        return 0
    }

    override fun hasImage(): Boolean {
        return true
    }

    override val contentDescription: String
        get() = context.getString(R.string.Slide_image)

    companion object {
        private val TAG = ImageSlide::class.java.simpleName
    }
}