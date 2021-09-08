package com.hahalolo.incognito.presentation.setting.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ManagerDocModel(
    val id: String? = null,
    val name: String? = null,
    val size: String? = null,
    val type: Int? = null,
    val userName: String? = null,
    val timeSend: Long? = null
) : Parcelable {

    companion object {
        val listType = listOf(1, 2, 3)
        val listSize = listOf(100, 250, 300, 500, 760)
        val listName = listOf(
            "Báo cáo thống kê.pdf",
            "Thông tin dịch 2021.docx",
            "Tài liệu tổng hợp.xls",
            "Lập trình android.pdf",
            "Android with kotlin.doc"
        )
        val listUserName = listOf(
            "Thanh Anh",
            "Nguyễn Nhân",
            "Hồng Đào",
            "Hải Đăng",
            "Thị Thắm"
        )

        @JvmStatic
        fun createDocModelTest(): MutableList<ManagerDocModel> {
            val result = mutableListOf<ManagerDocModel>()
            for (i in 1..20) {
                val item = ManagerDocModel(
                    id = UUID.randomUUID().toString(),
                    name = listName.random(),
                    size = listSize.random().toString(),
                    type = listType.random(),
                    userName = listUserName.random(),
                    timeSend = 0
                )
                result.add(item)
            }
            return result
        }
    }
}