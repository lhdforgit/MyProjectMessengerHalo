package com.hahalolo.incognito.presentation.conversation.thread

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.common.base.Strings
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMsgThreadActBinding
import com.hahalolo.incognito.presentation.base.AbsIncActivity
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class IncognitoMsgThreadAct @Inject constructor() : AbsIncActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoMsgThreadActBinding>()
    val viewModel: IncognitoMsgThreadViewModel by viewModels { factory }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_msg_thread_act)
    }

    override fun initializeLayout() {


    }
}