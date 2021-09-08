package com.halo.widget.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.halo.widget.R
import com.halo.widget.databinding.EditTextChangePasswordBinding

class EditTextChangePassword : FrameLayout {
    private var binding: EditTextChangePasswordBinding? = null
    private var title = ""
    private var hint = ""

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context, attrs)
    }

    fun getText(): String {
        binding?.contentEdt?.text?.toString()?.trim()?.let {
            return it
        }
        return ""
    }

    fun setOnEditorActionListener(onEditorActionListener: TextView.OnEditorActionListener) {
        onEditorActionListener.let { listener ->
            binding?.contentEdt?.setOnEditorActionListener(listener)
        }
    }

    fun getEditText(): EditText? {
        return binding?.contentEdt
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val attribute = context.obtainStyledAttributes(attrs, R.styleable.EditTextChangePassword)
        title = attribute.getString(R.styleable.EditTextChangePassword_title) ?: ""
        hint = attribute.getString(R.styleable.EditTextChangePassword_hint_edit) ?: ""
        attribute.recycle()
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.edit_text_change_password,
            null,
            false
        )
        addView(binding!!.root)
        binding?.titleTv?.text = title
        binding?.contentEdt?.hint = hint
    }
}