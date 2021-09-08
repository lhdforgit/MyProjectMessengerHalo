/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils.ktx

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.ViewDataBinding
import androidx.databinding.ViewStubProxy

inline fun <T : ViewDataBinding> T.executeAfter(block: T.() -> Unit) {
    block()
    executePendingBindings()
}

@BindingAdapter("isVisible")
fun View.bindIsVisible(visible: Boolean) {
    this.isVisible = visible
}

@BindingAdapter("isInvisible")
fun View.bindIsInvisible(invisible: Boolean) {
    this.isInvisible = invisible
}

@BindingAdapter("isGone")
fun View.bindIsGone(gone: Boolean) {
    this.isGone = gone
}

inline var ViewStubProxy.isVisible: Boolean
    get() = isInflated && root.isVisible
    set(value) {
        if (isInflated) {
            root.isVisible = value
        } else {
            if (value) {
                viewStub?.inflate()
            }
        }
    }