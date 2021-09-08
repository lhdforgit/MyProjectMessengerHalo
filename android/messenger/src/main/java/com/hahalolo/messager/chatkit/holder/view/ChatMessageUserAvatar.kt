package com.hahalolo.messager.chatkit.holder.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.databinding.ChatMessageUserAvatarBinding

class ChatMessageUserAvatar : FrameLayout {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private lateinit var binding: ChatMessageUserAvatarBinding

    private var member: MemberEntity? = null
    private var loader: ImageLoader? = null

    private fun init() {
        binding = ChatMessageUserAvatarBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

    fun updateImageLoader(loader: ImageLoader?) {
        this.loader = loader
    }

    fun updateMemberEntity(member: MemberEntity?) {
        this.member = member
        requestUpdateLayout()
    }

    private fun requestUpdateLayout() {
        this.member?.run {
            loader?.loadAvatar(binding.messageUserAvatar, this.userId(), this.getAvatar())
            binding.messageUserOnline.visibility = if (this.isOnline()) View.VISIBLE else View.GONE
        } ?: run {
            binding.messageUserAvatarGr.visibility = GONE
        }
    }

    fun getTargets(): MutableList<ImageView> {
        return mutableListOf(binding.messageUserAvatar)
    }

    fun invalidateLayout(requestManager: RequestManager?) {
        requestManager?.clear(binding.messageUserAvatar)
        binding.messageUserAvatar.setImageDrawable(null)
    }
}