package com.hahalolo.incognito.presentation.setting.managerfile.media

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.incognito.databinding.IncognitoManagerMediaFrBinding
import com.hahalolo.incognito.presentation.base.AbsIncFragment
import com.hahalolo.incognito.presentation.setting.managerfile.util.IncognitoPreloadHolder
import com.hahalolo.incognito.presentation.setting.model.ManagerMediaModel
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.ktx.autoCleared
import com.halo.widget.HaloGridLayoutManager
import com.halo.widget.recyclerview.RecyclerViewPreloaderFlexbox
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class IncognitoManagerMediaFr @Inject constructor() : AbsIncFragment() {

    private var binding by autoCleared<IncognitoManagerMediaFrBinding>()
    private var adapter: ManagerMediaAdapter? = null
    private val requestManager: RequestManager by lazy { Glide.with(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IncognitoManagerMediaFrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initializeLayout() {
        initRec()
    }

    private fun initRec() {
        binding.apply {
            val preloadSizeProvider = ViewPreloadSizeProvider<ManagerMediaModel>()
            mediaRec.layoutManager = HaloGridLayoutManager(requireContext(), 3)
            RecyclerViewUtils.optimization(mediaRec, activity)
            adapter = ManagerMediaAdapter(
                diffCallback = ManagerMediaDiffCallback(),
                preloadSizeProvider = preloadSizeProvider,
                requestManager = requestManager,
                onClickListener = {

                }
            )
            adapter?.let {
                val preloader = RecyclerViewPreloaderFlexbox(
                    requestManager,
                    it,
                    preloadSizeProvider,
                    5
                )
                mediaRec.addOnScrollListener(preloader)
            }
            mediaRec.setRecyclerListener { viewHolder ->
                kotlin.runCatching {
                    if (viewHolder is IncognitoPreloadHolder) {
                        (viewHolder as IncognitoPreloadHolder).invalidateLayout(requestManager)
                    }
                }
            }
            mediaRec.adapter = adapter
        }
    }
}