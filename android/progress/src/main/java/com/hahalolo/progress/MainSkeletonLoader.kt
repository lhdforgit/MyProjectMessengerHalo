package com.hahalolo.progress

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleObserver
import com.hahalolo.shimmer.ShimmerFrameLayout
import com.hahalolo.progress.annotation.ExperimentalProgressApi
import com.hahalolo.progress.custom.*
import com.hahalolo.progress.custom.RecyclerProgressView
import com.hahalolo.progress.memory.DelegateService
import com.hahalolo.progress.memory.SkeletonService
import com.hahalolo.progress.skeleton.RecyclerViewSkeleton
import com.hahalolo.progress.skeleton.Skeleton
import com.hahalolo.progress.skeleton.TextViewSkeleton
import com.hahalolo.progress.skeleton.ViewSkeleton
import com.hahalolo.progress.target.RecyclerViewTarget
import com.hahalolo.progress.target.SimpleViewTarget
import com.hahalolo.progress.target.TextViewTarget
import com.hahalolo.progress.target.ViewTarget
import com.hahalolo.progress.util.*
import com.hahalolo.progress.util.generateRecyclerProgressView
import com.hahalolo.progress.util.generateSimpleProgressView
import com.hahalolo.progress.util.generateTextProgressView
import com.hahalolo.progress.util.getParentViewGroup
import kotlinx.coroutines.*

@OptIn(ExperimentalProgressApi::class)
internal class MainSkeletonLoader(
    private val context: Context,
    override val defaults: DefaultSkeletonOptions
) : SkeletonLoader {

    companion object {
        private const val TAG = "MainSkeletonLoader"
    }

    private val loaderScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(TAG, throwable.message.orEmpty())
    }

    private val delegateService = DelegateService(this)

    override fun load(skeleton: Skeleton) {
        val job = loaderScope.launch(exceptionHandler) { loadInternal(skeleton) }
        val target = skeleton.target as? ViewTarget<*>
        target?.view?.progressManager?.setCurrentSkeletonJob(job)
    }

    override fun generate(skeleton: Skeleton): ProgressView {
        return generateProgressView(skeleton)
    }

    private suspend fun loadInternal(skeleton: Skeleton) =
        withContext(Dispatchers.Main.immediate) {
            val (lifecycle, mainDispatcher) = SkeletonService().lifecycleInfo(skeleton)
            val targetDelegate = delegateService.createTargetDelegate(skeleton)
            val deferred = async(mainDispatcher, CoroutineStart.LAZY) {
                val target = skeleton.target
                if (target is ViewTarget<*> && target is LifecycleObserver) {
                    lifecycle.addObserver(target)
                    with(target.view) {
                        if (parent !is ProgressView && isMeasured()) {
                            val progressView = generateProgressView(skeleton)
                            progressManager.setCurrentProgressView(progressView)
                            targetDelegate.success(progressView)
                        }
                    }
                }
            }
            val skeletonDelegate = delegateService.createSkeletonDelegate(
                skeleton,
                targetDelegate,
                lifecycle,
                mainDispatcher,
                deferred
            )
            deferred.invokeOnCompletion { throwable ->
                loaderScope.launch(Dispatchers.Main.immediate) { skeletonDelegate?.onComplete() }
            }
            deferred.await()
        }

    override fun hide(view: View, progressView: ProgressView) {
        progressView.hideSkeleton()
        val skeletonView = progressView as ShimmerFrameLayout
        val originalParams = skeletonView.layoutParams
        val originalParent = skeletonView.getParentViewGroup()
        skeletonView.removeView(view)
        originalParent.removeView(skeletonView)
        view.cloneTranslations(skeletonView)
        originalParent.addView(view, originalParams)
    }

    private fun generateProgressView(skeleton: Skeleton): ProgressView {
        return when (skeleton) {
            is RecyclerViewSkeleton -> generateRecyclerView(skeleton)
            is ViewSkeleton -> generateSimpleView(skeleton)
            is TextViewSkeleton -> generateTextView(skeleton)
        }
    }

    private fun generateTextView(skeleton: TextViewSkeleton) = with(skeleton) {
        return@with if (target is TextViewTarget) {
            val attributes = TextViewAttributes(
                    view = target.view,
                    color = color ?: defaults.color,
                    cornerRadius = cornerRadius ?: defaults.cornerRadius,
                    isShimmerEnabled = isShimmerEnabled ?: defaults.isShimmerEnabled,
                    shimmer = shimmer ?: defaults.shimmer,
                    lineSpacing = lineSpacing ?: defaults.lineSpacing,
                    length = length
            )
            target.view.generateTextProgressView(attributes)
        } else {
            TextProgressView(context)
        }
    }

    private fun generateRecyclerView(skeleton: RecyclerViewSkeleton) = with(skeleton) {
        return@with if (target is RecyclerViewTarget) {
            val attributes = RecyclerViewAttributes(
                view = target.view,
                color = color ?: defaults.color,
                cornerRadius = cornerRadius ?: defaults.cornerRadius,
                isShimmerEnabled = isShimmerEnabled ?: defaults.isShimmerEnabled,
                shimmer = shimmer ?: defaults.shimmer,
                lineSpacing = lineSpacing ?: defaults.lineSpacing,
                itemLayout = itemLayoutResId,
                itemCount = itemCount ?: defaults.itemCount
            )
            target.view.generateRecyclerProgressView(attributes)
        } else {
            RecyclerProgressView(context)
        }
    }

    private fun generateSimpleView(skeleton: ViewSkeleton) = with(skeleton) {
        return@with if (target is SimpleViewTarget) {
            val attributes = SimpleViewAttributes(
                color = color ?: defaults.color,
                cornerRadius = cornerRadius ?: defaults.cornerRadius,
                isShimmerEnabled = isShimmerEnabled ?: defaults.isShimmerEnabled,
                shimmer = shimmer ?: defaults.shimmer,
                lineSpacing = lineSpacing ?: defaults.lineSpacing
            )
            target.view.generateSimpleProgressView(attributes)
        } else {
            SimpleProgressView(context)
        }
    }
}