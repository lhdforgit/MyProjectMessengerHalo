package com.halo.widget.effect

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.annotation.LayoutRes
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import com.halo.widget.common.Log


class ScaleAnimation @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private var isErrorAction: Boolean = false
    private var isExpand: Boolean = false

    private var collapse: View? = null
    private var expand: View? = null
    private var isSelectOnly: Boolean = false

    init {
        removeAllViews()
        orientation = VERTICAL
    }

    fun setIsExpandLayout(isExpand: Boolean) {
        this.isExpand = isExpand
        "setIsExpandLayout :... ${this.isExpand} ".Log()
        if (!isExpand) {
            this.expand?.let { _expand -> collapse(_expand) }
        }
    }

    companion object {
        @JvmStatic
        fun setSelectOnly(contentLayout: LinearLayout, current: Int) {
            contentLayout.forEachIndexed { index, _itemView ->
                (_itemView as? ScaleAnimation)?.let {
                    if (current != index) {
                        it.setIsExpandLayout(false)
                    } else {
                        it.setIsExpandLayout(true)
                    }
                }
            }
        }

        @JvmStatic
        fun setErrorAction(contentLayout: LinearLayout) {
            contentLayout.forEach { _scale_animation ->
                (_scale_animation as? ScaleAnimation)?.let {
                    if (!it.isExpand) {
                        EffectUtils.makeAnimation(_scale_animation)
                    }
                }
            }
        }

        @JvmStatic
        fun setCollapse(contentLayout: LinearLayout) {
            contentLayout.forEach { _view ->
                (_view as? ScaleAnimation)?.let { _scale_animation ->
                    _scale_animation.setIsExpandLayout(false)
                }
            }
        }
    }


    fun setSelectOnly(isEnable: Boolean) {
        this.isSelectOnly = isEnable
    }

    fun setLayout(
        collapse: View,
        expand: View,
        isSelectOnly: Boolean = true,
        contentLayout: LinearLayout? = null,
        interact: (collapse: View?, expand: View?, isExpand: Boolean, action: () -> Unit) -> Unit = { _, _, _, _ -> }
    ) {
        removeAllViews()
        orientation = VERTICAL
        this.collapse = collapse
        this.expand = expand
        addView(this.collapse)
        addView(this.expand)
        if (!isExpand) {
            this.expand?.let { _expand -> collapse(_expand) }
        }
        interact(this.collapse, this.expand, isExpand) {
            //"action :... ".Log()
            isExpand = !isExpand
            if (!isExpand) {
                this.expand?.let { _expand -> collapse(_expand) }
            } else {
                this.expand?.let { _expand ->
                    expand(
                        _expand,
                        targetHeight = getViewHeight(_expand)
                    )
                }
            }
            if (isSelectOnly) {
                contentLayout?.let { _contentLayoyut ->
                    val current = _contentLayoyut.indexOfChild(this@ScaleAnimation)
                    _contentLayoyut.forEachIndexed { index, view ->
                        (view as? ScaleAnimation)?.let { _scale_animation ->
                            if (current != index) {
                                _scale_animation.setIsExpandLayout(false)
                            }
                        }
                    }
                }
            }
        }
    }

    fun setLayout(
        @LayoutRes collapse: Int,
        @LayoutRes expand: Int,
        isSelectOnly: Boolean = true,
        contentLayout: LinearLayout? = null,
        interact: (collapse: View?, expand: View?, isExpand: Boolean, action: () -> Unit) -> Unit = { _, _, _, _ -> }
    ) {
        removeAllViews()
        orientation = VERTICAL

        this.collapse = View.inflate(context, collapse, null)
        this.expand = View.inflate(context, expand, null)

        addView(this.collapse)
        addView(this.expand)
        if (!isExpand) {
            this.expand?.let { _expand -> collapse(_expand) }
        }
        interact(this.collapse, this.expand, isExpand) {
            isExpand = !isExpand
            if (!isExpand) {
                this.expand?.let { _expand -> collapse(_expand) }
            } else {
                this.expand?.let { _expand ->
                    expand(
                        _expand,
                        targetHeight = getViewHeight(_expand)
                    )
                }
            }
            if (isSelectOnly) {
                contentLayout?.let { _contentLayoyut ->
                    val current = _contentLayoyut.indexOfChild(this@ScaleAnimation)
                    _contentLayoyut.forEachIndexed { _index, view ->
                        (view as? ScaleAnimation)?.let { _scale_animation ->
                            "current :.. ${current} ".Log()
                            if (current != _index) {
                                _scale_animation.setIsExpandLayout(false)
                            } else {
                                _scale_animation.setIsExpandLayout(true)
                            }
                        }
                    }
                }
            }

        }
    }

    private fun getViewHeight(view: View): Int {
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
        val widthMeasureSpec = MeasureSpec.makeMeasureSpec(
            deviceWidth,
            MeasureSpec.AT_MOST
        )
        val heightMeasureSpec = MeasureSpec.makeMeasureSpec(
            0,
            MeasureSpec.UNSPECIFIED
        )
        view.measure(widthMeasureSpec, heightMeasureSpec)
        return view.measuredHeight
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

    private fun collapse(v: View, duration: Int = 100, targetHeight: Int = 0) {
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

    /**
     * @see setIsErrorAction : set enable animation error for view ...
     *
     */
    fun setIsErrorAction(isErrorAction: Boolean) {
        this.isErrorAction = isErrorAction
    }

}