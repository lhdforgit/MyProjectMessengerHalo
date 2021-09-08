/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.view.input

import android.graphics.Typeface
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan

internal class MentionSpannableString : SpannableString {
    constructor(spanEntity: MentionSpannableEntity) : super(spanEntity.content) {
        val colorSpan = MentionColorSpan(spanEntity, COLOR_TAG_EDIT)
        val backgroundColorSpan = BackgroundColorSpan(COLOR_TAG_BACKGROUND_EDIT)
        setSpan(colorSpan, 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(backgroundColorSpan, 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(StyleSpan(STYLE_TAG_EDIT), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    constructor(spanEntity: MentionSpannableEntity, tagingClickSpan: TagingClickSpan?) : super(
        spanEntity.content
    ) {
        val colorSpan = MentionColorSpan(spanEntity, COLOR_TAG_EDIT)
        val backgroundColorSpan = BackgroundColorSpan(COLOR_TAG_BACKGROUND_CLICK)
        setSpan(colorSpan, 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(backgroundColorSpan, 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        setSpan(StyleSpan(STYLE_TAG_CLICK), 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        if (tagingClickSpan != null) {
            setSpan(tagingClickSpan, 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    internal class MentionColorSpan(val spanEntity: MentionSpannableEntity, color: Int) :
        ForegroundColorSpan(color)

    internal abstract class TagingClickSpan : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.color = COLOR_TAG_CLICK
            ds.isUnderlineText = false
        }
    }

    companion object {
        private const val COLOR_TAG_EDIT = -0xb3ad9c
        private const val COLOR_TAG_CLICK = -0xb3ad9c
        private const val COLOR_TAG_BACKGROUND_EDIT = -0x551a1a1b
        private const val COLOR_TAG_BACKGROUND_CLICK = -0x551f1614
        private const val STYLE_TAG_EDIT = Typeface.BOLD
        private const val STYLE_TAG_CLICK = Typeface.BOLD
    }
}