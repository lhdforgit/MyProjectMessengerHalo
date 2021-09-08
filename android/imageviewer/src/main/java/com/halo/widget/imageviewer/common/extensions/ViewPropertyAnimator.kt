/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.common.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.ViewPropertyAnimator

internal fun ViewPropertyAnimator.setAnimatorListener(
    onAnimationEnd: ((Animator?) -> Unit)? = null,
    onAnimationStart: ((Animator?) -> Unit)? = null
) = this.setListener(
    object : AnimatorListenerAdapter() {

        override fun onAnimationEnd(animation: Animator?) {
            onAnimationEnd?.invoke(animation)
        }

        override fun onAnimationStart(animation: Animator?) {
            onAnimationStart?.invoke(animation)
        }
    })