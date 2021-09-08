/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.presentation.write_message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.hahalolo.messager.utils.Strings
import com.halo.data.repository.contact.ContactRepository
import com.halo.data.repository.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ChatWriteMessageViewModel @Inject
internal constructor(
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository,
    appController: MessengerController
) : AbsMessViewModel(appController) {

    val token = MutableLiveData<String>(token())
    val keywordData = MutableLiveData<String>()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val contacts = flowOf(
        token.asFlow().flatMapLatest { token ->
            contactRepository.contactPaging(
                token = token
            )
        }
    ).flattenMerge(2)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val userSearch = flowOf(
        keywordData.asFlow().flatMapLatest { keyword ->
            userRepository.searchUser(
                token = token(),
                keyword = keyword
            )
        }
    ).flattenMerge(2)

}