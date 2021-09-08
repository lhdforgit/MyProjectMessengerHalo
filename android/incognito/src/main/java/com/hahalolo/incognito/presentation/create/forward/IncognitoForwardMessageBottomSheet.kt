package com.hahalolo.incognito.presentation.create.forward

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoForwardMessageBottomSheetBinding
import com.hahalolo.incognito.presentation.base.IncognitoBottomSheetDagger
import com.hahalolo.incognito.presentation.create.forward.container.IncognitoForwardMessageFr
import javax.inject.Inject

class IncognitoForwardMessageBottomSheet @Inject constructor() :
    IncognitoBottomSheetDagger<IncognitoForwardMessageBottomSheetBinding>() {
    @Inject
    lateinit var providerForward: dagger.Lazy<IncognitoForwardMessageFr>

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): IncognitoForwardMessageBottomSheetBinding {
        return IncognitoForwardMessageBottomSheetBinding.inflate(inflater, container, false)
    }

    override fun initAddFragment() {
        val listType = listOf(1, 2, 3, 4, 5)
        val bundle = Bundle()
        bundle.putInt("TYPE_HEADER", listType.random())
        kotlin.runCatching {
            val transition = childFragmentManager.beginTransaction()
            providerForward.get()?.arguments = bundle
            transition.add(R.id.container_forward, providerForward.get())
            transition.commit()
        }
    }
}