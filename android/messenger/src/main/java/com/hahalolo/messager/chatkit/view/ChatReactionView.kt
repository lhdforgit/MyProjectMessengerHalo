/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.chatkit.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatReactionViewBinding
import com.halo.common.utils.SpanBuilderUtil
import com.halo.data.entities.reaction.Reactions
import com.halo.data.room.type.ReactionType
import com.halo.widget.HaloCenteredImageSpan
import com.halo.widget.reactions.ReactionEmotions
import com.halo.widget.reactions.ReactionNumber

class ChatReactionView : RelativeLayout {
    lateinit var binding: ChatReactionViewBinding

//    var listReactions = mutableListOf<ReactionTable>()

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.chat_reaction_view, this, false
        )
        addView(binding.root, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
    }

//    fun updateListReactions(listReactions: MutableList<ReactionTable>?) {
//        listReactions?.let {
//            this.listReactions = it
//        }
//        requestUpdateLayout()
//    }

    private fun requestUpdateLayout() {
//        listReactions.takeIf { it.isNotEmpty() }?.run {
//            binding.textReactions.visibility = View.VISIBLE
//            var haha = 0
//            var love = 0
//            var cry = 0
//            var angry = 0
//            var wow = 0
//            var like = 0
//            var dislike = 0
//
//            this.forEach {
//                when (it.type) {
//                    ReactionType.HAHA -> haha++
//                    ReactionType.LOVE -> love++
//                    ReactionType.CRY -> cry++
//                    ReactionType.ANGRY -> angry++
//                    ReactionType.WOW -> wow++
//                    ReactionType.LIKE -> like++
//                    ReactionType.DISLiKE -> dislike++
//                }
//            }
//
//            binding.textReactions.text = SpanBuilderUtil().apply {
//                var first = true
//                first = appenReactionType(this, ReactionNumber.HAHA, like, first)
//                first = appenReactionType(this, ReactionNumber.LOVE, love, first)
//                first = appenReactionType(this, ReactionNumber.LOLO, haha, first)
//                first = appenReactionType(this, ReactionNumber.WOW, wow, first)
//                first = appenReactionType(this, ReactionNumber.SAD, cry, first)
//                appenReactionType(this, ReactionNumber.ANGRY, angry, first)
//            }.build()
//        } ?: run {
//            binding.textReactions.visibility = View.GONE
//            binding.textReactions.text = ""
//        }
    }

    private fun appenReactionType(
        util: SpanBuilderUtil,
        type: Int,
        count: Int,
        first: Boolean
    ): Boolean {
        if (count > 0) {
            ReactionEmotions.reactionOf(type)?.apply {
                if (!first) util.append(" ")
                util.append(" ", HaloCenteredImageSpan(context, this.resDrawable))
                util.append(if (count > 1) ("$count") else "")
            }
            return false
        }
        return first
    }

    fun updateReaction(reactions: Reactions) {
        val haha = reactions.get(ReactionType.HAHA)?.size?:0
        val love = reactions.get(ReactionType.LOVE)?.size?:0
        val cry = reactions.get(ReactionType.CRY)?.size?:0
        val angry = reactions.get(ReactionType.ANGRY)?.size?:0
        val wow = reactions.get(ReactionType.WOW)?.size?:0
        val like = reactions.get(ReactionType.LIKE)?.size?:0
        val dislike = reactions.get(ReactionType.DISLiKE)?.size?:0

        binding.textReactions.text = SpanBuilderUtil().apply {
            var first = true
            first = appenReactionType(this, ReactionNumber.HAHA, like, first)
            first = appenReactionType(this, ReactionNumber.LOVE, love, first)
            first = appenReactionType(this, ReactionNumber.LOLO, haha, first)
            first = appenReactionType(this, ReactionNumber.WOW, wow, first)
            first = appenReactionType(this, ReactionNumber.SAD, cry, first)
            appenReactionType(this, ReactionNumber.ANGRY, angry, first)
        }.build()
    }
}