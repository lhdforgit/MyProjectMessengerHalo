package com.halo.widget.view

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.halo.widget.R
import com.halo.widget.databinding.EditTextChangeNameDisplayBinding

class EditTextChangeNameDisplay : FrameLayout {
    private lateinit var binding: EditTextChangeNameDisplayBinding
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

    fun setText(name: String): String{
       return binding.contentEdt.setText(name).toString()
    }

    fun getText(): String {
        binding.contentEdt.text?.toString()?.trim()?.let {
            return it
        }
        return ""
    }

    fun setOnEditorActionListener(onEditorActionListener: (Any, Any, Any) -> Boolean) {
        onEditorActionListener.let { listener ->
            binding.contentEdt.setOnEditorActionListener(listener)
        }
    }

    fun getEditText(): EditText? {
        return binding.contentEdt
    }

    fun getContent():String{
        return binding.contentEdt.text?.toString()?.trim()?:""
    }

    fun error(stringRes:Int){
        binding.contentEdt.error = context.getString(stringRes)
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        binding.contentEdt.requestFocus()
        return super.requestFocus(direction, previouslyFocusedRect)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val attribute = context.obtainStyledAttributes(attrs, R.styleable.EditTextChangeNameDisplay)
        title = attribute.getString(R.styleable.EditTextChangeNameDisplay_title) ?: ""
        hint = attribute.getString(R.styleable.EditTextChangeNameDisplay_hint_edit) ?: ""
        attribute.recycle()
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.edit_text_change_name_display,
            null,
            false
        )
        addView(binding.root)
        binding.titleTv.text = title
        binding.contentEdt.hint = hint
    }
}