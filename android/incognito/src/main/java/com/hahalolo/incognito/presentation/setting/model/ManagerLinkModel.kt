package com.hahalolo.incognito.presentation.setting.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ManagerLinkModel(
    val isDateItem: Boolean = false,
    val id: String? = null,
    val url: String? = null,
    val thumb: String? = null,
    val title: String? = null,
    val description: String? = null,
    val timeSend: Long? = null

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
        fun createLinkModelTest(): List<ManagerLinkModel> {
            val result = mutableListOf<ManagerLinkModel>()
            val today = Calendar.getInstance().timeInMillis

            for (i in 1..20) {
                val item = ManagerLinkModel(
                    id = UUID.randomUUID().toString(),
                    url = "https://www.hahalolo.com",
                    thumb = listImage.random(),
                    title = "Hahalolo happy birthday",
                    description = "Hahalolo",
                    timeSend = today
                )
                result.add(item)
            }

            return result
        }
    }
}