package com.hahalolo.incognito.presentation.main.owner.update.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahalolo.incognito.databinding.IncognitoUpdateGenderViewBinding
import com.halo.widget.dialog.BottomSheetDialogBase

class IncognitoUpdateGenderView constructor(
    context: Context,
    listener: IncognitoUpdateGenderInterface
) : BottomSheetDialogBase(context) {
    lateinit var binding: IncognitoUpdateGenderViewBinding

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IncognitoUpdateGenderViewBinding.inflate(inflater, container, false)
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
            cancelTv.setOnClickListener {
                dismiss()
            }
        }
    }

}

interface IncognitoUpdateGenderInterface {
    fun onClickMale()
    fun onClickFemale()
    fun onClickOther()
    fun onClickSave()
    fun onClickCancel()
}