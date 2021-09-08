package com.hahalolo.progress.memory

import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import com.hahalolo.progress.lifecycle.GlobalLifecycle
import com.hahalolo.progress.lifecycle.LifecycleCoroutineDispatcher
import com.hahalolo.progress.skeleton.RecyclerViewSkeleton
import com.hahalolo.progress.skeleton.Skeleton
import com.hahalolo.progress.skeleton.TextViewSkeleton
import com.hahalolo.progress.skeleton.ViewSkeleton
import com.hahalolo.progress.target.Target
import com.hahalolo.progress.target.ViewTarget
import com.hahalolo.progress.util.getLifecycle
import com.hahalolo.progress.util.isNotNull
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal class SkeletonService {

    @MainThread
    fun lifecycleInfo(skeleton: Skeleton): LifecycleInfo {
        when (skeleton) {
            is ViewSkeleton, is RecyclerViewSkeleton, is TextViewSkeleton -> {
                val lifecycle = skeleton.getLifecycle()
                return if (lifecycle != null) {
                    val mainDispatcher = LifecycleCoroutineDispatcher
                        .createUnlessStarted(Dispatchers.Main.immediate, lifecycle)
                    LifecycleInfo(lifecycle, mainDispatcher)
                } else {
                    LifecycleInfo.GLOBAL
                }
            }
        }
    }

    private fun Skeleton.getLifecycle(): Lifecycle? {
        return when {
            lifecycle.isNotNull() -> lifecycle
            this is ViewSkeleton || this is RecyclerViewSkeleton -> target?.getLifecycle()
            else -> context.getLifecycle()
        }
    }

    private fun Target.getLifecycle(): Lifecycle? {
        return (this as? ViewTarget<*>)?.view?.context?.getLifecycle()
    }

    data class LifecycleInfo(
        val lifecycle: Lifecycle,
        val mainDispatcher: CoroutineDispatcher
    ) {

        companion object {
            val GLOBAL = LifecycleInfo(
                lifecycle = GlobalLifecycle,
                mainDispatcher = Dispatchers.Main.immediate
            )
        }
    }
}