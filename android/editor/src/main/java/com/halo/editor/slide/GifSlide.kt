/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.slide

import android.content.Context
import android.net.Uri
import com.halo.editor.attachments.Attachment
import com.halo.editor.util.MediaUtil

class GifSlide : ImageSlide {
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
            MediaUtil.IMAGE_GIF,
            size,
            width,
            height,
            true,
            null,
            caption
        )
    )
}