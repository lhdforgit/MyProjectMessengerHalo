package com.hahalolo.messager.presentation.main.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatDiaglogMenuConversationBinding
import com.halo.widget.dialog.DialogFragmentBase

class MenuConversationDialog(context: Context) : DialogFragmentBase(context) {
    private var binding: ChatDiaglogMenuConversationBinding? = null
    private var listener: MenuConversationListener? = null

    fun setListener(listener: MenuConversationListener?) {
        this.listener = listener
    }

    override fun onCreateHeaderView(header: HeaderDialog?): View? {
        header?.setLogo(R.drawable.ic_conversation_dialog_header)
        header?.setTitle(getString(R.string.chat_message_conversation_title))
        return header?.getView()
    }

    override fun onCreateCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.chat_diaglog_menu_conversation, container, false)
        binding?.notiConversationTv?.text =
            if (listener?.isEnableNotify() == true) getString(R.string.chat_message_status_notify_off_title) else getString(R.string.chat_message_status_notify_on_title)
        initAction()
        binding?.isGroup = listener?.isGroup()
        return binding?.root
    }

    private fun initAction() {
        binding?.apply {
            notiConversationTv.setOnClickListener {
                listener?.onNotification()
                dismiss()
            }
            addMemberTv.setOnClickListener {
                listener?.onAddMember()
                dismiss()
            }
            leaveGroupTv.setOnClickListener {
                listener?.onLeaveGroup()
                dismiss()
            }
            removeConversationTv.setOnClickListener {
                listener?.onRemoveConversation()
                dismiss()
            }
            listMemberTv.setOnClickListener {
                listener?.onMemberDetail()
                dismiss()
            }
        }
    }
}