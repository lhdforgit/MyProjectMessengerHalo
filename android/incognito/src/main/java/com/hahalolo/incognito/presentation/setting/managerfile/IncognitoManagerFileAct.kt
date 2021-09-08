package com.hahalolo.incognito.presentation.setting.managerfile

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoManagerFileActBinding
import com.hahalolo.incognito.presentation.base.AbsIncBackActivity
import com.hahalolo.incognito.presentation.setting.managerfile.file.IncognitoManagerDocFr
import com.hahalolo.incognito.presentation.setting.managerfile.link.IncognitoManagerLinkFr
import com.hahalolo.incognito.presentation.setting.managerfile.media.IncognitoManagerMediaFr
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/26/21
 */
@ActivityScoped
class IncognitoManagerFileAct
@Inject constructor() :  AbsIncBackActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var providerMediaFr: dagger.Lazy<IncognitoManagerMediaFr>

    @Inject
    lateinit var providerLinkFr: dagger.Lazy<IncognitoManagerLinkFr>

    @Inject
    lateinit var providerDocFr: dagger.Lazy<IncognitoManagerDocFr>

    var binding by notNull<IncognitoManagerFileActBinding>()
    val viewModel: IncognitoManagerFileViewModel by viewModels { factory }

    override fun initActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    override fun isShowTitleToolbar(): Boolean {
        return false
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_manager_file_act)
    }

    override fun initializeLayout() {
        initViewPager()
    }

    private fun initViewPager() {
        binding.apply {
            if (viewPager.adapter == null) {
                // Sửa lỗi khi quay lại màn hình thì xuất hiện lỗi ID Fragment.
                viewPager.isSaveEnabled = false
                viewPager.offscreenPageLimit = 3
                viewPager.adapter = object : FragmentStateAdapter(this@IncognitoManagerFileAct) {
                    override fun createFragment(position: Int): Fragment {
                        return when (position) {
                            0 -> providerMediaFr.get()
                            1 -> providerLinkFr.get()
                            2 -> providerDocFr.get()
                            else -> throw ClassNotFoundException("Not found class with $position")
                        }
                    }

                    override fun getItemCount(): Int {
                        return 3
                    }
                }
                tabLayout.tabIconTint = null
                tabLayout.tabMode = TabLayout.MODE_FIXED
                tabLayout.tabGravity = TabLayout.GRAVITY_FILL
                viewPager.currentItem = 0

                TabLayoutMediator(tabLayout, viewPager, true, true) { tab, position ->
                    tab.text = when (position) {
                        0 -> "Hình ảnh"
                        1 -> "Liên kết"
                        2 -> "Tập tin"
                        else -> ""
                    }
                }.attach()
            }
        }
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoManagerFileAct::class.java)
        }
    }
}