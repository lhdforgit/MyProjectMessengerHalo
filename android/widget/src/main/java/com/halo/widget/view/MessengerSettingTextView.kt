package com.halo.widget.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.halo.widget.R
import com.halo.widget.databinding.MessengerSettingTextViewBinding

class MessengerSettingTextView : FrameLayout {

    private lateinit var binding: MessengerSettingTextViewBinding
    private var title = ""
    private var icon = 0

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.messenger_setting_text_view,
            null,
            false
        )
        addView(binding.root)
        val attribute = context.obtainStyledAttributes(attrs, R.styleable.MessengerSettingTextView)
        try {
            attribute.getString(R.styleable.MessengerSettingTextView_text_setting)?.let {
                binding.titleTv.text = it
            }
            attribute.getDrawable(R.styleable.MessengerSettingTextView_icon_setting)?.let {
                binding.iconImg.setImageDrawable(it)
            }
        } finally {
            attribute.recycle()
        }
    }
}