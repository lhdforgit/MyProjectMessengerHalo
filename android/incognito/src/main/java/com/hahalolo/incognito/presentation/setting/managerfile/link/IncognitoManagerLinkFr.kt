package com.hahalolo.incognito.presentation.setting.managerfile.link

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.incognito.databinding.IncognitoManagerLinkFrBinding
import com.hahalolo.incognito.presentation.base.AbsIncFragment
import com.hahalolo.incognito.presentation.setting.managerfile.link.adapter.ManagerLinkAdapter
import com.hahalolo.incognito.presentation.setting.managerfile.link.adapter.ManagerLinkDiffCallback
import com.hahalolo.incognito.presentation.setting.managerfile.util.IncognitoPreloadHolder
import com.hahalolo.incognito.presentation.setting.model.ManagerLinkModel
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.ktx.autoCleared
import com.halo.widget.HaloLinearLayoutManager
import com.halo.widget.recyclerview.RecyclerViewPreloaderFlexbox
import javax.inject.Inject

class IncognitoManagerLinkFr @Inject constructor() : AbsIncFragment(), MenuManagerLinkInterface {

    private var binding by autoCleared<IncognitoManagerLinkFrBinding>()
    private var adapter: ManagerLinkAdapter? = null
    private val requestManager: RequestManager by lazy { Glide.with(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IncognitoManagerLinkFrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initializeLayout() {
        initRec()
    }

    private fun initRec() {
        binding.apply {
            val preloadSizeProvider = ViewPreloadSizeProvider<ManagerLinkModel>()
            linkRec.layoutManager = HaloLinearLayoutManager(requireContext())
            RecyclerViewUtils.optimization(linkRec, activity)

            adapter = ManagerLinkAdapter(
                diffCallback = ManagerLinkDiffCallback(),
                preloadSizeProvider = preloadSizeProvider,
                requestManager = requestManager,
                onClickListener = {
                    showBottomSheetMenu()
                }
            )
            adapter?.let {
                val preloader = RecyclerViewPreloaderFlexbox(
                    requestManager,
                    it,
                    preloadSizeProvider,
                    5
                )
                linkRec.addOnScrollListener(preloader)
            }
            linkRec.setRecyclerListener { viewHolder ->
                kotlin.runCatching {
                    if (viewHolder is IncognitoPreloadHolder) {
                        (viewHolder as IncognitoPreloadHolder).invalidateLayout(requestManager)
                    }
                }
            }
            linkRec.adapter = adapter
            ManagerLinkModel.createLinkModelTest().toMutableList().let { data ->
            }
        }
    }

    private fun showBottomSheetMenu() {
        val menu = BottomSheetMenuManagerLink(requireContext(), this)
        menu.show(childFragmentManager, "IncognitoManagerLinkFr")
    }

    override fun openLink() {

    }

    override fun copyLink() {

    }

    override fun forwardLink() {

    }

    override fun deleteLink() {

    }

}