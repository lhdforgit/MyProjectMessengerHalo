package com.hahalolo.incognito.presentation.create.forward.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoForwardMessageSendStatusViewBinding

class IncognitoForwardMessageSendStatus : FrameLayout {

    private var binding: IncognitoForwardMessageSendStatusViewBinding? = null

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
            R.layout.incognito_forward_message_send_status_view,
            this,
            false
        )
        addView(binding?.root)
    }

    fun setStatus(status: Int) {
        binding?.apply {
            when (status) {
                ForwardStatus.SUCCESS -> {
                    statusImg.setImageResource(R.drawable.ic_incognito_forward_success)
                }
                ForwardStatus.LOADING -> {
                    statusImg.setImageResource(R.drawable.ic_incognito_forward_revoke)
                }
                ForwardStatus.SEND -> {
                    statusImg.setImageResource(R.drawable.ic_incognito_forward_un_active_send)
                }
                else -> {
                    statusImg.visibility = View.GONE
                }
            }
        }
    }
}

object ForwardStatus {
    const val SUCCESS = 1
    const val LOADING = 2
    const val SEND = 3
}