/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend

import android.content.Context
import androidx.annotation.WorkerThread

class VideoTrimTransform
internal constructor() : MediaTransform {

    @WorkerThread
    override fun transform(
        context: Context,
        media: Media
    ): Media {
        return Media(
            null,
            media.uri,
            media.mimeType,
            media.date,
            media.width,
            media.height,
            media.size,
            media.duration,
            media.bucketId,
            media.caption
        )
    }
}