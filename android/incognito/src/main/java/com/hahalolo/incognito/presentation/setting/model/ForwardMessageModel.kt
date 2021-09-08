package com.hahalolo.incognito.presentation.setting.model
data class ForwardMessageModel(
    val id : String? = null,
    val typeMsg: Int? = null,
    val userName: String? = null,
    val userAvatar: String? = null,
    val time: String? = null,
    val contentMsg: String? = null,
    val attachments: List<String>
)
