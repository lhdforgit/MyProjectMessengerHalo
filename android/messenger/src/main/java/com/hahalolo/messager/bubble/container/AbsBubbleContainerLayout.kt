package com.hahalolo.messager.bubble.container

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PixelFormat
import android.text.TextUtils
import android.util.AttributeSet
import android.view.*
import android.widget.LinearLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringChain
import com.hahalolo.messager.bubble.*
import com.hahalolo.messager.bubble.base.AbsLifecycleView
import com.hahalolo.messager.bubble.close.BubbleClose
import com.hahalolo.messager.bubble.close.BubbleCloseListener
import com.hahalolo.messager.bubble.conversation.BubbleConversationLayout
import com.hahalolo.messager.bubble.head.BubbleHead
import com.hahalolo.messager.bubble.head.BubbleHeadListener
import com.hahalolo.messager.bubble.model.BubbleLine
import com.hahalolo.messager.bubble.model.BubbleRectangle
import com.hahalolo.messager.bubble.notify.BubbleMessage
import com.hahalolo.messenger.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow


abstract class AbsBubbleContainerLayout : AbsLifecycleView, View.OnTouchListener {
    companion object {
        val CHAT_HEAD_OUT_OF_SCREEN_X: Int = dpToPx(16f)
        val CHAT_HEAD_SIZE: Int = dpToPx(64f)
        val CHAT_HEAD_WIDTH_SIZE: Int = dpToPx(64f)
        val CHAT_HEAD_HEIGHT_SIZE: Int = dpToPx(64f)
        //WITH MARGIN
        val BUBBLE_HEAD_HEIGHT_SIZE: Int = dpToPx(72f)

        val CHAT_HEAD_PADDING: Int = dpToPx(6f)
        val CHAT_HEAD_EXPANDED_PADDING: Int = dpToPx(4f)
        val CHAT_HEAD_EXPANDED_MARGIN_TOP: Float = dpToPx(6f).toFloat()
        val CHAT_HEAD_EXPANDED_MARGIN_END: Float = dpToPx(12f).toFloat()

        val CLOSE_SIZE = dpToPx(64f)
        val CLOSE_CAPTURE_DISTANCE = dpToPx(100f)
        val CLOSE_CAPTURE_DISTANCE_THROWN = dpToPx(300f)
        val CLOSE_ADDITIONAL_SIZE = dpToPx(24f)
        const val CHAT_HEAD_DRAG_TOLERANCE: Float = 20f

        const val MAX_BUBBLE = 3

        val BUBBLE_HEAD_DEFAUL_LOCATION_Y: Float = getScreenSize().heightPixels/4f

        fun distance(x1: Float, x2: Float, y1: Float, y2: Float): Float {
            return ((x1 - x2).pow(2) + (y1 - y2).pow(2))
        }
    }

    constructor(context: Context) : super(context) {
        initAbsContainerLayout()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAbsContainerLayout()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAbsContainerLayout()
    }

    private var serviceCallback: BubbleServiceCallback? = null

    fun updateBubbleServiceListener(callback: BubbleServiceCallback) {
        serviceCallback = callback
        contentConversation.updateBubbleServiceCallback(callback)
    }

    var viewModel: BubbleContainerViewModel? = null

    private var wasMoving = false
    private var closeCaptured = false

    private var closeVelocityCaptured = false
    private var movingOutOfClose = false

    private var initialX = 0.0f
    private var initialY = BUBBLE_HEAD_DEFAUL_LOCATION_Y

    private var initialTouchX = 0.0f
    private var initialTouchY = 0.0f

    private var initialVelocityX = 0.0
    private var initialVelocityY = 0.0

    private var lastY = 0.0

    private var moving = false
    var toggled = MutableLiveData<Boolean>(false)

    private fun isToggled(): Boolean {
        return toggled.value ?: false
    }

    private var motionTrackerUpdated = false
    private var collapsing = false
    private var blockAnim = false

    private var horizontalSpringChain: SpringChain? = null
    private var verticalSpringChain: SpringChain? = null

    private var bubbleTime = MutableLiveData<HashMap<String, Long>>()

    fun observerBubbleCount(observer: Observer<HashMap<String, Long>>) {
        bubbleTime.observe(this, observer)
    }

    private fun clearBubbleTime() {
        bubbleTime.value = HashMap<String, Long>()
    }

    private fun pushBubbleTime(id: String?) {
        id?.takeIf { it.isNotEmpty() }?.run {
            val current = bubbleTime.value ?: HashMap<String, Long>()
            current[this] = System.currentTimeMillis()
            bubbleTime.value = current
        }
    }

    private fun popBubbleTime(id: String?) {
        id?.takeIf { it.isNotEmpty() }?.run {
            val current = bubbleTime.value ?: HashMap<String, Long>()
            current[this]?.takeIf { it > 0 }?.run {
                current.remove(id)
                bubbleTime.value = current
            }
        }
    }

    // Trả về bubble cuối cùng tác động đến, và ko phải là bubble đang đc hiển thị
    private fun lastBubbleTime(): String? {
        var id: String? = null
        var time: Long = 0
        bubbleTime.value?.takeIf { it.isNotEmpty() }?.forEach {
            it.takeIf {
                !TextUtils.equals(activeBubbleHead.value?.bubbleId(), it.key)
                        && (id == null || it.value < time)
            }?.run {
                id = it.key
                time = it.value
            }
        }
        return id
    }

    private var isOnRight = true
        set(value) {
            field = value
            bubbleHeads.forEach {
                it.updateIsOnRight(value)
            }
        }

    private var detectedOutOfBounds = false
    private var animatingChatHeadInExpandedView = false

    private var velocityTracker: VelocityTracker? = null
    var motionTracker = LinearLayout(context).apply {
        if (BubbleService.DEBUG) setBackgroundColor(Color.parseColor("#88FF0000"))
    }
    private var showContentRunnable: Runnable? = null

    private var topBubbleHead: BubbleHead? = null

    private var movedBubbleHead: BubbleHead? = null

    private var activeBubbleHead: MutableLiveData<BubbleHead> = MutableLiveData()

    private var bubbleHeads = ArrayList<BubbleHead>()

    private val bubbleHome: BubbleHead by lazy {
        BubbleHead(context).apply {
//            this.updateBubbleEntity(null)
            this.updateBubbleHeadListener(bubbleHeadListener)
            this.updateBubbleServiceCallback(serviceCallback)
            addToContainer(this@AbsBubbleContainerLayout)
            visibility = View.GONE
        }
    }

    private val bubbleMessage: BubbleMessage by lazy {
        BubbleMessage(context).apply {
            this.updateBubbleServiceCallback(serviceCallback)
            addToContainer(this@AbsBubbleContainerLayout)
            visibility = View.GONE
        }
    }

    private val bubbleHeadListener = object : BubbleHeadListener {
        override fun currentBubbleHead(v: View?): BubbleHead? {
            return if (v == bubbleHome) bubbleHome
            else bubbleHeads.find { it == v }
        }

        override fun activeBubbleHead(): BubbleHead? {
            return activeBubbleHead.value
        }

        override fun updateActiveBubbleHead(bubbleHead: BubbleHead) {
            activeBubbleHead.value = bubbleHead
            updateActiveContent()
        }

        override fun topBubbleHead(): BubbleHead? {
            return topBubbleHead
        }

        override fun bubbleHeadIndex(bubbleHead: BubbleHead): Int {
            if (bubbleHead.isBubbleHome()) return bubbleHeads.size
            return bubbleHeads.indexOf(bubbleHead)
        }

        override fun collapse() {
            this@AbsBubbleContainerLayout.collapse()
        }

        override fun showContentMove() {
            contentConversation.showContent(
                home = activeBubbleHead.value?.isBubbleHome() == true,
                move = true
            )
        }

        override fun hideContentMove() {
            contentConversation.hideContent(move = true)
        }

        override fun onChatHeadSpringUpdate(
            bubbleHead: BubbleHead,
            spring: Spring,
            totalVelocity: Int
        ) {
            val metrics = getScreenSize()

            topBubbleHead?.takeIf { it == bubbleHead }?.run {
                if (horizontalSpringChain != null && bubbleHead.isSpringX(spring)) {
                    horizontalSpringChain?.controlSpring?.currentValue = spring.currentValue
                }

                if (verticalSpringChain != null && bubbleHead.isSpringY(spring)) {
                    verticalSpringChain?.controlSpring?.currentValue = spring.currentValue
                }
            }

            // Moving content with active chat head
            var tmpChatHead: BubbleHead? = null
            if (collapsing) {
                tmpChatHead = topBubbleHead
            } else if (bubbleHead == activeBubbleHead.value) {
                tmpChatHead = bubbleHead
            }

            //TODO UPDATE DRAG WITH HOME BUBBLE
            if (tmpChatHead != null && !tmpChatHead.isBubbleHome()) {
                // Whether the content should follow chat head x
                val x =
                    if (animatingChatHeadInExpandedView) tmpChatHead.springXEndValue() else tmpChatHead.springXCurrentValue()

                contentConversation.x =
                    x - metrics.widthPixels.toFloat() + bubbleHeads.indexOf(tmpChatHead) * (tmpChatHead.paramsWidth() + CHAT_HEAD_EXPANDED_PADDING) + tmpChatHead.paramsWidth() + CHAT_HEAD_EXPANDED_MARGIN_END
                contentConversation.y =
                    tmpChatHead.springYCurrentValue() - CHAT_HEAD_EXPANDED_MARGIN_TOP

                contentConversation.pivotX =
                    metrics.widthPixels.toFloat() - CHAT_HEAD_EXPANDED_MARGIN_END / 2 - bubbleHead.width / 2 - bubbleHeads.indexOf(
                        tmpChatHead
                    ) * (tmpChatHead.paramsWidth() + CHAT_HEAD_EXPANDED_PADDING)
            }

            contentConversation.pivotY = bubbleHead.height.toFloat()

            if (topBubbleHead != null) {
                val width = dpToPx(100f)
                val height = dpToPx(50f)
                val r1 = BubbleRectangle(
                    bubbleClose.x.toDouble() - if (isOnRight) dpToPx(32f) else width,
                    bubbleClose.y.toDouble() - height / 2,
                    bubbleClose.width.toDouble() + width,
                    bubbleClose.height.toDouble() + height
                )

                val x = (topBubbleHead?.springXCurrentValue() ?: 0f) + (topBubbleHead?.paramsWidth()
                    ?: 0) / 2
                val y =
                    (topBubbleHead?.springYCurrentValue() ?: 0f) + (topBubbleHead?.paramsHeight()
                        ?: 0) / 2
                val l1 = BubbleLine(
                    x.toDouble(),
                    y.toDouble(),
                    initialVelocityX + x,
                    initialVelocityY + y
                )

                // When a chat head has been thrown towards close
                if (
                    !moving
                    && initialVelocityY > 5000.0
                    && l1.intersects(r1)
                    && bubbleClose.visibility == VISIBLE
                    && !closeVelocityCaptured
                ) {
                    //TODO Disable close in this case
//                    closeVelocityCaptured = true
//                    topBubbleHead?.updateSpringEndValue(
//                        x = (bubbleClose.springXEndValue() + bubbleClose.width / 2 - (topBubbleHead?.paramsWidth()
//                            ?: 0) / 2 + 2).toDouble(),
//                        y = (bubbleClose.springYEndValue() + bubbleClose.height / 2 - (topBubbleHead?.paramsHeight()
//                            ?: 0) / 2 + 2).toDouble()
//                    )
//                    bubbleClose.onEnlarge()
//                    postDelayed({
//                        onClose()
//                    }, 100)
                }
            }

            if (wasMoving) {
                lastY = bubbleHead.springYCurrentValue().toDouble()

                if (!detectedOutOfBounds && !closeCaptured && !closeVelocityCaptured) {
                    if (bubbleHead.springYCurrentValue() < 0) {
                        bubbleHead.updateSpringYEndValue(0.0)
                        detectedOutOfBounds = true
                    } else if (bubbleHead.springYCurrentValue() > metrics.heightPixels) {
                        bubbleHead.updateSpringYEndValue(metrics.heightPixels - CHAT_HEAD_SIZE.toDouble())
                        detectedOutOfBounds = true
                    }
                }

                if (!moving) {
                    if (bubbleHead.isSpringX(spring)) {
                        val xPosition = bubbleHead.springXCurrentValue()
                        if (xPosition + bubbleHead.width > metrics.widthPixels && bubbleHead.springXVelocity() > 0) {
                            val newPos =
                                metrics.widthPixels - bubbleHead.width + CHAT_HEAD_OUT_OF_SCREEN_X
                            bubbleHead.updateSpringXConfig(SpringConfigs.NOT_DRAGGING)
                            bubbleHead.updateSpringXEndValue(newPos.toDouble())
                            isOnRight = true
                        } else if (xPosition < 0 && bubbleHead.springXVelocity() < 0) {
                            bubbleHead.updateSpringXConfig(SpringConfigs.NOT_DRAGGING)
                            bubbleHead.updateSpringXEndValue(-CHAT_HEAD_OUT_OF_SCREEN_X.toDouble())
                            isOnRight = false
                        }
                    } else if (bubbleHead.isSpringY(spring)) {
                        val yPosition = bubbleHead.springYCurrentValue()
                        if (yPosition + bubbleHead.height > metrics.heightPixels && bubbleHead.springYVelocity() > 0) {
                            bubbleHead.updateSpringYConfig(SpringConfigs.NOT_DRAGGING)
                            bubbleHead.updateSpringYEndValue(
                                metrics.heightPixels - bubbleHead.height.toDouble() - dpToPx(
                                    25f
                                )
                            )
                        } else if (yPosition < 0 && bubbleHead.springYVelocity() < 0) {
                            bubbleHead.updateSpringYConfig(SpringConfigs.NOT_DRAGGING)
                            bubbleHead.updateSpringYEndValue(0.0)
                        }
                    }
                }
                updateMotionLocation(totalVelocity)
            }
        }

        override fun removeBubble(bubbleId: String) {
            this@AbsBubbleContainerLayout.removeBubbleId(bubbleId)
        }

        override fun observerActiveBubble(observer: Observer<BubbleHead>) {
            activeBubbleHead.observe(this@AbsBubbleContainerLayout, observer)
        }

        override fun observerToggled(observer: Observer<Boolean>) {
            toggled.observe(this@AbsBubbleContainerLayout, observer)
        }

        override fun showBubbleClose(head: BubbleHead) {
            movedBubbleHead = head
            bubbleClose.onShow()
        }

        override fun hideBubbleClose(head: BubbleHead) {
            bubbleClose.onHide()
            if (closeCaptured) {
                removeBubbleHead(head)
                closeCaptured = false
                movingOutOfClose = false
            }
            movedBubbleHead = null
        }

        override fun moveBubbleHead(event: MotionEvent, head: BubbleHead): Boolean {
            val metrics = getScreenSize()
            bubbleClose.updateSpringEndValue(
                x = (metrics.widthPixels / 2) + (((event.rawX + (head.width
                    ?: 0) / 2) / 7) - metrics.widthPixels / 2 / 7) - bubbleClose.width.toDouble() / 2,
                y = (metrics.heightPixels - CLOSE_SIZE) + max(
                    ((event.rawY + bubbleClose.height / 2) / 10) - metrics.heightPixels / 10,
                    -dpToPx(30f).toFloat()
                ) - dpToPx(60f).toDouble()
            )

            if (distance(
                    bubbleClose.springXEndValue() + bubbleClose.width / 2,
                    event.rawX,
                    bubbleClose.springYEndValue() + bubbleClose.height / 2, event.rawY
                ) < CLOSE_CAPTURE_DISTANCE.toDouble().pow(2)
            ) {
                head.updateSpringConfig(SpringConfigs.CAPTURING)
                bubbleClose.onEnlarge()
                closeCaptured = true
                return false
            } else if (closeCaptured) {
                head.updateSpringConfig(SpringConfigs.CAPTURING)
                bubbleClose.onResetScale()
                head.updateSpringEndValue(
                    x = initialX + (event.rawX - initialTouchX).toDouble(),
                    y = initialY + (event.rawY - initialTouchY).toDouble()
                )
                closeCaptured = false
                movingOutOfClose = true

                postDelayed({
                    movingOutOfClose = false
                }, 100)
                return false
            } else if (!movingOutOfClose) {
//                head?.updateSpringConfig(SpringConfigs.DRAGGING)
//                head?.updateSpringCurrentValue(
//                    x = initialX + (event.rawX - initialTouchX).toDouble(),
//                    y = initialY + (event.rawY - initialTouchY).toDouble()
//                )
//                velocityTracker?.computeCurrentVelocity(2000)
                return true
            } else {
                return false
            }
        }

        override fun showHomeConversation() {
            //TODO Show Bubble Home
            activeBubbleHead.value = bubbleHome
            contentConversation.navigateHome()
        }

        override fun canTouchToBubbleHead(event: MotionEvent, bubbleHead: BubbleHead): Boolean {
            if (touchingBubble == null && event.action == MotionEvent.ACTION_DOWN) {
                touchingBubble = bubbleHead
                return true
            } else if (touchingBubble == bubbleHead) {
                if (event.action == MotionEvent.ACTION_UP) touchingBubble = null
                return true
            } else {
                return false
            }
        }
    }

    private var touchingBubble: BubbleHead? = null

    private val bubbleCloseListener = object : BubbleCloseListener {
        override fun onPositionUpdate(valueX: Double, valueY: Double, width: Int, height: Int) {
            //Nếu movedBublle !=null , áp dụng lên movedbble
            movedBubbleHead?.takeIf { closeCaptured }?.run {
                this.updateSpringEndValue(
                    (valueX + width / 2 - this.paramsWidth() / 2 + 2),
                    (valueY + height / 2 - this.paramsHeight() / 2 + 2)
                )
            } ?: topBubbleHead?.takeIf { closeCaptured }?.run {
                this.updateSpringEndValue(
                    (valueX + width / 2 - this.paramsWidth() / 2 + 2),
                    (valueY + height / 2 - this.paramsHeight() / 2 + 2)
                )
            }
        }

        override fun onXSpringUpdate() {
        }

        override fun onYSpringUpdate(spring: Spring) {
            movedBubbleHead?.takeIf { closeCaptured && wasMoving }
                ?.updateSpringYCurrentValue(spring.currentValue) ?: kotlin.run {
                topBubbleHead?.takeIf { closeCaptured && wasMoving }
                    ?.updateSpringYCurrentValue(spring.currentValue)
            }
        }
    }

    private var bubbleClose = BubbleClose(context).apply {
        this.updateBubbleCloseListener(bubbleCloseListener)
        this.addToContainer(this@AbsBubbleContainerLayout)
    }

    protected lateinit var contentConversation: BubbleConversationLayout

    private var motionTrackerParams = WindowManager.LayoutParams(
        CHAT_HEAD_SIZE,
        CHAT_HEAD_SIZE + 16,
        getOverlayFlag(),
        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        PixelFormat.TRANSLUCENT
    )

    private var params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        getOverlayFlag(),
        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
        PixelFormat.TRANSLUCENT
    )

    private fun updateMotionLocation(totalVelocity: Int = 0) {
        val metrics = getScreenSize()
        motionTrackerParams.x = if (isOnRight) metrics.widthPixels - CHAT_HEAD_WIDTH_SIZE else 0
        if (Math.abs(totalVelocity) % 10 == 0 && !moving && topBubbleHead != null) {
            motionTrackerParams.y = topBubbleHead?.springYCurrentValue()?.toInt() ?: 0
            BubbleService.instance?.updateViewLayout(
                motionTracker,
                motionTrackerParams
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val metrics = getScreenSize()
        if (topBubbleHead == null) return true

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = topBubbleHead?.springXCurrentValue() ?: 0f
                initialY = topBubbleHead?.springYCurrentValue() ?: 0f
                initialTouchX = event.rawX
                initialTouchY = event.rawY

                wasMoving = false
                collapsing = false
                blockAnim = false
                detectedOutOfBounds = false
                closeVelocityCaptured = false

                bubbleClose.onShow()

                topBubbleHead?.scaleX = 0.92f
                topBubbleHead?.scaleY = 0.92f

                topBubbleHead?.updateSpringConfig(SpringConfigs.DRAGGING)
                topBubbleHead?.setAtRest()

                motionTrackerUpdated = false

                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain()
                } else {
                    velocityTracker?.clear()
                }

                velocityTracker?.addMovement(event)
            }
            MotionEvent.ACTION_UP -> {
                if (moving) wasMoving = true

                postDelayed({
                    bubbleClose.onHide()
                }, 100)

                if (closeCaptured) {
                    onClose()
                    return true
                }

                if (!moving) {
                    if (!isToggled()) {
                        //SHOW CONTENT THIS TOP BUBBLE
                        showBubbleContent(topBubbleHead)
                    }
                } else if (!isToggled()) {
                    moving = false

                    var xVelocity = velocityTracker?.xVelocity?.toDouble() ?: 0.0
                    val yVelocity = velocityTracker?.yVelocity?.toDouble() ?: 0.0
                    var maxVelocityX = 0.0

                    velocityTracker?.recycle()
                    velocityTracker = null

                    if (xVelocity < -3500) {
                        val newVelocity = ((-(topBubbleHead?.springXCurrentValue()
                            ?: 0f) - CHAT_HEAD_OUT_OF_SCREEN_X) * SpringConfigs.DRAGGING.friction)
                        maxVelocityX = newVelocity - 5000
                        if (xVelocity > maxVelocityX) xVelocity = newVelocity - 500
                    } else if (xVelocity > 3500) {
                        val newVelocity =
                            ((metrics.widthPixels - (topBubbleHead?.springXCurrentValue()
                                ?: 0f) - (topBubbleHead?.width
                                ?: 0) + CHAT_HEAD_OUT_OF_SCREEN_X) * SpringConfigs.DRAGGING.friction)
                        maxVelocityX = newVelocity + 5000
                        if (maxVelocityX > xVelocity) xVelocity = newVelocity + 500
                    } else if (yVelocity > 20 || yVelocity < -20) {
                        topBubbleHead?.updateSpringXConfig(SpringConfigs.NOT_DRAGGING)
                        if ((topBubbleHead?.x ?: 0f) >= metrics.widthPixels / 2) {
                            topBubbleHead?.updateSpringXEndValue(
                                metrics.widthPixels - (topBubbleHead?.width
                                    ?: 0) + CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                            )
                            isOnRight = true
                        } else {
                            topBubbleHead?.updateSpringXEndValue(-CHAT_HEAD_OUT_OF_SCREEN_X.toDouble())
                            isOnRight = false
                        }
                    } else {
                        topBubbleHead?.updateSpringConfig(SpringConfigs.NOT_DRAGGING)
                        if ((topBubbleHead?.x ?: 0f) >= metrics.widthPixels / 2) {
                            topBubbleHead?.updateSpringEndValue(
                                x = metrics.widthPixels - (topBubbleHead?.width
                                    ?: 0) + CHAT_HEAD_OUT_OF_SCREEN_X.toDouble(),
                                y = topBubbleHead?.y?.toDouble() ?: 0.0
                            )
                            isOnRight = true
                        } else {
                            topBubbleHead?.updateSpringEndValue(
                                x = -CHAT_HEAD_OUT_OF_SCREEN_X.toDouble(),
                                y = topBubbleHead?.y?.toDouble() ?: 0.0
                            )
                            isOnRight = false
                        }
                    }

                    xVelocity = if (xVelocity < 0) {
                        max(xVelocity - 1000.0, maxVelocityX)
                    } else {
                        min(xVelocity + 1000.0, maxVelocityX)
                    }

                    initialVelocityX = xVelocity
                    initialVelocityY = yVelocity

                    topBubbleHead?.updateSpringVelocity(xVelocity, yVelocity)
                }

                topBubbleHead?.scaleX = 1f
                topBubbleHead?.scaleY = 1f
            }
            MotionEvent.ACTION_MOVE -> {

                if (distance(
                        initialTouchX,
                        event.rawX,
                        initialTouchY,
                        event.rawY
                    ) > CHAT_HEAD_DRAG_TOLERANCE.pow(2)
                ) {
                    moving = true
                    //ẩn bubble message khi di chuyển bubble
                    bubbleMessage.hideBubbleMessage()
                }

                velocityTracker?.addMovement(event)

                if (moving) {
                    bubbleClose.updateSpringEndValue(
                        x = (metrics.widthPixels / 2) + (((event.rawX + (topBubbleHead?.width
                            ?: 0) / 2) / 7) - metrics.widthPixels / 2 / 7) - bubbleClose.width.toDouble() / 2,
                        y = (metrics.heightPixels - CLOSE_SIZE) + max(
                            ((event.rawY + bubbleClose.height / 2) / 10) - metrics.heightPixels / 10,
                            -dpToPx(30f).toFloat()
                        ) - dpToPx(60f).toDouble()
                    )


                    if (distance(
                            bubbleClose.springXEndValue() + bubbleClose.width / 2,
                            event.rawX,
                            bubbleClose.springYEndValue() + bubbleClose.height / 2, event.rawY
                        ) < CLOSE_CAPTURE_DISTANCE.toDouble().pow(2)
                    ) {
                        topBubbleHead?.updateSpringConfig(SpringConfigs.CAPTURING)

                        bubbleClose.onEnlarge()
                        closeCaptured = true

                    } else if (closeCaptured) {
                        topBubbleHead?.updateSpringConfig(SpringConfigs.CAPTURING)

                        bubbleClose.onResetScale()

                        topBubbleHead?.updateSpringEndValue(
                            x = initialX + (event.rawX - initialTouchX).toDouble(),
                            y = initialY + (event.rawY - initialTouchY).toDouble()
                        )

                        closeCaptured = false
                        movingOutOfClose = true

                        postDelayed({
                            movingOutOfClose = false
                        }, 100)

                    } else if (!movingOutOfClose) {
                        topBubbleHead?.updateSpringConfig(SpringConfigs.DRAGGING)

                        topBubbleHead?.updateSpringCurrentValue(
                            x = initialX + (event.rawX - initialTouchX).toDouble(),
                            y = initialY + (event.rawY - initialTouchY).toDouble()
                        )

                        velocityTracker?.computeCurrentVelocity(2000)
                    }
                }
            }
        }
        return true
    }

    fun showTopBubbleContent() {
        showBubbleContent(topBubbleHead)
    }

    private fun showBubbleContent(bubble: BubbleHead?) {
        //hide Bubble message before show bubble content
        bubbleMessage.hideBubbleMessage()
        bubble?.run {
            if (bubble != topBubbleHead) {
                topBubbleHead = bubble
            }
            toggled.value = true
            rearrangeExpanded()
            enableTouchTracker(false)
            enableTouchContainer(true)

            activeBubbleHead.value = topBubbleHead
            updateActiveContent()

            showContentRunnable?.let { handler.removeCallbacks(it) }

            showContentRunnable = Runnable {
                contentConversation.showContent()
            }

            handler.postDelayed(showContentRunnable!!, 200)
        }
    }

    // show Layout content of Bubble have @{bubbleId}, for send data
    protected fun showBubbleContent(bubbleId: String): Boolean {
        //Nếu bubbleId là TopBubbleHead, hiển thị
        if (TextUtils.equals(topBubbleHead?.bubbleId(), bubbleId)) {
            toggled.value = true
            blockAnim = false
            collapsing = false
            showBubbleContent(topBubbleHead)
            return true
        } else {
            //Nếu tìm BubbleHead và set là TopBubbleHead, hiển thị
            bubbleHeads.find { TextUtils.equals(it.bubbleId(), bubbleId) }?.run {
                //Update lại trạng thái , show show Bubble Head
                toggled.value = true
                blockAnim = false
                collapsing = false
                //Add this bubble to Top
                updateTopBubbleHeader(this)
                showBubbleContent(topBubbleHead)
                return true
            } ?: run {
                // Bubble Head đã bị removed
                return false
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (!contentConversation.onBackPress()) {
                collapse()
            }
            true
        } else super.dispatchKeyEvent(event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return super.onKeyDown(keyCode, event)
    }

    private fun updateTopBubbleHeader(bubbleHead: BubbleHead?) {
        destroySpringChains()

        if (bubbleHead == null) {
            topBubbleHead = null
            return
        }
        val index = bubbleHeads.indexOf(bubbleHead)

        bubbleHeads.takeIf { it.isNotEmpty() && it.size > index && index >= 0 }?.run {
            this.removeAt(index)
            this.add(0, bubbleHead)
            topBubbleHead = bubbleHead
            resetSpringChains()
        }
    }

    private fun destroySpringChains() {
        if (horizontalSpringChain != null) {
            horizontalSpringChain?.allSprings?.forEach {
                it.destroy()
            }
        }

        if (verticalSpringChain != null) {
            verticalSpringChain?.allSprings?.forEach {
                it.destroy()
            }
        }

        verticalSpringChain = null
        horizontalSpringChain = null
    }

    private fun resetSpringChains() {
        destroySpringChains()

        horizontalSpringChain = SpringChain.create(0, 0, 200, 15)
        verticalSpringChain = SpringChain.create(0, 0, 200, 15)

        bubbleHeads.forEachIndexed { index, element ->
            element.z = (bubbleHeads.size - 1 - index).toFloat()

            if (index == 0) {
                horizontalSpringChain?.addSpring(object : SimpleSpringListener() {})
                verticalSpringChain?.addSpring(object : SimpleSpringListener() {})

                horizontalSpringChain?.setControlSpringIndex(index)
                verticalSpringChain?.setControlSpringIndex(index)
            } else {
                horizontalSpringChain?.addSpring(object : SimpleSpringListener() {
                    override fun onSpringUpdate(spring: Spring?) {
                        if (!isToggled() && !blockAnim) {
                            if (collapsing) {
                                element.updateSpringXEndValue(
                                    spring?.endValue ?: 0.0
                                    + index * CHAT_HEAD_PADDING * if (isOnRight) 1 else -1
                                )
                            } else {
                                element.updateSpringXCurrentValue(
                                    spring?.currentValue ?: 0.0
                                    + index * CHAT_HEAD_PADDING * if (isOnRight) 1 else -1
                                )
                            }
                        }

                        if (spring?.currentValue == spring?.endValue) {
                            animatingChatHeadInExpandedView = false
                        }
                    }
                })
                verticalSpringChain?.addSpring(object : SimpleSpringListener() {
                    override fun onSpringUpdate(spring: Spring?) {
                        if (!isToggled() && !blockAnim) {
                            element.updateSpringYCurrentValue(spring?.currentValue ?: 0.0)
                        }
                    }
                })
            }
        }
    }

    private fun removeBubbleId(bubbleId: String) {
        bubbleHeads.find { TextUtils.equals(it.bubbleId(), bubbleId) }?.run {
            removeBubbleHead(this)
        }
    }

    fun addBubbleHead(bubbleEntity: Any): BubbleHead? {
        var newChatHead = false

        // Tìm BubbleHead đã có , hoặc tạo mới nếu chưa có
        val bubbleHead: BubbleHead =
            bubbleHeads.find {
//                TextUtils.equals(it.bubbleId(), bubbleEntity.roomId)
                false
            } ?: run {
                //Trường Hợp Tạo mới Bubble
                // Số Lượng BubbleHead đã đạt tối đa số lượng cho phép
                bubbleHeads.takeIf { it.size >= MAX_BUBBLE }
                    ?.find { TextUtils.equals(it.bubbleId(), lastBubbleTime()) }
                    ?.run {
                        // Lấy BubbleHead đc tác động cũ nhất, và update lại dữ liệu BubbleHead này
                        // Remove Bubble Time
                        popBubbleTime(this.bubbleId())
                        this.apply {
//                            this.updateBubbleEntity(bubbleEntity)
                        }
                    } ?: run {
                    // Khởi Tạo 1 BubbleHead mới
                    bubbleHeads.forEach {
                        it.visibility = VISIBLE
                    }
                    newChatHead = true
                    BubbleHead(context).apply {
//                        this.updateBubbleEntity(bubbleEntity)
                        this.updateBubbleHeadListener(bubbleHeadListener)
                        this.updateBubbleServiceCallback(serviceCallback)
                        bubbleHeads.add(0, this)
                        addToContainer(this@AbsBubbleContainerLayout)
                    }
                }
            }

        //Update Time last
        pushBubbleTime(bubbleHead.bubbleId() ?: "")

        val metrics = getScreenSize()
        var lx = metrics.widthPixels - BUBBLE_HEAD_HEIGHT_SIZE + CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
        var ly = initialY.toDouble()

        topBubbleHead?.run {
            lx = this.springXCurrentValue().toDouble()
            ly = this.springYCurrentValue().toDouble()
        }

        updateTopBubbleHeader(bubbleHead)

        if (!isToggled()) {
            if (!moving) blockAnim = true
            bubbleHeads.forEachIndexed { index, element ->
                element.updateSpringCurrentValue(
                    lx + index * CHAT_HEAD_PADDING * if (isOnRight) 1 else -1,
                    ly
                )
            }
//            motionTrackerParams.x = bubbleHead.springXCurrentValue().toInt()
//            motionTrackerParams.y = bubbleHead.springYCurrentValue().toInt()
            updateMotionLocation()
            enableTouchTracker(true)
        } else {
            if (newChatHead) {
                bubbleHead.updateSpringCurrentValue(
                    x = metrics.widthPixels.toDouble(),
                    y = ly
                )
                bubbleHead.updateSpringXEndValue(lx)
            }

            animatingChatHeadInExpandedView = true
            rearrangeExpanded(true)
        }

        // Xóa đi bubble cũ nhất , nếu vượt quá số lượng cho phép
        lastBubbleTime()?.takeIf { it.isNotEmpty() && bubbleHeads.size > MAX_BUBBLE }?.run {
            removeBubbleId(this)
        }

        return bubbleHead
    }

    fun pushMessageWithBubbleHead(currentHead: BubbleHead?, pushMsg: String) {
        topBubbleHead?.takeIf {
            (TextUtils.equals(it.bubbleId(), currentHead?.bubbleId())
                    && toggled.value == false) && !moving
        }?.run {
            bubbleMessage.apply {
                val onTop =
                    topBubbleHead!!.springYCurrentValue() < this@AbsBubbleContainerLayout.height / 4
                showMessage(pushMsg, onTop, isOnRight, topBubbleHead!!)
            }
        }
    }

    private fun collapseBubble(){
        val metrics = getScreenSize()
        topBubbleHead?.run {
            val newX =
                if (isOnRight) metrics.widthPixels - this.width + CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
                else -CHAT_HEAD_OUT_OF_SCREEN_X.toDouble()
            if (initialY> metrics.heightPixels - BUBBLE_HEAD_HEIGHT_SIZE){
                // initialY không > maxHeight
                initialY = (metrics.heightPixels - BUBBLE_HEAD_HEIGHT_SIZE).toFloat()
            }
            val newY = initialY.toDouble()
            this.updateSpringEndValue(newX, newY)
        }
    }

    fun collapse() {
        // set flag
        toggled.value = false
        collapsing = true

        collapseBubble()
        activeBubbleHead.value = null
        bubbleHome.apply { visibility = View.GONE }

        contentConversation.hideContent()

        enableTouchTracker(true)
        enableTouchContainer(false)
    }

    fun updateActiveContent() {
        activeBubbleHead.value?.run {
            contentConversation.navigateMessage(this)
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        motionTracker.visibility = visibility
    }

    private fun enableTouchTracker(enable: Boolean) {
        if (enable && topBubbleHead != null) {
            motionTrackerParams.flags =
                motionTrackerParams.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
        } else {
            motionTrackerParams.flags =
                motionTrackerParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }
        BubbleService.instance?.updateViewLayout(motionTracker, motionTrackerParams)
    }

    private fun enableTouchContainer(enable: Boolean){
        if (enable){
            params.flags =
                (params.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()) or
                        WindowManager.LayoutParams.FLAG_DIM_BEHIND and
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL.inv() and
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
            BubbleService.instance?.updateViewLayout(
                this@AbsBubbleContainerLayout,
                params
            )
        }else{
            params.flags = (params.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE) and
                    WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv() and
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL.inv() or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE

            BubbleService.instance?.updateViewLayout(this, params)
        }
    }

    fun onClose() {
        removeAll()
        closeCaptured = false
        movingOutOfClose = false
        enableTouchTracker(false)
    }

    private fun removeAll() {
        updateTopBubbleHeader(null)
        bubbleHeads.forEach {
            this.removeView(it)
        }
        clearBubbleTime()
        bubbleHeads.clear()
    }

    private fun removeBubbleHead(head: BubbleHead) {

        // xóa bubble khỏi danh sách đang hiển thị
        if (head.isBubbleHome()) {
            //Nếu xóa Bubble Home, -> onClose()
            onClose()
            activeBubbleHead.value = null
            this@AbsBubbleContainerLayout.collapse()
            return
        }

        val isActive = TextUtils.equals(activeBubbleHead.value?.bubbleId(), head.bubbleId())
        val isTop = TextUtils.equals(topBubbleHead?.bubbleId(), head.bubbleId())

        this.removeView(head)
        popBubbleTime(head.bubbleId() ?: "")

        bubbleHeads.remove(head)
        val isEmpty = bubbleHeads.isEmpty()
        if (isEmpty) {
            //Nếu list bubble rỗng , thì close
            updateTopBubbleHeader(null)
            activeBubbleHead.value = null
            this.onClose()
            this@AbsBubbleContainerLayout.collapse()
        } else {
            //Nếu còn những bubble khác
            // Nếu xóa Bubble bị xóa là BubbleTop, thì update BubbleTop mới
            if (isTop) updateTopBubbleHeader(activeBubbleHead.value?.takeIf { !isActive && !it.isBubbleHome() }
                ?: bubbleHeads.firstOrNull())
            // Nếu Bubble bị xóa là BubbleActive, callapse layout
            if (isActive) {
                //nếu xóa bubble đang đc hiển thị, thì collapse
                activeBubbleHead.value = null
                this@AbsBubbleContainerLayout.collapse()
            }
            bubbleHeads.takeIf { it.isNotEmpty() && isToggled() }?.run {
                rearrangeExpanded(true)
            }
        }
    }

    private fun rearrangeExpanded(animation: Boolean = true) {
        topBubbleHead?.run {
            bubbleHome.takeIf { it.visibility == View.GONE }?.updateSpringCurrentValue(
                this.springXCurrentValue().toDouble(),
                this.springYCurrentValue().toDouble()
            )
        }

        bubbleHeads.forEachIndexed { index, it ->
            it.updateSpringConfig(SpringConfigs.NOT_DRAGGING)

            it.rearrangeExpanded(
                position = index,
                widthTop = (topBubbleHead?.paramsWidth()?.toDouble() ?: 0.0),
                animation = animation
            )

        }
        bubbleHome.run {
            visibility = View.VISIBLE
            this.updateSpringConfig(SpringConfigs.NOT_DRAGGING)

            this.rearrangeExpanded(
                position = bubbleHeads.size,
                widthTop = (topBubbleHead?.paramsWidth()?.toDouble() ?: 0.0),
                animation = animation
            )
        }
    }

    fun removeShowContentRunnable() {
        showContentRunnable?.let {
            handler.removeCallbacks(it)
        }
    }

    override fun initializeViewModel() {
        viewModel =
            serviceCallback?.createViewModel(BubbleContainerViewModel::class) as? BubbleContainerViewModel
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initAbsContainerLayout() {
        params.gravity = Gravity.START or Gravity.TOP
        params.dimAmount = 0.7f
        contentConversation =
            BubbleConversationLayout(ContextThemeWrapper(context, R.style.MessengerTheme))
        motionTrackerParams.gravity = Gravity.START or Gravity.TOP
        BubbleService.instance?.addView(motionTracker, motionTrackerParams)
        BubbleService.instance?.addView(this, params)

        this.addView(contentConversation)

        isFocusableInTouchMode = true

        motionTracker.setOnTouchListener(this)

        setOnTouchListener { v, event ->
            v.performClick()
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    if (v == this) {
                        collapse()
                    }
                }
            }
            return@setOnTouchListener false
        }
    }

    override fun invalidateLayout() {
        super.invalidateLayout()
        contentConversation.invalidateLayout()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //khi Thiet bi xoay man hinh, call collapse
        collapse()
        lifecycleScope.launch {
            delay(500)
            updateMotionLocation()
        }
    }
}