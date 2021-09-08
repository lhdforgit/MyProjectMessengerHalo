/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.editor.mediasend

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Point
import androidx.annotation.WorkerThread
import com.halo.editor.imageeditor.model.EditorModel
import com.halo.editor.providers.BlobProvider
import com.halo.editor.util.MediaUtil
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.IOException

class ImageEditorModelRenderMediaTransform
constructor(
    private val modelToRender: EditorModel,
    private val size: Point? = null
) : MediaTransform {

    @WorkerThread
    override fun transform(
        context: Context,
        media: Media
    ): Media {
        val outputStream = ByteArrayOutputStream()
        val bitmap = modelToRender.render(context, size)
        return try {
            bitmap.compress(
                Bitmap.CompressFormat.JPEG,
                80,
                outputStream
            )
            val uri = BlobProvider
                .getInstance()
                .forData(outputStream.toByteArray())
                .withMimeType(MediaUtil.IMAGE_JPEG)
                .createForSingleSessionOnDisk(context)
            Media.createImage(
                null,
                uri,
                MediaUtil.IMAGE_JPEG,
                media.date,
                bitmap.width,
                bitmap.height,
                outputStream.size().toLong(),
                media.bucketId,
                media.caption
            )
        } catch (e: IOException) {
            Timber.w( "Failed to render image. Using base image.")
            media
        } finally {
            bitmap.recycle()
            try {
                outputStream.close()
            } catch (e: IOException) {
               e.printStackTrace()
            }
        }
    }

    override fun toString(): String {
        return "ImageEditorModelRenderMediaTransform(modelToRender=$modelToRender)"
    }
}