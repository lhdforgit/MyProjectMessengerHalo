package com.halo.common.utils

import android.animation.ValueAnimator
import android.view.View
import android.view.ViewPropertyAnimator
import java.util.concurrent.atomic.AtomicInteger

/**
 * Create by ndn
 * Create on 7/22/20
 * com.halo.common.utils
 */

fun afterAll(animators: Collection<ViewPropertyAnimator>, action: () -> Unit) {
    val completed = AtomicInteger(0)
    for (animator in animators) {
        animator.withEndAction {
            if (completed.incrementAndGet() == animators.size) {
                action()
            }
        }.start()
    }
}

fun ViewPropertyAnimator.scale(scale: Float) = scaleX(scale).scaleY(scale)!!
fun ViewPropertyAnimator.translationXY(translationX: Float, translationY: Float? = null) =
    translationX(translationX).translationY(translationY ?: translationX)!!

fun ViewPropertyAnimator.translationXYBy(translationX: Float, translationY: Float? = null) =
    translationXBy(translationX).translationYBy(translationY ?: translationX)!!

var View.scale: Float
    get() {
        if (scaleX == scaleY) return scaleX
        else throw UnsupportedOperationException()
    }
    set(value) {
        scaleX = value
        scaleY = value
    }

var View.translationXY: Float
    get() {
        if (translationX == translationY) return translationX
        else throw UnsupportedOperationException()
    }
    set(value) {
        translationX = value
        translationY = value
    }

var View.layoutWidth
    get() = this.layoutParams.width
    set(value) {
        val layoutParams = this.layoutParams
        layoutParams.width = value
        this.layoutParams = layoutParams
    }

var View.layoutHeight
    get() = this.layoutParams.height
    set(value) {
        val layoutParams = this.layoutParams
        layoutParams.height = value
        this.layoutParams = layoutParams
    }


fun View.animateWidth(width: Int) {
    val anim = ValueAnimator.ofInt(this.measuredWidth, width)
    anim.addUpdateListener { valueAnimator ->
        val value = valueAnimator.animatedValue as Int
        this.layoutWidth = value
    }
    anim.start()
}

fun View.animateHeight(height: Int) {
    val anim = ValueAnimator.ofInt(this.measuredHeight, height)
    anim.addUpdateListener { valueAnimator ->
        val value = valueAnimator.animatedValue as Int
        this.layoutHeight = value
    }
    anim.start()
}

fun View.showTopToBottom(show: Boolean, h: Int) {
    if (show) {
        animateHeight(h)
    } else {
        animateHeight(0)
    }
}