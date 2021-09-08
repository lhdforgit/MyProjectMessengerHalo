package com.hahalolo.incognito.presentation.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayout
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoSearchFrBinding
import com.hahalolo.incognito.presentation.base.AbsIncFragment
import com.hahalolo.incognito.presentation.main.contact.IncognitoContactFr
import com.hahalolo.incognito.presentation.main.conversation.IncognitoConversationFr
import com.hahalolo.incognito.presentation.main.group.IncognitoGroupFr
import com.halo.common.utils.ktx.autoCleared
import dagger.Lazy
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 6/1/21
 * com.hahalolo.incognito.presentation.main.conversation
 */
class IncognitoSearchFr
@Inject constructor(): AbsIncFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var providerConversationFr: Lazy<IncognitoConversationFr>

    @Inject
    lateinit var providerContactsFr: Lazy<IncognitoContactFr>

    @Inject
    lateinit var providerGroupFr: Lazy<IncognitoGroupFr>

    private var binding by autoCleared<IncognitoSearchFrBinding>()
    private val viewModel: IncognitoSearchFrViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.incognito_search_fr,
            container, false
        )
        return binding.root
    }

    private var searPagerAdapter: IncognitoSearchPagerAdapter? = null

    override fun initializeLayout() {
        initViewPager()
    }

    private fun initViewPager() {
        searPagerAdapter = IncognitoSearchPagerAdapter(childFragmentManager)

        searPagerAdapter?.addFragment(
            providerConversationFr.get(),
            "Chat")
        searPagerAdapter?.addFragment(
            providerGroupFr.get(),
            "Nhóm"
        )
        searPagerAdapter?.addFragment(
            providerContactsFr.get(),
            "Danh bạ"
        )
        binding.viewPager.adapter = searPagerAdapter
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.tabLayout.tabMode = TabLayout.MODE_FIXED
        binding.viewPager.offscreenPageLimit = 3
    }

    override fun onDestroyView() {
        providerConversationFr.get().onDestroyView()
        providerContactsFr.get().onDestroyView()
        providerGroupFr.get().onDestroyView()
        super.onDestroyView()
    }
}