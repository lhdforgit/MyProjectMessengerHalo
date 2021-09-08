package com.hahalolo.incognito.presentation.setting.managerfile.file

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.incognito.databinding.IncognitoManagerDocFrBinding
import com.hahalolo.incognito.presentation.base.AbsIncFragment
import com.hahalolo.incognito.presentation.setting.managerfile.file.adapter.ManagerDocAdapter
import com.hahalolo.incognito.presentation.setting.managerfile.file.adapter.ManagerDocDiffCallback
import com.hahalolo.incognito.presentation.setting.managerfile.file.adapter.ManagerDocListener
import com.hahalolo.incognito.presentation.setting.managerfile.util.IncognitoPreloadHolder
import com.hahalolo.incognito.presentation.setting.model.ManagerDocModel
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.ktx.autoCleared
import com.halo.widget.HaloLinearLayoutManager
import com.halo.widget.recyclerview.RecyclerViewPreloaderFlexbox
import javax.inject.Inject

class IncognitoManagerDocFr @Inject constructor() : AbsIncFragment(), MenuManagerDocListener {

    private var binding by autoCleared<IncognitoManagerDocFrBinding>()
    private var adapter: ManagerDocAdapter? = null
    private val requestManager: RequestManager by lazy { Glide.with(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IncognitoManagerDocFrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initializeLayout() {
        initRec()
    }

    private fun initRec() {
        binding.apply {
            val preloadSizeProvider = ViewPreloadSizeProvider<ManagerDocModel>()
            docRec.layoutManager = HaloLinearLayoutManager(requireContext())
            RecyclerViewUtils.optimization(docRec, activity)

            adapter = ManagerDocAdapter(
                diffCallback = ManagerDocDiffCallback(),
                preloadSizeProvider = preloadSizeProvider,
                requestManager = requestManager,
                listener = object : ManagerDocListener {
                    override fun onItemClick(item: ManagerDocModel) {
                        showBottomSheetMenu(item)
                    }
                }
            )
            adapter?.let {
                val preloader = RecyclerViewPreloaderFlexbox(
                    requestManager,
                    it,
                    preloadSizeProvider,
                    5
                )
                docRec.addOnScrollListener(preloader)
            }
            docRec.setRecyclerListener { viewHolder ->
                kotlin.runCatching {
                    if (viewHolder is IncognitoPreloadHolder) {
                        (viewHolder as IncognitoPreloadHolder).invalidateLayout(requestManager)
                    }
                }
            }
            docRec.adapter = adapter
            ManagerDocModel.createDocModelTest().let { data ->

            }
        }
    }

    private fun showBottomSheetMenu(item: ManagerDocModel) {
        val memu = BottomSheetMenuManagerDoc(requireContext(), this, item)
        memu.show(childFragmentManager, "IncognitoManagerDocFr")
    }

    override fun downloadFile() {

    }

    override fun forwardFile() {

    }

    override fun deleteFile() {

    }
}