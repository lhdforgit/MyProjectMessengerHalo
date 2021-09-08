package com.hahalolo.messager.bubble.base

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

abstract class AbsLifecycleView : FrameLayout, LifecycleOwner {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private lateinit var lifecycleRegistry: LifecycleRegistry

    private fun initView() {
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
        initializeBinding()
    }

    override fun onAttachedToWindow() {
        lifecycleRegistry.markState(Lifecycle.State.STARTED)
        super.onAttachedToWindow()
        initializeViewModel()
        initializeLayout()
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
    }

    override fun dispatchVisibilityChanged(changedView: View, visibility: Int) {
        super.dispatchVisibilityChanged(changedView, visibility)
    }

    override fun dispatchWindowVisibilityChanged(visibility: Int) {
        super.dispatchWindowVisibilityChanged(visibility)
    }

    override fun onDetachedFromWindow() {
//        lifecycleRegistry.markState(Lifecycle.State.INITIALIZED)
        super.onDetachedFromWindow()
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    abstract fun initializeBinding()
    abstract fun initializeViewModel()
    abstract fun initializeLayout()
    open fun onBackPress(): Boolean {
        return false
    }

    open fun invalidateLayout() {
        lifecycleRegistry.markState(Lifecycle.State.DESTROYED)
        //Clear view
    }
}