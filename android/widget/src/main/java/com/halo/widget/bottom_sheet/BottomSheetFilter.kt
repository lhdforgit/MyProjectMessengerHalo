/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.bottom_sheet

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.halo.common.utils.LogTrace
import com.halo.common.utils.T
import com.halo.widget.R
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_container.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface BottomSheetFilter {

    suspend fun stateExpanded(): Boolean

    suspend fun stateHaftExpand(): Boolean

    suspend fun show(): Boolean

    suspend fun hide(): Boolean

    suspend fun addStateHaftExpand(view: View)
}


interface BottomSheetFilterCallback {


}


class BottomSheetFilterFr : Fragment(), BottomSheetFilter {

    private var callback  : BottomSheetFilterCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        /*require(context is BottomSheetFilterCallback) {
            "Must implement BottomSheetFilter."
        }*/
        //this.callback = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBottomSheet()
    }

    var isInside = false
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view  = inflater.inflate(R.layout.fragment_container, container, false)
        /*view?.setOnTouchListener { v, event ->
            val pointTouch = PointF(event.x,event.y)
            JobUtil.doJob(Dispatchers.Main){
                isInside = isInsideView(pointTouch.toPoint(),bottomView)
                LogTrace.traceE(T.EDITOR,"isInside $isInside")
            }
            true
        }*/
        return view
    }

    private suspend fun  isInsideView(pointF : Point,view: View?) : Boolean = withContext(Dispatchers.Default){
        view?.run {
            val rect = Rect(view.left,view.top,view.right,view.bottom)
            rect.contains(pointF.x,pointF.y)
        } ?: false
    }

    private fun initBottomSheet() {
        /*motionLayout?.setTransitionListener(object  : MotionLayout.TransitionListener {

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                LogTrace.traceE(T.EDITOR,"")
            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                LogTrace.traceE(T.EDITOR,"")
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                LogTrace.traceE(T.EDITOR,"")
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                LogTrace.traceE(T.EDITOR,"")
            }
        })*/
    }

    override fun onDetach() {
        clearFindViewByIdCache()
        super.onDetach()
    }


    suspend fun inflateFragment(fm: Fragment?): Boolean = withContext(Dispatchers.Main) {
        true
    }

    override suspend fun stateExpanded(): Boolean {
        return true
    }

    override suspend fun stateHaftExpand(): Boolean {
        return true
    }

    override suspend fun show(): Boolean {
        return true
    }

    override suspend fun hide(): Boolean {
        return true
    }

    override suspend fun addStateHaftExpand(view: View) {
        LogTrace.traceE(T.EDITOR,"")
        bottomView?.addView(view)
    }
}


class BottomSheetDrawer : MaterialCardView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)
    constructor(context: Context, attributes: AttributeSet, def: Int) : super(context, attributes)

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    override fun onDetachedFromWindow() {
        clearFindViewByIdCache()
        super.onDetachedFromWindow()
    }

    override fun onDragEvent(event: DragEvent?): Boolean {
        LogTrace.traceE(T.EDITOR,"onDragEvent")
        return super.onDragEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        LogTrace.traceE(T.EDITOR,"onInterceptTouchEvent")
        return super.onInterceptTouchEvent(ev)
    }
}