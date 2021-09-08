/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.bottom_sheet

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import androidx.core.animation.doOnCancel
import com.google.android.material.card.MaterialCardView
import com.halo.common.utils.JobUtil
import com.halo.common.utils.LogTrace
import com.halo.common.utils.T
import kotlinx.coroutines.Dispatchers

class FilterButton @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    def: Int = 0
) : MaterialCardView(context, attributes, def) {

    private var animatorFloat: ValueAnimator = ValueAnimator.ofFloat(1F)
    private var isShow = true

    init {
        animatorFloat.duration = 200
        animatorFloat.addUpdateListener {
            val value = it.animatedValue as Float
            scaleX = value
            scaleY = value
            alpha = value
            if (value == 1.0F) {
                isShow
            } else if (value == 0.0F) {
                !isShow
            }
        }
    }

    fun setStateShow(isShow: Boolean) {
        LogTrace.traceE(T.JOB, "state Show $isShow")
        if (isShow) {
            scaleX = 1F
            scaleY = 1F
            this.isShow = true
        } else {
            scaleX = 0F
            scaleY = 0F
            this.isShow = false
        }
    }

    fun showFab() {
        awaitingToDone {
            LogTrace.traceE(T.JOB, "Transition  showFab")
            JobUtil.doJob(Dispatchers.Main) {
                startAnimationFab(true)
            }
        }
    }

    fun hideFab() {
        awaitingToDone {
            LogTrace.traceE(T.JOB, "Transition hideFab")
            startAnimationFab(false)
        }
    }

    private fun startAnimationFab(process: Boolean) {
        JobUtil.doJob(Dispatchers.Main) {
            if (process) {
                animatorFloat.start()
            } else {
                animatorFloat.reverse()
            }
        }
    }

    private fun awaitingToDone(action: () -> Unit) {
        LogTrace.traceE(T.JOB, "Transition animatorFloat.isStarted ${animatorFloat.isStarted} || animatorFloat.isRunning ${animatorFloat.isRunning} || animatorFloat.isPaused ${animatorFloat.isPaused}")
        if (animatorFloat.isRunning) {
            animatorFloat.doOnCancel {
                LogTrace.traceE(T.JOB, "Transition doOnCancel")
                action()
            }
            animatorFloat.cancel()
        } else {
            action()
        }
    }
}