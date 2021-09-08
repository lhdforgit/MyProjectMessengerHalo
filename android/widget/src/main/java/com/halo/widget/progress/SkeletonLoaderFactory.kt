package com.halo.widget.progress

import android.app.Application
import com.hahalolo.progress.SkeletonLoader


/**
 * A factory that creates new [SkeletonLoader] instances.
 *
 * To configure how the default [SkeletonLoader] is created **either**:
 * - Implement [SkeletonLoaderFactory] in your [Application].
 *
 * class App : Application(), SkeletonLoaderFactory {
 *      override fun newSkeletonLoader(): SkeletonLoader {
 *           return SkeletonLoader.Builder(this)
 *           .color(R.color.colorSkeleton)
 *           .cornerRadius(getDimension(R.dimen.default_corner_radius))
 *           .lineSpacing(getDimension(R.dimen.default_line_spacing))
 *           .itemCount(ITEM_COUNT)
 *           .shimmer(true)
 *           .shimmer(getCustomShimmer())
 *           .build()
 *      }
 *
 *      private fun getCustomShimmer(): Shimmer {
 *           return Shimmer.AlphaHighlightBuilder()
 *           .setDuration(1000)
 *           .setBaseAlpha(0.5f)
 *           .setHighlightAlpha(0.9f)
 *           .setWidthRatio(1f)
 *           .setHeightRatio(1f)
 *           .setDropoff(1f)
 *           .build()
 *      }
 * }
 *
 * - **Or** call [Progress.setSkeletonLoader] with your [SkeletonLoaderFactory].
 */
interface SkeletonLoaderFactory {

    /**
     * Return a new [SkeletonLoader].
     */
    fun newSkeletonLoader(): SkeletonLoader
}
