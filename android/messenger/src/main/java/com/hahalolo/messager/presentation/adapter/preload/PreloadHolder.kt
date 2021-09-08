/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.presentation.adapter.preload

import android.view.View
import com.bumptech.glide.RequestManager

interface PreloadHolder {
    fun getTargets(): MutableList<View>
    fun invalidateLayout(requestManager: RequestManager?)
}