/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend

import android.content.Context
import androidx.annotation.WorkerThread

interface MediaTransform {
    @WorkerThread
    fun transform(
        context: Context,
        media: Media
    ): Media
}