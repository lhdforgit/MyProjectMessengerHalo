package com.hahalolo.incognito.presentation.main.owner.update.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahalolo.incognito.databinding.IncognitoUpdateAvatarViewBinding
import com.halo.widget.dialog.BottomSheetDialogBase

class IncognitoUpdateAvatarView constructor(
    context: Context,
    private val listener: IncognitoUpdateAvatarInterface
): BottomSheetDialogBase(context){
    lateinit var binding: IncognitoUpdateAvatarViewBinding

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IncognitoUpdateAvatarViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUpDialog(dialog: CustomDialog) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
       binding.apply {
           avatarView.setOnClickListener {
               listener.onClickAvatar()
           }
       }
    }

}

interface IncognitoUpdateAvatarInterface{
    fun onClickAvatar()
    fun onClickUpdateAvatarView()
    fun onClickCancel()
}