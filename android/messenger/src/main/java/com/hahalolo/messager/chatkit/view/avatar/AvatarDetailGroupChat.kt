/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.chatkit.view.avatar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.RequestManager
import com.halo.data.room.entity.ChannelEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.AvatarDetailGroupChatViewBinding
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.SpanBuilderUtil

class AvatarDetailGroupChat : FrameLayout {

    private var binding: AvatarDetailGroupChatViewBinding? = null
    private var listImage: List<String>? = null
    private var requestManager: RequestManager? = null
    private var listener: AvatarDetailGroupListener? = null
    private var userIdToken: String = ""

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
            DataBindingUtil.inflate(inflater, R.layout.avatar_detail_group_chat_view, null, false)
        addView(binding?.root)
    }

    fun updateListImage(
        listImage: List<String>,
        requestManager: RequestManager?,
        listener: AvatarDetailGroupListener?,
        userIdToken: String
    ) {
        this.listImage = listImage
        this.requestManager = requestManager
        this.listener = listener
        this.userIdToken = userIdToken
        binding?.updateView()
    }

    fun updateListImage(
        listImage: List<String>,
        requestManager: RequestManager?,
    ) {
        this.listImage = listImage
        this.requestManager = requestManager
        binding?.updateView()
    }

    private fun AvatarDetailGroupChatViewBinding.updateView() {
        countImage = 0
        listImage?.takeIf { it.isNotEmpty() }?.run { ->
            countImage = size
            requestManager?.apply {
                forEachIndexed { index, url ->
                    when (index) {
                        0 -> {
                            if (size > 1) {
                                loadImage(avatarUser1, url)
                            }
                            loadImage(avatarIv, url)
                        }
                        1 -> {
                            loadImage(avatarUser2, url)
                        }
                        2 -> {
                            loadImage(avatarUser3, url)
                        }
                        else -> {
                            val sp = SpanBuilderUtil()
                            sp.append("+${size - 3}")
                            countTv.text = sp.build()
                        }
                    }
                }
            }
            bindAction()
        }
    }

    private fun loadImage(imageView: ImageView, url: String?) {
        requestManager?.run {
            GlideRequestBuilder.getCircleCropRequest(this)
                .load(url)
                .error(R.drawable.community_avatar_holder)
                .placeholder(R.drawable.community_avatar_holder)
                .into(imageView)
        }
    }

    private fun bindAction() {
    }

    fun getAvatarImageView(): ImageView? {
        return binding?.avatarIv
    }
}