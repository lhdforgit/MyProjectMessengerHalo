package com.hahalolo.incognito.presentation.create.conversation

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoCreateConversationActBinding
import com.hahalolo.incognito.databinding.IncognitoLoginActBinding
import com.hahalolo.incognito.presentation.base.AbsIncActivity
import com.hahalolo.incognito.presentation.create.channel.IncognitoCreateChannelAct
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/26/21
 * com.hahalolo.incognito.presentation.main
 */
@ActivityScoped
class IncognitoCreateConversationAct
@Inject constructor() : AbsIncActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoCreateConversationActBinding>()
    val viewModel: IncognitoCreateConversationViewModel by viewModels { factory }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_create_conversation_act)
    }

    override fun initializeLayout() {

    }

    companion object{
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoCreateConversationAct::class.java)
        }
    }
}