/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.main.group

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.bubble.conversation.home.adapter.ChatAdapterListener
import com.hahalolo.messager.bubble.conversation.home.adapter.RoomDiffCallback
import com.hahalolo.messager.bubble.conversation.home.adapter.SearchInputListener
import com.hahalolo.messager.bubble.conversation.home.adapter.v2.Conversation2Adapter
import com.hahalolo.messager.presentation.adapter.holder.NetworkStateListener
import com.hahalolo.messager.presentation.base.AbsMessFragment
import com.hahalolo.messager.presentation.group.create.ChatGroupCreateAct
import com.hahalolo.messager.presentation.main.ChatActListener
import com.hahalolo.messager.presentation.message.ChatMessageAct
import com.hahalolo.messager.presentation.message.ChatMessageResultAction
import com.hahalolo.messager.presentation.search.ChatSearchAct
import com.hahalolo.messager.presentation.search.ChatSearchTab
import com.halo.data.room.entity.ChannelEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatHomeGroupFrBinding
import com.halo.common.utils.KeyboardUtils
import com.halo.common.utils.RecyclerViewUtils
import com.halo.constant.HaloConfig
import com.hahalolo.messager.presentation.main.contacts.owner.ChatOwnerAct
import com.halo.widget.HaloLinearLayoutManager
import com.halo.widget.recyclerview.RecyclerViewPreloader
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
class ChatHomeGroupFr @Inject constructor() : AbsMessFragment(),
    ChatAdapterListener<ChannelEntity>, NetworkStateListener, SearchInputListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var binding: ChatHomeGroupFrBinding
    private lateinit var viewModel: ChatHomeGroupViewModel

    private val requestManager: RequestManager by lazy { Glide.with(this) }

    private val preloadSizeProvider: ViewPreloadSizeProvider<ChannelEntity> = ViewPreloadSizeProvider()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_home_group_fr, container, false)
        return binding.root
    }

    private var chatActListener: ChatActListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            chatActListener = context as? ChatActListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement PageWallActListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        chatActListener = null
    }

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ChatHomeGroupViewModel::class.java)
    }


    override fun initializeLayout() {
        binding.apply {
            initRecyclerView(rec)
            createGroupBt.setOnClickListener {
                navigateCreateGroup()
            }
        }
        initObserver()
    }

    private fun navigateSearchMain(){
        appController.navigateSearchMain()
    }

    private fun navigatePersonalWall(){
        startActivity(ChatOwnerAct.getIntent(requireContext()))
    }

    private fun navigateCreateGroup(){
        startActivity(ChatGroupCreateAct.getIntentCreate(requireContext()))
    }

    fun initRecyclerView(recyclerView: RecyclerView) {
        try {
            recyclerView.layoutManager = HaloLinearLayoutManager(context)
            (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            recyclerView.setHasFixedSize(true)
            RecyclerViewUtils.optimization(recyclerView, activity)

            val adapter = Conversation2Adapter(
                listener = this,
                requestManager = requestManager,
                context = requireContext(),
                viewPreloadSizeProvider = preloadSizeProvider,
                diffCallback = RoomDiffCallback(appController.ownerId),
                networkStateListener = this,
                userIdToken = userIdToken,
                appLang = appController.appLang()
            )

            adapter.searchListener = this

            val preloader = RecyclerViewPreloader(
                requestManager,
                adapter,
                preloadSizeProvider,
                HaloConfig.PRELOAD_AHEAD_ITEMS
            )
            recyclerView.addOnScrollListener(preloader)
            recyclerView.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initObserver() {
//        viewModel.groupPageList.observe(viewLifecycleOwner, { listData ->
//            (binding.rec.adapter as? Conversation2Adapter)?.run {
//                submitList(listData?: mutableListOf())
//                binding.empty = listData.isNullOrEmpty()
//            }
//        })
    }

    override fun itemClick(t: ChannelEntity) {
        startActivityForResult(ChatMessageAct.getIntentChannel(requireContext(), t.channelId()), REQUEST_MESSAGE)
    }

    override fun search() {
        startActivity(ChatSearchAct.getIntent(requireContext(), ChatSearchTab.CHAT_SEARCH_GROUP))
    }

    override fun onSearch(query: String?) {
        observeDelaySearch(object : DisposableObserver<Long>() {
            override fun onComplete() {
                viewModel.updateQuery(query)
            }

            override fun onNext(t: Long) {
            }

            override fun onError(e: Throwable) {
            }
        })
    }

    override fun onCloseSearch(input: EditText) {
        KeyboardUtils.hideSoftInput(input)
    }

    override fun onOpenSearch(input: EditText) {
        KeyboardUtils.showSoftInput(input)
    }

    override fun onClickMenuMore(room: ChannelEntity) {
        chatActListener?.onClickMenuMore(room)
    }

    override fun onClickMenuRemove(room: ChannelEntity) {
        chatActListener?.onClickMenuRemove(room)
    }

    private var disposablesSearch: CompositeDisposable? = null

    private fun observeDelaySearch( longDisposableObserver: DisposableObserver<Long>) {
        if (disposablesSearch == null) disposablesSearch = CompositeDisposable()
        disposablesSearch?.clear()
        disposablesSearch!!.add(
            Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(longDisposableObserver)
        )
    }

    private var disposables: CompositeDisposable? = null

    override fun onDestroy() {
        disposables?.clear()
        disposablesSearch?.clear()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_MESSAGE ->{
                    data?.getBooleanExtra(ChatMessageResultAction.FINISH_CHAT, false)?.takeIf { it }?.run {
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    companion object{
        const val REQUEST_MESSAGE = 111

    }
}