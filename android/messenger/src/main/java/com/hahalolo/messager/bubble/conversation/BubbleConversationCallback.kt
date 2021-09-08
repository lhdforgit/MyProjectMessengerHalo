package com.hahalolo.messager.bubble.conversation

import com.hahalolo.messager.bubble.conversation.dialog.HaloDialogCustomView
import com.hahalolo.messager.bubble.conversation.view.ChatGroupAvatarMenuView
import com.hahalolo.messager.bubble.conversation.view.react_detail.bubble.BubbleBottomReactDetailView
import com.hahalolo.messager.bubble.conversation.view.reader_detail.bubble.BubbleBottomReaderDetailView

interface BubbleConversationCallback{

    fun hideDetailRoom()

    fun getDialog() : HaloDialogCustomView?

    fun getBottomReactDetail() : BubbleBottomReactDetailView?

    fun getBottomReaderDetail() : BubbleBottomReaderDetailView?

    fun getAvatarGroupMenu() : ChatGroupAvatarMenuView?
}