/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.view.react_detail

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.hahalolo.messager.bubble.conversation.view.react_detail.listener.ChatReactionNavigationListener
import com.hahalolo.messager.bubble.conversation.view.react_detail.listener.Constant
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatReactionDetailNavigationBinding
import com.halo.common.utils.SpanBuilderUtil
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.type.ReactionType
import com.halo.widget.HaloCenteredImageSpan
import com.halo.widget.reactions.ReactionEmotions
import com.halo.widget.reactions.ReactionNumber

class ChatReactionDetailNavigation : FrameLayout {

    private var binding: ChatReactionDetailNavigationBinding? = null
    private var listReaction: MutableList<Pair<String, MutableList<MemberEntity>>> = mutableListOf()

    private var lifecycleObserver: LifecycleOwner? = null

    private var selectReaction = MutableLiveData<Int>()

    private var listener: ChatReactionNavigationListener? = null

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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateLayout()
    }

    fun setListReaction(listReaction: MutableList<Pair<String, MutableList<MemberEntity>>>) {
        this.listReaction = listReaction
        updateLayout()
    }

    fun setLifecycleObserver(lifecycleObserver: LifecycleOwner?) {
        this.lifecycleObserver = lifecycleObserver
    }

    fun setListener(listener: ChatReactionNavigationListener?) {
        this.listener = listener
    }

    private fun initView(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding =
            DataBindingUtil.inflate(inflater, R.layout.chat_reaction_detail_navigation, null, false)
        addView(binding?.root)
    }

    private fun updateLayout() {
        listReaction.apply {
            selectReaction.value = ReactionNumber.UNHAHA
            var haha = 0
            var love = 0
            var cry = 0
            var angry = 0
            var wow = 0
            var like = 0
            this.forEach {
                when (it.first) {
                    ReactionType.HAHA -> haha++
                    ReactionType.LOVE -> love++
                    ReactionType.CRY -> cry++
                    ReactionType.ANGRY -> angry++
                    ReactionType.WOW -> wow++
                    ReactionType.LIKE -> like++
                }
            }
            binding?.angryBt?.text = SpanBuilderUtil().apply {
                appendReactionType(this, ReactionNumber.ANGRY, angry, binding?.angryBt)
            }.build()
            binding?.cryBt?.text = SpanBuilderUtil().apply {
                appendReactionType(this, ReactionNumber.SAD, cry, binding?.cryBt)
            }.build()
            binding?.wowBt?.text = SpanBuilderUtil().apply {
                appendReactionType(this, ReactionNumber.WOW, wow, binding?.wowBt)
            }.build()
            binding?.loloBt?.text = SpanBuilderUtil().apply {
                appendReactionType(this, ReactionNumber.LOLO, haha, binding?.loloBt)
            }.build()
            binding?.loveBt?.text = SpanBuilderUtil().apply {
                appendReactionType(this, ReactionNumber.LOVE, love, binding?.loveBt)
            }.build()
            binding?.hahaBt?.text = SpanBuilderUtil().apply {
                appendReactionType(this, ReactionNumber.HAHA, like, binding?.hahaBt)
            }.build()
            lifecycleObserver?.apply {
                selectReaction.observe(this, Observer {
                    it?.let { type ->
                        binding?.type = type
                        listener?.onReactionTypeClick(getTypeReaction(type))
                    }
                })
            }
            binding?.initAction()
        }
    }

    private fun getTypeReaction(type: Int): String {
        return when (type) {
            ReactionNumber.HAHA -> ReactionType.LIKE
            ReactionNumber.LOVE -> ReactionType.LOVE
            ReactionNumber.SAD -> ReactionType.CRY
            ReactionNumber.ANGRY -> ReactionType.ANGRY
            ReactionNumber.WOW -> ReactionType.WOW
            ReactionNumber.LOLO -> ReactionType.HAHA
            else -> Constant.ALL_REACT
        }
    }

    private fun ChatReactionDetailNavigationBinding.initAction() {
        //allBt.setOnClickListener { selectReaction.value = ReactionNumber.UNHAHA }
        hahaBt.setOnClickListener { selectReaction.value = ReactionNumber.HAHA }
        loveBt.setOnClickListener { selectReaction.value = ReactionNumber.LOVE }
        cryBt.setOnClickListener { selectReaction.value = ReactionNumber.SAD }
        angryBt.setOnClickListener { selectReaction.value = ReactionNumber.ANGRY }
        wowBt.setOnClickListener { selectReaction.value = ReactionNumber.WOW }
        loloBt.setOnClickListener { selectReaction.value = ReactionNumber.LOLO }
    }

    private fun appendReactionType(util: SpanBuilderUtil, type: Int, count: Int, view: View?) {
        if (count > 0) {
            view?.visibility = View.VISIBLE
            selectReaction.value = type
            ReactionEmotions.reactionOf(type)?.apply {
                util.append(" ", HaloCenteredImageSpan(context, this.resDrawable))
                util.append(" $count")
            }
        } else {
            view?.visibility = View.GONE
        }
    }
}