/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.main.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.adapter.preload.PreloadHolder
import com.hahalolo.messager.presentation.base.AbsMessFragment
import com.hahalolo.messager.presentation.main.contacts.adapter.ChatContactsAdapter
import com.hahalolo.messager.presentation.main.contacts.adapter.state.ContactStateAdapter
import com.hahalolo.messager.presentation.main.contacts.detail.PersonalDetailPopup
import com.hahalolo.messager.presentation.message.ChatMessageAct
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatContactsFrBinding
import com.halo.common.utils.RecyclerViewUtils
import com.halo.data.entities.contact.Contact
import com.halo.widget.HaloLinearLayoutManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
class ChatContactsFr @Inject
constructor() : AbsMessFragment(), ChatContactsListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var binding: ChatContactsFrBinding
    private lateinit var viewModel: ChatContactsViewModel

    private var preloadSizeProvider: ViewPreloadSizeProvider<Contact> =
        ViewPreloadSizeProvider()

    private val requestManager: RequestManager by lazy { Glide.with(this) }
    private var adapter: ChatContactsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_contacts_fr, container, false)
        return binding.root
    }

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ChatContactsViewModel::class.java)
    }

    override fun initializeLayout() {
        initRec()
        initHandleContacts()
    }

    private fun initHandleContacts() {
        lifecycleScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            viewModel.contacts.collectLatest {
                adapter?.submitData(it)
            }
        }
    }

    private fun initRec() {
        preloadSizeProvider = ViewPreloadSizeProvider()
        binding.rec.layoutManager = HaloLinearLayoutManager(context)
        binding.rec.setHasFixedSize(true)
        adapter = ChatContactsAdapter(
            true,
            requestManager,
            this,
            preloadSizeProvider
        )
        val stateAdapter = ContactStateAdapter(adapter)

        val contactAdapter = adapter?.withLoadStateFooter(stateAdapter)

        binding.rec.adapter = contactAdapter

        binding.rec.setRecyclerListener { viewHolder ->
            try {
                if (viewHolder is PreloadHolder) {
                    (viewHolder as PreloadHolder).invalidateLayout(requestManager)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        adapter?.differ?.addLoadStateListener {
            if (it.refresh is LoadState.NotLoading) {

            }
        }

        RecyclerViewUtils.optimization(binding.rec, activity)
    }

    override fun detailUser(user: String?, contact: String?) {
        contact?.takeIf { it.isNotEmpty() }?.let { contactId ->
            PersonalDetailPopup.startContactDetail(childFragmentManager, contactId)
        }?: kotlin.run {
            errorNetwork()
        }
    }

    override fun onMessage(contact: Contact) {
        startActivity(ChatMessageAct.getIntent(requireContext(), contact.userId ?: ""))
    }

    companion object {
        const val USER_ID = "ChatContactsFr_PAGE_ID"
    }
}
