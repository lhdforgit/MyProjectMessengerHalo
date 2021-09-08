package com.hahalolo.messager.presentation.main.contacts.owner

import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.halo.data.common.resource.Resource
import com.halo.data.entities.contact.Contact
import com.halo.data.entities.user.User
import com.halo.data.repository.contact.ContactRepository
import com.halo.data.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatOwnerViewModel
@Inject
constructor(
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository,
    appController: MessengerController,
) : AbsMessViewModel(appController) {

    fun clearChatCache() {
//        chatRepository.onLogout()
    }

    suspend fun owner(): Flow<Resource<Contact>>{
        return contactRepository.userMe(
            token()
        )
    }

//    suspend fun userDetail(): Flow<Resource<User>> {
//        return userRepository.user(personalId, token())
//    }
}