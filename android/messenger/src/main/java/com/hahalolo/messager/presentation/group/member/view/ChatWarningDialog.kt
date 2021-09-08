/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.member.view

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatWarningDialogBinding
import com.halo.common.utils.SizeUtils
import com.halo.common.utils.SpanBuilderUtil
import com.halo.data.common.utils.Strings
import com.halo.widget.HaloTypefaceSpan
import com.halo.widget.dialog.DialogFragmentBase


class ChatWarningDialog(builder: ChatWarningDialogBuilder) : DialogFragmentBase() {
    private var title: String? = null
    private var logo: Int? = null
    private var description: String? = null
    private var descriptionSub: String? = null
    private var content: String? = null
    private var btnCancelText: String? = null
    private var btnSubmitText: String? = null
    private var btnOptionText: String? = null
    private var enableOption: Boolean = false
    private var enableCancel: Boolean = true
    private var enableEdt: Boolean = false
    private var submitType: Int? = null
    private var optionType: Int? = null
    private var listener: ChatWarningDialogBuilder.ChatWarningDialogListener? = null

    init {
        this.title = builder.title
        this.logo = builder.logo
        this.description = builder.description
        this.descriptionSub = builder.descriptionSub
        this.content = builder.content
        this.btnCancelText = builder.btnCancelText
        this.btnSubmitText = builder.btnSubmitText
        this.btnOptionText = builder.btnOptionText
        this.enableOption = builder.enableOption
        this.enableEdt = builder.enableEdt
        this.enableCancel = builder.enableCancel
        this.submitType = builder.submitType
        this.optionType = builder.optionType
        this.listener = builder.listener
    }

    private fun ChatWarningDialogBinding.initActionLayout() {
        context?.apply {
            val fontSpan = HaloTypefaceSpan.REGULAR(this)
            createButton(submitType ?: 1)?.let {
                it.text = SpanBuilderUtil().append(
                    btnSubmitText ?: getString(R.string.chat_message_change_nick_name_save),
                    fontSpan
                ).build()

                it.setOnClickListener {
                    contentEdt.text?.toString()?.let { nickName ->
                        listener?.onSubmit(nickName)
                    }
                    dismiss()
                }
                it.id = R.id.chat_warning_submit_bt
                groupAction.addView(it)
            }
            if (enableOption) {
                createButton(optionType ?: 3)?.let {
                    it.text = SpanBuilderUtil().append(btnOptionText, fontSpan).build()
                    it.setOnClickListener {
                        listener?.onOtherOption()
                        dismiss()
                    }
                    groupAction.addView(it)
                }
            }
            if (enableCancel) {
                createButton(3)?.let {
                    it.text = SpanBuilderUtil().append(
                        btnCancelText ?: getString(R.string.chat_message_change_nick_name_cancel),
                        fontSpan
                    ).build()
                    it.setOnClickListener {
                        dismiss()
                    }
                    groupAction.addView(it)
                }
            }
        }
    }

    private fun createButton(type: Int): AppCompatButton? {
        context?.run {
            val btn = AppCompatButton(this)
            btn.let { button ->
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).run {
                    val margin = SizeUtils.dp2px(16.0f)
                    setMargins(0, 0, 0, margin)
                    button.layoutParams = this

                }

                button.isAllCaps = false
                ButtonCustom.getByType(type)?.let { style ->
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        button.stateListAnimator = AnimatorInflater.loadStateListAnimator(context,
                            R.animator.halo_btn_state_list_anim)
                    }
                    button.setBackgroundResource(style.background())
                    button.backgroundTintList = ContextCompat.getColorStateList(
                        this@run, style.backgroundTint()
                    )
                    button.setTextColor(
                        ContextCompat.getColorStateList(
                            this@run,
                            style.textColor()
                        )
                    )
                }
                return button
            }
        }
        return null
    }

    private fun ChatWarningDialogBinding.initAction(view: View) {
        contentEdt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                checkStatusSaveBt(s?.toString(), view)
            }
        })
    }

    private fun ChatWarningDialogBinding.initView(context: Context, view: View) {
        descriptionTv.text = description
        descriptionSubTv.text = descriptionSub
        descriptionSubTv.visibility =
            if (TextUtils.isEmpty(descriptionSub)) View.GONE else View.VISIBLE
        contentEdt.setText(content)
        Strings.checkMoreSpace(contentEdt)
        contentEdt.visibility = if (enableEdt) View.VISIBLE else View.GONE
        if (!enableEdt) {
            checkStatusSaveBt("enableSave", view)
        } else {
            checkStatusSaveBt(content, view)
        }
    }

    private fun checkStatusSaveBt(content: String?, view: View) {
        val button = view.findViewById<AppCompatButton>(R.id.chat_warning_submit_bt)
        content?.takeIf { it.isNotEmpty() }?.run {
            button?.isEnabled = true
            button?.alpha = 1.0f
        } ?: kotlin.run {
            button?.isEnabled = false
            button?.alpha = 0.5f
        }
    }

    override fun onCreateHeaderView(header: HeaderDialog?): View? {
        header?.run {
            setTitle(title ?: "")
            setLogo(logo ?: R.drawable.ic_chat_dialog_warning)
            return getView()
        }
        return null
    }

    override fun onCreateCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context?.apply {
            val binding = DataBindingUtil.inflate<ChatWarningDialogBinding>(
                LayoutInflater.from(this),
                R.layout.chat_warning_dialog,
                container,
                false
            )
            binding.initView(this, binding.root)
            binding.initAction(binding.root)
            binding.initActionLayout()
            return binding.root
        }
        return null
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
}

class ChatWarningDialogBuilder {
    var title: String? = null
    var logo: Int? = null
    var description: String? = null
    var descriptionSub: String? = null
    var content: String? = null
    var btnCancelText: String? = null
    var enableCancel: Boolean = true
    var btnSubmitText: String? = null
    var btnOptionText: String? = null
    var enableOption: Boolean = false
    var enableEdt: Boolean = false
    var submitType: Int? = null
    var optionType: Int? = null
    var listener: ChatWarningDialogListener? = null

    constructor() {}
    constructor(
        title: String?,
        logo: Int?,
        description: String?,
        descriptionSub: String?,
        content: String?,
        btnCancelText: String?,
        enableCancel: Boolean,
        btnSubmitText: String?,
        btnOptionText: String?,
        enableOption: Boolean,
        enableEdt: Boolean,
        submitType: Int?,
        optionType: Int?,
        listener: ChatWarningDialogListener?
    ) {
        this.title = title
        this.logo = logo
        this.description = description
        this.descriptionSub = descriptionSub
        this.content = content
        this.btnCancelText = btnCancelText
        this.enableCancel = enableCancel
        this.btnSubmitText = btnSubmitText
        this.btnOptionText = btnOptionText
        this.enableOption = enableOption
        this.enableEdt = enableEdt
        this.submitType = submitType
        this.optionType = optionType
        this.listener = listener
    }

    fun withTitle(title: String?): ChatWarningDialogBuilder {
        this.title = title
        return this
    }

    fun withLogo(@DrawableRes logo: Int?): ChatWarningDialogBuilder {
        this.logo = logo
        return this
    }

    fun withDescription(description: String?): ChatWarningDialogBuilder {
        this.description = description
        return this
    }

    fun withDescriptionSub(descriptionSub: String?): ChatWarningDialogBuilder {
        this.descriptionSub = descriptionSub
        return this
    }

    fun withContent(content: String?): ChatWarningDialogBuilder {
        this.content = content
        return this
    }

    fun withCancelTextButton(btnCancelText: String?): ChatWarningDialogBuilder {
        this.btnCancelText = btnCancelText
        return this
    }

    fun withSubmitTextButton(btnSubmitText: String?): ChatWarningDialogBuilder {
        this.btnSubmitText = btnSubmitText
        return this
    }

    fun withOptionTextButton(btnOptionText: String?): ChatWarningDialogBuilder {
        this.btnOptionText = btnOptionText
        return this
    }

    fun withListener(listener: ChatWarningDialogListener?): ChatWarningDialogBuilder {
        this.listener = listener
        return this
    }

    fun withIsVisibilityOptionButton(enableOption: Boolean): ChatWarningDialogBuilder {
        this.enableOption = enableOption
        return this
    }

    fun withIsVisibilityCancelButton(enableCancel: Boolean): ChatWarningDialogBuilder {
        this.enableCancel = enableCancel
        return this
    }

    fun withIsVisibilityEditText(enableEdt: Boolean): ChatWarningDialogBuilder {
        this.enableEdt = enableEdt
        return this
    }

    fun withSubmitButtonType(submitType: Int?): ChatWarningDialogBuilder {
        this.submitType = submitType
        return this
    }

    fun withOptionButtonType(optionType: Int?): ChatWarningDialogBuilder {
        this.optionType = optionType
        return this
    }

    fun build(): ChatWarningDialog {
        return ChatWarningDialog(this)
    }

    fun setDisableDismiss() {
        build().isCancelable = false
    }

    interface ChatWarningDialogListener {
        fun onSubmit(newContent: String)
        fun onOtherOption() {}
    }
}

enum class ButtonCustom(val type: Int) {
    BUTTON_PRIMARY(ButtonType.PRIMARY) {
        override fun background(): Int {
            return R.drawable.bg_chat_dialog_bt_primary
        }

        override fun backgroundTint(): Int {
            return R.color.messenger_dialog_btn_bg_primary_selector
        }

        override fun textColor(): Int {
            return R.color.messenger_text_primary
        }

    },
    BUTTON_ERROR(ButtonType.ERROR) {
        override fun background(): Int {
            return R.drawable.bg_chat_dialog_bt_error
        }

        override fun backgroundTint(): Int {
            return R.color.messenger_dialog_btn_bg_error_selector

        }

        override fun textColor(): Int {
            return R.color.messenger_text_notice
        }

    },
    BUTTON_TEXT(ButtonType.TEXT) {
        override fun background(): Int {
            return R.color.transparent
        }

        @SuppressLint("PrivateResource")
        override fun backgroundTint(): Int {
            return R.color.mtrl_btn_text_btn_bg_color_selector
        }

        override fun textColor(): Int {
            return R.color.messenger_text_dark
        }
    };

    abstract fun background(): Int
    abstract fun backgroundTint(): Int
    abstract fun textColor(): Int

    companion object {
        private val values = values()
        fun getByType(value: Int) = values.firstOrNull { it.type == value }
    }
}