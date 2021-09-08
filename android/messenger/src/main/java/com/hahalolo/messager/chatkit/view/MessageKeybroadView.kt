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
import com.hahalolo.messenger.databinding.MessageKeybroadViewBinding
import com.halo.data.cache.pref.utils.PrefUtilsImpl

class MessageKeybroadView : FrameLayout {
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

    private lateinit var binding: MessageKeybroadViewBinding
    private fun initView() {
        binding = MessageKeybroadViewBinding.inflate(LayoutInflater.from(context))
        addView(binding.root, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT  )
        initHeight()
    }

    private fun initHeight() {
        binding.view.layoutParams?.height = PrefUtilsImpl.getInstance(context).heightKeybroad
    }

    fun updateHeight() {
        binding.view.post {
            val layoutParams = binding.view.layoutParams
            layoutParams.height = PrefUtilsImpl.getInstance(context).heightKeybroad
            binding.view.layoutParams = layoutParams
        }
    }
}