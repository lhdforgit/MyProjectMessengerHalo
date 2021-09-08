package com.hahalolo.incognito.presentation.setting.member.list.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahalolo.incognito.databinding.BottomSheetMenuMemberBinding
import com.halo.widget.dialog.BottomSheetDialogBase

class BottomSheetMenuMember constructor(context: Context) : BottomSheetDialogBase(context) {

    lateinit var binding: BottomSheetMenuMemberBinding

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetMenuMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUpDialog(dialog: CustomDialog) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}