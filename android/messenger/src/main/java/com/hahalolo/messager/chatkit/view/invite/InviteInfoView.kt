package com.hahalolo.messager.chatkit.view.invite

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatInviteViewBinding
import com.hahalolo.pickercolor.util.setVisibility
import com.halo.data.entities.invite.InviteInfo

class InviteInfoView : FrameLayout {
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

    private lateinit var binding: ChatInviteViewBinding

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.chat_invite_view, this, false
        )
        addView(binding.root)
    }

    fun bind(inviteInfo: InviteInfo, joinClick: ()->Unit){
        binding.avatarView.updateListImage(inviteInfo.channelAvatars(), Glide.with(context), null,  "")
        binding.groupNameTv.text = inviteInfo.channelName()
        binding.userNameTv.text = inviteInfo.inviter()?.run{
            "Bạn có 1 lời mời tham gia nhóm từ ${this.memberName()}"
        }?: kotlin.run {
            "Bạn có 1 lời mời tham gia nhóm"
        }
        binding.joinBtn.text = if(inviteInfo.joined) "Đã tham gia" else "Tham gia"
        binding.joinBtn.isEnabled = (!inviteInfo.joined)
        binding.joinBtn.setOnClickListener {
            joinClick.invoke()
        }
    }
}