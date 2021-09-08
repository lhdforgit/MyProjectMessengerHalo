/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.gallery

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.hahalolo.messager.presentation.base.AbsMessBackActivity
import com.hahalolo.messager.presentation.group.gallery.adapter.pager.GalleryPagerAdapter
import com.hahalolo.messager.presentation.group.gallery.doc.ChatRoomGalleryDocFr
import com.hahalolo.messager.presentation.group.gallery.link.ChatRoomGalleryLinkFr
import com.hahalolo.messager.presentation.group.gallery.photo.ChatRoomGalleryPhotoFr
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatRoomGalleryActBinding
import com.halo.common.utils.SpanBuilderUtil
import com.halo.widget.HaloTypefaceSpan
import dagger.Lazy
import javax.inject.Inject


class ChatRoomGalleryAct : AbsMessBackActivity() {

    @Inject
    lateinit var providerGalleryPhotoFr: Lazy<ChatRoomGalleryPhotoFr>

    @Inject
    lateinit var providerGalleryLinkFr: Lazy<ChatRoomGalleryLinkFr>

    @Inject
    lateinit var providerGalleryDocFr: Lazy<ChatRoomGalleryDocFr>

    private var galleryAdapter: GalleryPagerAdapter? = null


    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val binding by lazy<ChatRoomGalleryActBinding> {
        DataBindingUtil.setContentView(
            this,
            R.layout.chat_room_gallery_act
        )
    }

    private val viewModel by lazy {
        ViewModelProvider(viewModelStore, factory).get(
            ChatRoomGalleryViewModel::class.java
        )
    }

    override fun initActionBar() {

    }

    override fun initializeBindingViewModel() {
        intent?.run {
            getStringExtra(ROOM_ID)?.takeIf { it.isNotEmpty() }?.let { id ->
                viewModel.roomId = id
            }
            getIntExtra(TAB_INDEX, 0).let {
                viewModel.tabIndex = it
            }
        }
    }

    override fun isLightTheme(): Boolean {
        return true
    }

    override fun initializeLayout() {
        initViewPager()
        initAction()
    }

    private fun initAction() {
        binding.navigationBt.setOnClickListener { onBackPressed() }
    }

    private fun initViewPager() {
        val manager: FragmentManager = supportFragmentManager
        galleryAdapter = GalleryPagerAdapter(manager)

        val bundle = Bundle()
        bundle.putString(ROOM_ID, viewModel.roomId)
        providerGalleryPhotoFr.get()?.arguments = bundle
        providerGalleryLinkFr.get()?.arguments = bundle
        providerGalleryDocFr.get()?.arguments = bundle
        galleryAdapter?.addFragment(
            providerGalleryPhotoFr.get(),
            getString(R.string.chat_message_gallery_tab_media)
        )
        galleryAdapter?.addFragment(
            providerGalleryLinkFr.get(),
            getString(R.string.chat_message_gallery_tab_link)
        )
        galleryAdapter?.addFragment(
            providerGalleryDocFr.get(),
            getString(R.string.chat_message_gallery_tab_doc)
        )
        binding.galleryPager.adapter = galleryAdapter
        binding.tabLayout.setupWithViewPager(binding.galleryPager)
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {}

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
        binding.galleryPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
            }
        })
        binding.galleryPager.setCurrentItem(viewModel.tabIndex, true)
        setFontTabLayout()
    }

    private fun setFontTabLayout() {
        try {
            val fontSpan = HaloTypefaceSpan.REGULAR(this)
            binding.tabLayout.getTabAt(0)?.text = SpanBuilderUtil().append(
                getString(R.string.chat_message_gallery_tab_media),
                fontSpan
            ).build()

            binding.tabLayout.getTabAt(1)?.text = SpanBuilderUtil().append(
                getString(R.string.chat_message_gallery_tab_link),
                fontSpan
            ).build()
            binding.tabLayout.getTabAt(2)?.text = SpanBuilderUtil().append(
                getString(R.string.chat_message_gallery_tab_doc),
                fontSpan
            ).build()

        } catch (e: Exception) {

        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResumeChatAct()
    }

    override fun finish() {
        viewModel.onFinishChatAct()
        super.finish()
    }

    companion object {
        const val ROOM_ID = "ChatRoomGalleryAct_ROOM_ID"
        const val TAB_INDEX = "ChatRoomGalleryAct_TAB_INDEX"
        fun getIntent(context: Context, rooId: String, tabIndex: Int): Intent {
            val intent = Intent(context, ChatRoomGalleryAct::class.java)
            intent.putExtra(ROOM_ID, rooId)
            intent.putExtra(TAB_INDEX, tabIndex)
            return intent
        }
    }
}