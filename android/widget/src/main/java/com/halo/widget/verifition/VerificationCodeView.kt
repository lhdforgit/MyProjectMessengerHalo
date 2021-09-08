/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.verifition

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.MainThread
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.halo.common.utils.KeyboardUtils
import com.halo.widget.R
import kotlinx.android.synthetic.main.verification_code_view.view.*
import java.util.*
import javax.annotation.Nonnull


class VerificationCodeView @JvmOverloads constructor(
    @Nonnull context: Context,
    attr: AttributeSet? = null,
    def: Int = 0
) : FrameLayout(context, attr, def) {
    private val codes: MutableList<AppCompatEditText> = ArrayList(6)

    //private val containers: MutableList<MaterialCardView> = ArrayList(6)
    private var onCodeEnteredListener: (code: String) -> Unit = {}
    private var onSuccessInputCodeListener: (code: String) -> Unit = {}

    init {
        initialize(context)
    }

    fun setOnSuccessInputCodeListener(onSuccessInputCodeListener: (code: String) -> Unit) {
        this.onSuccessInputCodeListener = onSuccessInputCodeListener
    }

    private fun initialize(context: Context) {
        removeAllViews()
        val view = View.inflate(context, R.layout.verification_code_view, null)

        view?.code_zero?.let {
            codes.add(it)
            it.handleInputCode(view)

        }
        view?.code_one?.let {
            codes.add(it)
            it.handleInputCode(view)
        }
        view?.code_two?.let {
            codes.add(it)
            it.handleInputCode(view)
        }
        view?.code_three?.let {
            codes.add(it)
            it.handleInputCode(view)
        }
        view?.code_four?.let {
            codes.add(it)
            it.handleInputCode(view)
        }
        view?.code_five?.let {
            codes.add(it)
            it.handleInputCode(view)
        }

        //card view error ...
        addView(view)
    }

    var isNext = true
    private var isError = false
    private var currentFocus = Pairs(0, isNext) // current focus and  is focus change ...

    private fun AppCompatEditText.handleInputCode(view: View) {

        this.setOnClickListener {
            this.setText("")
            if (TextUtils.isEmpty(this.text.toString())) {
                this.hint = ""
            } else {
                this.hint = "*"
            }
            this.setBackgroundResource(R.drawable.bg_edit_text_vertify_focus_v3)
        }

        this.setOnFocusChangeListener { _v, _ ->
            codes.forEach { _textView ->
                when {
                    _textView.isFocused -> {
                        currentFocus = Pairs(
                            codes.indexOfFirst { it.id == _v.id },
                            true
                        )
                        _textView.hint = ""
                        _textView.setText("")
                        _textView.setBackgroundResource(R.drawable.bg_edit_text_vertify_focus_v3)
                    }
                    TextUtils.isEmpty(_textView.text.toString()) -> {
                        _textView.hint = "*"
                        _textView.setBackgroundResource(R.drawable.bg_edit_text_vertify)
                    }
                    else -> {
                        _textView.setBackgroundResource(R.drawable.bg_edit_text_verify_inputted)
                    }
                }
            }
        }

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                nextText: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (!TextUtils.isEmpty(nextText)) {
                    codes.getOrNull(currentFocus.first)?.hint = ""
                    if (currentFocus.first != (codes.size - 1)) {
                        val textViewNext = codes.getOrNull(currentFocus.first + 1)
                        if (TextUtils.isEmpty(textViewNext?.text?.toString())) {
                            codes.getOrNull(currentFocus.first + 1)?.requestFocus()
                        }
                    } else {
                        onSuccessInputCodeListener.invoke(getVerifyCode())
                        KeyboardUtils.hideSoftInput(view)
                        this@handleInputCode.setBackgroundResource(R.drawable.bg_edit_text_verify_inputted)
                    }
                }
            }
        }

        this.addTextChangedListener(textWatcher)

        setOnKeyListener { _, keyCode, _event ->

            //"setOnKeyListener :... $_event ".Log()
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                isNext = if (isNext) {
                    codes.getOrNull(currentFocus.first)?.hint = "*"
                    if (currentFocus.first != 0) {
                        codes.getOrNull(currentFocus.first - 1)?.setText("")
                        codes.getOrNull(currentFocus.first - 1)?.requestFocus()
                    } else {
                        KeyboardUtils.hideSoftInput(view)
                        this.setBackgroundResource(R.drawable.bg_edit_text_vertify)
                    }
                    false
                } else {
                    true
                }
            }
            return@setOnKeyListener false
        }
    }

    class Pairs(var first: Int, var second: Boolean)

    private fun isValidInputNumber(key: Int): Boolean {
        return (key != KeyEvent.KEYCODE_DEL) and (key != KeyEvent.KEYCODE_ENTER)
    }

    private fun CharSequence.getIndex(): Int {
        var index = 0
        try {
            index = this.toString().toInt()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return index
    }

    fun setVerifyCodeError() {
        isError = true
        codes.forEach { _textView ->
            _textView.background =
                ContextCompat.getDrawable(context, R.drawable.bg_edit_text_verify_error)
        }
    }

    private fun AppCompatEditText.removeFocus() {
        this.isFocusableInTouchMode = false
        this.isFocusable = false
        this.isFocusableInTouchMode = true
        this.isFocusable = true
        //Todo : thanh continue ...
        this.clearFocus()
        this.requestFocus()
    }

    private fun checkEmptyVerifyCode(): Boolean {
        var isEmpty = true
        codes.forEach { _textView ->
            if (!TextUtils.isEmpty(_textView.text.toString())) {
                isEmpty = false
            }
        }
        return isEmpty
    }

    fun getVerifyCode(): String {
        var resultCode = ""
        codes.forEach { _tv ->
            resultCode += _tv.text
        }
        return resultCode
    }

    @MainThread
    fun setOnCompleteListener(onCodeEnteredListener: (code: String) -> Unit) {
        this.onCodeEnteredListener = onCodeEnteredListener
    }


    companion object {
        private var MaterialCardView_Radius = 10f
        private var MaterialCardView_StrokeWidth = 1f
    }
}

