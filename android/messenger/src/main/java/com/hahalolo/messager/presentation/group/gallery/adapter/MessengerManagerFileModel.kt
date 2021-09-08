package com.hahalolo.messager.presentation.group.gallery.adapter

import android.os.Parcelable
import com.halo.data.entities.attachment.Attachment
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class MessengerManagerFileModel(
    var id: String? = null,
    var url: String? = null,
    var name: String? = null,
    var timeSend: Long? = null,
    var sizeFile: Long? = null,
    var typeFile: String = ManagerFileType.OTHER_TYPE
) : Parcelable {

    companion object {
        fun convertToMediaModel(attachment: Attachment): MessengerManagerFileModel {
            val result = MessengerManagerFileModel()
            result.id = attachment.messageId
            result.typeFile = attachment.type?:""
            result.timeSend = 0
            result.url = attachment.url
            result.name = attachment.name
            return result
        }
    }
}

object ManagerFileType {
    const val IMAGE_TYPE = "image"
    const val DOCUMENT_TYPE = "document"
    const val LINK_TYPE = "links"
    const val VIDEO_TYPE = "video"
    const val OTHER_TYPE = "other"
}