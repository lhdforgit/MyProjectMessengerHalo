package com.hahalolo.incognito.presentation.setting.managerfile.link

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahalolo.incognito.databinding.BottomSheetMenuManagerLinkBinding
import com.halo.widget.dialog.BottomSheetDialogBase

class BottomSheetMenuManagerLink constructor(
    context: Context,
    val listener: MenuManagerLinkInterface
) : BottomSheetDialogBase(context) {
    lateinit var binding: BottomSheetMenuManagerLinkBinding

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetMenuManagerLinkBinding.inflate(inflater, container, false)
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
            openLinkTv.setOnClickListener {
                listener.openLink()
                dismiss()
            }
            copyLinkTv.setOnClickListener {
                listener.copyLink()
                dismiss()
            }
            forwardLinkTv.setOnClickListener {
                listener.forwardLink()
                dismiss()
            }
            deleteTv.setOnClickListener {
                listener.deleteLink()
                dismiss()
            }
            cancelTv.setOnClickListener { dismiss() }
        }
    }
}

interface MenuManagerLinkInterface {
    fun openLink()
    fun copyLink()
    fun forwardLink()
    fun deleteLink()
}