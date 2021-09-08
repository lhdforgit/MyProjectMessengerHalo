package com.hahalolo.incognito.presentation.main.owner.update.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahalolo.incognito.databinding.IncognitoDisplayViewBinding
import com.halo.widget.dialog.BottomSheetDialogBase

class IncognitoDisplayView constructor(
    context: Context,
    listener: IncognitoDisplayInterface
): BottomSheetDialogBase(context){
    lateinit var binding: IncognitoDisplayViewBinding

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IncognitoDisplayViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUpDialog(dialog: CustomDialog) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {

    }
}

interface IncognitoDisplayInterface{
    fun onClickshining()
    fun onClickDark()
}