package com.hahalolo.incognito.presentation.conversation.adapter

import com.hahalolo.incognito.presentation.conversation.adapter.model.IncognitoMsgModel

interface IncognitoConversationListener {
    fun getLanguageCode(): String

    fun onClickMessage(incognitoMsgModel: IncognitoMsgModel)
}