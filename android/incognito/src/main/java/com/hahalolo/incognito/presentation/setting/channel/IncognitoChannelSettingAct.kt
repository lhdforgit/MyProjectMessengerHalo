package com.hahalolo.incognito.presentation.setting.channel

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoChannelSettingActBinding
import com.hahalolo.incognito.presentation.base.AbsIncBackActivity
import com.hahalolo.incognito.presentation.setting.managerfile.IncognitoManagerFileAct
import com.hahalolo.incognito.presentation.setting.member.list.IncognitoMemberListAct
import com.hahalolo.incognito.presentation.setting.view.IncognitoGalleryListener
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/26/21
 */
@ActivityScoped
class IncognitoChannelSettingAct
@Inject constructor() : AbsIncBackActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoChannelSettingActBinding>()
    val viewModel: IncognitoChannelSettingViewModel by viewModels { factory }

    override fun initActionBar() {
       setSupportActionBar(binding.toolbar)
    }

    override fun isShowTitleToolbar(): Boolean {
        return false
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_channel_setting_act)
    }

    override fun initializeLayout() {
        initAction()
    }

    private fun initAction() {
        binding.apply {
            galleryView.setListener(object : IncognitoGalleryListener {
                override fun navigateGallery() {
                    startActivity(IncognitoManagerFileAct.getIntent(this@IncognitoChannelSettingAct))
                }

                override fun openMedia(url: String) {

                }
            })
            memberTv.setOnClickListener {
                startActivity(IncognitoMemberListAct.getIntent(this@IncognitoChannelSettingAct))
            }
        }
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoChannelSettingAct::class.java)
        }
    }
}