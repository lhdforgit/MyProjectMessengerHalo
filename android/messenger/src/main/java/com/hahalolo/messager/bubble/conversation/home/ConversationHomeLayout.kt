package com.hahalolo.messager.bubble.conversation.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.BubbleServiceCallback
import com.hahalolo.messager.bubble.base.AbsLifecycleView
import com.hahalolo.messager.bubble.conversation.BubbleConversationCallback
import com.hahalolo.messager.bubble.conversation.BubbleConversationLayout
import com.hahalolo.messager.bubble.conversation.home.adapter.v2.Conversation2Adapter
import com.hahalolo.messenger.databinding.ConversationHomeLayoutBinding

class ConversationHomeLayout : AbsLifecycleView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    lateinit var binding: ConversationHomeLayoutBinding
    private var viewModel: ConversationHomeViewModel? = null
    private var serviceCallback: BubbleServiceCallback? = null
    private var conversationCallback: BubbleConversationCallback? = null
//    private var adapter: ConversationAdapter? = null

    private var adapter2: Conversation2Adapter? = null

    private val requestManager: RequestManager by lazy { Glide.with(context) }

    fun updateBubbleServiceCallback(serviceCallback: BubbleServiceCallback) {
        this.serviceCallback = serviceCallback
    }

    fun updateBubbleConversationCallback(conversationCallback: BubbleConversationLayout) {
        this.conversationCallback = conversationCallback
    }

    override fun initializeBinding() {
        binding = ConversationHomeLayoutBinding.inflate(
            LayoutInflater.from(context),
            this@ConversationHomeLayout,
            true
        )
    }

    override fun initializeViewModel() {
        viewModel =
            serviceCallback?.createViewModel(ConversationHomeViewModel::class) as? ConversationHomeViewModel
    }


    override fun initializeLayout() {

    }

    /*NAVIGATE ACT END*/
    fun onShowContent() {

    }

    fun onHideContent() {

    }
}