/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.presentation.write_message

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.adapter.preload.PreloadHolder
import com.hahalolo.messager.presentation.base.AbsMessBackActivity
import com.hahalolo.messager.presentation.group.create.ChatGroupCreateAct
import com.hahalolo.messager.presentation.group.create.adapter.ChannelContactListener
import com.hahalolo.messager.presentation.main.contacts.ChatContactsListener
import com.hahalolo.messager.presentation.main.contacts.adapter.ChatContactsAdapter
import com.hahalolo.messager.presentation.main.contacts.adapter.state.ContactStateAdapter
import com.hahalolo.messager.presentation.main.contacts.detail.PersonalDetailPopup
import com.hahalolo.messager.presentation.message.ChatMessageAct
import com.hahalolo.messager.presentation.write_message.search.ChatWriteSearchInterface
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatWriteMessageActBinding
import com.halo.common.utils.RecyclerViewUtils
import com.halo.data.entities.contact.Contact
import com.halo.data.entities.user.User
import com.halo.widget.HaloLinearLayoutManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ChatWriteMessageAct : AbsMessBackActivity(){
    @Inject
    lateinit var factory: ViewModelProvider.Factory

//    private var listener: ChatWriteSearchInterface?= null
    private var binding: ChatWriteMessageActBinding? = null
    private var viewModel: ChatWriteMessageViewModel? = null
    private val requestManager: RequestManager by lazy { Glide.with(this) }
    private var preloadSizeProvider: ViewPreloadSizeProvider<Contact> =
        ViewPreloadSizeProvider()
    private var adapter: ChatContactsAdapter? = null
    private var disposablesSearch: CompositeDisposable? = null

    private val listener = object: ChatContactsListener {
        override fun detailUser(user: String?, contact: String?) {
            contact?.takeIf { it.isNotEmpty() }?.let { contactId ->
                PersonalDetailPopup.startContactDetail(supportFragmentManager, contactId)
            } ?: kotlin.run {
                errorNetwork()
            }
        }

        override fun onMessage(contact: Contact) {
            startActivity(ChatMessageAct.getIntent(this@ChatWriteMessageAct, contact.userId ?: ""))
        }

    }

    override fun initActionBar() {
        setSupportActionBar(binding?.toolbar)
    }

    override fun isLightTheme(): Boolean {
        return true
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.chat_write_message_act)
        viewModel = ViewModelProvider(this, factory).get(ChatWriteMessageViewModel::class.java)
    }

    override fun initializeLayout() {
        initRec()
        initAction()
        initUserSearch()
        initHandleContacts()
    }

    private fun initHandleContacts() {
        lifecycleScope.launch {
            @OptIn(ExperimentalCoroutinesApi::class)
            viewModel?.contacts?.collectLatest {
                adapter?.submitData(it)
            }
        }
    }

    private fun initUserSearch() {
        lifecycleScope.launch {
            viewModel?.userSearch?.collect {
                when {
                    it.isLoading -> {

                    }
                    it.isSuccess -> {
                        it.data?.let {
                            it.takeIf { it.isNotEmpty() }?.run {
                                binding?.searching = true
                                binding?.userSearchResult?.submitList(this)
                            }
                        }
                    }
                    else -> {
                        binding?.searching = false
                    }
                }
            }
        }
    }

    private fun initRec() {
        preloadSizeProvider = ViewPreloadSizeProvider()
        binding?.usersRec?.layoutManager = HaloLinearLayoutManager(this)
        binding?.usersRec?.setHasFixedSize(true)
        adapter = ChatContactsAdapter(
            false,
            requestManager,
            listener,
            preloadSizeProvider
        )
        val stateAdapter = ContactStateAdapter(adapter)

        val contactAdapter = adapter?.withLoadStateFooter(stateAdapter)

        binding?.usersRec?.adapter = contactAdapter

        binding?.usersRec?.setRecyclerListener { viewHolder ->
            try {
                if (viewHolder is PreloadHolder) {
                    (viewHolder as PreloadHolder).invalidateLayout(requestManager)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding?.searching = false
        binding?.userSearchResult?.initView()

        RecyclerViewUtils.optimization(binding?.usersRec, this)
    }

    private fun initAction() {
        binding?.navigationBt?.setOnClickListener {
            finish()
        }
        binding?.createGroup?.setOnClickListener {
            startActivity(ChatGroupCreateAct.getIntentCreate(this))
            finish()
        }

        binding?.searchView?.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.takeIf { it.isNotEmpty() }?.let { keyword ->
                    updateSearch(keyword)
                } ?: kotlin.run {
                    binding?.searching = false
                }
                return false
            }
        })
    }

    private fun updateSearch(keyword: String) {
        observeDelaySearch(300, object: DisposableObserver<Long>() {
            override fun onNext(t: Long) {

            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
                viewModel?.keywordData?.value = keyword
            }
        })
    }

    private fun observeDelaySearch(time: Long, longDisposableObserver: DisposableObserver<Long>) {
        if (disposablesSearch == null) disposablesSearch = CompositeDisposable()
        disposablesSearch?.clear()
        disposablesSearch?.add(
            Observable.timer(time, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(longDisposableObserver)
        )
    }

    companion object {

        fun getIntent(context: Context): Intent {
            return Intent(context, ChatWriteMessageAct::class.java)
        }
    }

//    override fun detailUser(user: String?, contact: String?) {
//        contact?.takeIf { it.isNotEmpty() }?.let { contactId ->
//            PersonalDetailPopup.startContactDetail(supportFragmentManager, contactId)
//        } ?: kotlin.run {
//            errorNetwork()
//        }
//    }
//
//    override fun onMessage(contact: Contact) {
//        startActivity(ChatMessageAct.getIntent(this, contact.userId ?: ""))
//    }
}