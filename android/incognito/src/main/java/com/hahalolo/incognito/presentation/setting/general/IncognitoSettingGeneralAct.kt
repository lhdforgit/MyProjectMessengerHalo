package com.hahalolo.incognito.presentation.setting.general

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoSettingGeneralActBinding
import com.hahalolo.incognito.presentation.base.AbsIncBackActivity
import com.hahalolo.incognito.presentation.main.owner.IncognitoOwnerProfileAct
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
class IncognitoSettingGeneralAct
@Inject constructor() : AbsIncBackActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoSettingGeneralActBinding>()
    val viewModel: IncognitoSettingGeneralViewModel by viewModels { factory }

    override fun initActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_setting_general_act)
    }

    override fun initializeLayout() {

    }

    companion object{
        @JvmStatic
        fun getIntent(context : Context): Intent {
            return Intent(context, IncognitoSettingGeneralAct::class.java)
        }
    }
}