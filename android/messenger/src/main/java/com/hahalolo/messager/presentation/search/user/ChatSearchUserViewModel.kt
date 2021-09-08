package com.hahalolo.messager.presentation.search.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.hahalolo.messager.utils.Strings
import com.halo.data.repository.user.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ChatSearchUserViewModel @Inject
constructor(
   private val userRepository: UserRepository,
    appController: MessengerController,
) : AbsMessViewModel(appController) {

    val keywordData = MutableLiveData<String>()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val users = flowOf(
        keywordData.asFlow().flatMapLatest { keyword ->
            userRepository.searchUser(
                token = token(),
                keyword = keyword
            )
        }
    ).flattenMerge(2)
}