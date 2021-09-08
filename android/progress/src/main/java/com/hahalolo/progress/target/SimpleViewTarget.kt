package com.hahalolo.progress.target

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.hahalolo.progress.custom.ProgressView

/** A [Target] that handles setting skeleton on a [View]. */
open class SimpleViewTarget(override val view: View) : ViewTarget<View>, DefaultLifecycleObserver {

    override fun onStart() = Unit

    /** Show the [skeleton] view */
    override fun onSuccess(skeleton: ProgressView) = skeleton.showSkeleton()

    override fun onError() = Unit

    override fun onStart(owner: LifecycleOwner) {}

    override fun onStop(owner: LifecycleOwner) {}
}