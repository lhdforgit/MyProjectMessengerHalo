@file:OptIn(ExperimentalProgressApi::class)

package com.hahalolo.progress.memory

import androidx.annotation.MainThread
import com.hahalolo.progress.annotation.ExperimentalProgressApi
import com.hahalolo.progress.custom.ProgressView
import com.hahalolo.progress.skeleton.Skeleton
import com.hahalolo.progress.target.Target

internal sealed class TargetDelegate {

    @MainThread
    open fun start() {}

    @MainThread
    open fun success(skeleton: ProgressView) {}

    @MainThread
    open fun error() {}

    @MainThread
    open fun clear() {}
}

internal class ViewTargetDelegate(
    private val skeleton: Skeleton,
    private val target: Target?
) : TargetDelegate() {

    override fun start() {
        target?.onStart()
    }

    override fun success(skeleton: ProgressView) {
        target?.onSuccess(skeleton)
    }

    override fun error() {
        target?.onError()
    }

    override fun clear() {}
}