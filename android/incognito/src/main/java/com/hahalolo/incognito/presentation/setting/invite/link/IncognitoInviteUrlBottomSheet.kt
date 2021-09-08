package com.hahalolo.incognito.presentation.setting.invite.link

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahalolo.incognito.databinding.BottomSheetInviteFriendUrlBinding
import com.halo.widget.dialog.BottomSheetDialogBase

class IncognitoInviteUrlBottomSheet constructor(context: Context) : BottomSheetDialogBase(context) {

    lateinit var binding: BottomSheetInviteFriendUrlBinding

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetInviteFriendUrlBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUpDialog(dialog: CustomDialog) {

    }
}