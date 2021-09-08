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
import com.halo.editor.util.ResUtil

class VideoSlide : Slide {

    constructor(
        context: Context,
        uri: Uri,
        dataSize: Long,
        caption: String
    ) : super(
        context,
        constructAttachmentFromUri(
            context,
            uri,
            MediaUtil.VIDEO_UNSPECIFIED,
            dataSize,
            0,
            0,
            MediaUtil.hasVideoThumbnail(uri),
            null,
            caption
        )
    )

    constructor(
        context: Context,
        attachment: Attachment
    ) : super(context, attachment)

    override fun hasPlayOverlay(): Boolean {
        return true
    }

    @DrawableRes
    override fun getPlaceholderRes(theme: Resources.Theme?): Int {
        return ResUtil.getDrawableRes(theme, R.attr.conversation_icon_attach_video)
    }

    override fun hasImage(): Boolean {
        return true
    }

    override fun hasVideo(): Boolean {
        return true
    }

    override val contentDescription: String
        get() = context.getString(R.string.Slide_video)
}