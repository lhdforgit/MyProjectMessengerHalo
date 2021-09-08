package com.hahalolo.progress.memory

import android.view.ViewTreeObserver
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import com.hahalolo.progress.custom.ProgressView
import com.hahalolo.progress.util.isMainThread
import com.hahalolo.progress.util.isMeasured
import com.hahalolo.progress.util.notNull
import com.hahalolo.progress.util.removeOnGlobalLayoutListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*


internal class ViewTargetSkeletonManager : ViewTreeObserver.OnGlobalLayoutListener {

    private var currentSkeleton: ViewTargetSkeletonDelegate? = null

    private var currentProgressView: ProgressView? = null

    private var pendingBlock: () -> Unit = {}

    @Volatile
    private var pendingClear: Job? = null

    @Volatile
    var currentSkeletonId: UUID? = null
        private set

    @Volatile
    var currentSkeletonJob: Job? = null
        private set

    private var isRestart = false
    private var skipAttach = true

    @MainThread
    fun setCurrentProgressView(progressView: ProgressView?) {
        currentProgressView = progressView
    }

    /** Attach [skeleton] to this view and dispose the old skeleton. */
    @MainThread
    fun setCurrentSkeleton(skeleton: ViewTargetSkeletonDelegate?) {
        if (isRestart) {
            isRestart = false
        } else {
            pendingClear?.cancel()
            pendingClear = null
        }

        currentSkeleton?.dispose()
        currentSkeleton = skeleton
        skipAttach = true
    }

    /** Hide and cancel any skeleton attached to this view. */
    @MainThread
    fun hideSkeleton() {
        currentSkeletonId = null
        currentSkeletonJob = null
        pendingClear?.cancel()
        pendingClear = CoroutineScope(Dispatchers.Main.immediate).launch {
            currentSkeleton.notNull { currentProgressView.notNull { view -> it.hideSkeleton(view) } }
            setCurrentSkeleton(null)
            setCurrentProgressView(null)
        }
        pendingBlock()
        pendingBlock = {}
    }

    /** Returns the visibility of the skeleton. */
    @MainThread
    fun isSkeletonShown(): Boolean {
        return currentProgressView?.isSkeletonShown ?: false
    }

    /** Set the current [job] attached to this view and assign it an ID. */
    @AnyThread
    fun setCurrentSkeletonJob(job: Job): UUID {
        val skeletonId = newSkeletonId()
        currentSkeletonId = skeletonId
        currentSkeletonJob = job
        return skeletonId
    }

    /** Return an ID to use for the next skeleton attached to this manager. */
    @AnyThread
    private fun newSkeletonId(): UUID {
        val skeletonId = currentSkeletonId
        if (skeletonId != null && isRestart && isMainThread()) {
            return skeletonId
        }
        return UUID.randomUUID()
    }

    override fun onGlobalLayout() {
        currentSkeleton.notNull { skeleton ->
            skeleton.getViewTarget().notNull {
                if (it.isMeasured()) {
                    it.removeOnGlobalLayoutListener(this)
                    isRestart = true
                    skeleton.restart()
                }
            }
        }
    }

    /** Calls the specified function [block] after the skeleton is hidden. */
    fun afterHide(block: () -> Unit) {
        if (isSkeletonShown()) pendingBlock = block
        else block()
    }
}
