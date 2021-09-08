package com.hahalolo.incognito.presentation.setting.member

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMemberSettingActBinding
import com.hahalolo.incognito.presentation.base.AbsIncBackActivity
import com.hahalolo.incognito.presentation.create.channel.IncognitoCreateChannelAct
import com.hahalolo.incognito.presentation.setting.managerfile.IncognitoManagerFileAct
import com.hahalolo.incognito.presentation.setting.view.IncognitoGalleryListener
import com.halo.common.utils.ktx.launchActivity
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/26/21
 */
@ActivityScoped
class IncognitoMemberSettingAct
@Inject constructor() : AbsIncBackActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoMemberSettingActBinding>()
    val viewModel: IncognitoMemberSettingViewModel by viewModels { factory }

    override fun initActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    override fun isShowTitleToolbar(): Boolean {
        return false
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_member_setting_act)
    }

    override fun initializeLayout() {
        initView()
    }

    private fun initView() {
        binding.apply {
            galleryView.setListener(object : IncognitoGalleryListener {
                override fun navigateGallery() {
                    startActivity(IncognitoManagerFileAct.getIntent(this@IncognitoMemberSettingAct))
                }

                override fun openMedia(url: String) {

                }
            })
            createGroupTv.setOnClickListener { navigateCreateGroup() }
        }
    }

    private fun navigateCreateGroup() {
        launchActivity<IncognitoCreateChannelAct> {  }
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoMemberSettingAct::class.java)
        }
    }
}