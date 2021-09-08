package com.hahalolo.incognito.presentation.create.forward.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hahalolo.incognito.databinding.IncognitoForwardMessageFrBinding
import com.hahalolo.incognito.presentation.base.AbsIncFragment
import com.hahalolo.incognito.presentation.create.forward.container.contact.IncognitoForwardMessageContactFr
import com.hahalolo.incognito.presentation.create.forward.container.group.IncognitoForwardMessageGroupFr
import com.halo.common.utils.ktx.autoCleared
import javax.inject.Inject

class IncognitoForwardMessageFr @Inject constructor() : AbsIncFragment() {

    private var binding by autoCleared<IncognitoForwardMessageFrBinding>()

    @Inject
    lateinit var groupFr: dagger.Lazy<IncognitoForwardMessageGroupFr>

    @Inject
    lateinit var contactFr: dagger.Lazy<IncognitoForwardMessageContactFr>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IncognitoForwardMessageFrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initializeLayout() {
        initView()
        arguments?.getInt("TYPE_HEADER")?.let {
            binding.forwardHeader.setTypeForward(it)
        }
    }

    private fun initView() {
        binding.apply {
            if (viewPager.adapter == null) {
                // Sửa lỗi khi quay lại màn hình thì xuất hiện lỗi ID Fragment.
                viewPager.isSaveEnabled = false
                viewPager.offscreenPageLimit = 2
                viewPager.adapter = object : FragmentStateAdapter(requireActivity()) {
                    override fun createFragment(position: Int): Fragment {
                        return when (position) {
                            0 -> groupFr.get()
                            1 -> contactFr.get()
                            else -> throw ClassNotFoundException("Not found class with $position")
                        }
                    }

                    override fun getItemCount(): Int {
                        return 2
                    }
                }
                tabLayout.tabIconTint = null
                tabLayout.tabMode = TabLayout.MODE_FIXED
                tabLayout.tabGravity = TabLayout.GRAVITY_FILL
                viewPager.currentItem = 0

                TabLayoutMediator(tabLayout, viewPager, true, true) { tab, position ->
                    tab.text = when (position) {
                        0 -> "Nhóm"
                        1 -> "Liên hệ"
                        else -> ""
                    }
                }.attach()
            }
        }
    }
}