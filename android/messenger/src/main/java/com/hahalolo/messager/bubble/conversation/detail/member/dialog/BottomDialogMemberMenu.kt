package com.hahalolo.messager.bubble.conversation.detail.member.dialog

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hahalolo.messager.bubble.base.BottomSheetAbs
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.BubbleBottomDialogBinding

class BottomDialogMemberMenu : BottomSheetAbs {

    lateinit var binding: BubbleBottomDialogBinding
    lateinit var listener: MemberMenuListener
    lateinit var memberEntity: MemberEntity

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.bubble_bottom_dialog, viewGroup, false)
        return binding.root
    }

    override fun intLayout() {
        binding.apply {
            actionSetNickname.setOnClickListener { listener.changeNickName(memberEntity)}
            actionLeaveGroup.setOnClickListener { listener.leaveGroup(memberEntity)}
            actionPersonalWall.setOnClickListener { listener.navigatePersonalWall(memberEntity)}
            actionRemoveAdmin.setOnClickListener { listener.removeAdmin(memberEntity)}
            actionRemoveFromGroup.setOnClickListener { listener.removeMember(memberEntity)}
            actionSetAdmin.setOnClickListener { listener.setAdmin(memberEntity)}
            actionSendMessage.setOnClickListener { listener.sendMessage(memberEntity)}
        }
    }

    fun setData(memberEntity: MemberEntity) {
        this.memberEntity = memberEntity
        binding.apply {
            actionSetNickname.visibility = View.VISIBLE
            actionPersonalWall.visibility = View.VISIBLE
            actionRemoveAdmin.visibility = View.VISIBLE
            actionRemoveFromGroup.visibility = View.VISIBLE
            actionSetAdmin.visibility = View.VISIBLE
            actionSendMessage.visibility = View.VISIBLE
            actionLeaveGroup.visibility = View.GONE
        }
    }
}