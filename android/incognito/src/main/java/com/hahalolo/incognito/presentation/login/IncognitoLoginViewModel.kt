package com.hahalolo.incognito.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halo.data.entities.mess.VerifyBody
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
class IncognitoLoginViewModel
@Inject constructor(
    private val messRepository: MessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LatestNewUiState>(LatestNewUiState.Nothing)
    val uiState: StateFlow<LatestNewUiState> = _uiState

    fun login(
        phone: String?,
        code: String?
    ) {
        viewModelScope.launch {
            val oauth = VerifyBody(
                phone = phone,
                code = code
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
                        _uiState.value = LatestNewUiState.Success
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