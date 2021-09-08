package com.hahalolo.messager.bubble.notify

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.*
import androidx.lifecycle.lifecycleScope
import com.facebook.rebound.*
import com.hahalolo.messager.bubble.BubbleServiceCallback
import com.hahalolo.messager.bubble.SpringConfigs
import com.hahalolo.messager.bubble.base.AbsLifecycleView
import com.hahalolo.messager.bubble.container.AbsBubbleContainerLayout
import com.hahalolo.messager.bubble.getOverlayFlag
import com.hahalolo.messager.bubble.getScreenSize
import com.hahalolo.messager.bubble.head.BubbleHead
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.BubbleMessageBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BubbleMessage : AbsLifecycleView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    lateinit var binding: BubbleMessageBinding

    private var serviceCallback: BubbleServiceCallback? = null

    fun updateBubbleServiceCallback(serviceCallback: BubbleServiceCallback?) {
        this.serviceCallback = serviceCallback
    }

    override fun initializeBinding() {
        binding = BubbleMessageBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun initializeViewModel() {}

    private var params: WindowManager.LayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        AbsBubbleContainerLayout.BUBBLE_HEAD_HEIGHT_SIZE,
        getOverlayFlag(),
        0,
        PixelFormat.TRANSLUCENT
    )

    private var springSystem: SpringSystem = SpringSystem.create()
    private var springX: Spring = springSystem.createSpring()
    private var springY: Spring = springSystem.createSpring()
    private val scaleSpring = springSystem.createSpring()

    private val paint = Paint()

    private val springListener = object : SpringListener {
        override fun onSpringUpdate(spring: Spring?) {
        }

        override fun onSpringAtRest(spring: Spring?) = Unit

        override fun onSpringActivate(spring: Spring?) = Unit

        override fun onSpringEndStateChange(spring: Spring?) = Unit
    }

    override fun initializeLayout() {
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        //init params
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 0

        //SpringX
        springX.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                x = spring.currentValue.toFloat()
            }
        })

        springX.springConfig = SpringConfigs.NOT_DRAGGING
        springX.addListener(springListener)

        //SpringY
        springY.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                y = spring.currentValue.toFloat()
            }
        })
        springY.springConfig = SpringConfigs.NOT_DRAGGING
        springY.addListener(springListener)

        this.setLayerType(View.LAYER_TYPE_HARDWARE, paint)

        scaleSpring.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                scaleX = spring.currentValue.toFloat()
                scaleY = spring.currentValue.toFloat()
            }

            override fun onSpringEndStateChange(spring: Spring?) {
                super.onSpringEndStateChange(spring)
                if (!showed) visibility = View.GONE

            }
        })
        scaleSpring.springConfig = SpringConfigs.CONTENT_SCALE
        scaleSpring.currentValue = 0.0
    }

    fun addToContainer(parent: ViewGroup) {
        parent.addView(this, params)
    }

    fun showMessage(pushMsg: String, onTop: Boolean, onRight: Boolean, topBubbleHead: BubbleHead) {
        if (onRight){
            binding.messageContent.setBackgroundResource(R.drawable.shape_bubble_message_right)
        }else{
            binding.messageContent.setBackgroundResource(R.drawable.shape_bubble_message_left)
        }
        binding.container.gravity = if (onRight) {
            Gravity.END or Gravity.CENTER_VERTICAL
        } else {
            Gravity.START or Gravity.CENTER_VERTICAL
        }

        binding.messageContent.text = pushMsg

        updateSpringCurrentValue(0.0, topBubbleHead.springYCurrentValue().toDouble())
        visibility = View.VISIBLE
        showed = true
        scaleSpring.endValue = 1.0
        this.pivotX = topBubbleHead.springXCurrentValue() + if (onRight) 0f else  AbsBubbleContainerLayout.BUBBLE_HEAD_HEIGHT_SIZE.toFloat()
        this.pivotY = if (onTop) 0f else  AbsBubbleContainerLayout.BUBBLE_HEAD_HEIGHT_SIZE.toFloat()
        lifecycleScope.launch {
            delay(2500)
            hideBubbleMessage()
        }
    }

    private var showed = false

    fun hideBubbleMessage(){
        showed = false
        if (visibility == View.VISIBLE){
            scaleSpring.endValue = 0.0
        }
    }

    //update lại vị trí của của bubble khi đang đc hiển thị
// ở vị trí position, và width của topBubble
    fun rearrangeExpanded(position: Int, widthTop: Double, animation: Boolean = true) {
        val metrics = getScreenSize()
        val x: Double = metrics.widthPixels - widthTop -
                position * (this.paramsWidth() + AbsBubbleContainerLayout.CHAT_HEAD_EXPANDED_PADDING).toDouble() -
                AbsBubbleContainerLayout.CHAT_HEAD_EXPANDED_MARGIN_END

        val y: Double = AbsBubbleContainerLayout.CHAT_HEAD_EXPANDED_MARGIN_TOP.toDouble()

        if (animation) {
            this.updateSpringEndValue(x, y)
        } else {
            this.updateSpringCurrentValue(x, y)
        }
    }

    fun paramsWidth(): Int {
        return params.width
    }

    fun paramsHeight(): Int {
        return params.width
    }

    fun springXConfig(): SpringConfig {
        return springX.springConfig
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

    fun springXVelocity(): Double {
        return springX.velocity
    }

    fun springYVelocity(): Double {
        return springY.velocity
    }

    fun setAtRest() {
        springX.setAtRest()
        springY.setAtRest()
    }

    /*UPDATE*/
    fun updateSpringConfig(config: SpringConfig) {
        springX.springConfig = config
        springY.springConfig = config
    }

    fun updateSpringXConfig(config: SpringConfig) {
        springX.springConfig = config
    }

    fun updateSpringYConfig(config: SpringConfig) {
        springY.springConfig = config
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

    fun updateSpringCurrentValue(x: Double, y: Double) {
        springX.currentValue = x
        springY.currentValue = y
    }

    fun updateSpringXCurrentValue(value: Double) {
        springX.currentValue = value
    }

    fun updateSpringYCurrentValue(value: Double) {
        springY.currentValue = value
    }

    fun updateSpringVelocity(x: Double, y: Double) {
        springX.velocity = x
        springY.velocity = y
    }

    fun isSpringX(spring: Spring): Boolean {
        return springX == spring
    }

    fun isSpringY(spring: Spring): Boolean {
        return springY == spring
    }

}