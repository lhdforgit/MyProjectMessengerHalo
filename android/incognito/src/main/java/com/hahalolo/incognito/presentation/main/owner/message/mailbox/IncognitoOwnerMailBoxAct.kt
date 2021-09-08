package com.hahalolo.incognito.presentation.main.owner.message.mailbox

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoOwnerMailBoxActBinding
import com.hahalolo.incognito.presentation.base.AbsIncBackActivity
import com.hahalolo.incognito.presentation.main.owner.message.waiting.IncognitoOwnerMessageWaitingAct
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import javax.inject.Inject

class IncognitoOwnerMailBoxAct
@Inject constructor() : AbsIncBackActivity() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoOwnerMailBoxActBinding>()
    val viewModel: IncognitoOwnerMailBoxViewModel by viewModels { factory }

    override fun initActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_owner_mail_box_act)
    }

    override fun initializeLayout() {
        initAction()
    }

    private fun initAction() {
        binding.btnMsgSpam.setOnClickListener {
            navigateMessageSpam()
        }
        binding.btnMsgTick.setOnClickListener {
            navigateMessageTick()
        }
        binding.btnMsgWait.setOnClickListener {
            navigateMessageWait()
        }
        binding.btnInvitation.setOnClickListener {
            //Todo list invigation
        }
    }

    private fun navigateMessageWait() {
        startActivity(IncognitoOwnerMessageWaitingAct.getIntent(this))
    }

    private fun navigateMessageTick() {
        startActivity(IncognitoOwnerMessageWaitingAct.getIntent(this))
    }

    private fun navigateMessageSpam() {
        startActivity(IncognitoOwnerMessageWaitingAct.getIntent(this))
    }

    companion object {
        @JvmStatic
        fun getIntent(contect: Context): Intent {
            return Intent(contect, IncognitoOwnerMailBoxAct::class.java)
        }
    }
}