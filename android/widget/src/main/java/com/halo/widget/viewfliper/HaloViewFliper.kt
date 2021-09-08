/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.viewfliper

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Interpolator
import android.widget.Scroller
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import java.lang.reflect.Field
import kotlin.math.abs


fun log(string: String) {
    Log.e("HaloViewFliper", string)
}

open class HaloViewFliper : ViewPager {
    constructor(context: Context) : super(context) {
        init()
        changePagerScroller()
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init()
        changePagerScroller()
    }

    protected var isInfinite = true
    private var isAutoScroll = false
    private var wrapContent = true
    private var aspectRatio: Float = 0f
    var scroller: CustomScroll? = null

    //AutoScroll
    private var interval = 5000
    private var previousPosition = 0
    private var currentPagePosition = 0
    private val autoScrollHandler = Handler()

    private val autoScrollRunnable = Runnable {
        adapter?.also {
            if (!isAutoScroll || it.count < 2) return@Runnable
            if (isInfinite && it.count - 1 == currentPagePosition) {
                currentPagePosition = 0
            } else {
                currentPagePosition++
            }
        } ?: return@Runnable
        setCurrentItem(currentPagePosition, true)
    }

    //For Indicator
    private var indicatorPageChangeListener: IndicatorPageChangeListener? = null
    private var previousScrollState = SCROLL_STATE_IDLE
    private var scrollState = SCROLL_STATE_IDLE
    private var isToTheRight = true
    /**
     * This boolean indicates whether LoopingViewPager needs to continuously tell the indicator about
     * the progress of the scroll, even after onIndicatorPageChange().
     * If indicator is smart, it should be able to finish the animation by itself after we told it that a position has been selected.
     * If indicator is not smart, then LoopingViewPager will continue to fire onIndicatorProgress() to update the indicator
     * transition position.
     */
    private var isIndicatorSmart = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpecs: Int) {

        var heightMeasureSpec = heightMeasureSpecs

        val width = View.MeasureSpec.getSize(widthMeasureSpec)

        if (aspectRatio > 0) {
            val height = Math.round(View.MeasureSpec.getSize(widthMeasureSpec).toFloat() / aspectRatio)
            val finalWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
            val finalHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            super.onMeasure(finalWidthMeasureSpec, finalHeightMeasureSpec)
        } else {
            if (wrapContent) {
                val mode = View.MeasureSpec.getMode(heightMeasureSpec)
                // Unspecified means that the ViewPager is in a ScrollView WRAP_CONTENT.
                // At Most means that the ViewPager is not in a ScrollView WRAP_CONTENT.
                if (mode == MeasureSpec.UNSPECIFIED || mode == MeasureSpec.AT_MOST) {
                    // super has to be called in the beginning so the child views can be initialized.
                    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
                    var height = 0
                    // Remove padding from width
                    val childWidthSize = width - paddingLeft - paddingRight
                    // Make child width MeasureSpec
                    val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY)
                    for (i in 0 until childCount) {
                        val child = getChildAt(i)
                        child.measure(childWidthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
                        val h = child.measuredHeight
                        if (h > height) {
                            height = h
                        }
                    }
                    // Add padding back to child height
                    height += paddingTop + paddingBottom
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                }
            }
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    protected fun init() {

        addOnPageChangeListener(object : OnPageChangeListener {
            var currentPosition: Float = 0f
            // Handler paged scroll
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                if (indicatorPageChangeListener == null) return

                isToTheRight = position + positionOffset >= currentPosition
                if (positionOffset == 0f) currentPosition = position.toFloat()
                val realPosition = getSelectingIndicatorPosition(isToTheRight)
                val progress: Float = if (scrollState == SCROLL_STATE_SETTLING && abs(currentPagePosition - previousPosition) > 1) {
                    val pageDiff = abs(currentPagePosition - previousPosition)
                    if (isToTheRight) {
                        (position - previousPosition).toFloat() / pageDiff + positionOffset / pageDiff
                    } else {
                        (previousPosition - (position + 1)).toFloat() / pageDiff + (1 - positionOffset) / pageDiff
                    }
                } else {
                    if (isToTheRight) positionOffset else 1 - positionOffset
                }
                if (progress == 0f || progress > 1) return

                if (isIndicatorSmart) {
                    if (scrollState != SCROLL_STATE_DRAGGING) return
                    indicatorPageChangeListener!!.onIndicatorProgress(realPosition, progress)
                } else {
                    if (scrollState == SCROLL_STATE_DRAGGING) {
                        if (isToTheRight && Math.abs(realPosition - currentPagePosition) == 2 || !isToTheRight && realPosition == currentPagePosition) {
                            //If this happens, it means user is fast scrolling where onPageSelected() is not fast enough
                            //to catch up with the scroll, thus produce wrong position value.
                            return
                        }
                    }
                    indicatorPageChangeListener!!.onIndicatorProgress(realPosition, progress)
                }
            }

            // Page selected
            override fun onPageSelected(position: Int) {
                previousPosition = currentPagePosition
                currentPagePosition = position
                if (indicatorPageChangeListener != null) {
                    indicatorPageChangeListener!!.onIndicatorPageChange(getIndicatorPosition())
                }
                autoScrollHandler.removeCallbacks(autoScrollRunnable)
                autoScrollHandler.postDelayed(autoScrollRunnable, interval.toLong())
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (!isIndicatorSmart) {
                    if (scrollState == SCROLL_STATE_SETTLING && state == SCROLL_STATE_DRAGGING) {
                        if (indicatorPageChangeListener != null) {
                            indicatorPageChangeListener!!.onIndicatorProgress(
                                    getSelectingIndicatorPosition(isToTheRight), 1f)
                        }
                    }
                }
                previousScrollState = scrollState
                scrollState = state
                if (state == SCROLL_STATE_IDLE) {
                    //Below are code to achieve infinite scroll.
                    //We silently and immediately flip the item to the first / last.
                    if (isInfinite) {
                        if (adapter == null) return
                        val itemCount = adapter!!.count
                        if (itemCount < 2) {
                            return
                        }
                        val index = currentItem
                        if (index == 0) {
                            setCurrentItem(itemCount - 2, false) //Real last item
                        } else if (index == itemCount - 1) {
                            setCurrentItem(1, false) //Real first item
                        }
                    }

                    if (indicatorPageChangeListener != null) {
                        indicatorPageChangeListener!!.onIndicatorProgress(getIndicatorPosition(), 1f)
                    }
                }
            }
        })
        if (isInfinite) setCurrentItem(1, false)
    }

    internal fun setAdapter(adapter: PagerAdapter) {
        super.setAdapter(adapter)
        if (isInfinite) setCurrentItem(1, false)
    }

    private fun resumeAutoScroll() {
        autoScrollHandler.postDelayed(autoScrollRunnable, interval.toLong())
    }

    private fun pauseAutoScroll() {
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }


    /**
     * A method that helps you integrate a ViewPager Indicator.
     * This method returns the expected position (Starting from 0) of indicators.
     * This method should be used after currentPagePosition is updated.
     */
    fun getIndicatorPosition(): Int {
        if (!isInfinite) {
            return currentPagePosition
        } else {
            if (adapter !is HaloViewFliperAdapter<*>) return currentPagePosition
            return if (currentPagePosition == 0) { //Dummy last item is selected. Indicator should be at the last one
                (adapter as HaloViewFliperAdapter<*>).getListCount() - 1
            } else if (currentPagePosition == (adapter as HaloViewFliperAdapter<*>).getLastItemPosition() + 1) {
                //Dummy first item is selected. Indicator should be at the first one
                0
            } else {
                currentPagePosition - 1
            }
        }
    }

    /**
     * A method that helps you integrate a ViewPager Indicator.
     * This method returns the expected position (Starting from 0) of indicators.
     * This method should be used before currentPagePosition is updated, when user is trying to
     * select a different page, i.e. onPageScrolled() is triggered.
     */
    fun getSelectingIndicatorPosition(isToTheRight: Boolean): Int {
        if (scrollState == SCROLL_STATE_SETTLING || scrollState == SCROLL_STATE_IDLE
                || previousScrollState == SCROLL_STATE_SETTLING && scrollState == SCROLL_STATE_DRAGGING) {
            return getIndicatorPosition()
        }
        val delta = if (isToTheRight) 1 else -1
        if (isInfinite) {
            if (adapter !is HaloViewFliperAdapter<*>) return currentPagePosition + delta
            return if (currentPagePosition == 1 && !isToTheRight) { //Special case for first page to last page
                (adapter as HaloViewFliperAdapter<*>).getLastItemPosition() - 1
            } else if (currentPagePosition == (adapter as HaloViewFliperAdapter<*>).getLastItemPosition() && isToTheRight) { //Special case for last page to first page
                0
            } else {
                currentPagePosition + delta - 1
            }
        } else {
            return currentPagePosition + delta
        }
    }

    /**
     * A method that helps you integrate a ViewPager Indicator.
     * This method returns the expected count of indicators.
     */
    fun getIndicatorCount(): Int {
        return if (adapter is HaloViewFliperAdapter<*>) {
            (adapter as HaloViewFliperAdapter<*>).getListCount()
        } else {
            adapter!!.count
        }
    }


    /**
     * This function needs to be called if dataSet has changed,
     * in order to reset current selected item and currentPagePosition and autoPageSelectionLock.
     */
    fun reset() {
        currentPagePosition = if (isInfinite) {
            setCurrentItem(1, false)
            1
        } else {
            setCurrentItem(0, false)
            0
        }
    }

    fun setIndicatorSmart(isIndicatorSmart: Boolean) {
        this.isIndicatorSmart = isIndicatorSmart
    }

    fun setIndicatorPageChangeListener(callback: IndicatorPageChangeListener) {
        this.indicatorPageChangeListener = callback
    }

    fun setInterval(interval: Int) {
        this.interval = interval
        resetAutoScroll()
    }

    fun setAutoScroll() {
        this.isAutoScroll = true
    }

    private fun resetAutoScroll() {
        pauseAutoScroll()
        resumeAutoScroll()
    }

    fun start(interval: Int){
        this.interval = interval
        resetAutoScroll()
        setAutoScroll()
    }

    fun pendingScroll(){
        pauseAutoScroll()
    }

    private fun changePagerScroller() {
        try {
            var mScroller: Field? = null
            mScroller = ViewPager::class.java.getDeclaredField("mScroller")
            mScroller.isAccessible = true
            val interpolator = ViewPager::class.java.getDeclaredField("sInterpolator")
            interpolator.isAccessible = true
            scroller = CustomScroll(context, interpolator.get(null) as Interpolator)
            mScroller.set(this@HaloViewFliper, scroller)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    interface IndicatorPageChangeListener {
        fun onIndicatorProgress(selectingPosition: Int, progress: Float)

        fun onIndicatorPageChange(newIndicatorPosition: Int)
    }


}

/**
 * Customize Scrollable of ViewFlipper
 *
 * */
class CustomScroll : Scroller, GestureDetector.OnGestureListener {

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    var scrollFactor = 1

    constructor(context: Context) : super(context)

    constructor(context: Context, interpolator: Interpolator) : super(context, interpolator)

    @SuppressLint("NewApi")
    constructor(context: Context, interpolator: Interpolator, flywheel: Boolean) : super(context, interpolator, flywheel)

    override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
        super.startScroll(startX, startY, dx, dy, duration * scrollFactor)
    }
}



