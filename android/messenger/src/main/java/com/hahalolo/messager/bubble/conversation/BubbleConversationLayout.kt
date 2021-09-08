package com.hahalolo.messager.bubble.conversation

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.lifecycle.MutableLiveData
import com.facebook.rebound.SimpleSpringListener
import com.facebook.rebound.Spring
import com.facebook.rebound.SpringSystem
import com.hahalolo.messager.bubble.BubbleService
import com.hahalolo.messager.bubble.BubbleServiceCallback
import com.hahalolo.messager.bubble.SpringConfigs
import com.hahalolo.messager.bubble.base.AbsLifecycleView
import com.hahalolo.messager.bubble.conversation.dialog.HaloDialogCustomView
import com.hahalolo.messager.bubble.conversation.view.ChatGroupAvatarMenuView
import com.hahalolo.messager.bubble.conversation.view.react_detail.bubble.BubbleBottomReactDetailView
import com.hahalolo.messager.bubble.conversation.view.reader_detail.bubble.BubbleBottomReaderDetailView
import com.hahalolo.messager.bubble.head.BubbleHead
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.databinding.ConversationLayoutBinding
import com.halo.common.utils.ktx.bindIsVisible

class BubbleConversationLayout : AbsLifecycleView, BubbleConversationCallback {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var listener: BubbleServiceCallback? = null
    private lateinit var binding: ConversationLayoutBinding
    private var viewModel: BubbleConversationViewModel? = null

    fun updateBubbleServiceCallback(listener: BubbleServiceCallback) {
        this.listener = listener
        binding.detailLayout.updateBubbleServiceCallback(listener)
        binding.messageLayout.updateBubbleServiceCallback(listener)
        binding.homeLayout.updateBubbleServiceCallback(listener)
        binding.friendLayout.updateBubbleServiceCallback(listener)
        binding.memberLayout.updateBubbleServiceCallback(listener)
        binding.galleryLayout.updateBubbleServiceCallback(listener)
    }

    private val springSystem = SpringSystem.create()
    private val scaleSpring = springSystem.createSpring()

    private val conversationType = MutableLiveData(BubbleConversationType.CONVERSATION_HOME)

    override fun initializeBinding() {
        binding = ConversationLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        binding.messageLayout.updateBubbleConversationCallback(this)
        binding.detailLayout.updateBubbleConversationCallback(this)
        binding.homeLayout.updateBubbleConversationCallback(this)
        binding.memberLayout.updateBubbleConversationCallback(this)
    }

    override fun initializeViewModel() {
        viewModel = listener?.createViewModel(BubbleConversationViewModel::class) as? BubbleConversationViewModel
    }

    private var closing = false

    override fun initializeLayout() {
        binding.container.setOnClickListener { }
        scaleSpring.addListener(object : SimpleSpringListener() {
            override fun onSpringUpdate(spring: Spring) {
                scaleX = spring.currentValue.toFloat()
                scaleY = spring.currentValue.toFloat()
            }

            override fun onSpringEndStateChange(spring: Spring?) {
                super.onSpringEndStateChange(spring)
            }
        })
        scaleSpring.springConfig = SpringConfigs.CONTENT_SCALE
        scaleSpring.currentValue = 0.0

        conversationType.observe(this, {
            binding.messageLayout.bindIsVisible(it == BubbleConversationType.CONVERSATION_MESSAGE)
            binding.detailLayout.bindIsVisible(it == BubbleConversationType.CONVERSATION_DETAIL)
            binding.homeLayout.bindIsVisible(it == BubbleConversationType.CONVERSATION_HOME)
            binding.memberLayout.bindIsVisible(it == BubbleConversationType.CONVERSATION_MEMBERS)
            binding.friendLayout.bindIsVisible(it == BubbleConversationType.CONVERSATION_CREATE)
            binding.galleryLayout.bindIsVisible(it == BubbleConversationType.CONVERSATION_MEDIA)
        })
    }

    private fun updateConversationType(type: Int) {
        conversationType.takeIf { it.value != type }?.apply {
            this.value = type
        }
    }

    fun showContent(home: Boolean = false, move: Boolean = false) {
        closing = false
        if (home) {
            if (!move) binding.homeLayout.onShowContent()
            if (!move) binding.messageLayout.onHideContent()
            updateConversationType(BubbleConversationType.CONVERSATION_HOME)
        } else {
            if (!move) binding.messageLayout.onShowContent()
            if (!move) binding.homeLayout.onHideContent()
            updateConversationType(BubbleConversationType.CONVERSATION_MESSAGE)
        }
        scaleSpring.endValue = 1.0
        if (scaleSpring.currentValue!= 1.0){
            val anim = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 100
            anim.repeatMode = Animation.RELATIVE_TO_SELF
            startAnimation(anim)
        }
    }

    fun hideContent(move: Boolean = false) {
        closing = true
        if (!move) binding.messageLayout.onHideContent()
        if (!move) binding.homeLayout.onHideContent()
        hideDetailRoom()
        hideAllDialog()
        BubbleService.instance?.removeShowContentRunnable()
        scaleSpring.endValue = 0.0
        val anim = AlphaAnimation(1.0f, 0.0f)
        anim.duration = 200
        anim.repeatMode = Animation.RELATIVE_TO_SELF
        anim.setAnimationListener(object : Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                if (closing) { updateConversationType(BubbleConversationType.CONVERSATION_NONE) }
                closing = false
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
        startAnimation(anim)
    }

    private fun hideAllDialog() {
        binding.haloDialogView.hideDialog()
        binding.reactDetailView.hideDialog()
        binding.readerDetailView.hideDialog()
        binding.avatarMenuView.hideDialog()
    }

    override fun hideDetailRoom() {
        updateConversationType(BubbleConversationType.CONVERSATION_MESSAGE)
        binding.detailLayout.onHideContent()
    }

    override fun getDialog(): HaloDialogCustomView{
        return binding.haloDialogView
    }

    override fun getBottomReactDetail(): BubbleBottomReactDetailView{
        return binding.reactDetailView
    }

    override fun getBottomReaderDetail(): BubbleBottomReaderDetailView{
        return binding.readerDetailView
    }

    override fun getAvatarGroupMenu(): ChatGroupAvatarMenuView? {
        return binding.avatarMenuView
    }

    override fun onBackPress(): Boolean {
        return binding.messageLayout.takeIf { it.visibility == View.VISIBLE }?.onBackPress()
            ?: false
    }

    /*Navigate show Bubble conversation */
    fun navigateMessage(activeChatHead: BubbleHead) {
        hideAllDialog()
        binding.messageLayout.onShowContent()
        binding.messageLayout.updateInfor(activeChatHead.bubbleId() ?: "")
        updateConversationType(BubbleConversationType.CONVERSATION_MESSAGE)
    }

    fun navigateSetting(roomId: String) {
        binding.detailLayout.onShowContent()
        binding.detailLayout.updateRoomEntity(roomId)
        updateConversationType(BubbleConversationType.CONVERSATION_DETAIL)
    }

    fun navigateHome() {
        hideAllDialog()
        binding.homeLayout.onShowContent()
        updateConversationType(BubbleConversationType.CONVERSATION_HOME)
    }
    /*Navigate show Bubble conversation END*/

    fun navigateMember(roomId: String, tag: Int?) {
        binding.memberLayout.startMemberList(roomId, tag)
        updateConversationType(BubbleConversationType.CONVERSATION_MEMBERS)
    }

    fun navigateAddMember(roomId: String, tag: Int) {
        binding.friendLayout.startAddMemberToGroup(roomId, tag)
        updateConversationType(BubbleConversationType.CONVERSATION_CREATE)
    }

    fun navigateCreateGroupWith(member: MemberEntity, tag: Int) {
        binding.friendLayout.startCreateGroupWith(member, tag)
        updateConversationType(BubbleConversationType.CONVERSATION_CREATE)
    }

    fun navigateMemberNickName(roomId: String, tag: Int) {
        binding.memberLayout.startMemberNickName(roomId, tag)
        updateConversationType(BubbleConversationType.CONVERSATION_MEMBERS)
    }

    fun navigateUpdateOwnerMember(roomId: String, tag: Int) {
        binding.memberLayout.startMemberAssignOwner(roomId, tag)
        updateConversationType(BubbleConversationType.CONVERSATION_MEMBERS)
    }

    fun navigateGalleryShared(roomId: String, tabIndex: Int) {
        when (tabIndex) {
            1 -> {
                binding.galleryLayout.startMediaShared(roomId)
            }
            2 -> {
                binding.galleryLayout.startLinkShared(roomId)
            }
            3 -> {
                binding.galleryLayout.startDocShared(roomId)
            }
            else -> {

            }
        }
        updateConversationType(BubbleConversationType.CONVERSATION_MEDIA)
    }

    //send list media
    fun takeMediaMessage(roomId: String?, medias: MutableList<String>?) {
        binding.messageLayout.takeMediaMessage(roomId, medias)
    }

    //send file extenal
    fun takeFileMessage(roomId: String?, path: String) {
        binding.messageLayout.takeFileMessage(roomId, path)
    }

    override fun invalidateLayout() {
        super.invalidateLayout()
        binding.homeLayout.invalidateLayout()
        binding.messageLayout.invalidateLayout()
        binding.detailLayout.invalidateLayout()
        binding.memberLayout.invalidateLayout()
    }
}