package com.hahalolo.messager.presentation.message.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.halo.common.utils.KeyboardUtils
import com.halo.common.utils.Strings
import com.halo.widget.HaloLinearLayoutManager
import com.halo.widget.recyclerview.RecyclerViewPreloader
import com.halo.widget.sticky_header.StickyRecyclerHeadersDecoration

class ChatMessageRecycleView : RecyclerView{
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @SuppressLint("ClickableViewAccessibility")
    fun initAdapter(adapter: AbsChatMessageAdapter){
        setItemViewCacheSize(0)
        isDrawingCacheEnabled = true
        drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        animation = null
        itemAnimator = null
        // Fix bug lag personal wall
        if (itemAnimator is SimpleItemAnimator) {
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        // Ẩn bàn phím khi người dùng thực hiện hành động cuộn
        setOnTouchListener(OnTouchListener { v: View?, event: MotionEvent? ->
            try {
                (context as? Activity)?.run{
                    KeyboardUtils.hideSoftInput(this)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            false
        })
        val layoutManager = HaloLinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        setLayoutManager(layoutManager)
        val headersDecor = StickyRecyclerHeadersDecoration(adapter)
        this.addItemDecoration(headersDecor)
        super.setAdapter(adapter)

        val preloader = RecyclerViewPreloader(
            adapter.requestManager,
            adapter,
            adapter.getPreloadProvider(),
            5
        )
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                preloader.onScrollStateChanged(recyclerView, newState)
                listener?.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                preloader.onScrolled(recyclerView, dx, dy)
                if (!canScrollVertically(-1)){
                    listener?.onItemAtEndLoaded()
                }
                listener?.onScrolled(recyclerView, dx, dy)
            }
        })

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                post {
                    if (positionStart == 0 && itemCount == 1 && listener?.autoScrollNewItem() == true) {
                        layoutManager.findFirstCompletelyVisibleItemPosition().takeIf { it <= 1 }
                            ?.run {
                                // nếu danh sách tin nhắn đã chưa đc cuộn lên trên
                                // tin nhắn đến sẽ cuộn đến tin nhắn mới nhất
                                scrollToPosition(0)
                            }
                    }
                }
            }
        })
    }

    private var listener:Listener?=null

    fun updateListener(listener: Listener){
        this.listener = listener
    }

    interface Listener{
        fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int){}
        fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){}
        fun autoScrollNewItem(): Boolean= true
        fun onItemAtEndLoaded()
    }
}