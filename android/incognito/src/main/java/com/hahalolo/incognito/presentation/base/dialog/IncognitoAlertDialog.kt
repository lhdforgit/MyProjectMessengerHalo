package com.hahalolo.incognito.presentation.base.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.setPadding
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoArlertDialogBinding
import com.halo.common.utils.SizeUtils

class IncognitoAlertDialog : Dialog {
    constructor(context: Context) : super(context, R.style.Incognito_Dialog) {
        initView()
    }

    constructor(context: Context, themeResId: Int) : super(context, R.style.Incognito_Dialog) {
        initView()
    }

    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener) {
        initView()
    }

    private lateinit var binding: IncognitoArlertDialogBinding

    private fun initView() {
        binding = IncognitoArlertDialogBinding.inflate(LayoutInflater.from(context), null, false)
        setContentView(binding.root)
        window?.setBackgroundDrawable(null)
    }

    private fun initDialog(
        icon: Int,
        title: String,
        content: String,
        btns: MutableList<Triple<Int, String, (() -> Unit)?>>,
        cancelClick: (() -> Unit)?,
        cancelable: Boolean = true
    ): IncognitoAlertDialog {
        binding.dialogIcon.setImageResource(icon)
        binding.dialogTitle.text = title
        binding.dialogDescription.text = content
        binding.dialogIcon.setImageResource(icon)
        binding.btnGr.removeAllViews()
        btns.takeIf { it.isNotEmpty() }?.forEach { tripleBtn ->
//            addButton(tripleBtn.first, tripleBtn.second, tripleBtn.third)
        }
        this.setOnCancelListener {
            cancelClick?.invoke()
        }
        this.setCancelable(cancelable)
        return this
    }

    private fun addButton(type: Int, name: String, onClick: (() -> Unit)?) {
        val btn = AppCompatTextView(context)
        val style = when (type) {
            1 -> {
                R.style.Incognito_TextView_H5_Primary_Normal
            }
            2 -> {
                R.style.Incognito_TextView_H5_Notice_Normal
            }
            else -> {
                R.style.Incognito_TextView_H5_Normal
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btn.setTextAppearance(style)
        } else {
            btn.setTextAppearance(context, style)
        }
        btn.gravity = Gravity.CENTER_HORIZONTAL
        btn.text = name
        btn.setPadding(SizeUtils.dp2px(16f))
        btn.setBackgroundResource(R.drawable.bg_incognito_button_dialog)
        btn.setOnClickListener {
            onClick?.invoke()
            dismiss()
        }
        binding.btnGr.addView(
            btn,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
    }

    class Builder(private val context: Context) {
        private var icon: Int = R.drawable.ic_incognito_delete_icon_dialog
        private var title: String = ""
        private var content: String = ""
        private val btns = mutableListOf<Triple<Int, String, (() -> Unit)?>>()
        private var cancelClick: (() -> Unit)? = null
        private var cancelable: Boolean = true

        fun icon(icon: Int): Builder {
            this.icon = icon
            return this
        }

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun title(titleId: Int): Builder {
            this.title = context.getString(titleId)
            return this
        }

        fun content(content: String): Builder {
            this.content = content
            return this
        }

        fun content(contentId: Int): Builder {
            this.content = context.getString(contentId)
            return this
        }

        fun addPrimaryBtn(name: String, onClick: (() -> Unit)? = null): Builder {
            btns.add(Triple(1, name, onClick))
            return this
        }

        fun addNoticeBtn(name: String, onClick: (() -> Unit)? = null): Builder {
            btns.add(Triple(2, name, onClick))
            return this
        }

        fun addBtn(name: String, onClick: (() -> Unit)? = null): Builder {
            btns.add(Triple(3, name, onClick))
            return this
        }

        fun addPrimaryBtn(stringId: Int, onClick: () -> Unit): Builder {
            btns.add(Triple(1, context.getString(stringId), onClick))
            return this
        }

        fun addNoticeBtn(stringId: Int, onClick: () -> Unit): Builder {
            btns.add(Triple(2, context.getString(stringId), onClick))
            return this
        }

        fun addBtn(stringId: Int, onClick: () -> Unit): Builder {
            btns.add(Triple(3, context.getString(stringId), onClick))
            return this
        }

        fun cancelClick(onClick: () -> Unit): Builder {
            cancelClick = onClick
            return this
        }

        fun cancelable(enable: Boolean): Builder {
            cancelable = enable
            return this
        }

        fun build(): IncognitoAlertDialog {
            val dialog = IncognitoAlertDialog(context)
            dialog.initDialog(
                icon = icon,
                title = title,
                content = content,
                btns = btns,
                cancelClick = cancelClick,
                cancelable = cancelable
            ).show()
            return dialog
        }
    }


}