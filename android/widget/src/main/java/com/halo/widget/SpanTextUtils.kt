/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.util.ArrayMap
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.TypefaceCompat
import com.bumptech.glide.Glide
import com.halo.common.target.SimpleTargetSpan
import com.halo.common.utils.ktx.preventFastClickable
import com.halo.widget.Utils.dp2px
import com.halo.widget.Utils.indexesOf
import com.halo.widget.Utils.ranges
import java.io.Serializable
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * nguyen
 * */
@SuppressLint("InlinedApi")
class HaloSpanSimpleText(val context: Context, val text: CharSequence) :
        SpannableStringBuilder(text) {

    private val rangeList: ArrayList<Range> = ArrayList()
    private val tagsMap: ArrayMap<Range, Any> = ArrayMap()
    private var textColor = 0
    private var pressedTextColor = 0
    private var pressedBackgroundColor = 0
    private var pressedBackgroundRadius = 0

    fun first(target: String?): HaloSpanSimpleText {
        if (target == null) {
            return this
        }
        rangeList.clear()
        val index = toString().indexOf(target)
        val range = Range.create(index, index + target.length)
        rangeList.add(range)
        return this
    }

    fun last(target: String): HaloSpanSimpleText {
        rangeList.clear()
        val index = toString().lastIndexOf(target)
        val range = Range.create(index, index + target.length)
        rangeList.add(range)
        return this
    }

    fun all(target: String): HaloSpanSimpleText {
        rangeList.clear()
        val indexes = indexesOf(toString(), target)
        for (index in indexes) {
            val range = Range.create(index, index + target.length)
            rangeList.add(range)
        }
        return this
    }

    fun all(): HaloSpanSimpleText {
        rangeList.clear()
        val range = Range.create(0, toString().length)
        rangeList.add(range)
        return this
    }

    fun allStartWith(vararg prefixs: String?): HaloSpanSimpleText {
        rangeList.clear()
        for (prefix in prefixs) {
            val ranges = ranges(toString(), Pattern.quote(prefix ?: "") + "\\w+")
            rangeList.addAll(ranges)
        }
        return this
    }

    fun range(from: Int, to: Int): HaloSpanSimpleText {
        rangeList.clear()
        val range = Range.create(from, to + 1)
        rangeList.add(range)
        return this
    }

    fun rangeToEnd(from: Int): HaloSpanSimpleText {
        rangeList.clear()
        val range = Range.create(from, text.length)
        rangeList.add(range)
        return this
    }

    fun ranges(ranges: List<Range>?): HaloSpanSimpleText {
        rangeList.clear()
        ranges?.let { rangeList.addAll(ranges.toList()) }
        return this
    }

    fun between(startText: String, endText: String?): HaloSpanSimpleText {
        rangeList.clear()
        val startIndex = toString().indexOf(startText) + startText.length + 1
        val endIndex = toString().lastIndexOf(endText!!) - 1
        val range = Range.create(startIndex, endIndex)
        rangeList.add(range)
        return this
    }

    fun icon(@DrawableRes drawable: Int): HaloSpanSimpleText {
        for (range in rangeList) {
            setSpan(HaloCenteredImageSpan(context, drawable), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun url(url: String, width: Int, height: Int): HaloSpanSimpleText {
        val glide = Glide.with(context)
        val simpleTarget = SimpleTargetSpan<Bitmap>(width, height) { res, _ ->
            val span = HaloCenteredImageSpan(context, res)
            for (range in rangeList) {
                setSpan(span, range.from, range.to, SPAN_MODE)
            }
        }
        glide
                .asBitmap()
                .load(url)
                .centerInside()
                .into(simpleTarget)
        return this
    }

    fun alignCenter(): HaloSpanSimpleText {
        for (range in rangeList) {
            setSpan(ImageSpan.ALIGN_CENTER, range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun size(dp: Int): HaloSpanSimpleText {
        for (range in rangeList) {
            setSpan(AbsoluteSizeSpan(dp, true), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun scaleSize(proportion: Float): HaloSpanSimpleText {
        for (range in rangeList) {
            setSpan(RelativeSizeSpan(proportion), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun bold(): HaloSpanSimpleText {
        for (range in rangeList) {
            setSpan(StyleSpan(Typeface.BOLD), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun italic(): HaloSpanSimpleText {
        for (range in rangeList) {
            setSpan(StyleSpan(Typeface.ITALIC), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun normal(): HaloSpanSimpleText {
        for (range in rangeList) {
            setSpan(StyleSpan(Typeface.NORMAL), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun font(font: String?): HaloSpanSimpleText {
        for (range in rangeList) {
            setSpan(TypefaceSpan(font), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun font(font: Int): HaloSpanSimpleText {
        for (range in rangeList) {
            val resource = ResourcesCompat.getFont(context, font)
            val typeface = TypefaceCompat.create(context, resource, Typeface.BOLD)
            resource?.let {
                setSpan(
                        CustomTypefaceSpan("", typeface),
                        range.from,
                        range.to,
                        SPAN_MODE
                )
            }
        }
        return this
    }

    fun fromFontResource(@FontRes font: Int): HaloSpanSimpleText {
        runCatching {
            context.resources.getFont(font)
        }.getOrElse {
            ResourcesCompat.getFont(context, font).apply {

            }
        }?.apply {
            for (range in rangeList) {
                setSpan(
                        CustomTypefaceSpan("", this),
                        range.from,
                        range.to,
                        SPAN_MODE
                )
            }
        }
        return this
    }

    fun textAppearance(appearance: Int): HaloSpanSimpleText {
        for (range in rangeList) {
            setSpan(
                    TextAppearanceSpan(context, appearance),
                    range.from,
                    range.to,
                    SPAN_MODE
            )
        }
        return this
    }


    fun strikethrough(): HaloSpanSimpleText {
        for (range in rangeList) {
            setSpan(StrikethroughSpan(), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun underline(): HaloSpanSimpleText {
        for (range in rangeList) {
            setSpan(UnderlineSpan(), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun background(@ColorRes colorRes: Int): HaloSpanSimpleText {
        val color: Int = ContextCompat.getColor(context, colorRes)
        for (range in rangeList) {
            setSpan(BackgroundColorSpan(color), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun textColor(colorValue: Int): HaloSpanSimpleText {
        fun isColorResource(value: Int): Boolean {
            return try {
                ResourcesCompat.getColor(context.resources, value, null)
                true
            } catch (e: Resources.NotFoundException) {
                false
            }
        }
        textColor = if (isColorResource(colorValue))
            ContextCompat.getColor(context, colorValue) else
            colorValue
        for (range in rangeList) {
            setSpan(ForegroundColorSpan(textColor), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun textColorAttr(@AttrRes attr: Int): HaloSpanSimpleText {
        val typeValue = TypedValue()
        context.theme.resolveAttribute(attr, typeValue, true)
        textColor = ContextCompat.getColor(context, typeValue.resourceId)
        for (range in rangeList) {
            setSpan(ForegroundColorSpan(textColor), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun onClick(view: TextView, onClick: () -> Unit): HaloSpanSimpleText {
        val clickAble = object : ClickableSpan() {
            override fun onClick(widget: View) {
                widget.preventFastClickable {
                    onClick.invoke()
                }
            }
        }
        if (view.movementMethod == null || view.movementMethod !is LinkMovementMethod) {
            view.movementMethod = LinkMovementMethod.getInstance()
        }
        for (range in rangeList) {
            setSpan(clickAble, range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun subscript(): HaloSpanSimpleText? {
        for (range in rangeList) {
            setSpan(SubscriptSpan(), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun superscript(): HaloSpanSimpleText? {
        for (range in rangeList) {
            setSpan(SuperscriptSpan(), range.from, range.to, SPAN_MODE)
        }
        return this
    }

    fun pressedTextColor(@ColorRes colorRes: Int): HaloSpanSimpleText? {
        pressedTextColor = ContextCompat.getColor(context, colorRes)
        return this
    }

    fun pressedBackground(@ColorRes colorRes: Int, radiusDp: Int): HaloSpanSimpleText? {
        pressedBackgroundColor = ContextCompat.getColor(context, colorRes)
        pressedBackgroundRadius = dp2px(context, radiusDp)
        return this
    }

    fun pressedBackground(@ColorRes colorRes: Int): HaloSpanSimpleText? {
        return pressedBackground(colorRes, 0)
    }

    fun tag(tag: Any?): HaloSpanSimpleText? {
        val lastRange = rangeList[rangeList.size - 1]
        tagsMap[lastRange] = tag
        return this
    }

    fun tags(vararg tags: Any?): HaloSpanSimpleText? {
        return tags(listOf(tags))
    }

    fun tags(tags: List<Any?>): HaloSpanSimpleText? {
        var i = 0
        for (tag in tags) {
            tagsMap[rangeList[i++]] = tag
        }
        return this
    }

    fun appendChar(text: CharSequence?): HaloSpanSimpleText {
        if (text != null) {
            super.append(text)
        }
        return this
    }

    fun appendCharAfterFrom(textAppend: CharSequence?): HaloSpanSimpleText {
        for (range in rangeList) {
            if (!textAppend.isNullOrBlank()) {
                super.append(textAppend, range.to, range.to.plus(textAppend.length))
            }
        }
        return this
    }

    companion object {
        const val SPAN_MODE = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        const val textSizeNormal = 16
        const val textSizeNomralS = 24
        const val textSizeLarge = 32
        const val textSizeMini = 12

        @JvmStatic
        fun style(context: Context, text: CharSequence?, style: Int): HaloSpanSimpleText {
            return HaloSpanSimpleText(context, text ?: "")
                    .first(text?.toString())
                    .textAppearance(style)
        }
    }
}

/**
 * Utils for create span
 * */
internal object Utils {

    fun dp2px(context: Context, dp: Int): Int {
        val density: Float =
                context.applicationContext.resources.displayMetrics.density
        return (dp * density + 0.5f).toInt()
    }

    fun openURL(context: Context, url: String?) {
        val uri: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun indexesOf(src: String, target: String?): List<Int> {
        val positions: MutableList<Int> = ArrayList()
        var index = src.indexOf(target!!)
        while (index >= 0) {
            positions.add(index)
            index = src.indexOf(target, index + 1)
        }
        return positions
    }

    fun ranges(src: String?, pattern: String?): List<Range> {
        val ranges: MutableList<Range> = ArrayList()
        val matcher: Matcher = Pattern.compile(pattern ?: "").matcher(src ?: "")
        while (matcher.find()) {
            val range: Range = Range.create(matcher.start(), matcher.end())
            ranges.add(range)
        }
        return ranges
    }
}

class Range private constructor(var from: Int, var to: Int) : Serializable {
    override fun toString(): String {
        val sb = StringBuilder("Range{")
        sb.append("from=").append(from)
        sb.append(", to=").append(to)
        sb.append('}')
        return sb.toString()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val range = o as Range
        return if (from != range.from) false else to == range.to
    }

    override fun hashCode(): Int {
        var result = from
        result = 31 * result + to
        return result
    }

    companion object {
        fun create(from: Int, to: Int): Range {
            return Range(from, to)
        }
    }
}

class CustomTypefaceSpan(family: String?, private val newType: Typeface) :
        TypefaceSpan(family) {
    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    companion object {
        private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
            val oldStyle: Int
            paint.typeface?.also { old ->
                oldStyle = old.style
                val fake = oldStyle and tf.style.inv()
                if (fake and Typeface.BOLD != 0) {
                    paint.isFakeBoldText = true
                }
                if (fake and Typeface.ITALIC != 0) {
                    paint.textSkewX = -0.25f
                }
                paint.typeface = tf
            }
        }
    }

}

