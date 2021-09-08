package com.hahalolo.incognito.presentation.setting.managerfile.file

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.BottomSheetMenuManagerDocBinding
import com.hahalolo.incognito.presentation.setting.model.ManagerDocModel
import com.halo.widget.dialog.BottomSheetDialogBase

class BottomSheetMenuManagerDoc constructor(context: Context, val listener : MenuManagerDocListener, val item: ManagerDocModel) : BottomSheetDialogBase(context) {
    lateinit var binding: BottomSheetMenuManagerDocBinding

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetMenuManagerDocBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setUpDialog(dialog: CustomDialog) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
        setHeader()
    }

    private fun initAction() {
        binding.apply {
            downloadTv.setOnClickListener {
                listener.downloadFile()
                dismiss()
            }
            forwardTv.setOnClickListener {
                listener.forwardFile()
                dismiss()
            }
            deleteTv.setOnClickListener {
                listener.deleteFile()
                dismiss()
            }
            cancelTv.setOnClickListener {
                dismiss()
            }
        }
    }
    private fun getDrawableType(type: Int): Int {
        return when (type) {
            1 -> R.drawable.ic_incognito_manager_file_pdf
            2 -> R.drawable.ic_incognito_manager_file_xls
            3 -> R.drawable.ic_incognito_manager_file_doc
            else -> R.drawable.ic_incognito_manager_file_doc
        }
    }
    private fun setHeader(){
        binding.apply {
            docItem.titleTv.text = item.name ?: ""
            docItem.sizeFileTv.text = item.size ?: ""
            docItem.usernameTv.text = item.userName ?: ""
            docItem.avatarImg.setBackgroundResource(getDrawableType(item.type ?: 0))
        }
    }
}

interface MenuManagerDocListener{
    fun downloadFile()
    fun forwardFile()
    fun deleteFile()
}