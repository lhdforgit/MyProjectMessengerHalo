/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.badge

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.halo.widget.R

/**
 *
 * textSize	dimension	Text size.
 * textColor	color	Text color.
 * nbBackground	reference	Badge background image.
 * nbAnimationEnabled	boolean	true to enable animation, falseto disable it. The default is true.
 * nbAnimationDuration	integer	Duration of the animation in milliseconds. The default is 500 milliseconds.
 * nbMaxTextLength	integer	Max text length allowed to show on the badge. The default is 2.
 * nbEllipsizeText	string	Special text to show when the length of the original text reaches nbMaxTextLength. The default is ....
 */
class NotificationBadge(
        context: Context,
        attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    var animationEnabled: Boolean =
        DEFAULT_ANIMATION_ENABLED
    var animationDuration: Int =
        DEFAULT_ANIMATION_DURATION
    var maxTextLength: Int =
        DEFAULT_MAX_TEXT_LENGTH
    var ellipsizeText: String =
        DEFAULT_ELLIPSIZE_TEXT

    var badgeContainer: FrameLayout? = null
    var badgeTextView: TextView? = null
    var badgeImg: ImageView? = null

    private var isVisible: Boolean
        get() = badgeContainer?.visibility == VISIBLE
        set(value) {
            badgeContainer?.visibility = if (value) View.VISIBLE else INVISIBLE
        }

    private val update: Animation by lazy {
        ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = animationDuration.toLong()
            repeatMode = Animation.REVERSE
            repeatCount = 1
        }
    }

    private val show: Animation by lazy {
        ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = animationDuration.toLong()
        }
    }

    private val hide: Animation by lazy {
        ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = animationDuration.toLong()
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    isVisible = false
                }

                override fun onAnimationStart(animation: Animation?) {}
            })
        }
    }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.notification_badge, this, true)

        badgeContainer = view.findViewById(R.id.fl_container)
        badgeTextView = view.findViewById(R.id.tv_badge_text)
        badgeImg = view.findViewById(R.id.iv_badge_bg)

        val a = context.theme.obtainStyledAttributes(attrs,
            R.styleable.NotificationBadge, 0, 0)
        try {
            val textColor = a.getColor(
                R.styleable.NotificationBadge_android_textColor,
                    DEFAULT_TEXT_COLOR.toInt())
            badgeTextView?.setTextColor(textColor)

            val textSize = a.getDimension(
                R.styleable.NotificationBadge_android_textSize,
                    dpToPx(DEFAULT_TEXT_SIZE))
            badgeTextView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)

            val textStyle = a.getInt(R.styleable.NotificationBadge_android_textStyle, 0)
            val typeface = badgeTextView?.typeface?: Typeface.DEFAULT
            when(textStyle){
                1-> badgeTextView?.setTypeface(typeface, Typeface.BOLD)
                2-> badgeTextView?.setTypeface(typeface, Typeface.ITALIC)
                else -> badgeTextView?.setTypeface(typeface, Typeface.NORMAL)
            }

            animationEnabled = a.getBoolean(
                R.styleable.NotificationBadge_nbAnimationEnabled,
                DEFAULT_ANIMATION_ENABLED
            )
            animationDuration = a.getInt(
                R.styleable.NotificationBadge_nbAnimationDuration,
                DEFAULT_ANIMATION_DURATION
            )

            a.getDrawable(R.styleable.NotificationBadge_nbBackground)?.let {
                badgeImg?.setImageDrawable(it)
            }

            maxTextLength = a.getInt(
                R.styleable.NotificationBadge_nbMaxTextLength,
                DEFAULT_MAX_TEXT_LENGTH
            )
            a.getString(R.styleable.NotificationBadge_nbEllipsizeText)?.let {
                ellipsizeText = it
            }
        } finally {
            a.recycle()
        }
    }

    @JvmOverloads
    fun setText(text: String?, animation: Boolean = animationEnabled) {
        val badgeText = when {
            text == null -> ""
            text.length > maxTextLength -> ellipsizeText
            else -> text
        }
        if (badgeText.isEmpty()) {
            clear(animation)
        } else if (animation) {
            if (isVisible) {
                badgeContainer?.startAnimation(update)
            } else {
                badgeContainer?.startAnimation(show)
            }
        }
        badgeTextView?.text = badgeText
        isVisible = true
    }

    @JvmOverloads
    fun setNumber(number: Int, animation: Boolean = animationEnabled) {
        if (number == 0) {
            clear(animation)
        } else {
            setText(number.toString(), animation)
        }
    }

    @JvmOverloads
    fun clear(animation: Boolean = animationEnabled) {
        if (!isVisible) return

        if (animation) {
            badgeContainer?.startAnimation(hide)
        } else {
            isVisible = false
        }
    }

    private fun dpToPx(dp: Int): Float {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics)
    }

    companion object {
        private const val DEFAULT_TEXT_COLOR = 0xFFFFFFFF
        private const val DEFAULT_TEXT_SIZE = 10
        private const val DEFAULT_ANIMATION_ENABLED = true
        private const val DEFAULT_ANIMATION_DURATION = 250
        private const val DEFAULT_MAX_TEXT_LENGTH = 3
        private const val DEFAULT_ELLIPSIZE_TEXT = "..."
    }
}