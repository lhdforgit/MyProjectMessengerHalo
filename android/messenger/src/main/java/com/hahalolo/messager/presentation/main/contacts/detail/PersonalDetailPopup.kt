package com.hahalolo.messager.presentation.main.contacts.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import com.hahalolo.messager.presentation.base.MessengerBottomSheetDagger
import com.hahalolo.messager.presentation.main.contacts.ChatContactsFr
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatContactsDetailBottomSheetBinding
import javax.inject.Inject

class PersonalDetailPopup @Inject constructor(val personalId: String, val isContact: Boolean) :
    MessengerBottomSheetDagger<ChatContactsDetailBottomSheetBinding>() {
    @Inject
    lateinit var providerUserDetail: dagger.Lazy<ChatContactDetailFr>

    override fun initAddFragment() {
        val bundle = Bundle()
        bundle.putString(PERSONAL_ID_ARG, personalId)
        bundle.putBoolean(IS_CONTACT_DETAIL_ARG, isContact)
        val transition = childFragmentManager.beginTransaction()
        providerUserDetail.get()?.arguments = bundle
        transition.add(R.id.container_user_detail, providerUserDetail.get())
        transition.commit()
    }

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ChatContactsDetailBottomSheetBinding? {
        return ChatContactsDetailBottomSheetBinding.inflate(inflater, container, false)
    }


    companion object {
        const val PERSONAL_ID_ARG = "ChatContactsDetailBottomSheet_PERSONAL_ID_ARG"
        const val IS_CONTACT_DETAIL_ARG = "ChatContactsDetailBottomSheet_IS_CONTACT_DETAIL_ARG"

        @JvmStatic
        fun startUserDetail(fragmentManager: FragmentManager, userId: String) {
            val popup = PersonalDetailPopup(userId, false)
            popup.show(fragmentManager, "PersonalDetailPopup_User")
        }

        @JvmStatic
        fun startContactDetail(fragmentManager: FragmentManager, contactId: String) {
            val popup = PersonalDetailPopup(contactId, true)
            popup.show(fragmentManager, "PersonalDetailPopup_Contact")
        }

    }
}