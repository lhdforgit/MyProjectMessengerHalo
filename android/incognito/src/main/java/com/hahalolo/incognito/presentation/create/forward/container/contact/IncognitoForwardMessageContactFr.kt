package com.hahalolo.incognito.presentation.create.forward.container.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadType
import androidx.paging.map
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoForwardMessageContactFrBinding
import com.hahalolo.incognito.presentation.base.AbsIncFragment
import com.hahalolo.incognito.presentation.base.IncognitoLoadStateAdapter
import com.hahalolo.incognito.presentation.base.IncognitoLoadStateCallback
import com.hahalolo.incognito.presentation.create.forward.container.ForwardTaskCallback
import com.hahalolo.incognito.presentation.create.forward.container.ForwardTaskUtil
import com.hahalolo.incognito.presentation.create.forward.container.IncognitoForwardMessageViewModel
import com.hahalolo.incognito.presentation.create.forward.container.contact.adapter.*
import com.hahalolo.incognito.presentation.create.forward.view.ForwardStatus
import com.hahalolo.incognito.presentation.setting.model.ContactModel
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.ktx.autoCleared
import com.halo.widget.HaloLinearLayoutManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class IncognitoForwardMessageContactFr @Inject constructor() : AbsIncFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    var binding by autoCleared<IncognitoForwardMessageContactFrBinding>()
    private val viewModel: IncognitoForwardMessageViewModel by activityViewModels { viewModelFactory }
    private val requestManager: RequestManager by lazy { Glide.with(this) }
    private var adapter: IncognitoForwardContactAdapter? = null
    private val listTask = mutableListOf<Pair<String, CoroutineScope>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.incognito_forward_message_contact_fr,
            container,
            false
        )
        return binding.root
    }

    override fun initializeViewModel() {

    }

    override fun initializeLayout() {
        initRec()
        initObserver()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.listContact().collect {
                it.map {
                    ContactModel(it.targetId ?: "", it.firstName ?: "", it.avatar ?: "", 3)
                }.let {
                    adapter?.submitData(it)
                }
            }
        }
    }

    private fun initRec() {
        binding.apply {
            contactRec.layoutManager = HaloLinearLayoutManager(requireContext())
            RecyclerViewUtils.optimization(contactRec, requireActivity())
            adapter = IncognitoForwardContactAdapter(
                requestManager = requestManager,
                listener = object : ForwardContactListener {
                    override fun onItemClick(contact: ContactModel) {
                        handleForwardMessage(contact)
                    }
                }
            )
            contactRec.adapter = adapter?.withLoadStateFooter(
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