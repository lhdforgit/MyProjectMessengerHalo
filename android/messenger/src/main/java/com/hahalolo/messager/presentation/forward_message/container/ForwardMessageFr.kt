package com.hahalolo.messager.presentation.forward_message.container

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadType
import androidx.paging.map
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.adapter.paging.MessengerLoadStateAdapter
import com.hahalolo.messager.presentation.adapter.paging.MessengerLoadStateCallback
import com.hahalolo.messager.presentation.base.AbsMessFragment
import com.hahalolo.messager.presentation.forward_message.ForwardMessageBottomSheet
import com.hahalolo.messager.presentation.forward_message.ForwardModel
import com.hahalolo.messager.presentation.forward_message.util.ForwardMessageUtil
import com.hahalolo.messager.presentation.forward_message.util.ForwardStatus
import com.hahalolo.messager.presentation.forward_message.util.ForwardTaskCallback
import com.hahalolo.messager.presentation.main.contacts.detail.PersonalDetailPopup
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ForwardMessageFrBinding
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.ktx.autoCleared
import com.halo.widget.HaloLinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ForwardMessageFr @Inject constructor() : AbsMessFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var binding by autoCleared<ForwardMessageFrBinding>()
    private var viewModel: ForwardMessageViewModel? = null

    private val requestManager: RequestManager by lazy { Glide.with(this) }
    private var adapter: ForwardMessageChannelAdapter? = null
    private val listTask = mutableListOf<Pair<String, CoroutineScope>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.forward_message_fr, container, false)
        return binding.root
    }

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ForwardMessageViewModel::class.java)
        arguments?.getString(ForwardMessageBottomSheet.FORWARD_MESSAGE_ID)
            ?.takeIf { it.isNotEmpty() }
            ?.let { messageId ->

            }
    }

    override fun initializeLayout() {
        initRec()
        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel?.listChannel()?.collect {
                it.map {
                    ForwardModel(it.channelId ?: "", it.channelName(), it.channelAvatars().firstOrNull(), ForwardStatus.SEND)
                }.let { models ->
                    adapter?.submitData(models)
                }
            }
        }
    }

    private fun initRec() {
        binding.apply {
            val preloadSizeProvider = ViewPreloadSizeProvider<ForwardModel>()
            contactRec.layoutManager = HaloLinearLayoutManager(requireContext())
            RecyclerViewUtils.optimization(contactRec, requireActivity())
            adapter = ForwardMessageChannelAdapter(
                requestManager = requestManager,
                listener = object : ForwardMessageListener {
                    override fun onItemClick(contact: ForwardModel) {
                        handleForwardMessage(contact)
                    }
                },
                preloadSizeProvider = preloadSizeProvider
            )
            contactRec.adapter = adapter?.withLoadStateFooter(
                footer = MessengerLoadStateAdapter(
                    object : MessengerLoadStateCallback {
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

    private fun handleForwardMessage(item: ForwardModel?) {
        item?.let {
            ForwardMessageUtil.createTask(
                item = it,
                listTask = listTask,
                callback = object : ForwardTaskCallback {
                    override fun loading(item: ForwardModel) {
                        adapter?.notifyUpdateStatus(item.id, ForwardStatus.LOADING)
                    }

                    override fun cancel(item: ForwardModel) {
                        adapter?.notifyUpdateStatus(item.id, ForwardStatus.SEND)
                    }

                    override fun success(item: ForwardModel) {
                        adapter?.notifyUpdateStatus(item.id, ForwardStatus.SUCCESS)
                    }
                }
            )
        }
    }
}