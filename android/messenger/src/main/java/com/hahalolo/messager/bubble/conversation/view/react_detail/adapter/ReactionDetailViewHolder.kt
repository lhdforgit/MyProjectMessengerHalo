/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.view.react_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatReactionDetailItemBinding
import com.halo.common.utils.SpanBuilderUtil
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.type.ReactionType
import com.halo.widget.HaloCenteredImageSpan
import com.halo.widget.reactions.ReactionEmotions
import com.halo.widget.reactions.ReactionNumber

class ReactionDetailViewHolder(
    val binding: ChatReactionDetailItemBinding,
    val requestManager: RequestManager
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(reactData: MemberEntity, type: String) {
        reactData.apply {
            requestUpdateReaction(type)
            updateAvatar(getAvatar())
            updateUsername(memberName())
        }
    }

    private fun updateUsername(userName: String?) {
       userName?.let {
           binding.nameTv.text = it
       }
    }

    private fun updateAvatar(avatar: String?) {
        avatar?.let {
            requestManager.load(it)
                .placeholder(R.drawable.ic_dummy_personal_circle)
                .error(R.drawable.ic_dummy_personal_circle)
                .circleCrop()
                .into(binding.avatarIv)
        }
    }

    private fun requestUpdateReaction(type: String) {
        var haha = 0
        var love = 0
        var cry = 0
        var angry = 0
        var wow = 0
        var like = 0
        var dislike = 0

        when (type) {
            ReactionType.HAHA -> haha++
            ReactionType.LOVE -> love++
            ReactionType.CRY -> cry++
            ReactionType.ANGRY -> angry++
            ReactionType.WOW -> wow++
            ReactionType.LIKE -> like++
            ReactionType.DISLiKE -> dislike++
        }

        binding.textReactions.text = SpanBuilderUtil().apply {
            appendReactionType(this, ReactionNumber.HAHA, like)
            appendReactionType(this, ReactionNumber.LOVE, love)
            appendReactionType(this, ReactionNumber.LOLO, haha)
            appendReactionType(this, ReactionNumber.WOW, wow)
            appendReactionType(this, ReactionNumber.SAD, cry)
            appendReactionType(this, ReactionNumber.ANGRY, angry)
        }.build()
    }

    private fun appendReactionType(util: SpanBuilderUtil, type: Int, count: Int) {
        if (count > 0) {
            ReactionEmotions.reactionOf(type)?.apply {
                util.append(" ", HaloCenteredImageSpan(itemView.context, this.resDrawable))
                util.append(" ")
            }
        }
    }

    companion object {
        fun createViewHolder(
            parent: ViewGroup,
            requestManager: RequestManager
        ): ReactionDetailViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = DataBindingUtil.inflate<ChatReactionDetailItemBinding>(
                inflater,
                R.layout.chat_reaction_detail_item,
                parent,
                false
            )
            return ReactionDetailViewHolder(
                binding,
                requestManager
            )
        }
    }
}