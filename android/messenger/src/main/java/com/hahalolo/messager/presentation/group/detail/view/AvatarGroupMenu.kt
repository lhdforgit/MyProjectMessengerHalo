package com.hahalolo.messager.presentation.group.detail.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatGroupAvatarMenuBinding
import com.halo.widget.dialog.BottomSheetDialogBase

class AvatarGroupMenu(val listener: AvatarGroupMenuListener?, private val isShowMember: Boolean, private val isHaveAvatar : Boolean) :
    BottomSheetDialogBase() {
    interface AvatarGroupMenuListener {
        fun navigateAvatarView()
        fun navigateUpdateAvatar()
        fun navigateViewMember()
    }

    private var binding: ChatGroupAvatarMenuBinding? = null


    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.chat_group_avatar_menu,
            container,
            false
        )
        return binding?.root
    }

    override fun setUpDialog(dialog: CustomDialog) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
        binding?.apply {
            viewAvatarBt.setOnClickListener {
                listener?.navigateAvatarView()
                dismiss()
            }
            changeAvatarBt.setOnClickListener {
                listener?.navigateUpdateAvatar()
                dismiss()
            }
            viewMemberBt.setOnClickListener {
                listener?.navigateViewMember()
                dismiss()
            }
            viewAvatarBt.visibility = if(isHaveAvatar) View.VISIBLE else View.GONE
            viewMemberBt.visibility = if (isShowMember) View.VISIBLE else View.GONE
        }
    }
}
