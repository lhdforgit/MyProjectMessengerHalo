@file:JvmName("Views")
@file:Suppress("unused")
@file:OptIn(ExperimentalProgressApi::class)

package com.halo.widget.progress

import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.progress.SkeletonLoader
import com.hahalolo.progress.annotation.ExperimentalProgressApi
import com.hahalolo.progress.custom.ProgressView
import com.hahalolo.progress.skeleton.RecyclerViewSkeleton
import com.hahalolo.progress.skeleton.TextViewSkeleton
import com.hahalolo.progress.skeleton.ViewSkeleton
import com.hahalolo.progress.util.ProgressUtils

/**
 * This is the type-unsafe version of [View.loadSkeleton].
 *
 * Example:
 * ```
 * view.loadSkeleton {
 *      color(R.color.colorExample)
 * }
 * ```
 *
 * @param skeletonLoader The [SkeletonLoader] that will be used to create the [ViewSkeleton].
 * @param builder An optional lambda to configure the skeleton before it is loaded.
 */
@JvmSynthetic
inline fun View.loadSkeleton(
    skeletonLoader: SkeletonLoader = Progress.skeletonLoader(context),
    builder: ViewSkeleton.Builder.() -> Unit = {}
) {
    val skeleton = ViewSkeleton.Builder(context)
        .target(this)
        .apply(builder)
        .build()
    skeletonLoader.load(skeleton)
}


/**
 * This is the type-unsafe version of [View.generateSkeleton].
 *
 * Example:
 * ```
 * val progressView = view.generateSkeleton {
 *      color(R.color.colorSkeleton)
 * }
 * progressView.showSkeleton()
 * ```
 *
 * @param skeletonLoader The [SkeletonLoader] that will be used to create the [ViewSkeleton].
 * @param builder An optional lambda to configure the skeleton.
 * @return the [ProgressView] that contains the skeleton
 */
@JvmSynthetic
inline fun View.generateSkeleton(
    skeletonLoader: SkeletonLoader = Progress.skeletonLoader(context),
    builder: ViewSkeleton.Builder.() -> Unit = {}
): ProgressView {
    val skeleton = ViewSkeleton.Builder(context)
        .target(this)
        .apply(builder)
        .build()
    return skeletonLoader.generate(skeleton)
}

/**
 * Load the skeleton referenced by [itemLayout] and set it on this [RecyclerView].
 *
 * This is the type-unsafe version of [RecyclerView.loadSkeleton].
 *
 * Example:
 * ```
 * recyclerView.loadSkeleton(R.layout.item_example) {
 *      color(R.color.colorExample)
 * }
 * ```
 * @param itemLayout Layout resource of the itemView that will be used to create the skeleton view.
 * @param skeletonLoader The [SkeletonLoader] that will be used to create the [RecyclerViewSkeleton].
 * @param builder An optional lambda to configure the skeleton before it is loaded.
 */
@JvmSynthetic
inline fun RecyclerView.loadSkeleton(
    @LayoutRes itemLayout: Int,
    skeletonLoader: SkeletonLoader = Progress.skeletonLoader(context),
    builder: RecyclerViewSkeleton.Builder.() -> Unit = {}
) {
    val skeleton = RecyclerViewSkeleton.Builder(context, itemLayout)
        .target(this)
        .apply(builder)
        .build()
    skeletonLoader.load(skeleton)
}

/**
 * Set [length] of the [TextView].
 *
 * This is the type-unsafe version of [TextView.loadSkeleton].
 *
 * Example:
 * ```
 * textView.loadSkeleton(10) {
 *      color(R.color.colorExample)
 * }
 * ```
 * @param length Length of the [TextView].
 * @param skeletonLoader The [SkeletonLoader] that will be used to create the [TextViewSkeleton].
 * @param builder An optional lambda to configure the skeleton before it is loaded.
 */
@JvmSynthetic
inline fun TextView.loadSkeleton(
    length: Int,
    skeletonLoader: SkeletonLoader = Progress.skeletonLoader(context),
    builder: TextViewSkeleton.Builder.() -> Unit = {}
) {
    val skeleton = TextViewSkeleton.Builder(context, length)
            .target(this)
            .apply(builder)
            .build()
    skeletonLoader.load(skeleton)
}


/**
 * @return True if the skeleton associated with this [View] is shown.
 */
fun View.isSkeletonShown(): Boolean {
    return ProgressUtils.isSkeletonShown(this)
}

/**
 * Calls the specified function [block] after the skeleton is hidden.
 */
fun View.afterHideSkeleton(block: () -> Unit) {
    ProgressUtils.afterHide(this, block)
}

/**
 * Hide all skeletons associated with this [View].
 */
fun View.hideSkeleton() {
    ProgressUtils.hide(this)
}