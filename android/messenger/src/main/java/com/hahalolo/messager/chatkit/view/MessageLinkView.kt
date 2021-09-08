package com.hahalolo.messager.chatkit.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.RequestManager
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.MessageLinkViewBinding
import com.halo.data.entities.message.Message

class MessageLinkView : FrameLayout {
    private var requestManager: RequestManager? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private lateinit var binding: MessageLinkViewBinding

    fun setRequestManager(requestManager: RequestManager) {
        this.requestManager = requestManager
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.message_link_view, this, false
        )
        addView(binding.root)
    }

    fun onBind(embed: Message.Embed) {
        val meta = embed.meta()
        binding.apply {
            requestManager
                ?.load(meta?.image)
                ?.error(R.drawable.holder_rect)
                ?.into(contentImg)

            titleTv.text = meta?.title ?:""
            descriptionTv.text = meta?.description?:""
        }
    }
}