package com.hahalolo.incognito.presentation.create.forward.container.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.LoadType
import androidx.paging.flatMap
import androidx.paging.map
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.incognito.databinding.IncognitoForwardMessageGroupFrBinding
import com.hahalolo.incognito.presentation.base.AbsIncFragment
import com.hahalolo.incognito.presentation.base.IncognitoLoadStateAdapter
import com.hahalolo.incognito.presentation.base.IncognitoLoadStateCallback
import com.hahalolo.incognito.presentation.create.forward.container.ForwardTaskCallback
import com.hahalolo.incognito.presentation.create.forward.container.ForwardTaskUtil
import com.hahalolo.incognito.presentation.create.forward.container.IncognitoForwardMessageViewModel
import com.hahalolo.incognito.presentation.create.forward.container.contact.adapter.ForwardContactListener
import com.hahalolo.incognito.presentation.create.forward.container.contact.adapter.IncognitoForwardContactAdapter
import com.hahalolo.incognito.presentation.create.forward.container.group.adapter.ForwardGroupListener
import com.hahalolo.incognito.presentation.create.forward.container.group.adapter.IncognitoForwardGroupAdapter
import com.hahalolo.incognito.presentation.create.forward.container.group.adapter.IncognitoForwardGroupDiffUtil
import com.hahalolo.incognito.presentation.create.forward.view.ForwardStatus
import com.hahalolo.incognito.presentation.setting.managerfile.util.IncognitoPreloadHolder
import com.hahalolo.incognito.presentation.setting.model.ContactModel
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.Strings

import com.halo.common.utils.ktx.autoCleared
import com.halo.widget.HaloLinearLayoutManager
import com.halo.widget.recyclerview.RecyclerViewPreloaderFlexbox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class IncognitoForwardMessageGroupFr @Inject constructor() : AbsIncFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: IncognitoForwardMessageViewModel by activityViewModels { viewModelFactory }

    var binding by autoCleared<IncognitoForwardMessageGroupFrBinding>()
    private val requestManager: RequestManager by lazy { Glide.with(this) }

    private var adapter: IncognitoForwardContactAdapter? = null
    private val listTask = mutableListOf<Pair<String, CoroutineScope>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IncognitoForwardMessageGroupFrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initializeLayout() {
        initRec()
        initObserver()
    }

    private fun initRec() {
        binding.apply {
            binding.apply {
                groupRec.layoutManager = HaloLinearLayoutManager(requireContext())
                RecyclerViewUtils.optimization(groupRec, requireActivity())
                adapter = IncognitoForwardContactAdapter(
                    requestManager = requestManager,
                    listener = object : ForwardContactListener {
                        override fun onItemClick(contact: ContactModel) {
                            handleForwardMessage(contact)
                        }
                    }
                )
                groupRec.adapter = adapter?.withLoadStateFooter(
                    footer = IncognitoLoadStateAdapter(
                        object : IncognitoLoadStateCallback {
                            override fun retry() {
                                adapter?.retry()
                            }
                        })
                )
                adapter?.addLoadStateListener {
                    it.source.forEach { loadType, loadState ->
                        when (loadType) {
                            LoadType.APPEND -> {

                            }
                            LoadType.PREPEND -> {

                            }
                            LoadType.REFRESH -> {
                                lifecycleScope.launch {
                                    delay(500)
                                    adapter?.notifyEmpty = adapter?.differ?.itemCount == 0
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.listChannel().collect {
                it.map {
                    ContactModel(it.channelId ?: "", it.name, it.avatar, 3)
                }.let { models ->
                    adapter?.submitData(models)
                }
            }
        }
    }

    private fun handleForwardMessage(item: ContactModel?) {
        item?.let {
            ForwardTaskUtil.createTask(
                item = it,
                listTask = listTask,
                callback = object : ForwardTaskCallback {
                    override fun loading(item: ContactModel) {
                        adapter?.notifyUpdateStatus(item.id, ForwardStatus.LOADING)
                    }

                    override fun cancel(item: ContactModel) {
                        adapter?.notifyUpdateStatus(item.id, ForwardStatus.SEND)
                    }

                    override fun success(item: ContactModel) {
                        adapter?.notifyUpdateStatus(item.id, ForwardStatus.SUCCESS)
                        sendMessage()
                    }
                }
            )
        }
    }

    private fun sendMessage() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        listTask.clear()
    }
}