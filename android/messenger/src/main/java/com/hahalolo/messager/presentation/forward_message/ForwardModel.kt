package com.hahalolo.messager.presentation.forward_message

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ForwardModel(
    var id: String = "",
    var name: String? = null,
    var avatar: String? = null,
    var status: Int = -1,
) : Parcelable
