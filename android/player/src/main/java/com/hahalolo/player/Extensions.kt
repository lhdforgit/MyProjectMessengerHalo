/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.player

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
import android.view.View
import androidx.annotation.RestrictTo
import androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
import androidx.core.util.Pools.Pool
import com.hahalolo.player.internal.BehaviorWrapper
import timber.log.Timber
import kotlin.math.abs

/**
 * @author ndn (2018/10/27).
 */
inline fun <T> Pool<T>.onEachAcquired(action: (T) -> Unit) {
    var item: T?
    do {
        item = this.acquire()
        if (item == null) break
        else action(item)
    } while (true)
}

// Return a View that is ancestor of container, and has direct parent is a CoordinatorLayout
internal fun View.findCoordinatorLayoutDirectChildContainer(target: View?): View? {
    // val root = peekDecorView() ?: return null
    var view = target
    do {
        if (view != null && view.parent is CoordinatorLayout) {
            return view
        } else if (view === this) {
            return null
        }

        if (view != null) {
            // Else, we will loop and crawl up the view hierarchy and try to find a parent
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)
    return null
}

internal inline fun <T, R> Iterable<T>.partitionToMutableSets(
    predicate: (T) -> Boolean,
    transform: (T) -> R
): Pair<MutableSet<R>, MutableSet<R>> {
    val first = mutableSetOf<R>()
    val second = mutableSetOf<R>()
    for (element in this) {
        if (predicate(element)) {
            first.add(transform(element))
        } else {
            second.add(transform(element))
        }
    }
    return Pair(first, second)
}

internal infix fun Rect.distanceTo(target: Pair<Pair<Int, Int>, Pair<Int, Int>>): Int {
    val (targetCenterX, targetHalfWidth) = target.first
    val (targetCenterY, targetHalfHeight) = target.second
    val distanceX = abs(this.centerX() - targetCenterX) / targetHalfWidth
    val distanceY = abs(this.centerY() - targetCenterY) / targetHalfHeight
    return distanceX + distanceY // no need to be the fancy Euclid sqrt distance.
}

// Learn from Glide: com/bumptech/glide/manager/RequestManagerRetriever.java#L304
internal fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

/** Utility to help client to quickly fetch the original [Behavior] of a View if available */
fun View.viewBehavior(): Behavior<*>? {
    val params = layoutParams
    val behavior = if (params is CoordinatorLayout.LayoutParams) params.behavior else null
    return if (behavior is BehaviorWrapper) behavior.delegate else behavior
}

// Because I want to compose the message first, then log it.
@RestrictTo(LIBRARY_GROUP)
fun String.logDebug() {
    //Timber.d(this)
}

@RestrictTo(LIBRARY_GROUP)
fun String.logInfo() {
}

@RestrictTo(LIBRARY_GROUP)
fun String.logWarn() {
    Timber.w(this)
}

@RestrictTo(LIBRARY_GROUP)
fun String.logError() {
    Timber.e(this)
}

internal inline fun debugOnly(action: () -> Unit) {
    if (BuildConfig.DEBUG) action()
}
