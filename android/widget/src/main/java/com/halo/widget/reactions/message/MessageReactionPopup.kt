/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.reactions.message

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.*
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.view.GestureDetectorCompat
import androidx.databinding.DataBindingUtil
import com.halo.common.utils.UtilsVibrator
import com.halo.widget.R
import com.halo.widget.databinding.MessageReactionLayoutBinding
import com.halo.widget.reactions.MessageMenuListener
import com.halo.widget.reactions.ReactionClickListener
import com.halo.widget.reactions.ReactionSelectedListener
import com.halo.widget.reactions.ReactionsConfig

class MessageReactionPopup @JvmOverloads constructor(
    var context: Context,
    var reactionsConfig: ReactionsConfig,
    var reactionSelectedListener: ReactionSelectedListener? = null,
    var reactionClickListener: ReactionClickListener? = null,
    var messageMenuListener: MessageMenuListener? = null
) : PopupWindow(context),
    View.OnTouchListener,
    GestureDetector.OnGestureListener {

    fun updateListener(
        target: View?,
        reactionSelectedListener: ReactionSelectedListener?,
        reactionClickListener: ReactionClickListener?,
        messageMenuListener: MessageMenuListener?
    ) {
        target?.let {
            reactionsConfig.reactionButton = target
            this.reactionClickListener = reactionClickListener
            this.reactionSelectedListener = reactionSelectedListener
            this.messageMenuListener = messageMenuListener
            setPopupWithTarget(target)
        }
    }

    private val rootView = FrameLayout(context).also {
        it.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        DataBindingUtil.inflate<MessageReactionLayoutBinding>(
            LayoutInflater.from(context),
            R.layout.message_reaction_layout,
            null, false
        ).apply {
            rootView.removeAllViews()
            rootView.addView(
                this.root,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    private var popupView: MessageReactionViewGroup? =null
    private fun getPopupView(): MessageReactionViewGroup{
        if (popupView==null){
            popupView =  MessageReactionViewGroup(context, reactionsConfig).also {
                it.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER
                )
                it.reactionSelectedListener = reactionSelectedListener
                it.messageMenuListener = messageMenuListener
                binding.layoutContainer.addView(it)
            }.also { it.dismissListener = ::dismiss }
        }
        return popupView!!
    }


    private fun showPopup(event: MotionEvent, parent: View) {
        initView()
        bindAction()
        getPopupView().reactionSelectedListener = reactionSelectedListener
        getPopupView().messageMenuListener = messageMenuListener
        getPopupView().show(event, parent)
        MessageReactionUtils.show(this)
    }

    private val mDetector = GestureDetectorCompat(context, this)
    lateinit var v: View
    private val onSingleTapUpCallback: Boolean = reactionsConfig.onSingleTapUpCallback

    init {
        contentView = rootView
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }

    private fun initView() {
        binding?.btnQuote?.visibility =
            if (messageMenuListener?.enableQuoteBtn() == true) View.VISIBLE else View.GONE
        binding?.btnCoppy?.visibility =
            if (messageMenuListener?.enableCopy() == true) View.VISIBLE else View.GONE
        binding?.btnEdit?.visibility =
            if (messageMenuListener?.enableEdit() == true) View.VISIBLE else View.GONE
        binding?.btnRevoke?.visibility =
            if (messageMenuListener?.enableRevoke() == true) View.VISIBLE else View.GONE
        binding?.btnHide?.visibility =
            if (messageMenuListener?.enableDelete() == true) View.VISIBLE else View.GONE
        binding?.btnDownload?.visibility =
            if (messageMenuListener?.enableDownload() == true) View.VISIBLE else View.GONE
        binding?.btnForward?.visibility =
            if(messageMenuListener?.enableForward() == true) View.VISIBLE else View.GONE


        binding?.layoutAction?.visibility =
            if (messageMenuListener?.enableCopy() == true
                || messageMenuListener?.enableEdit() == true
                || messageMenuListener?.enableRevoke() == true
                || messageMenuListener?.enableDelete() == true
                || messageMenuListener?.enableDownload() == true
                || messageMenuListener?.enableForward() == true
            ) View.VISIBLE else View.GONE
    }

    private fun bindAction() {
        binding.btnCoppy.setOnClickListener {
            messageMenuListener?.onCopyMessage()
            dismiss()
        }
        binding.btnEdit.setOnClickListener {
            messageMenuListener?.onEditMessage()
            dismiss()
        }
        binding.btnRevoke.setOnClickListener {
            messageMenuListener?.onRevokeMessage()
            dismiss()
        }
        binding.btnHide.setOnClickListener {
            messageMenuListener?.onDeleteMessage()
            dismiss()
        }
        binding.btnDownload.setOnClickListener {
            messageMenuListener?.onDownloadMedia()
            dismiss()
        }
        binding.btnQuote.setOnClickListener {
            messageMenuListener?.onQuoteMessage()
            dismiss()
        }
        binding.btnForward.setOnClickListener {
            messageMenuListener?.onForwardMessage()
            dismiss()
        }
    }

    /**
     * Nếu được set long click = false, khi người dùng click vào button
     * thì hiển thị lên Reaction View
     */
    override fun onDown(event: MotionEvent): Boolean {
        /*if (!onSingleTapUpCallback) {
            if (!isShowing) {
                UtilsVibrator.startVibratorSmall(context)
                // Show fullscreen with button as context provider
                showAtLocation(v, Gravity.NO_GRAVITY, 0, 0)
                showPopup(event, v)
                return false
            }
        }*/
        return false
    }

    override fun onFling(
        event1: MotionEvent, event2: MotionEvent,
        velocityX: Float, velocityY: Float
    ): Boolean {
        return false
    }

    /**
     * Khi người dùng thực hiện hành động long click
     * Hiển thị lên reaction
     */
    override fun onLongPress(event: MotionEvent) {
        /*if (onSingleTapUpCallback) {
            if (!isShowing) {
                UtilsVibrator.startVibratorSmall(context)
                // Show fullscreen with button as context provider
                showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0, 0)
                showPopup(event, v)
            }
        }*/
//        longPressd = true
        if (!isShowing) {
            UtilsVibrator.startVibratorSmall(context)
            // Show fullscreen with button as context provider
            showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0, 0)
            showPopup(event, v)
        }
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        messageMenuListener?.onShowPopup()
        super.showAtLocation(parent, gravity, x, y)
    }

    override fun onScroll(
        event1: MotionEvent, event2: MotionEvent, distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onShowPress(event: MotionEvent) {
    }

    /**
     * Thực hiện hành động khi người dùng click vào View.
     * Không hiển thị lên Reaction mà trả về sự kiện click
     */
    override fun onSingleTapUp(event: MotionEvent): Boolean {
        return if (onSingleTapUpCallback) {
            reactionClickListener?.invoke() ?: false
        } else {
            if (!isShowing) {
                UtilsVibrator.startVibratorSmall(context)
                // Show fullscreen with button as context provider
                showAtLocation(v, Gravity.NO_GRAVITY, 0, 0)
                showPopup(event, v)
            }
            false
        }
    }

    var showed = false

    fun setPopupWithTarget(target: View) {
        this.v = target
        target.setOnLongClickListener {
            if (!isShowing) {
                UtilsVibrator.startVibratorSmall(context)
                // Show fullscreen with button as context provider
                showAtLocation(v, Gravity.CENTER_HORIZONTAL, 0, 0)
                event?.run {
                    showed = true
                    showPopup(this, v)
                }
            }
            true
        }
        target.setOnTouchListener(this)
    }

    var event: MotionEvent? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View, event: MotionEvent): Boolean {
        this.v = v
        this.event = event
        if (showed && (event.action == MotionEvent.ACTION_MOVE
                    || event.action == MotionEvent.ACTION_UP)
        ) {
            return getPopupView().onTouchEvent(event)
        } else if (event.action == MotionEvent.ACTION_CANCEL) {
            dismiss()
        }
        return v.onTouchEvent(event)
    }

    override fun dismiss() {
        showed = false
        popupView?.dismiss()
        super.dismiss()
    }

    private val CLICK_ACTION_THRESHOLD = 200
    private val startX = 0f
    private val startY = 0f

    private fun isAClick(startX: Float, endX: Float, startY: Float, endY: Float): Boolean {
        val differenceX = Math.abs(startX - endX)
        val differenceY = Math.abs(startY - endY)
        return !(differenceX > CLICK_ACTION_THRESHOLD /* =5 */ || differenceY > CLICK_ACTION_THRESHOLD)
    }


}