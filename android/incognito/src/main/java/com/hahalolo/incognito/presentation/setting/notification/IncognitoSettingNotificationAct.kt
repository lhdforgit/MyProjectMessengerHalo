package com.hahalolo.incognito.presentation.setting.notification

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoSettingNotificationActBinding
import com.hahalolo.incognito.presentation.base.AbsIncBackActivity
import com.hahalolo.incognito.presentation.setting.general.IncognitoSettingGeneralViewModel
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import javax.inject.Inject

class IncognitoSettingNotificationAct : AbsIncBackActivity(){

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoSettingNotificationActBinding>()
    val viewModel: IncognitoSettingGeneralViewModel by viewModels { factory }

    override fun initActionBar() {

    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_setting_notification_act)
    }

    override fun initializeLayout() {

    }

    companion object{
        @JvmStatic
        fun launchSettingNotification(context : Context){
            val intent = Intent(context, IncognitoSettingNotificationAct::class.java)
            context.startActivity(intent)
        }
    }
}