/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.main.contacts

import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.asFlow
import androidx.paging.PagedList
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.halo.data.common.paging.Listing
import com.halo.data.common.paging.NetworkState
import com.halo.data.entities.contact.Contact
import com.halo.data.repository.contact.ContactRepository
import com.halo.di.AbsentLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
class ChatContactsViewModel @Inject
internal constructor(
    private val contactRepository: ContactRepository,
    appController: MessengerController,
) : AbsMessViewModel(appController) {

    var idContact: Contact? = null
    val token = MutableLiveData<String>(token())

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val contacts = flowOf(
        token.asFlow().flatMapLatest { token ->
            contactRepository.contactPaging(
                token = token
            )
        }
    ).flattenMerge(2)
}
