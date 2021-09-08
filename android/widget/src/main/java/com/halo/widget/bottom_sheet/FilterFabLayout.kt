/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.bottom_sheet

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.halo.common.utils.JobUtil
import com.halo.common.utils.LogTrace
import com.halo.common.utils.T
import com.halo.widget.R
import kotlinx.android.synthetic.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface FilterFabCallback {

    interface State {

        suspend fun haftExpand(): Boolean

        suspend fun expand(): Boolean

        suspend fun hide(): Boolean
    }


}

interface FilterFabView {

    suspend fun setBottomViewFr(fr: Fragment): Boolean

    fun scrollWithAppBar(appbar: AppBarLayout?)

    fun expand()
}

class FilterFabLayout @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    def: Int = 0) : MotionLayoutMutipleListener(context, attributes, def), FilterFabView {

    val callback = object : TransitionListener {
        override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
            LogTrace.traceE(T.JOB, "Transition triggerId $triggerId || positive $positive || process $progress")
            /*JobUtil.doJob(Dispatchers.Main) {
                if (triggerId == R.id.hideFilterBtn && positive) {
                    LogTrace.traceE(T.JOB, "Transition hide")
                    filterBtn?.hideFab()
                } else if (triggerId == R.id.showFilterBtn && !positive) {
                    LogTrace.traceE(T.JOB, "Transition show")
                    filterBtn?.showFab()
                }
            }*/
        }

        override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
            LogTrace.traceE(
                T.JOB,
                "Transition onTransitionStarted =>  triggerId  R.id.hideFilterBtn ${R.id.hideFilterBtn}"
            )
            LogTrace.traceE(
                T.JOB,
                "Transition onTransitionStarted =>  triggerId  R.id.showFilterBtn ${R.id.showFilterBtn}"
            )
            LogTrace.traceE(
                T.JOB,
                "Transition onTransitionStarted =>  startId $startId || endId $endId"
            )
        }

        override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, process: Float) {}

        override fun onTransitionCompleted(motionLayout: MotionLayout?, transitionId: Int) {
            LogTrace.traceE(T.JOB, "Transition onTransitionCompleted => transitionId $transitionId")
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.filter_fab_layout, this, true)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun onFinishInflate() {
        JobUtil.doJob(Dispatchers.Main) {
            val childContentLayout = findChildContentLayout()
            childContentLayout.id = R.id.contentFilterFabId
            loadLayoutDescription(R.xml.filter_fab_layout_scene)
            setTransition(R.id.transitionStart)
            rebuildScene()
            setDebugMode(3)
            super.onFinishInflate()
            delay(100)
            iniAction()
            requestLayout()
        }
    }

    override fun expand() {
        rebuildScene()
        transitionToEnd()
        if (currentState == R.id.setAfterInit) {
            setTransition(R.id.dragFabScene)
        }
    }

    private fun iniAction() {

        JobUtil.doJob(Dispatchers.Main) {
            addTransitionListener(callback)
            awaitTransitionComplete(R.id.setAfterInitEnd)
        }
    }

    private suspend fun findChildContentLayout(): View = suspendCoroutine {
        require(childCount >= 3) {
            "FilterFabLayout must have child"
        }
        for (i in 0 until childCount) {
            if (getChildAt(i).id == R.id.bottomView || getChildAt(i).id == R.id.filterBtn) {
                continue
            } else {
                it.resume(getChildAt(i))
                break
            }
        }
    }

    private fun onOffsetScroll(process: Float) {
        progress = process
    }

    override fun scrollWithAppBar(appbar: AppBarLayout?) {
        appbar?.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val process = -verticalOffset / appBarLayout?.totalScrollRange?.toFloat()!!
            startAnimationFab(process)
        })
    }

    private var animatorFloat: ValueAnimator = ValueAnimator.ofFloat(1F)
    private fun startAnimationFab(process: Float) {
        JobUtil.doJob(Dispatchers.Main) {
            if (process == 0.0F) {
                animatorFloat.start()
            } else if (process == 1F) {
                animatorFloat.reverse()
            }
        }
    }

    override fun onDetachedFromWindow() {
        clearFindViewByIdCache()
        super.onDetachedFromWindow()
    }

    override suspend fun setBottomViewFr(fr: Fragment): Boolean {
        if (context is AppCompatActivity) {
            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.bottomViewContentFr, fr, "bottomViewContentFr")
                .commit()
        }
        return true
    }
}