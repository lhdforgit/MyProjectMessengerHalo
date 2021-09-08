package com.hahalolo.incognito.presentation.conversation.view

import android.content.Context
import android.util.AttributeSet
import com.hahalolo.incognito.presentation.conversation.view.mention.IncognitoMentionEditText

class IncognitoMessageEditText: IncognitoMentionEditText {
    fun clearFocusWhenEmpty() {
        if (!(text?.toString()?.trim()?.isNotEmpty()==true)){
            setText("")
            clearFocus()
        }
    }

    fun getTextSend(): String {
        return text?.toString()?.trim()?:""
    }

    fun clearTextInput() {
        setText("")
    }

    constructor(context: Context?) : super(context){initView()}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){initView()}

    private fun initView(){

    }
}