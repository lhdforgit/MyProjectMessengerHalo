/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.main.conversation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.presentation.base.AbsMessFragment
import com.hahalolo.messager.presentation.main.ChatActListener
import com.hahalolo.messager.presentation.message.ChatMessageAct
import com.hahalolo.messager.presentation.write_message.ChatWriteMessageAct
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatConversationListFrBinding
import com.halo.common.utils.*
import com.halo.widget.HaloLinearLayoutManager
import javax.inject.Inject
import com.hahalolo.messager.presentation.main.conversation.adapter.ConversationAdapter
import com.hahalolo.messager.presentation.main.conversation.adapter.ConversationListener
import com.hahalolo.pickercolor.util.setVisibility
import com.halo.data.common.paging.NetworkState
import com.halo.data.entities.channel.Channel
import com.halo.data.room.entity.ChannelEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
class ConversationListFr @Inject
constructor() : AbsMessFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private lateinit var binding : ChatConversationListFrBinding
    private lateinit var viewModel: ConversationListViewModel

    private val requestManager: RequestManager by lazy { Glide.with(this) }

    private var chatActListener: ChatActListener? = null

    private var adapter: ConversationAdapter? = null

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.chat_conversation_list_fr,
            container, false
        )
        return binding.root
    }

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ConversationListViewModel::class.java)
    }

    override fun initializeLayout() {
        initActions()
        initRecyclerView(binding.rec)
        initHandleChannels()
    }


    private fun initActions(){
        binding.errorBt.setOnClickListener {
            viewModel.refresh()
        }
        binding.createMessBt.setOnClickListener {
            navigateWriteMessage()
        }
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        try {
            recyclerView.layoutManager = HaloLinearLayoutManager(context)
            (recyclerView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            recyclerView.setHasFixedSize(false)
            RecyclerViewUtils.optimization(recyclerView, requireActivity())

            adapter = ConversationAdapter(
                appController = appController,
                listener = object : ConversationListener{
                    override fun onClick(channel: ChannelEntity) {
                        navigateChannel(channel.channelId())
                    }

                    override fun onClickMenuMore(channelEntity: ChannelEntity) {

                    }

                    override fun onClickMenuRemove(channelEntity: ChannelEntity) {

                    }
                },
                requestManager = requestManager
            )

            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        //todo hide keybroad
                    }
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    viewModel.channelPaging
                        .value
                        ?.takeIf { !recyclerView.canScrollVertically(1) }
                        ?.onItemAtEndLoaded()
                }
            })
            recyclerView.adapter = adapter

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initHandleChannels() {
        viewModel.channelState.observe(this, {
            it.second?.run {
                //state loadmore

            }?: it.first?.run {
                binding.loading = this == NetworkState.LOADING
                binding.empty = this == NetworkState.LOADED  && viewModel.channelPaging.value?.isEmpty==true
                binding.error = this == NetworkState.ERROR &&  viewModel.channelPaging.value?.isEmpty==true
            }
        })

        viewModel.channels.observe(this, Observer {
            adapter?.submitList(it ?: mutableListOf())
        })
    }

    private fun navigateWriteMessage() {
        startActivityForResult(ChatWriteMessageAct.getIntent(requireContext()), REQUEST_WRITE_MESSAGE)
    }

    private fun navigateChannel(channelId: String) {
        startActivity(ChatMessageAct.getIntentChannel(requireContext(), channelId))
    }

    companion object{
        private const val REQUEST_MESSAGE = 111
        private const val REQUEST_WRITE_MESSAGE = 112
    }
}