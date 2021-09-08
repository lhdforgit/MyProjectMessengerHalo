package com.hahalolo.incognito.presentation.setting.model

import android.os.Parcelable
import com.halo.data.entities.attachment.Attachment
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ManagerMediaModel(
    var id: String? = null,
    var url: String? = null,
    var timeSend: Long? = null,
    var isVideo: Boolean? = false
) : Parcelable {

    companion object {
        private var listImage = listOf(
            "https://media.hahalolo.com/2020/12/09/09/5c205932d577a7125c98425e201209095558G5_1080xauto_high.jpg",
            "https://media.hahalolo.com/5c205932d577a7125c98425e/ByPMDdluA8HZbkSt_1080xauto_high.jpg",
            "https://media.hahalolo.com/5c205932d577a7125c98425e/ZK75NiXC95a4R0DE_1080xauto_high.jpg",
            "https://media.hahalolo.com/5c205932d577a7125c98425e/ELgJYpEWOaEk0q5c_1080xauto_high.jpg",
            "https://media.hahalolo.com/5c205932d577a7125c98425e/Hw96vG2T0GsXHa4y_1080xauto_high.jpg",
            "https://media.hahalolo.com/5c205932d577a7125c98425e/t7HBgPJaiUvY5xKx_1080xauto_high.jpg"
        )

        @JvmStatic
        fun createMediaTest(): MutableList<ManagerMediaModel> {
            val result = mutableListOf<ManagerMediaModel>()
            for (i in 1..30) {
                val item = ManagerMediaModel(
                    id = UUID.randomUUID().toString(),
                    url = listImage.random(),
                    timeSend = 0,
                    isVideo = false
                )
                result.add(item)
            }
            return result
        }

        fun create(attachment: Attachment): ManagerMediaModel {
            val result = ManagerMediaModel()
            result.id = UUID.randomUUID().toString()
            result.isVideo = false
            result.timeSend = 0
            result.url = attachment.fileUrl
            return result
        }
    }
}