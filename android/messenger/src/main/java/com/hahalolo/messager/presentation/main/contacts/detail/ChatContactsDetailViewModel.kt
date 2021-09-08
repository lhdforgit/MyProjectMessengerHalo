package com.hahalolo.messager.presentation.main.contacts.detail

import androidx.lifecycle.MutableLiveData
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.presentation.base.AbsMessViewModel
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileModel
import com.hahalolo.messager.utils.Strings
import com.halo.data.common.resource.Resource
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.contact.*
import com.halo.data.entities.user.User
import com.halo.data.repository.contact.ContactRepository
import com.halo.data.repository.user.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class ChatContactsDetailViewModel
@Inject constructor(
    private val contactRepository: ContactRepository,
    private val userRepository: UserRepository,
    appController: MessengerController,
) : AbsMessViewModel(appController) {

    private val _uiState = MutableStateFlow<ContactUiState>(ContactUiState.Nothing(null))
    val uiState: MutableStateFlow<ContactUiState> = _uiState

    val body = MutableLiveData<String>()
    var personalId = ""
    var isContactDetail = false

    var contactResponse = MutableLiveData<Contact>()

    private val contactEntity: Contact?
        get() = contactResponse.value

    val contactName: String?
        get() = contactEntity?.aliasName

    suspend fun userDetail(): Flow<Resource<User>> {
        return userRepository.user(personalId, token())
    }

    suspend fun contactDetail() {
        return contactRepository.contactDetail(
            token(),
            personalId
        ).collect {
            when {
                it.isLoading -> {

                }
                it.isSuccess -> {
                    it.data?.run {
                        setContact(this)
                    }
                }
                else -> {
                    uiState.value = ContactUiState.Error(null)
                }
            }
        }
    }

    suspend fun updateContactName(name: String) {
        val body = ContactBody(aliasName = name, status = ContactType.CONTACTED)
        contactRepository.updateName(
            token = token(),
            contactId = personalId,
            body = body
        ).collect {
            when {
                it.isLoading -> {

                }
                it.isSuccess -> {
                    it.data?.run {
                        setContact(this)
                    }
                }
                else -> {
                    uiState.value = ContactUiState.Error(null)
                }
            }
        }
    }

    suspend fun updateContactAliasName(name: String) {
        val body = ContactAliasBody(aliasName = name)
        contactRepository.updateAlias(
            token = token(),
            contactId = personalId,
            body = body
        ).collect {
            when {
                it.isLoading -> {

                }
                it.isSuccess -> {
                    it.data?.run {
                        setContact(this)
                    }
                }
                else -> {
                    uiState.value = ContactUiState.Error(null)
                }
            }
        }
    }

    private fun setContact(contact: Contact) {
        contactResponse.value = contact
    }
}

sealed class ContactUiState {
    data class Nothing(val message: String?) : ContactUiState()
    data class Loading(val message: String?) : ContactUiState()
    data class Error(val message: String?) : ContactUiState()
    data class Success(val message: String?) : ContactUiState()
    data class ChannelDetail(val data: Channel?) : ContactUiState()
    data class ChannelDelete(val data: Channel?) : ContactUiState()
    data class ChannelMedia(val data: MutableList<MessengerManagerFileModel>?) : ContactUiState()
}