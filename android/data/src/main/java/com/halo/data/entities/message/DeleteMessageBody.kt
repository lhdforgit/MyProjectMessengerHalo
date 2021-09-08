package com.halo.data.entities.message

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class DeleteMessageBody(
    var token : String? = null,
    var workspaceId: String? = null,
    var channelId: String? = null,
    var messageId: String? = null,
    val revoke: Boolean = false
)