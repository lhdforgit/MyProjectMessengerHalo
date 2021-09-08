package com.hahalolo.incognito.presentation.main.owner.update.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahalolo.incognito.databinding.IncognitoStatusViewBinding
import com.halo.widget.dialog.BottomSheetDialogBase

class IncognitoStatusView constructor(
    context: Context,
    private val listener: IncognitoStatusInterface
) : BottomSheetDialogBase(context) {
    lateinit var binding: IncognitoStatusViewBinding

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = IncognitoStatusViewBinding.inflate(inflater, container, false)
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

interface IncognitoStatusInterface {
    fun onClickLive()
    fun onClickAbsent()
    fun onClickBusy()
}