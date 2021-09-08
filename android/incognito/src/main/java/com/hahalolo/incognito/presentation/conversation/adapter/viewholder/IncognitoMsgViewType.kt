package com.hahalolo.incognito.presentation.conversation.adapter.viewholder

import androidx.annotation.IntDef

@IntDef
annotation class IncognitoMsgViewType {
    companion object {
        const val VIEW_TYPE_UNKNOWN     : Int = -1
        const val VIEW_TYPE_EMPTY       : Int = 0

        const val VIEW_TYPE_TEXT_INCOMING        : Int = 1001
        const val VIEW_TYPE_IMAGE_INCOMING       : Int = 1002
        const val VIEW_TYPE_VIDEO_INCOMING       : Int = 1003
        const val VIEW_TYPE_STICKER_INCOMING     : Int = 1004
        const val VIEW_TYPE_GIF_INCOMING         : Int = 1005
        const val VIEW_TYPE_LINK_INCOMING        : Int = 1006
        const val VIEW_TYPE_FILE_INCOMING        : Int = 1007
        const val VIEW_TYPE_REMOVED_INCOMING     : Int = 1008
        const val VIEW_TYPE_REPLAY_INCOMING      : Int = 1009

        const val VIEW_TYPE_TEXT_OUTCOMING        : Int = -1001
        const val VIEW_TYPE_IMAGE_OUTCOMING       : Int = -1002
        const val VIEW_TYPE_VIDEO_OUTCOMING       : Int = -1003
        const val VIEW_TYPE_STICKER_OUTCOMING     : Int = -1004
        const val VIEW_TYPE_GIF_OUTCOMING         : Int = -1005
        const val VIEW_TYPE_LINK_OUTCOMING        : Int = -1006
        const val VIEW_TYPE_FILE_OUTCOMING        : Int = -1007
        const val VIEW_TYPE_REMOVED_OUTCOMING     : Int = -1008
        const val VIEW_TYPE_REPLAY_OUTCOMING      : Int = -1009

        const val VIEW_TYPE_TYPING      : Int = 1010

        const val VIEW_TYPE_DATE        : Int = 1011

        const val VIEW_TYPE_NOTIFICATION: Int = 1012
    }
}