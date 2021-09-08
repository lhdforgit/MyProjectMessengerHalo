package com.halo.widget.sticker.sticker_group.sticker

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.tabs.TabLayout
import com.halo.data.entities.mongo.tet.GifCard
import com.halo.widget.felling.repository.EditorFeelingRepositoryImpl
import com.halo.widget.sticker.R
import com.halo.widget.sticker.sticker_group.StickerGroupView
import com.halo.widget.sticker.sticker_group.sticker.tab.StickerTabView

class GifCardView : FrameLayout, LifecycleOwner {

    private var root: View? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    private var stickerPagerAdapter: StickerPagerAdapter? = null
    private var requestManager: RequestManager? = null
    private var listener: StickerGroupView.StickerGroupListener? = null
    private var onItemTouchListener: RecyclerView.OnItemTouchListener? = null

    private var mLifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    fun setOnItemTouchListener(onItemTouchListener: RecyclerView.OnItemTouchListener?) {
        this.onItemTouchListener = onItemTouchListener
    }

    fun setListener(listener: StickerGroupView.StickerGroupListener?) {
        this.listener = listener
    }

    fun setRequestManager(requestManager: RequestManager?) {
        this.requestManager = requestManager
    }

    constructor(
        context: Context?,
        requestManager: RequestManager?
    ) : super(context!!) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!,
        attrs
    ) {
        initView()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr) {
        initView()
    }


    private fun initView() {
        mLifecycleRegistry.currentState = Lifecycle.State.CREATED
        root = LayoutInflater.from(context).inflate(R.layout.layout_sticker_page, null)
        tabLayout = root?.findViewById(R.id.sticker_tab)
        tabLayout?.tabMode = TabLayout.MODE_SCROLLABLE
        viewPager = root?.findViewById(R.id.sticker_pager)
        this.addView(root)

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mLifecycleRegistry.currentState = Lifecycle.State.STARTED
        initPager()
    }

    override fun onDetachedFromWindow() {
        mLifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        super.onDetachedFromWindow()
    }

    private fun getRequestManager(): RequestManager {
        return requestManager ?: Glide.with(context)
    }

    private fun initPager() {
        stickerPagerAdapter = StickerPagerAdapter(
            getRequestManager()
        )

        stickerPagerAdapter?.setListener(listener)
        onItemTouchListener?.run {
            stickerPagerAdapter?.setOnItemTouchListener(this)
        }
        viewPager?.adapter = stickerPagerAdapter
        tabLayout?.setupWithViewPager(viewPager)
        tabLayout?.tabMode = TabLayout.MODE_SCROLLABLE
        for (i in 0 until (stickerPagerAdapter?.count ?: 0)) {
            val view = StickerTabView(context)
            val tab = tabLayout?.getTabAt(i)
            if (tab != null && context != null && requestManager != null) {
                val link = stickerPagerAdapter?.getPageIcon(i)
                view.setRequestManager(getRequestManager())
                view.updateTabIcon(link)
                if (tab.isSelected) {
                    view.onTabSelected()
                } else {
                    view.onTabUnselected()
                }
                tab.customView = view
            }
        }
        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.customView is StickerTabView) {
                    (tab.customView as StickerTabView).onTabSelected()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                if (tab.customView is StickerTabView) {
                    (tab.customView as StickerTabView).onTabUnselected()
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    override fun getLifecycle(): Lifecycle {
        return mLifecycleRegistry
    }

    interface GifCardListener {
        fun observerGifCards(observer: Observer<List<GifCard>>)
        fun onClickGifCardItem(gifCard: GifCard)
    }
}