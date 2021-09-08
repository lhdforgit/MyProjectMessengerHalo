/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.chatkit.holder.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.RequestManager
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageGroupAvatarViewBinding
import com.halo.common.utils.SpanBuilderUtil

class ChatMessageGroupAvatarView : FrameLayout {
    private var binding: ChatMessageGroupAvatarViewBinding? = null
    private var listAvatar: MutableList<String>? = null
    private var requestManager: RequestManager? = null

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
        val inflater = LayoutInflater.from(context)
        binding =
            DataBindingUtil.inflate(inflater,
                R.layout.chat_message_group_avatar_view,
                this,
                false)
        addView(binding?.root)
    }

    fun updateListAvatar(listAvatar: MutableList<String>?, requestManager: RequestManager?) {
        this.requestManager = requestManager
        listAvatar?.let {
            this.listAvatar = it
            binding?.requestUpdateLayout()
        }
    }

    private fun ChatMessageGroupAvatarViewBinding.requestUpdateLayout() {
        countImage = 0
        listAvatar?.takeIf { it.isNotEmpty() }?.run {
            countImage = size
            forEachIndexed { index, url ->
                when (index) {
                    0 -> {
                        requestManager?.load(url)?.into(avatarUser1)
                    }
                    1 -> {
                        requestManager?.load(url)?.into(avatarUser2)
                    }
                    2 -> {
                        requestManager?.load(url)?.into(avatarUser3)
                    }
                    else -> {
                        val sp = SpanBuilderUtil()
                        sp.append("+ ${size - 3}")
                        countTv.text = sp.build()
                    }
                }
            }
        }
    }
}