package com.hahalolo.incognito.presentation.setting.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class MemberModel(
    var id: String? = null,
    var name: String? = null,
    var avatar: String? = null,
    var role: Int = MemberRole.Member
) : Parcelable {

    companion object {
        @JvmStatic
        fun createListTest(): List<MemberModel> {
            val urlTest =
                "https://media.hahalolo.com/5c205932d577a7125c98425e/ZK75NiXC95a4R0DE_1080xauto_high.jpg"
            val result = mutableListOf<MemberModel>()
            for (i in 1..25) {
                var item: MemberModel = if (i == 3 || i == 9 || i == 12) {
                    MemberModel(
                        UUID.randomUUID().toString(),
                        "Member $i",
                        urlTest,
                        MemberRole.Member
                    )
                } else if (i == 5 || i == 10) {
                    MemberModel(
                        UUID.randomUUID().toString(),
                        "Member $i",
                        urlTest,
                        MemberRole.Admin
                    )
                } else if (i == 7) {
                    MemberModel(
                        UUID.randomUUID().toString(),
                        "Member $i",
                        urlTest,
                        MemberRole.Owner
                    )
                } else {
                    MemberModel(
                        UUID.randomUUID().toString(),
                        "Member $i",
                        urlTest,
                        MemberRole.Member
                    )
                }
                result.add(item)
            }
            return result
        }
    }
}

annotation class MemberRole {
    companion object {
        const val Member = 1
        const val Admin = 2
        const val Owner = 3
    }
}

sealed class MemberItem {
    class Member(val member: MemberModel) : MemberItem()
    class Header(val title: String) : MemberItem()
}

object MemberUtil {
    fun getListMemberWithHeader(listMember: List<MemberModel>): List<MemberItem> {
        val result = mutableListOf<MemberItem>()
        var isAdminHeader = true
        var isMemberHeader = true
        listMember.sortedByDescending { it.role }.apply {
            this.forEachIndexed { index, memberModel ->
                if (memberModel.role == MemberRole.Owner) {
                    result.add(MemberItem.Header("Người tạo nhóm"))
                } else if (memberModel.role == MemberRole.Admin && isAdminHeader) {
                    result.add(MemberItem.Header("Quản trị viên"))
                    isAdminHeader = false
                } else if (memberModel.role == MemberRole.Member && isMemberHeader) {
                    result.add(MemberItem.Header("Thành viên"))
                    isMemberHeader = false
                }
                result.add(MemberItem.Member(memberModel))
            }
        }
        return result
    }
}
