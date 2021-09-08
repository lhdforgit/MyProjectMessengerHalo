package com.hahalolo.messager.bubble.conversation.view.react_detail.bubble

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.hahalolo.messager.bubble.base.BubbleBottomDialogAbs
import com.hahalolo.messager.bubble.conversation.view.react_detail.adapter.ReactionDetailAdapter
import com.hahalolo.messager.bubble.conversation.view.react_detail.listener.ChatReactionDetailListener
import com.hahalolo.messager.bubble.conversation.view.react_detail.listener.ChatReactionNavigationListener
import com.hahalolo.messager.bubble.conversation.view.react_detail.listener.Constant
import com.hahalolo.messenger.databinding.BubbleBottomReactDetailViewBinding
import com.halo.widget.HaloLinearLayoutManager

class BubbleBottomReactDetailView : BubbleBottomDialogAbs {

    lateinit var binding: BubbleBottomReactDetailViewBinding

    private var listReaction: MutableList<Any>? = null

    private var listener: ChatReactionDetailListener? = null

    private var adapter: ReactionDetailAdapter? = null


    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override val listSize: Int?
        get() = listReaction?.size
    override val contentLayout: FrameLayout?
        get() = binding.contentLayout

    override fun initializeBinding() {
        val inflater = LayoutInflater.from(context)
        binding = BubbleBottomReactDetailViewBinding.inflate(inflater, this, false)
        addView(
            binding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun updateLayout() {
        super.updateLayout()
        initRec()
        initNav()
    }

    private fun initNav() {
        binding.apply {
           // reactNav.setListReaction(listReaction)
            reactNav.setLifecycleObserver(this@BubbleBottomReactDetailView)
            reactNav.setListener(object :
                ChatReactionNavigationListener {
                override fun onReactionTypeClick(type: String) {
                    var newList = mutableListOf<Any>()
                    listReaction?.run {
                        type.takeIf { TextUtils.equals(it, Constant.ALL_REACT) }?.let {
                            newList = this
                        } ?: kotlin.run {
                            listReaction?.forEach {
//                                if (it.hasReactionType(type)) {
//                                    newList.add(it)
//                                }
                            }
                        }
                    }
                    //adapter?.updateData(newList, type)
                }
            })
        }
    }

    private fun initRec() {
        binding.apply {
            reactRec.layoutManager = HaloLinearLayoutManager(context)
            reactRec.setHasFixedSize(false)
//            adapter =
//                ReactionDetailAdapter(
//                    listReaction,
//                    Glide.with(this@BubbleBottomReactDetailView)
//                )
            binding.reactRec.adapter = adapter
            adapter?.setListener(listener)
            adapter?.notifyDataSetChanged()
        }
    }

    override fun initAction() {
        super.initAction()
        binding.closeBt.setOnClickListener {
            hideDialog()
        }
    }

    fun setListReaction(listReaction: MutableList<Any>?) {
        this.listReaction = listReaction
    }
}