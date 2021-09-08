/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.common.glide.transformations.face

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.RectF
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.halo.common.glide.transformations.face.GlideFaceDetector.getFaceDetector
import java.security.MessageDigest

class FaceCenterCrop : BitmapTransformation() {

    /**
     * @param bitmapPool A [com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool] that can be used to obtain and
     * return intermediate [Bitmap]s used in this transformation. For every
     * [android.graphics.Bitmap] obtained from the pool during this transformation, a
     * [android.graphics.Bitmap] must also be returned.
     * @param original   The [android.graphics.Bitmap] to transform
     * @param width      The ideal width of the transformed bitmap
     * @param height     The ideal height of the transformed bitmap
     * @return a transformed bitmap with face being in center.
     */
    override fun transform(
        bitmapPool: BitmapPool,
        original: Bitmap,
        width: Int,
        height: Int
    ): Bitmap {
        val scaleX = width.toFloat() / original.width
        val scaleY = height.toFloat() / original.height
        return if (scaleX != scaleY) {
            val config = if (original.config != null) original.config else Bitmap.Config.ARGB_8888
            val result = bitmapPool[width, height, config]
            val scale = scaleX.coerceAtLeast(scaleY)
            var left = 0f
            var top = 0f
            var scaledWidth = width.toFloat()
            var scaledHeight = height.toFloat()
            val focusPoint = PointF()
            detectFace(original, focusPoint)
            if (scaleX < scaleY) {
                scaledWidth = scale * original.width
                val faceCenterX = scale * focusPoint.x
                left = getLeftPoint(width, scaledWidth, faceCenterX)
            } else {
                scaledHeight = scale * original.height
                val faceCenterY = scale * focusPoint.y
                top = getTopPoint(height, scaledHeight, faceCenterY)
            }
            val targetRect = RectF(left, top, left + scaledWidth, top + scaledHeight)
            val canvas = Canvas(result)
            canvas.drawBitmap(original, null, targetRect, null)
            //No need to recycle() original Bitmap as Glide will take care of returning our original Bitmap to the BitmapPool
            result
        } else {
            original
        }
    }

    /**
     * Calculates a point (focus point) in the bitmap, around which cropping needs to be performed.
     *
     * @param bitmap           Bitmap in which faces are to be detected.
     * @param centerOfAllFaces To store the center point.
     */
    private fun detectFace(bitmap: Bitmap, centerOfAllFaces: PointF) {
        try {
            val faceDetector = getFaceDetector()
            if (faceDetector != null) {
                if (!faceDetector.isOperational) {
                    centerOfAllFaces[bitmap.width / 2.toFloat()] = bitmap.height / 2.toFloat()
                    return
                }
                val frame = Frame.Builder().setBitmap(bitmap).build()
                val faces = faceDetector.detect(frame)
                val totalFaces = faces.size()
                if (totalFaces > 0) {
                    var sumX = 0f
                    var sumY = 0f
                    for (i in 0 until totalFaces) {
                        val faceCenter = PointF()
                        getFaceCenter(faces[faces.keyAt(i)], faceCenter)
                        sumX += faceCenter.x
                        sumY += faceCenter.y
                    }
                    centerOfAllFaces[sumX / totalFaces] = sumY / totalFaces
                    return
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        centerOfAllFaces[bitmap.width / 2.toFloat()] = bitmap.height / 2.toFloat()
    }

    /**
     * Calculates center of a given face
     *
     * @param face   Face
     * @param center Center of the face
     */
    private fun getFaceCenter(
        face: Face,
        center: PointF
    ) {
        val x = face.position.x
        val y = face.position.y
        val width = face.width
        val height = face.height
        center[x + width / 2] = y + height / 2
    }

    private fun getTopPoint(
        height: Int,
        scaledHeight: Float,
        faceCenterY: Float
    ): Float {
        return when {
            faceCenterY <= height / 2.0f -> 0f
            scaledHeight - faceCenterY <= height / 2.0f -> height - scaledHeight
            else -> height / 2.0f - faceCenterY
        }
    }

    private fun getLeftPoint(
        width: Int,
        scaledWidth: Float,
        faceCenterX: Float
    ): Float {
        return when {
            faceCenterX <= width / 2.0f -> 0f
            scaledWidth - faceCenterX <= width / 2.0f -> width - scaledWidth
            else -> width / 2.0f - faceCenterX
        }
    }

    override fun hashCode(): Int {
        return ID.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is FaceCenterCrop
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
    }

    companion object {
        private const val VERSION = 1
        private const val ID = "com.halo.common.glide.transformations.face.FaceCenterCrop.$VERSION"
        private val ID_BYTES = ID.toByteArray(Key.CHARSET)
    }
}