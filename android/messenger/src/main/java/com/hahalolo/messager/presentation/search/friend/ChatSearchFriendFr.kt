/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.search.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.presentation.base.AbsMessFragment
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatSearchFriendFrBinding
import javax.inject.Inject

class ChatSearchFriendFr @Inject
constructor() : AbsMessFragment() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var binding: ChatSearchFriendFrBinding? = null
    private var viewModel: ChatSearchFriendViewModel? = null
    private var requestManager: RequestManager? = null

    private fun getRequestManager(): RequestManager {
        if (requestManager == null) requestManager = Glide.with(this)
        return requestManager!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.chat_search_friend_fr, container, false)
        return binding?.root
    }

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ChatSearchFriendViewModel::class.java)
    }

    override fun initializeLayout() {

    }
}
