/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.common.glide.transformations.face

import android.content.Context
import com.google.android.gms.vision.face.FaceDetector

object GlideFaceDetector {

    @Volatile
    private var faceDetector: FaceDetector? = null

    private var mContext: Context? = null

    val context: Context?
        get() {
            if (mContext == null) {
                throw RuntimeException("Initialize GlideFaceDetector by calling GlideFaceDetector.initialize(context).")
            }
            return mContext
        }

    @JvmStatic
    fun initialize(context: Context) {
        mContext = context.applicationContext
    }

    private fun initDetector() {
        if (faceDetector == null) {
            synchronized(GlideFaceDetector::class.java) {
                if (faceDetector == null) {
                    faceDetector =
                        FaceDetector.Builder(context)
                            .setTrackingEnabled(false)
                            .build()
                }
            }
        }
    }

    @JvmStatic
    fun getFaceDetector(): FaceDetector? {
        initDetector()
        return faceDetector
    }

    fun releaseDetector() {
        faceDetector?.release()
        faceDetector = null
    }
}