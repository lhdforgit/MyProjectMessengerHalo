package com.hahalolo.messager.bubble.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class BubbleInfo(val bubbleId: String,
                 val title: String,
                 val message:String,
                 val avatarUrl: String?,
                 val isPrivate: Boolean = false ,
                 var channelId: String ): Parcelable {
}
