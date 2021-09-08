package com.hahalolo.messager.bubble.conversation.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahalolo.messager.bubble.base.BottomSheetAbs
import com.halo.data.room.entity.ChannelEntity
import com.hahalolo.messenger.databinding.ChatGroupAvatarMenuBinding
import com.halo.common.utils.ThumbImageUtils
import com.halo.data.entities.media.MediaEntity

class ChatGroupAvatarMenuView : BottomSheetAbs {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    lateinit var binding: ChatGroupAvatarMenuBinding

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup): View {
        binding = ChatGroupAvatarMenuBinding.inflate(LayoutInflater.from(context), viewGroup, false)
        return binding.root
    }

    override fun intLayout() {

    }

    fun updateRoomEntity(room: ChannelEntity, listener: Listener) {
        binding.apply {
            viewAvatarBt.visibility = if (room.isHaveAvatarGroup()) View.VISIBLE else View.GONE
            viewMemberBt.setOnClickListener {
                listener.navigateChatMessageMember(room.channelId())
            }
            changeAvatarBt.setOnClickListener {
                listener.navigateChangeGroupAvatar()
            }
            viewAvatarBt.setOnClickListener {
                listener.navigateViewAvatar(
                    mutableListOf(
                        MediaEntity(
                            path = ThumbImageUtils.replaceThumbUrl(
                                room.getAvatarGroup()
                            )
                        )
                    )
                )
            }
        }
    }

    interface Listener {
        fun navigateViewAvatar(medias: MutableList<MediaEntity>)

        fun navigateChangeGroupAvatar()

        fun navigateChatMessageMember(roomId: String)

        fun dismiss()
    }
}