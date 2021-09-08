package com.hahalolo.incognito.presentation.conversation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahalolo.incognito.databinding.IncognitoMessageDetailDialogBinding
import com.halo.widget.dialog.BottomSheetDialogBase

class IncognitoMsgDetailDialog(
    val listener: IncognitoMsgDetailListener
) : BottomSheetDialogBase() {
    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding =
            IncognitoMessageDetailDialogBinding.inflate(LayoutInflater.from(context), null, false)
        val customView = binding.root


        return customView
    }

    override fun setUpDialog(dialog: CustomDialog) {

    }
}