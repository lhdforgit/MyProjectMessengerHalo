package com.halo.widget.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.halo.widget.R
import com.halo.widget.databinding.HaloDialogCustomBinding

class HaloDialogCustom(builder: HaloDialogCustomBuilder) : DialogFragmentBase() {

    private var title: String? = null
    private var icon: Int? = null
    private var description: String? = null
    private var descriptionStyle: CharSequence? = null
    private var onClickPrimary: View.OnClickListener? = null
    private var onClickSuccess: View.OnClickListener? = null
    private var onClickWarning: View.OnClickListener? = null
    private var onClickCancel: View.OnClickListener? = null
    private var textPrimary: String? = null
    private var textSuccess: String? = null
    private var textWarning: String? = null
    private var textCancel: String? = null
    private var customView: View? = null

    init {
        this.title = builder.title
        this.icon = builder.icon
        this.description = builder.description
        this.descriptionStyle = builder.descriptionStyle
        this.onClickPrimary = builder.onClickPrimary
        this.onClickSuccess = builder.onClickSuccess
        this.onClickWarning = builder.onClickWarning
        this.onClickCancel = builder.onClickCancel
        this.textPrimary = builder.textPrimary
        this.textSuccess = builder.textSuccess
        this.textWarning = builder.textWarning
        this.textCancel = builder.textCancel
        this.customView = builder.customView
    }

    override fun onCreateHeaderView(header: HeaderDialog?): View? {
        header?.run {
            setTitle(title ?: "")
            setLogo(icon ?: R.drawable.ic_chat_dialog_warning)
            return getView()
        }
        return null
    }

    override fun onCreateCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<HaloDialogCustomBinding>(
            inflater,
            R.layout.halo_dialog_custom,
            container,
            false
        )
        binding?.initView()
        binding?.intAction()
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(context).show()
        /**
         * fix bug dialog can not show keyboard
         * https://stackoverflow.com/questions/9102074/android-edittext-in-dialog-doesnt-pull-up-soft-keyboard
         * AlertDialog is automatically setting the flag, that things don't trigger a soft input to show up.
         * **/
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        return dialog
    }

    private fun HaloDialogCustomBinding.initView() {
        descriptionStyle?.takeIf { it.isNotEmpty() }?.let {
            descriptionTv.text = it
        } ?: kotlin.run {
            description?.takeIf { it.isNotEmpty() }?.let {
                descriptionTv.text = it
            } ?: kotlin.run {
                descriptionTv.isVisible = false
            }
        }
        textPrimary?.takeIf { it.isNotEmpty() }?.let {
            primaryBt.text = it
        } ?: kotlin.run {
            primaryBt.text = getString(R.string.chat_message_change_nick_name_save)
        }
        textSuccess?.takeIf { it.isNotEmpty() }?.let {
            successBt.text = it
        } ?: kotlin.run {
            successBt.text = "no content"
        }
        textWarning?.takeIf { it.isNotEmpty() }?.let {
            warningBt.text = it
        } ?: kotlin.run {
            warningBt.text = "no content"
        }
        textCancel?.takeIf { it.isNotEmpty() }?.let {
            cancelBt.text = it
        } ?: kotlin.run {
            cancelBt.text = getString(R.string.chat_message_change_nick_name_cancel)
        }
        customView?.let { item ->
            contentView.addView(item)
        } ?: kotlin.run {
            contentView.isVisible = false
        }
    }

    private fun HaloDialogCustomBinding.intAction() {
        onClickPrimary?.let { listener ->
            primaryBt.setOnClickListener {
                listener.onClick(it)
                dismiss()
            }
        } ?: kotlin.run {
            primaryBt.isVisible = false
        }
        onClickSuccess?.let { listener ->
            successBt.setOnClickListener {
                listener.onClick(it)
                dismiss()
            }
        } ?: kotlin.run {
            successBt.isVisible = false
        }
        onClickWarning?.let { listener ->
            warningBt.setOnClickListener {
                listener.onClick(it)
                dismiss()
            }
        } ?: kotlin.run {
            warningBt.isVisible = false
        }
        onClickCancel?.let { listener ->
            cancelBt.setOnClickListener {
                listener.onClick(it)
                dismiss()
            }
        } ?: kotlin.run {
            cancelBt.isVisible = false
        }
    }

    class Builder : HaloDialogCustomBuilder() {
        override fun setIcon(): HaloDialogCustomBuilder {
            return this
        }

        override fun build(): HaloDialogCustom {
            return HaloDialogCustom(this)
        }
    }

    class BuilderSuccess : HaloDialogCustomBuilder() {
        override fun setIcon(): HaloDialogCustomBuilder {
            setIcon(R.drawable.ic_dialog_header_success)
            return this
        }

        override fun build(): HaloDialogCustom {
            return HaloDialogCustom(this.setIcon())
        }
    }

    class BuilderEdit : HaloDialogCustomBuilder() {
        override fun setIcon(): HaloDialogCustomBuilder {
            setIcon(R.drawable.ic_chat_dialog_edit)
            return this
        }

        override fun build(): HaloDialogCustom {
            return HaloDialogCustom(this.setIcon())
        }
    }

    class BuilderDelete : HaloDialogCustomBuilder() {
        override fun setIcon(): HaloDialogCustomBuilder {
            setIcon(R.drawable.ic_chat_dialog_delete)
            return this
        }

        override fun build(): HaloDialogCustom {
            return HaloDialogCustom(this.setIcon())
        }
    }

    class BuilderCancel : HaloDialogCustomBuilder() {
        override fun setIcon(): HaloDialogCustomBuilder {
            setIcon(R.drawable.ic_request_failed)
            return this
        }

        override fun build(): HaloDialogCustom {
            return HaloDialogCustom(this.setIcon())
        }
    }
}