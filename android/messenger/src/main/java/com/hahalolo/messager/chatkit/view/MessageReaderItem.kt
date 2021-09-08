/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.R
import com.halo.common.utils.SizeUtils
import kotlin.math.min

class MessageReaderItem : LinearLayout {

    companion object {
        private const val MAX_PERSION = 3
        private const val COUNT_FORMAT = "+%d"
    }

    private var memberEntities: MutableList<MemberEntity>? = null
    private var imageLoader: ImageLoader? = null

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    @SuppressLint("RtlHardcoded")
    private fun initView() {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL or Gravity.END or Gravity.RIGHT
    }

    fun setMemberEntities(memberEntities: MutableList<MemberEntity>?) {
        this.memberEntities = memberEntities
        updateListMemberReader()
    }

    fun setImageLoader(imageLoader: ImageLoader?) {
        this.imageLoader = imageLoader
    }

    private fun updateListMemberReader() {
        removeAllViews()
        memberEntities?.takeIf { it.isNotEmpty() }?.run {
            val size = this.size
            val end = min(size, MAX_PERSION)
            for (i in 0 until end) {
                addReaderView(this[i])
            }
            if (size > end) {
                addCountReader(size - end)
            }
        }
    }

    private fun addCountReader(i: Int) {
        val textView = AppCompatTextView(context)
        textView.setTextAppearance(context, R.style.Messenger_Character_H7)
        val size = SizeUtils.dp2px(18f)
        val margin = SizeUtils.dp2px(1f)
        val padding = SizeUtils.dp2px(2f)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, size)
        layoutParams.setMargins(margin, margin, margin, margin)
        textView.layoutParams = layoutParams
        textView.setPadding(padding, 0, padding, 0)
        textView.gravity = Gravity.CENTER
        textView.setTextColor(ContextCompat.getColor(context, R.color.text_body))
        textView.setBackgroundResource(R.drawable.shape_count_reader_message)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
        textView.text = String.format(COUNT_FORMAT, i)
        addView(textView)
    }

    private fun addReaderView(memberEntity: MemberEntity) {
        val imageView = AppCompatImageView(context)
        val size = SizeUtils.dp2px(18f)
        val margin = SizeUtils.dp2px(1f)
        val layoutParams = LayoutParams(size, size)
        layoutParams.setMargins(margin, margin, margin, margin)
        imageView.layoutParams = layoutParams

        imageLoader?.run{
            this.loadSmallAvatar(imageView, memberEntity.getAvatar())
            addView(imageView)
        }
    }

    fun invalidateLayout(requestManager: RequestManager?) {
        val childCount = childCount
        if (childCount > 0) {
            for (i in 0 until childCount) {
                val view = getChildAt(i)
                if (view is ImageView) {
                    requestManager?.clear(view)
                    view.setImageDrawable(null)
                }
            }
        }
    }
}