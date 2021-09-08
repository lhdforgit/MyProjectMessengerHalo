package com.hahalolo.messager.presentation.main.contacts.owner.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.OwnerStatusDialogBinding

import com.halo.widget.dialog.DialogFragmentBase

class OwnerStatusDialog(contect: Context) : DialogFragmentBase(contect) {

    private var binding: OwnerStatusDialogBinding? = null
    private var listener: OwnerStatusListener? = null

    fun setListener(listener: OwnerStatusListener?) {
        this.listener = listener
    }

    override fun onCreateHeaderView(header: HeaderDialog?): View? {
        header?.setLogo(R.drawable.ic_status)
        header?.setTitle(getString(R.string.chat_message_owner_status))
        return header?.getView()
    }

    override fun onCreateCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.owner_status_dialog, container, false)
        initAction()

        return binding?.root
    }

    private fun initAction() {
        binding?.apply {
            statusOnlineTv.setOnClickListener {
                listener?.onClickActive()
                dismiss()
            }
            statusAbsentTv.setOnClickListener {
                listener?.onClickAbsent()
                dismiss()
            }
            statusBusyTv.setOnClickListener {
                listener?.onClickBusy()
                dismiss()
            }
            statusUpdateTv.setOnClickListener {
                listener?.onClickUpdateStatus()
//                dismiss()
            }
        }
    }
}