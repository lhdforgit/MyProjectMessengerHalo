/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.startapp.haslogin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.repository.oauth.MessRepository
import com.halo.presentation.HahaloloAppManager
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
class SignInHasLoginViewModel
@Inject constructor(
    private val messRepository: MessRepository,
    private val haloAppManager: HahaloloAppManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LatestNewUiState>(LatestNewUiState.Nothing)
    val uiState: StateFlow<LatestNewUiState> = _uiState

    val token: String?
        get() = haloAppManager.dataMess?.accessToken

    fun signIn() {
        viewModelScope.launch {
            messRepository.haloAuthorize(null, token).collect {
                when {
                    it.isLoading -> {
                        _uiState.value = LatestNewUiState.Loading
                    }
                    it.isError -> {
                        _uiState.value = LatestNewUiState.Error(it.message)
                    }
                    it.isSuccess -> {
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

    fun notMe() {
        viewModelScope.launch {
            messRepository.oauth().collect {
                _uiState.value = LatestNewUiState.Loading
                it?.apply {
                    // Cập nhật Oauth vào App
                    messApplication.oauth = MessOauth(
                        token = this.token,
                        refreshToken = this.refreshToken
                    )
                    _uiState.value = LatestNewUiState.Success
                } ?: kotlin.run {
                    _uiState.value = LatestNewUiState.Error("Oauth is empty")
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