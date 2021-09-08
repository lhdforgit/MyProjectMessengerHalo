package com.hahalolo.incognito.presentation.main.owner.message.tick

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoOwnerMsgTickActBinding
import com.hahalolo.incognito.presentation.base.AbsIncActivity
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class IncognitoOwnerMessageTickAct @Inject constructor(): AbsIncActivity(){

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoOwnerMsgTickActBinding>()
    val viewModel: IncognitoOwnerMessageTickModel by viewModels { factory }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_owner_msg_tick_act)
    }

    override fun initializeLayout() {

    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoOwnerMessageTickAct::class.java)
        }
    }
}