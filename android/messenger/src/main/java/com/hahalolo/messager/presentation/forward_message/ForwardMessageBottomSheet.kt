package com.hahalolo.messager.presentation.forward_message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.hahalolo.messager.presentation.base.MessengerBottomSheetDagger
import com.hahalolo.messager.presentation.forward_message.container.ForwardMessageFr
import com.hahalolo.messager.presentation.main.contacts.detail.PersonalDetailPopup
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ForwardMessageBottomSheetBinding
import javax.inject.Inject

class ForwardMessageBottomSheet @Inject constructor(val messageId: String) :
    MessengerBottomSheetDagger<ForwardMessageBottomSheetBinding>() {

    @Inject
    lateinit var providerForward: dagger.Lazy<ForwardMessageFr>

    override fun initAddFragment() {
        val bundle = Bundle()
        bundle.putString(FORWARD_MESSAGE_ID, messageId)
        val transition = childFragmentManager.beginTransaction()
        providerForward.get()?.arguments = bundle
        transition.add(R.id.container_forward, providerForward.get())
        transition.commit()
    }

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ForwardMessageBottomSheetBinding {
        return ForwardMessageBottomSheetBinding.inflate(inflater, container, false)
    }

    companion object {
        const val FORWARD_MESSAGE_ID = "ForwardMessageBottomSheet_FORWARD_MESSAGE_ID"

        @JvmStatic
        fun startForwardMessage(fragmentManager: FragmentManager, messageId: String) {
            val popup = ForwardMessageBottomSheet(messageId)
            popup.show(fragmentManager, "ForwardMessageBottomSheet")
        }
    }
}

