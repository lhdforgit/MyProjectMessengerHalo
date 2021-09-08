package com.halo.presentation.search.general.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.hahalolo.cache.entity.search_main.SearchEntity
import com.halo.R
import com.halo.common.utils.RecyclerViewUtils
import com.halo.databinding.SearchHistoryViewBinding
import com.halo.presentation.search.general.SearchHistoryListener
import com.halo.presentation.search.general.view.adapter.SearchHistoryAdapter
import com.halo.widget.HaloLinearLayoutManager

class SearchHistoryView : FrameLayout {
    private var binding: SearchHistoryViewBinding? = null
    private var listData: List<SearchEntity>? = null
    private var adapter: SearchHistoryAdapter? = null
    private var listener: SearchHistoryListener? = null
    private var activity: Activity? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = DataBindingUtil.inflate(inflater, R.layout.search_history_view, null, false)
        addView(binding?.root)
    }

    fun setData(listData: List<SearchEntity>?) {
        this.listData = listData
        updateView()
    }

    fun setListener(listener: SearchHistoryListener?, activity: Activity?){
        this.listener = listener
        this.activity = activity
    }

    private fun updateView() {
        listData?.run {
            binding?.isEmpty = this.isEmpty()
            binding?.historyRec?.layoutManager = HaloLinearLayoutManager(context)
            adapter = SearchHistoryAdapter(Glide.with(context), listener)
            binding?.historyRec?.adapter = adapter
            RecyclerViewUtils.optimization(binding?.historyRec, activity)
            binding?.removeAllBt?.setOnClickListener { listener?.deleteAllHistory() }
            adapter?.setData(this.toMutableList())
            binding?.isLoaded = true
        }?: kotlin.run {
            binding?.isEmpty = true
        }
    }
}