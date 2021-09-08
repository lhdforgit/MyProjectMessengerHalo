package com.hahalolo.messager.bubble.close

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import com.hahalolo.messager.bubble.*
import com.hahalolo.messager.bubble.container.AbsBubbleContainerLayout
import com.hahalolo.messenger.R

class BubbleClose : View {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        initView()
    }

    private var params = WindowManager.LayoutParams(
        AbsBubbleContainerLayout.CLOSE_SIZE + AbsBubbleContainerLayout.CLOSE_ADDITIONAL_SIZE,
        AbsBubbleContainerLayout.CLOSE_SIZE + AbsBubbleContainerLayout.CLOSE_ADDITIONAL_SIZE,
            getOverlayFlag(),
            WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
    ).apply {
        this.gravity = Gravity.START or Gravity.TOP
    }

    private var bubbleCloseListener: BubbleCloseListener? = null

    fun updateBubbleCloseListener(listener: BubbleCloseListener) {
        this.bubbleCloseListener = listener
    }

    private var gradientParams = FrameLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dpToPx(150f))
    private var springSystem = SpringSystem.create()

    private var springY = springSystem.createSpring()
    private var springX = springSystem.createSpring()
    private var springAlpha = springSystem.createSpring()
    private var springScale = springSystem.createSpring()

    private val paint = Paint()

    private val gradient = FrameLayout(context)

    private var hidden = true

    private var bitmapBg = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(BubbleService.instance?.resources, R.drawable.ic_bubble_close_bg),
        AbsBubbleContainerLayout.CLOSE_SIZE, AbsBubbleContainerLayout.CLOSE_SIZE, false)
    private val bitmapClose = Bitmap.createScaledBitmap(
            BitmapFactory.decodeResource(BubbleService.instance?.resources, R.drawable.ic_bubble_close),
            dpToPx(28f),
            dpToPx(28f), false)

    private fun initView() {
        if (BubbleService.DEBUG)  setBackgroundColor(Color.parseColor("#2200ff00"))

    }

    fun onHide() {
        val metrics = getScreenSize()
        springY.endValue = metrics.heightPixels.toDouble() + height
        springX.endValue = metrics.widthPixels.toDouble() / 2 - width / 2

        springAlpha.endValue = 0.0
        hidden = true
    }

    fun onShow() {

        hidden = false
        visibility = View.VISIBLE

        springAlpha.endValue = 1.0
        onResetScale()
    }

    fun onEnlarge() {
        springScale.endValue = AbsBubbleContainerLayout.CLOSE_ADDITIONAL_SIZE.toDouble()
    }

    fun onResetScale() {
        springScale.endValue = 1.0
    }

    private fun onPositionUpdate() {
        bubbleCloseListener?.onPositionUpdate(springX.endValue, springY.endValue, width, height)
    }

    init {
        this.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
        visibility = View.INVISIBLE
        onHide()

        springX.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                x = spring.currentValue.toFloat()
                onPositionUpdate()
            }
        })

        springY.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                y = spring.currentValue.toFloat()
                bubbleCloseListener?.onYSpringUpdate(spring)
                onPositionUpdate()
            }
        })

        springScale.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                BubbleService.instance?.resources?.run {
                    Bitmap.createScaledBitmap(BitmapFactory.decodeResource(
                        this,
                        R.drawable.ic_bubble_close_bg), (spring.currentValue + AbsBubbleContainerLayout.CLOSE_SIZE).toInt(),
                        (spring.currentValue + AbsBubbleContainerLayout.CLOSE_SIZE).toInt(),
                        false)?.run{
                        bitmapBg = this
                    }
                }
                invalidate()
            }
        })

        springAlpha.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                gradient.alpha = spring.currentValue.toFloat()
            }
        })

        springScale.springConfig = SpringConfigs.CLOSE_SCALE
        springY.springConfig = SpringConfigs.CLOSE_Y

        params.gravity = Gravity.START or Gravity.TOP
        gradientParams.gravity = Gravity.BOTTOM

        gradient.background = ContextCompat.getDrawable(context, R.drawable.ic_bubble_close_gradient_bg)
        springAlpha.currentValue = 0.0

        z = 100f
    }

    fun addToContainer(parent: ViewGroup) {
        parent.addView(this, params)
        parent.addView(gradient, gradientParams)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawBitmap(bitmapBg, width / 2 - bitmapBg.width.toFloat() / 2, height / 2 - bitmapBg.height.toFloat() / 2, paint)
        canvas?.drawBitmap(bitmapClose, width / 2 - bitmapClose.width.toFloat() / 2, height / 2 - bitmapClose.height.toFloat() / 2, paint)
    }

    fun springXCurrentValue(): Float {
        return springX.currentValue.toFloat()
    }

    fun springYCurrentValue(): Float {
        return springY.currentValue.toFloat()
    }

    fun springXEndValue(): Float {
        return springX.endValue.toFloat()
    }

    fun springYEndValue(): Float {
        return springY.endValue.toFloat()
    }


    fun updateSpringEndValue(x: Double, y: Double) {
        springX.endValue = x
        springY.endValue = y
    }

    fun updateSpringXEndValue(value: Double) {
        springX.endValue = value
    }

    fun updateSpringYEndValue(value: Double) {
        springY.endValue = value
    }
}