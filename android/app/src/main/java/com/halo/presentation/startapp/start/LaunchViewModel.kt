/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.startapp.start

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.halo.data.entities.mess.oauth.MessOauth
import com.halo.data.repository.oauth.HasLogin
import com.halo.data.repository.oauth.HasLoginHahalolo
import com.halo.data.repository.oauth.MessRepository
import com.halo.presentation.HahaloloAppManager
import com.halo.workmanager.user.UserWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * @author ndn
 * Created by ndn
 * Created on 6/11/18.
 *
 * A. Kiểm tra xem có phải mở App từ Hahalolo không.
 *
 * ** Mở messenger từ hahalolo **
 * 1. Nếu user chưa đăng nhập messenger: Khi nhấn vào icon MSGR sẽ mở trang đăng nhập
 * 2. Nếu user đã đăng nhập messenger: Khi nhấn vào icon MSGR
 * 2.1. Cùng tài khoản với HHLL: Mở giao diện chat messenger
 * 2.2. Khác tài khoản với HHLL: Hiển thị modal xác nhận tài khoản
 * 2.2.1. Hiển thị avatar user của tài khoản HHLL
 * 2.2.2. Tiêu đề: Tên hiển thị
 * 2.2.3. Nội dung: Bạn tiếp tục đăng nhập messenger bằng tài khoản này?
 * 2.2.4. Tiếp tục: Khi nhấn vào button: Thực hiện đăng nhập messenger bởi tài khoản vừa xác nhận
 * 2.2.5. Huỷ: Khi nhấn vào button: Mở giao diện messenger với tài khoản đã đăng nhập trước đó
 *
 * B. Nếu không phải mở App từ Hahalolo.
 * 1. Kiểm tra xem đã đăng nhập bằng tài khoản nào. Hahalolo hay Incognito
 * 1.1. Nếu đăng nhập bằng Hahalolo mở App Hahalolo.
 * 1.2. Nếu đăng nhập bằng Incognito thì mở App Incognito.
 *
 * C. Nếu chưa đăng nhập thì đi đến màn hình Start
 */
class LaunchViewModel
@Inject constructor(
    val hahaloloAppManager: HahaloloAppManager,
    private val messRepository: MessRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LatestNewUiState>(LatestNewUiState.Nothing(null))
    val uiState: StateFlow<LatestNewUiState> = _uiState

    private val isOpenFromHahalolo: Boolean
        get() = hahaloloAppManager.isOpenFromHahalolo
    val token: String?
        get() = hahaloloAppManager.dataMess?.accessToken
    val userId: String?
        get() = hahaloloAppManager.dataMess?.userId


    fun makeLoginRequest() {
        Timber.i("LaunchViewModel $isOpenFromHahalolo")
        if (isOpenFromHahalolo) {
            viewModelScope.launch {
                messRepository.hasLoginByHahalolo(token, userId).collect { hasLoginByHahalolo ->
                    when (hasLoginByHahalolo) {
                        is HasLoginHahalolo.NoLoginByHahalolo -> {
                            _uiState.value = LatestNewUiState.OpenLoginHahalolo(token)
                        }
                        is HasLoginHahalolo.LoginByHahaloloNotAccount -> {
                            _uiState.value = LatestNewUiState.OpenHasLoginHahalolo(token)
                        }
                        is HasLoginHahalolo.LoginByHahaloloSameAccount -> {
                            val oauth = hasLoginByHahalolo.oauth
                            // TODO: Connect Socket oauth
                            _uiState.value = LatestNewUiState.OpenHahaloloMess(oauth)
                        }
                    }
                }
            }
        } else {
            viewModelScope.launch {
                messRepository.hasLogin().collect { hasLogin ->
                    when (hasLogin) {
                        is HasLogin.NoLogin -> {
                            _uiState.value = LatestNewUiState.OpenStartLogin(token)
                        }
                        is HasLogin.LoginByHahalolo -> {
                            val oauth = hasLogin.oauth
                            // TODO: Connect Socket oauth
                            _uiState.value = LatestNewUiState.OpenHahaloloMess(oauth)
                        }
                        is HasLogin.LoginByIncognito -> {
                            val oauth = hasLogin.oauth
                            // TODO: Connect Socket oauth
                            _uiState.value = LatestNewUiState.OpenIncognitoMess(oauth)
                        }
                    }
                }
            }
        }
    }
}

sealed class LatestNewUiState {
    data class Nothing(val token: String?) : LatestNewUiState()

    /** Nếu user chưa đăng nhập messenger: Khi nhấn vào icon MSGR sẽ mở trang đăng nhập */
    data class OpenLoginHahalolo(val token: String?) : LatestNewUiState()

    /** Nếu user đã đăng nhập messenger: Khác tài khoản với HHLL: Hiển thị modal xác nhận tài khoản */
    data class OpenHasLoginHahalolo(val token: String?) : LatestNewUiState()

    /** Nếu user đã đăng nhập messenger: Cùng tài khoản với HHLL: Mở giao diện chat messenger*/
    data class OpenHahaloloMess(var oauth: MessOauth?) : LatestNewUiState()

    /** Nếu đăng nhập bằng Incognito thì mở App Incognito. */
    data class OpenIncognitoMess(var oauth: MessOauth?) : LatestNewUiState()

    /** Nếu chưa đăng nhập thì đi đến màn hình Start */
    data class OpenStartLogin(val token: String?) : LatestNewUiState()
}