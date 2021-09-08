package com.hahalolo.incognito.presentation.setting.model

import android.os.Parcelable
import com.hahalolo.incognito.presentation.create.forward.view.ForwardStatus
import com.halo.data.entities.contact.Contact
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ContactModel(
    var id: String = "",
    var name: String? = null,
    var avatar: String? = null,
    var status: Int = -1,
) : Parcelable {

    companion object {
        @JvmStatic
        fun createListTest(): MutableList<ContactModel> {
            val urlTest =
                "https://media.hahalolo.com/5c205932d577a7125c98425e/ZK75NiXC95a4R0DE_1080xauto_high.jpg"
            val result = mutableListOf<ContactModel>()
            for (i in 1..15) {
//                val item: ContactModel = if (i == 3 || i == 9 || i == 12) {
//                    ContactModel(UUID.randomUUID().toString(), "Contact $i", urlTest, 3)
//                } else if (i == 5 || i == 10) {
//                    ContactModel(UUID.randomUUID().toString(), "Contact $i", "", 2)
//                } else {
//                    ContactModel(UUID.randomUUID().toString(), "Contact $i", urlTest, 1)
//                }
                val item = ContactModel(
                    UUID.randomUUID().toString(),
                    "Contact $i",
                    urlTest,
                    ForwardStatus.SEND
                )
                result.add(item)
            }
            return result
        }

        @JvmStatic
        fun convertToModel(contact: Contact): ContactModel {
            val model = ContactModel()
            model.id = contact.contactId ?: ""
            model.avatar = contact.avatar ?: ""
            model.name = "${contact.lastName ?: ""} ${contact.firstName ?: ""}"
            model.status = 3
            return model
        }
    }
}
