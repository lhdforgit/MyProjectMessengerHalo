package com.hahalolo.messager.bubble.conversation.detail

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.BubbleServiceCallback
import com.hahalolo.messager.bubble.base.AbsLifecycleView
import com.hahalolo.messager.bubble.conversation.BubbleConversationCallback
import com.hahalolo.messager.bubble.conversation.BubbleConversationLayout
import com.hahalolo.messager.bubble.conversation.BubbleConversationType
import com.hahalolo.messager.bubble.conversation.detail.adapter.ConversationDetailMediaAdapter
import com.hahalolo.messager.bubble.conversation.detail.adapter.ConversationDetailMediaListener
import com.hahalolo.messager.bubble.conversation.view.ChatGroupAvatarMenuView
import com.hahalolo.messager.chatkit.view.avatar.AvatarDetailGroupListener
import com.halo.data.room.entity.ChannelEntity
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ConversationDetailLayoutBinding
import com.halo.common.utils.list.ListUtils
import com.halo.data.common.resource.StatusNetwork
import com.halo.data.entities.media.MediaEntity
import com.halo.widget.HaloGridLayoutManager
import com.halo.widget.tone.HaloTone
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConversationDetailLayout : AbsLifecycleView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    lateinit var binding: ConversationDetailLayoutBinding
    var viewModel: ConversationDetailViewModel? = null
    var serviceCallback: BubbleServiceCallback? = null
    var conversationCallback: BubbleConversationCallback? = null

    fun updateBubbleServiceCallback(serviceCallback: BubbleServiceCallback) {
        this.serviceCallback = serviceCallback
    }

    fun updateBubbleConversationCallback(conversationCallback: BubbleConversationLayout) {
        this.conversationCallback = conversationCallback
    }

    override fun initializeBinding() {
        binding = ConversationDetailLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun initializeViewModel() {
        viewModel =
            serviceCallback?.createViewModel(ConversationDetailViewModel::class) as? ConversationDetailViewModel
    }

    override fun initializeLayout() {
    }


    fun updateRoomEntity(roomId: String) {

    }

    /*NAVIGATE ACT END*/
    fun onShowContent() {

    }

    fun onHideContent() {

    }
}