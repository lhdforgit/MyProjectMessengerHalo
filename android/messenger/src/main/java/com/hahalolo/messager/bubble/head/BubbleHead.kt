package com.hahalolo.messager.bubble.head

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.util.AttributeSet
import android.view.*
import android.view.View.OnTouchListener
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.facebook.rebound.*
import com.hahalolo.messager.bubble.*
import com.hahalolo.messager.bubble.base.AbsLifecycleView
import com.hahalolo.messager.bubble.container.AbsBubbleContainerLayout
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.BubbleHeadBinding
import com.halo.common.utils.SizeUtils
import com.halo.widget.reactions.message.MessageReactionUtils
import kotlin.math.pow

class BubbleHead : AbsLifecycleView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    lateinit var binding: BubbleHeadBinding

    var viewModel: BubbleHeadViewModel? = null
    private var serviceCallback: BubbleServiceCallback? = null

    fun updateBubbleServiceCallback(serviceCallback: BubbleServiceCallback?) {
        this.serviceCallback = serviceCallback
    }

    override fun initializeBinding() {
        binding = BubbleHeadBinding.inflate(LayoutInflater.from(context), this, true)
        if (BubbleService.DEBUG) setBackgroundColor(Color.parseColor("#440000FF"))
    }

    override fun initializeViewModel() {
        viewModel =
            serviceCallback?.createViewModel(BubbleHeadViewModel::class) as? BubbleHeadViewModel
    }

//    private var bubbleEntity: BubbleEntity? = null

//    fun updateBubbleEntity(bubbleEntity: BubbleEntity?) {
//        this.bubbleEntity = bubbleEntity
//        //Nếu đã có VIEWMODEL
//        viewModel?.updateRoomId(bubbleEntity?.roomId ?: "")
//        //Hiển thị lần đầu tiên
//        requestUpdateBubbleHeadLayout(bubbleEntity)
//    }

    fun bubbleId(): String? {
//        return bubbleEntity?.roomId
        return ""
    }

//    fun getBubbleEntity(): BubbleEntity? {
//        return bubbleEntity
//    }

    fun isBubbleHome(): Boolean {
//        return bubbleEntity == null
        return false
    }

    private var params: WindowManager.LayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        getOverlayFlag(),
        0,
        PixelFormat.TRANSLUCENT
    )

    private var springSystem: SpringSystem = SpringSystem.create()
    private var springX: Spring = springSystem.createSpring()
    private var springY: Spring = springSystem.createSpring()

    private val paint = Paint()

    private var initialX = 0.0f
    private var initialY = 0.0f

    private var initialTouchX = 0.0f
    private var initialTouchY = 0.0f

    private var moving = false

    private var notifications = 0
        set(value) {
            if (value >= 0) field = value
            if (value > 0) {
                binding.bubbleNotifications.visibility = VISIBLE
                binding.bubbleNotificationsText.text = if (value > 99) "99+" else value.toString()
            } else {
                binding.bubbleNotifications.visibility = INVISIBLE
            }
        }

    private val springListener = object : SpringListener {
        override fun onSpringUpdate(spring: Spring?) {
            if (spring !== springX && spring !== springY) return
            val totalVelocity = Math.hypot(springX.velocity, springY.velocity).toInt()
            bubbleHeadListener?.onChatHeadSpringUpdate(this@BubbleHead, spring, totalVelocity)
        }

        override fun onSpringAtRest(spring: Spring?) = Unit

        override fun onSpringActivate(spring: Spring?) = Unit

        override fun onSpringEndStateChange(spring: Spring?) = Unit
    }

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = OnTouchListener { v, event ->
        MessageReactionUtils.clearPopup()
        if (bubbleHeadListener?.canTouchToBubbleHead(event, this)!=true) return@OnTouchListener false
        val currentChatHead = bubbleHeadListener?.currentBubbleHead(v)
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = x
                initialY = y

                initialTouchX = event.rawX
                initialTouchY = event.rawY

                scaleX = 0.92f
                scaleY = 0.92f

                //TODO UPDATE CLOSE WHEN MOVE BUBBLE_HEAD
                bubbleHeadListener?.showBubbleClose(this@BubbleHead)
            }
            MotionEvent.ACTION_UP -> {

                if (!moving) {
                    if (currentChatHead == bubbleHeadListener?.activeBubbleHead()) {
                        // cuộn bubble
                        bubbleHeadListener?.collapse()
                    } else {
                        currentChatHead?.run {
                            if (this.isBubbleHome()) {
                                bubbleHeadListener?.showHomeConversation()
                            } else {
                                bubbleHeadListener?.updateActiveBubbleHead(this)
                            }
                        }
                    }
                } else {
                    val index = bubbleHeadListener?.bubbleHeadIndex(this@BubbleHead) ?: 0
                    rearrangeExpanded(
                        position = index,
                        widthTop = paramsWidth().toDouble()
                    )

//                        if (this@BubbleHead == bubbleHeadListener?.activeBubbleHead()) {
//                            bubbleHeadListener?.showContent()
//                        }

                    //TODO UPDATE SHOW CONTENT WHEN MOVED ALL BUBBLE_HEAD
                    bubbleHeadListener?.showContentMove()
                }
                scaleX = 1f
                scaleY = 1f
                moving = false

                //TODO UPDATE CLOSE WHEN MOVE BUBBLE_HEAD
                bubbleHeadListener?.hideBubbleClose(this@BubbleHead)
            }
            MotionEvent.ACTION_MOVE -> {
                if (AbsBubbleContainerLayout.distance(
                        initialTouchX,
                        event.rawX,
                        initialTouchY,
                        event.rawY
                    ) > AbsBubbleContainerLayout.CHAT_HEAD_DRAG_TOLERANCE.pow(2) && !moving
                ) {
                    moving = true
//                        if (this@BubbleHead == bubbleHeadListener?.activeBubbleHead()) {
//                            bubbleHeadListener?.hideContent()
//                        }

                    //TODO UPDATE HIDE CONTENT WHEN MOVE ALL BUBBLE_HEAD
                    bubbleHeadListener?.hideContentMove()
                }

                if (moving) {
//                        springX.currentValue = initialX + (event.rawX - initialTouchX).toDouble()
//                        springY.currentValue = initialY + (event.rawY - initialTouchY).toDouble()
                }

                //TODO UPDATE CLOSE WHEN MOVE BUBBLE_HEAD
                if (moving) {
                    if (bubbleHeadListener?.moveBubbleHead(event, this@BubbleHead) == true) {
                        springX.currentValue =
                            initialX + (event.rawX - initialTouchX).toDouble()
                        springY.currentValue =
                            initialY + (event.rawY - initialTouchY).toDouble()
                    } else {
//                            springX.currentValue = initialX + (event.rawX - initialTouchX).toDouble()
//                            springY.currentValue = initialY + (event.rawY - initialTouchY).toDouble()
                    }
                }
            }
        }
        true
    }

    private var bubbleHeadListener: BubbleHeadListener? = null

    fun updateBubbleHeadListener(listener: BubbleHeadListener) {
        this.bubbleHeadListener = listener
    }

    override fun initializeLayout() {
        initView()
//        viewModel?.updateRoomId(bubbleEntity?.roomId ?: "")

//        viewModel?.bubbleEntity?.observe(this, Observer {
//            //Update data khi có thay đổi lần đầu tiên
//            it?.takeIf { it.isActivated() }?.run {
//                requestUpdateBubbleHeadLayout(it)
//            } ?: it?.run {
//                // Remove Bubble Head
//                bubbleHeadListener?.removeBubble(bubbleEntity?.roomId ?: "")
//            }
//        })
        bubbleHeadListener?.observerActiveBubble(Observer {
//            it?.takeIf { TextUtils.equals(it.bubbleId(), bubbleId()) || (it.isBubbleHome() && isBubbleHome())}?.run {
            it?.takeIf { it == this }?.run {
                binding.container.setBackgroundResource(R.drawable.bg_bubble_active)
            } ?: kotlin.run {
                binding.container.background = null
            }
        })
        bubbleHeadListener?.observerToggled(Observer {
            binding.bubbleIconIv.visibility = if(it) View.GONE else View.VISIBLE
            binding.bubbleHeadBg.setBackgroundResource(if(it) R.drawable.bg_transparent_circle else R.drawable.bg_shadow_circle)
        })
    }

//    private fun requestUpdateBubbleHeadLayout(bubbleEntity: BubbleEntity?) {
//        bubbleEntity?.run {
//            notifications = bubbleEntity.newMessage ?: 0
//            updateBubbleAvatars(bubbleEntity.getAvatars())
//            binding.bubbleAvatar.visibility = View.VISIBLE
//            binding.bubbleHome.visibility = View.GONE
//        } ?: run {
//            //Bubble Homde
//            notifications = 0
//            binding.bubbleAvatar.visibility = View.GONE
//            binding.bubbleHome.visibility = View.VISIBLE
//        }
//    }

    private fun updateBubbleAvatars(avatars: MutableList<String>?) {
        val size = when (avatars?.size ?: 0) {
            0, 1 -> 0f
            else -> 6f
        }
        val margin = SizeUtils.dp2px(size)
        binding.bubbleAvatar.layoutParams?.run{
            (this as RelativeLayout.LayoutParams).setMargins(margin, margin, margin, margin)
            binding.bubbleAvatar.layoutParams = this
        }
        binding.bubbleAvatar.setRequestManager(Glide.with(context))
        binding.bubbleAvatar.setImageList(avatars ?: mutableListOf())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        //init params
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 0
        params.width = AbsBubbleContainerLayout.CHAT_HEAD_WIDTH_SIZE + 15
        params.height = AbsBubbleContainerLayout.CHAT_HEAD_HEIGHT_SIZE + 30

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

        this.setOnTouchListener(onTouchListener)
    }

    fun addToContainer(parent: ViewGroup) {
        parent.addView(this, params)
    }

    fun updateNotifications() {
        notifications++
    }

    fun updateIsOnRight(value: Boolean) {
        try {
            val params = (binding.bubbleNotifications.layoutParams as? RelativeLayout.LayoutParams)
            params?.removeRule(if (value) RelativeLayout.ALIGN_PARENT_RIGHT else RelativeLayout.ALIGN_PARENT_LEFT)
            params?.addRule(if (value) RelativeLayout.ALIGN_PARENT_LEFT else RelativeLayout.ALIGN_PARENT_RIGHT)
            binding.bubbleNotifications.layoutParams = params
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val params = (binding.bubbleIconIv.layoutParams as? RelativeLayout.LayoutParams)
            params?.removeRule(if (value) RelativeLayout.ALIGN_PARENT_RIGHT else RelativeLayout.ALIGN_PARENT_LEFT)
            params?.addRule(if (value) RelativeLayout.ALIGN_PARENT_LEFT else RelativeLayout.ALIGN_PARENT_RIGHT)
            binding.bubbleIconIv.layoutParams = params
        } catch (e: Exception) {
            e.printStackTrace()
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