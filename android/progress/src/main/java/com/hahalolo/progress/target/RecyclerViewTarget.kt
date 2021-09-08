package com.hahalolo.progress.target

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.hahalolo.progress.custom.ProgressView

/** A [Target] that handles setting skeleton on a [RecyclerView]. */
open class RecyclerViewTarget(override val view: RecyclerView) : ViewTarget<RecyclerView>, DefaultLifecycleObserver {

    override fun onStart() = Unit

    /** Show the [skeleton] view */
    override fun onSuccess(skeleton: ProgressView) = skeleton.showSkeleton()

    override fun onError() = Unit

    override fun onStart(owner: LifecycleOwner) {}

    override fun onStop(owner: LifecycleOwner) {}
}