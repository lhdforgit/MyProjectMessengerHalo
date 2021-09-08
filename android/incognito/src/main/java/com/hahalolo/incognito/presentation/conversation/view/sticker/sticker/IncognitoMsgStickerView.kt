package com.hahalolo.incognito.presentation.conversation.view.sticker.sticker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.tabs.TabLayout
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMsgStickerViewBinding
import com.hahalolo.incognito.presentation.conversation.view.sticker.IncognitoMessageStickerListener

class IncognitoMsgStickerView : FrameLayout {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private lateinit var binding: IncognitoMsgStickerViewBinding
    private var pagerAdapter: IncognitoMsgStickerPager? = null

    private var listener: IncognitoMessageStickerListener? = null

    fun updateListener(listener: IncognitoMessageStickerListener) {
        this.listener = listener
        initPagerAdapter()
        bindLayout()
    }



    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.incognito_msg_sticker_view, this, false
        )
        addView(binding.root)
    }

    private fun initPagerAdapter() {
        pagerAdapter = IncognitoMsgStickerPager( listener)
        binding.pager.adapter = pagerAdapter
    }

    private fun initTablayout() {
        binding.tabLayout.setupWithViewPager(binding.pager)
        binding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        for (i in 0 until (pagerAdapter?.count ?: 0)) {
            val view = IncognitoMsgStickerTabView(context)
            val tab = binding.tabLayout.getTabAt(i)
            if (tab != null && context != null) {
                val link = pagerAdapter?.getPageIcon(i)
                view.updateIcon(link)
                if (tab.isSelected) {
                    view.onTabSelected()
                } else {
                    view.onTabUnselected()
                }
                tab.customView = view
            }
        }
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                (tab.customView as? IncognitoMsgStickerTabView)?.onTabSelected()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                (tab.customView as? IncognitoMsgStickerTabView)?.onTabUnselected()
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    private fun bindLayout() {
        listener?.observerStickerPacks(Observer {
            pagerAdapter?.submit(it?: mutableListOf())
            initTablayout()
        })
    }
}