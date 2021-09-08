/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.bubble.conversation.view.react_detail.listener

interface ChatReactionDetailListener {
    fun onRemoveReactionClick(messageId: String?, type: String)
    fun onDismissListener()
}