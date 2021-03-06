/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.ellipsize

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.halo.widget.R


class ExpainableEllipsizeTextView : AppCompatTextView {

    private val TRIM_MODE_LINES = 0
    private val TRIM_MODE_LENGTH = 1
    private val DEFAULT_TRIM_LENGTH = 240
    private val DEFAULT_TRIM_LINES = 10
    private val INVALID_END_INDEX = -1
    private val DEFAULT_SHOW_TRIM_EXPANDED_TEXT = true
    private val ELLIPSIZE = "... "

    private var sequence: CharSequence? = text
    private var bufferType: TextView.BufferType? = null
    private var readMore = true
    private var trimLength: Int = 0
    private var trimCollapsedText: CharSequence? = null
    private var trimExpandedText: CharSequence? = null
    private var viewMoreSpan: ReadMoreClickableSpan
    private var colorClickableText: Int = 0
    private val showTrimExpandedText: Boolean

    private var trimMode: Int = 0
    private var lineEndIndex: Int = 0
    private var trimLines: Int = 0

    constructor(context: Context) : this(context, null) {

    }

    @SuppressLint("CustomViewStyleable")
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ReadMoreTextView)
        this.trimLength = typedArray.getInt(R.styleable.ReadMoreTextView_trimLength, DEFAULT_TRIM_LENGTH)
        val resourceIdTrimCollapsedText = typedArray.getResourceId(R.styleable.ReadMoreTextView_trimCollapsedText, R.string.read_more)
        val resourceIdTrimExpandedText = typedArray.getResourceId(R.styleable.ReadMoreTextView_trimExpandedText, R.string.common_read_less)
        this.trimCollapsedText = resources.getString(resourceIdTrimCollapsedText)
        this.trimExpandedText = resources.getString(resourceIdTrimExpandedText)
        this.trimLines = typedArray.getInt(R.styleable.ReadMoreTextView_trimLines, DEFAULT_TRIM_LINES)
        this.colorClickableText = typedArray.getColor(R.styleable.ReadMoreTextView_colorClickableText,
                ContextCompat.getColor(context, R.color.text_focus))
        this.showTrimExpandedText = typedArray.getBoolean(R.styleable.ReadMoreTextView_showTrimExpandedText, DEFAULT_SHOW_TRIM_EXPANDED_TEXT)
        this.trimMode = typedArray.getInt(R.styleable.ReadMoreTextView_trimMode, TRIM_MODE_LINES)
        typedArray.recycle()
        viewMoreSpan = ReadMoreClickableSpan()
        onGlobalLayoutLineEndIndex()
        setText()
    }

    private fun setText() {
        super.setText(getDisplayableText(), bufferType)
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
    }

    private fun getDisplayableText(): CharSequence? {
        return getTrimmedText(sequence)
    }

    override fun setText(text: CharSequence, type: TextView.BufferType) {
        this.sequence = text
        bufferType = type
        setText()
    }

    private fun getTrimmedText(text: CharSequence?): CharSequence? {
        if (trimMode == TRIM_MODE_LENGTH) {
            if (text != null && text.length > trimLength) {
                return if (readMore) {
                    updateCollapsedText()
                } else {
                    updateExpandedText()
                }
            }
        }
        if (trimMode == TRIM_MODE_LINES) {
            if (text != null && lineEndIndex > 0) {
                if (readMore) {
                    if (layout.lineCount > trimLines) {
                        return updateCollapsedText()
                    }
                } else {
                    return updateExpandedText()
                }
            }
        }
        return text
    }

    private fun updateCollapsedText(): CharSequence {
        var trimEndIndex = sequence!!.length
        when (trimMode) {
            TRIM_MODE_LINES -> {
                trimEndIndex = lineEndIndex - (ELLIPSIZE.length + trimCollapsedText!!.length + 1)
                if (trimEndIndex < 0) {
                    trimEndIndex = trimLength + 1
                }
            }
            TRIM_MODE_LENGTH -> trimEndIndex = trimLength + 1
        }
        val s = SpannableStringBuilder(sequence, 0, trimEndIndex)
                .append(ELLIPSIZE)
                .append(trimCollapsedText)
        return addClickableSpan(s, trimCollapsedText!!)
    }

    private fun updateExpandedText(): CharSequence? {
        if (showTrimExpandedText) {
            val s = SpannableStringBuilder(sequence, 0, sequence!!.length).append(trimExpandedText)
            return addClickableSpan(s, trimExpandedText!!)
        }
        return sequence
    }

    private fun addClickableSpan(s: SpannableStringBuilder, trimText: CharSequence): CharSequence {
        s.setSpan(viewMoreSpan, s.length - trimText.length, s.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return s
    }

    fun setTrimLength(trimLength: Int) {
        this.trimLength = trimLength
        setText()
    }

    fun setColorClickableText(colorClickableText: Int) {
        this.colorClickableText = colorClickableText
    }

    fun setTrimCollapsedText(trimCollapsedText: CharSequence) {
        this.trimCollapsedText = trimCollapsedText
    }

    fun setTrimExpandedText(trimExpandedText: CharSequence) {
        this.trimExpandedText = trimExpandedText
    }

    fun setTrimMode(trimMode: Int) {
        this.trimMode = trimMode
    }

    fun setTrimLines(trimLines: Int) {
        this.trimLines = trimLines
    }

    private inner class ReadMoreClickableSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            readMore = !readMore
            setText()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = colorClickableText
        }
    }

    private fun onGlobalLayoutLineEndIndex() {
        if (trimMode == TRIM_MODE_LINES) {
            viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        this@ExpainableEllipsizeTextView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    } else {
                        this@ExpainableEllipsizeTextView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                    refreshLineEndIndex()
                    setText()
                }
            })
        }
    }

    private fun refreshLineEndIndex() {
        try {
            if (trimLines == 0) {
                lineEndIndex = layout.getLineEnd(0)
            } else if (trimLines > 0 && lineCount >= trimLines) {
                lineEndIndex = layout.getLineEnd(trimLines - 1)
            } else {
                lineEndIndex = INVALID_END_INDEX
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}