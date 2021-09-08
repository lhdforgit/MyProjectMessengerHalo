package com.halo.widget.effect

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.TranslateAnimation
import androidx.annotation.AnimRes
import com.halo.widget.R

object EffectUtils {

    /**
     *
     * @param view      view that will be animated
     * @param duration  for how long in ms will it shake
     * @param offset    start offset of the animation
     * @return          returns the same view with animation properties
     */
    fun makeShake(view: View, duration: Long, offset: Long): View? {
        val anim: Animation =
            TranslateAnimation(-offset.toFloat(), offset.toFloat(), 0.toFloat(), 0.toFloat())
        anim.duration = duration
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = 5
        view.startAnimation(anim)
        return view
    }

    fun makeAnimation(
        view: View,
        duration: Long? = null,
        @AnimRes animRes: Int = R.anim.shake
    ): View? {
        val anim: Animation = AnimationUtils.loadAnimation(view.context, animRes)
        duration?.apply {
            anim.duration = this
        }
        view.startAnimation(anim)
        return view
    }


    fun expand(v: View, duration: Int = 100, targetHeight: Int = 0) {
        val prevHeight = v.height
        v.visibility = View.VISIBLE
        val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
        valueAnimator.addUpdateListener { animation ->
            v.layoutParams.height = animation.animatedValue as Int
            v.requestLayout()
        }
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = duration.toLong()
        valueAnimator.start()
    }

     fun collapse(v: View, duration: Int = 100, targetHeight: Int = 0) {
        val prevHeight = v.height
        val valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight)
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            v.layoutParams.height = animation.animatedValue as Int
            v.requestLayout()
        }
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.duration = duration.toLong()
        valueAnimator.start()
    }


     fun getViewHeight(view: View): Int {
        val wm = view.context
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = wm.defaultDisplay
        val deviceWidth: Int
        deviceWidth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            val size = Point()
            display.getSize(size)
            size.x;
        } else {
            display.width
        }
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            deviceWidth,
            View.MeasureSpec.AT_MOST
        )
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(
            0,
            View.MeasureSpec.UNSPECIFIED
        )
        view.measure(widthMeasureSpec, heightMeasureSpec)
        return view.measuredHeight
    }


}