/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.target

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import java.lang.ref.WeakReference

class SimpleTargetSpan<T> constructor(width: Int, height: Int,val resourceReady : (resource: T, transition: Transition<in T>?) -> Unit ) : CustomTarget<T>(width, height) {
    var resource: WeakReference<T>? = null
    override fun onLoadCleared(placeholder: Drawable?) {
        resource = null
    }

    override fun onResourceReady(resource: T, transition: Transition<in T>?) {
        this.resource = WeakReference(resource)
        resourceReady(resource,transition)
    }
}