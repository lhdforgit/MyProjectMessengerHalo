package com.hahalolo.messager.bubble.conversation.detail.member

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.BubbleServiceCallback
import com.hahalolo.messager.bubble.base.AbsLifecycleView
import com.hahalolo.messager.bubble.conversation.BubbleConversationCallback
import com.hahalolo.messager.bubble.conversation.BubbleConversationLayout
import com.hahalolo.messager.bubble.conversation.BubbleConversationType
import com.hahalolo.messager.bubble.conversation.detail.member.adapter.ChatMemberAdapter
import com.hahalolo.messager.bubble.conversation.detail.member.adapter.ChatMemberListener
import com.hahalolo.messager.bubble.conversation.detail.member.dialog.MemberMenuListener
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.BubbleMemberLayoutBinding
import com.halo.common.utils.RecyclerViewUtils
import com.halo.data.common.resource.Resource
import com.halo.data.common.resource.StatusNetwork
import com.halo.data.common.utils.Strings
import com.halo.widget.HaloLinearLayoutManager
import com.halo.widget.HaloTypefaceSpan
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BubbleMemberLayout : AbsLifecycleView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var binding: BubbleMemberLayoutBinding? = null
    private var viewModel: BubbleMemberViewModel? = null
    private var serviceCallback: BubbleServiceCallback? = null
    private var conversationCallback: BubbleConversationCallback? = null
    private var adapter: ChatMemberAdapter? = null
    private var requestManager: RequestManager? = null

    private fun getRequestManager(): RequestManager {
        if (requestManager == null) requestManager = Glide.with(context)
        return requestManager!!
    }

    fun updateBubbleServiceCallback(serviceCallback: BubbleServiceCallback) {
        this.serviceCallback = serviceCallback
    }

    fun updateBubbleConversationCallback(conversationCallback: BubbleConversationLayout) {
        this.conversationCallback = conversationCallback
    }

    override fun initializeBinding() {
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.bubble_member_layout, this, true)
    }

    override fun initializeViewModel() {
        viewModel =
            serviceCallback?.createViewModel(BubbleMemberViewModel::class) as? BubbleMemberViewModel
    }

    override fun initializeLayout() {

    }

    /**
     * start member list view
     * @param roomId
     * @param tag flag used to init action back
     * */
    fun startMemberList(roomId: String, tag: Int?) {

    }

    /**
     * start member list view
     * @param roomId
     * @param tag flag used to init action back
     * */
    fun startMemberNickName(roomId: String, tag: Int?) {

    }

    /**
     * start member to assign new owner group
     *@param roomId
     * @param tag : BubbleConversationType
     * */
    fun startMemberAssignOwner(roomId: String, tag: Int?) {

    }
}