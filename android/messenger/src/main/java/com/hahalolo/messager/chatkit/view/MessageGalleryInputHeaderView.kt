/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.hahalolo.messenger.databinding.MessageGalleryInputHeaderViewBinding

class MessageGalleryInputHeaderView : FrameLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private lateinit var binding: MessageGalleryInputHeaderViewBinding
    private fun initView() {
        binding = MessageGalleryInputHeaderViewBinding.inflate(LayoutInflater.from(context))
        addView(binding.root)
        bindAction()
    }

    fun bindAction() {
        binding.root.setOnClickListener {

        }
        binding.btnClose.setOnClickListener {
            listener?.onClose()
        }
    }

    var listener: Listener? = null

    interface Listener {
        fun onClose()
    }
}