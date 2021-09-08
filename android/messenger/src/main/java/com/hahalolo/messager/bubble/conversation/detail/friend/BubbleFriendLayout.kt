package com.hahalolo.messager.bubble.conversation.detail.friend

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.BubbleServiceCallback
import com.hahalolo.messager.bubble.base.AbsLifecycleView
import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendSelectData
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatBubbleFriendViewBinding

class BubbleFriendLayout : AbsLifecycleView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var binding: ChatBubbleFriendViewBinding? = null
    private var viewModel: BubbleFriendViewModel? = null
    private var serviceCallback: BubbleServiceCallback? = null

    private var roomId = MutableLiveData<String>()
    private var friend = MutableLiveData<FriendSelectData>()
    private var actionType = MutableLiveData<Int>()

    private var requestManager: RequestManager? = null
    private fun getRequestManager(): RequestManager {
        if (requestManager == null) requestManager = Glide.with(context)
        return requestManager!!
    }

    fun updateBubbleServiceCallback(serviceCallback: BubbleServiceCallback) {
        this.serviceCallback = serviceCallback
    }

    override fun initializeBinding() {
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_bubble_friend_view, this, true)
    }

    override fun initializeViewModel() {
        viewModel =
            serviceCallback?.createViewModel(BubbleFriendViewModel::class) as? BubbleFriendViewModel
    }

    override fun initializeLayout() {

    }

    /**
     * init create group chat with user
     * @param memberEntity
     * action type: BubbleFriendType.CREATE_GROUP
     * */
    fun startCreateGroupWith(memberEntity: MemberEntity?, tag: Int) {
        resetLayoutDefault()
        val friend =
            FriendSelectData().apply {
                userId = memberEntity?.userId()
                fullName = memberEntity?.getFullName()
                avatar = memberEntity?.getAvatar()
            }
        this.friend.value = friend
//        viewModel?.tag = tag
        actionType.value = BubbleFriendType.CREATE_GROUP
    }

    /**
     * init add member to Group
     * @param roomId
     * action type: BubbleFriendType.ADD_MEMBER
     * */
    fun startAddMemberToGroup(roomId: String, tag: Int) {
        resetLayoutDefault()
        this.roomId.value = roomId
//        viewModel?.tag = tag
        actionType.value = BubbleFriendType.ADD_MEMBER
    }

    private fun resetLayoutDefault() {

    }
}