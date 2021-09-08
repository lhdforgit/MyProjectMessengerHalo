package com.hahalolo.progress.memory

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import com.hahalolo.progress.SkeletonLoader
import com.hahalolo.progress.annotation.ExperimentalProgressApi
import com.hahalolo.progress.skeleton.RecyclerViewSkeleton
import com.hahalolo.progress.skeleton.Skeleton
import com.hahalolo.progress.skeleton.TextViewSkeleton
import com.hahalolo.progress.skeleton.ViewSkeleton
import com.hahalolo.progress.target.ViewTarget
import com.hahalolo.progress.util.progressManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred

@OptIn(ExperimentalProgressApi::class)
internal class DelegateService(
    private val imageLoader: SkeletonLoader
) {

    fun createTargetDelegate(
        skeleton: Skeleton
    ): TargetDelegate {
        return ViewTargetDelegate(skeleton, skeleton.target)
    }

    @MainThread
    fun createSkeletonDelegate(
        skeleton: Skeleton,
        targetDelegate: TargetDelegate,
        lifecycle: Lifecycle,
        mainDispatcher: CoroutineDispatcher,
        deferred: Deferred<*>
    ): SkeletonDelegate? {
        val skeletonDelegate: SkeletonDelegate
        when (skeleton) {
            is ViewSkeleton, is RecyclerViewSkeleton, is TextViewSkeleton -> when (val target = skeleton.target) {
                is ViewTarget<*> -> {
                    skeletonDelegate = ViewTargetSkeletonDelegate(
                        imageLoader = imageLoader,
                        skeleton = skeleton,
                        target = targetDelegate,
                        lifecycle = lifecycle,
                        dispatcher = mainDispatcher,
                        job = deferred
                    )
                    lifecycle.addObserver(skeletonDelegate)
                    target.view.progressManager.setCurrentSkeleton(skeletonDelegate)
                }
                else -> {
                    skeletonDelegate = BaseRequestDelegate(lifecycle, mainDispatcher, deferred)
                    lifecycle.addObserver(skeletonDelegate)
                }
            }
        }
        return skeletonDelegate
    }
}
