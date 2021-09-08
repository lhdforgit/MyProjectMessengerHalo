/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.chatkit.holder.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.RequestManager
import com.halo.data.room.entity.ChannelEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageCreateGroupViewBinding
import com.halo.common.utils.SpanBuilderUtil

class ChatMessageCreateGroupView : FrameLayout {
    private var roomEntity: ChannelEntity? = null
    private var binding: ChatMessageCreateGroupViewBinding? = null
    private var listener: ChatMessageCreateGroupListener? = null
    private var requestManager: RequestManager? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    fun updateMessageCreateGroup(
        roomEntity: ChannelEntity?,
        userIdToken: String,
        requestManager: RequestManager?,
        listener: ChatMessageCreateGroupListener?
    ) {
        this.listener = listener
        this.requestManager = requestManager
        roomEntity?.let {
            this.roomEntity = it
            binding?.requestUpdateLayout(userIdToken)
        }
    }

    private fun initView() {
        val inflater = LayoutInflater.from(context)
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.chat_message_create_group_view,
                this,
                false
            )
        addView(binding?.root, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun ChatMessageCreateGroupViewBinding.requestUpdateLayout(userIdToken: String) {
        roomEntity?.run {
            //avatarView.updateListImage(this, requestManager, null,  userIdToken)
            getNameGroupChat(this, userIdToken).let { roomName ->
                groupNameTv.text = roomName
            }
            //TODO
//            getListMembers().takeIf { it.isNotEmpty() }?.run {
//                countMemberTv.text = context?.getString(R.string.chat_message_group_count_member, size)
//            }
//            getOwnerName().takeIf { it.isNotEmpty() }?.let { name ->
//                userNameTv.visibility = View.VISIBLE
//                val spName = SpanBuilderUtil()
//                spName.appendWithSpace(name)
//                    .append(context?.getString(R.string.chat_message_group_status_create))
//                userNameTv.text = spName.build()
//            } ?: run {
//                userNameTv.visibility = View.GONE
//            }
            addMemberBt.setOnClickListener { listener?.onClickAddMember(channelId()) }
        }
    }

    private fun getNameGroupChat(entity: ChannelEntity?, userIdToken: String): String {
        entity?.run {
            roomNameSetting().takeIf { it.isNotEmpty() }?.let {
                return it
            } ?: kotlin.run {
                //TODO
                context?.run {
//                    getListMemberOther(userIdToken).takeIf { it.isNotEmpty() }?.run {
//                        when (size) {
//                            1 -> {
//                                return getString(
//                                    R.string.chat_message_name_group_you_with_user,
//                                    get(0).memberName()
//                                )
//                            }
//                            2 -> {
//                                return getString(
//                                    R.string.chat_message_name_group_you_with_user_and_user,
//                                    get(0).memberName(),
//                                    get(1).memberName()
//                                )
//                            }
//                            else -> {
//                                return getString(
//                                    R.string.chat_message_name_group_you_with_three_user,
//                                    get(0).memberName(),
//                                    get(1).memberName(),
//                                    size - 2
//                                )
//                            }
//                        }
//                    } ?: kotlin.run {
//                        return getString(R.string.chat_message_name_group_only_you)
//                    }
                }
            }
        }
        return ""
    }
}