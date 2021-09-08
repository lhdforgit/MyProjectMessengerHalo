package com.hahalolo.incognito.presentation.login.confirm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hahalolo.incognito.presentation.controller.IncognitoController
import com.halo.data.entities.mess.VerifyBody
import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.repository.oauth.MessRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/26/21
 * com.hahalolo.incognito.presentation.main
 */
class IncognitoConfirmLoginViewModel
@Inject constructor(
    private val messRepository: MessRepository,
    val controller: IncognitoController
) : ViewModel() {

    val phone: String? = null

    private val _uiState = MutableStateFlow<LatestNewUiState>(LatestNewUiState.Nothing)
    val uiState: StateFlow<LatestNewUiState> = _uiState

    fun verify(
        phone: String?,
        code: String?,
        context: Context
    ) {
        viewModelScope.launch {
            val verify = VerifyBody(
                phone = phone,
                code = code
            )
            messRepository.verify(verify).collect {
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
                                controller.setOauthToApplication(this)
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

    fun resend() {
        viewModelScope.launch {
            val oauth = VerifyBody(
                phone = phone
            )
            messRepository.authorize(oauth).collect {
                when {
                    it.isLoading -> {
                        _uiState.value = LatestNewUiState.Loading
                    }
                    it.isError -> {
                        _uiState.value = LatestNewUiState.Error(it.message)
                    }
                    it.isSuccess -> {
                        _uiState.value = LatestNewUiState.ResendSuccess
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
    object ResendSuccess : LatestNewUiState()
    data class Error(var error: String?) : LatestNewUiState()
}