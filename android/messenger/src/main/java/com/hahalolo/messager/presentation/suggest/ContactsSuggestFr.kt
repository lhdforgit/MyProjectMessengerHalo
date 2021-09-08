/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.suggest

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
import com.hahalolo.messenger.databinding.ChatContactSuggestFrBinding
import com.halo.common.utils.ktx.autoCleared
import javax.inject.Inject

/**
 * @author ndn
 * Created by ndn
 * Created on 06/27/2020.
 */
class ContactsSuggestFr @Inject
constructor() : AbsMessFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding: ChatContactSuggestFrBinding by autoCleared()
    lateinit var viewModel: ContactsSuggestViewModel

    private val requestManager: RequestManager by lazy { Glide.with(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_contact_suggest_fr, container, false)
        return binding.root
    }

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ContactsSuggestViewModel::class.java)
    }

    override fun initializeLayout() {
       
    }

}
