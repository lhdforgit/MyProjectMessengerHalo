/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.imageviewer.viewer.view

import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.halo.widget.imageviewer.common.extensions.*

internal class TransitionImageAnimator(
        private val externalImage: ImageView?,
        private val internalImage: ImageView,
        private val internalImageContainer: FrameLayout
) {

    companion object {
        private const val TRANSITION_DURATION_OPEN = 200L
        private const val TRANSITION_DURATION_CLOSE = 250L
    }

    internal var isAnimating = false

    private var isClosing = false

    private val transitionDuration: Long
        get() = if (isClosing) TRANSITION_DURATION_CLOSE else TRANSITION_DURATION_OPEN

    private val internalRoot: ViewGroup
        get() = internalImageContainer.parent as ViewGroup

    internal fun animateOpen(
            containerPadding: IntArray,
            onTransitionStart: (Long) -> Unit,
            onTransitionEnd: () -> Unit
    ) {
        if (externalImage.isRectVisible) {
            onTransitionStart(TRANSITION_DURATION_OPEN)
            doOpenTransition(containerPadding, onTransitionEnd)
        } else {
            onTransitionEnd()
        }
    }

    internal fun animateClose(
            shouldDismissToBottom: Boolean,
            onTransitionStart: (Long) -> Unit,
            onTransitionEnd: () -> Unit
    ) {

        val enable = (externalImage?.visibility != View.VISIBLE) && (externalImage?.isLaidOut ?: false)
        //todo update fix bug treo image
        if (externalImage.isRectVisible && enable && !shouldDismissToBottom) {
            onTransitionStart(TRANSITION_DURATION_CLOSE)
            doCloseTransition(onTransitionEnd)
        } else {
            externalImage?.visibility = View.VISIBLE
            onTransitionEnd()
        }
    }

    private fun doOpenTransition(containerPadding: IntArray, onTransitionEnd: () -> Unit) {
        isAnimating = true
        prepareTransitionLayout()

        internalRoot.postApply {
            //ain't nothing but a kludge to prevent blinking when transition is starting
            externalImage?.postDelayed(50) { visibility = View.INVISIBLE }

            TransitionManager.beginDelayedTransition(internalRoot, createTransition {
                if (!isClosing) {
                    isAnimating = false
                    onTransitionEnd()
                }
            })

            internalImageContainer.makeViewMatchParent()
            internalImage.makeViewMatchParent()

            internalRoot.applyMargin(
                    containerPadding[0],
                    containerPadding[1],
                    containerPadding[2],
                    containerPadding[3])

            internalImageContainer.requestLayout()
        }
    }

    private fun doCloseTransition(onTransitionEnd: () -> Unit) {
        isAnimating = true
        isClosing = true

        TransitionManager.beginDelayedTransition(
                internalRoot, createTransition { handleCloseTransitionEnd(onTransitionEnd) })

        prepareTransitionLayout()
        internalImageContainer.requestLayout()
    }

    private fun prepareTransitionLayout() {
        externalImage?.let {
            if (externalImage.isRectVisible) {
                with(externalImage.localVisibleRect) {
                    internalImage.requestNewSize(it.width, it.height)
                    internalImage.applyMargin(top = -top, start = -left)
                }
                with(externalImage.globalVisibleRect) {
                    internalImageContainer.requestNewSize(width(), height())
                    internalImageContainer.applyMargin(left, top, right, bottom)
                }
            }

            resetRootTranslation()
        }
    }

    private fun handleCloseTransitionEnd(onTransitionEnd: () -> Unit) {
        externalImage?.visibility = View.VISIBLE
        externalImage?.post { onTransitionEnd() }
        isAnimating = false
    }

    private fun resetRootTranslation() {
        internalRoot
                .animate()
                .translationY(0f)
                .setDuration(transitionDuration)
                .start()
    }

    private fun createTransition(onTransitionEnd: (() -> Unit)? = null): Transition =
            AutoTransition()
                    .setDuration(transitionDuration)
                    .setInterpolator(DecelerateInterpolator())
                    .addListener(object : Transition.TransitionListener {
                        override fun onTransitionResume(transition: Transition) {
                        }

                        override fun onTransitionPause(transition: Transition) {
                        }

                        override fun onTransitionCancel(transition: Transition) {
                            onTransitionEnd?.invoke()
                        }

                        override fun onTransitionStart(transition: Transition) {
                        }

                        override fun onTransitionEnd(transition: Transition) {
                            onTransitionEnd?.invoke()
                        }
                    })
}