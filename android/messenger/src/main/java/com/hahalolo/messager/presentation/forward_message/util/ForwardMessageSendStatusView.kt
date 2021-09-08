package com.hahalolo.messager.presentation.forward_message.util

import android.content.Context
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ForwardMessageSendStatusViewBinding
import com.halo.common.utils.SpanBuilderUtil
import com.halo.widget.HaloTypefaceSpan

class ForwardMessageSendStatusView : FrameLayout {

    private var binding: ForwardMessageSendStatusViewBinding? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        val layoutInflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.forward_message_send_status_view,
            this,
            false
        )
        addView(binding?.root)
    }

    fun setStatus(status: Int) {
        ForwardType.getResource(status)?.let {
            binding?.statusBt?.applyTextStyle(
                text = it.text(),
                color = it.textColor(),
                background = it.borderBackground()
            )
        }
    }

    private fun AppCompatTextView.applyTextStyle(text: String, color: Int, background: Int) {
        kotlin.runCatching {
            val fontSpan = HaloTypefaceSpan.REGULAR(context)
            val sp = SpanBuilderUtil()
            sp.append(
                text, fontSpan, ForegroundColorSpan(
                    ContextCompat.getColor(
                        context,
                        color
                    )
                )
            )
            this.text = sp.build()
            this.setBackgroundResource(background)
        }
    }
}

object ForwardStatus {
    const val SUCCESS = 1
    const val LOADING = 2
    const val SEND = 3
}

enum class ForwardType(
    val status: Int
) {
    SEND(ForwardStatus.SEND) {
        override fun text(): String {
            return "Gửi"
        }

        override fun textColor(): Int {
            return R.color.text_primary
        }

        override fun borderBackground(): Int {
            return R.drawable.bg_forward_message_send
        }

    },
    LOADING(ForwardStatus.LOADING) {
        override fun text(): String {
            return "Hoàn tác"
        }

        override fun textColor(): Int {
            return R.color.text_secondary
        }

        override fun borderBackground(): Int {
            return R.drawable.bg_forward_message_sending
        }

    },
    SUCCESS(ForwardStatus.SUCCESS) {
        override fun text(): String {
            return "Đã gửi"
        }

        override fun textColor(): Int {
            return R.color.text_primary
        }

        override fun borderBackground(): Int {
            return R.drawable.bg_forward_message_send_success
        }

    };

    abstract fun text(): String
    abstract fun textColor(): Int
    abstract fun borderBackground(): Int

    companion object {
        private val values = values()
        fun getResource(value: Int) = values.firstOrNull { it.status == value }
    }
}