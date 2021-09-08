/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.startapp.signin

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halo.data.common.utils.Strings
import com.halo.data.entities.mess.halo.HaloAuthorBody
import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.entities.mongo.login.LoginEntity
import com.halo.data.repository.oauth.MessRepository
import com.halo.presentation.messApplication
import com.halo.workmanager.user.UserWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author ndn
 * Created by ndn
 * Created on 6/8/18.
 */
class SignInViewModel
@Inject constructor(
    private val messRepository: MessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LatestNewUiState>(LatestNewUiState.Nothing)
    val uiState: StateFlow<LatestNewUiState> = _uiState

    /**
     * When value change, [MutableLiveData] Set value to [SignInViewModel.signIn]
     *
     * @param model [LoginEntity]
     */
    fun updateSignIn(model: LoginEntity, context: Context) {
        viewModelScope.launch {
            val oauth = HaloAuthorBody(
                account = model.username,
                password = Strings.md5(model.password)
            )
            messRepository.haloAuthorize(oauth, null).collect {
                when {
                    it.isLoading -> {
                        _uiState.value = LatestNewUiState.Loading
                    }
                    it.isError -> {
                        _uiState.value = LatestNewUiState.Error(it.message)
                    }
                    it.isSuccess -> {
                        _uiState.value = LatestNewUiState.Success
                        it.data?.apply {
                            if (token.isEmpty()) {
                                _uiState.value = LatestNewUiState.Error(it.message)
                            } else {
                                // Cập nhật Oauth vào App
                                messApplication.oauth = this
                                _uiState.value = LatestNewUiState.Success
                            }
                        } ?: kotlin.run {
                            _uiState.value = LatestNewUiState.Error(it.message)
                        }
                    }
                }
            }
        }
    }
}

sealed class LatestNewUiState {
    object Nothing : LatestNewUiState()
    object Loading : LatestNewUiState()
    object Success : LatestNewUiState()
    data class Error(var error: String?) : LatestNewUiState()
}