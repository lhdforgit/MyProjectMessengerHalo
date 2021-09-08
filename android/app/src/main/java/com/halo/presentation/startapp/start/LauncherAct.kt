/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.halo.presentation.startapp.start

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.internal.ConfigFetchHandler
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.hahalolo.incognito.presentation.main.IncognitoMainAct
import com.hahalolo.messager.presentation.main.ChatAct
import com.halo.R
import com.halo.common.utils.ktx.launchActivity
import com.halo.presentation.HahaloloAppManager
import com.halo.presentation.base.AbsActivity
import com.halo.presentation.isForceVersionApp
import com.halo.presentation.messApplication
import com.halo.presentation.startapp.haslogin.SignInHasLoginAct
import com.halo.presentation.startapp.signin.SignInAct
import com.hahalolo.playcore.update.PlayCoreUpdateAct
import com.halo.workmanager.user.UserWorker
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Xác định được App cần force update không.
 * 1. Nếu có thì hiễn thị màn hình update.
 * 2. Tới các màn hình tiếp theo.
 * ---->
 * 1. Nếu trường hợp App chưa đăng nhập thì hiễn thị màn hình chọn chức năng đăng nhập Hahalolo hay Ẩn danh.
 * 2. Nếu trường hợp đã đăng nhập, phải xác định được đăng nhập Hahalolo hay Ẩn danh.
 * 3. Nếu đã đăng nhập tài khoản khác trên Hahalolo, thì tiếp tục hiện màn hình hỏi có đăng nhập bằng account Hahalolo hay không.
 */
class LauncherAct : AbsActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var hahaloloAppManager: HahaloloAppManager

    val viewModel: LaunchViewModel by viewModels { factory }

    private var remoteConfig: FirebaseRemoteConfig? = null

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hahaloloAppManager.getIntentDataFromHahaloloApp(intent)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is LatestNewUiState.Nothing -> {
                            Timber.i("Launch Act Nothing")
                        }
                        is LatestNewUiState.OpenStartLogin -> {
                            Timber.i("Launch Act Open Start Login ")
                            launchActivity<StartAct> {  }
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finishAffinity()
                        }
                        is  LatestNewUiState.OpenIncognitoMess -> {
                            Timber.i("Launch Act Open Incognito Mess")
                            messApplication.oauth = uiState.oauth
                            UserWorker.updateUserOauth(this@LauncherAct)
                            launchActivity<IncognitoMainAct> {  }
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finishAffinity()
                        }
                        is LatestNewUiState.OpenHahaloloMess -> {
                            Timber.i("Launch Act Open Hahalolo Mess")
                            messApplication.oauth = uiState.oauth
                            UserWorker.updateUserOauth(this@LauncherAct)
                            launchActivity<ChatAct> {  }
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finishAffinity()
                        }
                        is LatestNewUiState.OpenLoginHahalolo -> {
                            Timber.i("Launch Act Open Login Hahalolo")
                            launchActivity<SignInAct> {  }
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            finishAffinity()
                        }
                        is LatestNewUiState.OpenHasLoginHahalolo -> {
                            Timber.i("Launch Act Open Has Login Hahalolo")
                            launchActivity<SignInHasLoginAct> { }
                            overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                            finishAffinity()
                        }
                    }
                }
            }
        }

        viewModel.makeLoginRequest()
        /*initFirebaseRemoteConfig()*/
    }

    /**** Firebase Remote Config *****/
    private fun initFirebaseRemoteConfig() {
        kotlin.runCatching {
            remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings {
                minimumFetchIntervalInSeconds =
                    ConfigFetchHandler.DEFAULT_MINIMUM_FETCH_INTERVAL_IN_SECONDS
            }
            remoteConfig?.setConfigSettingsAsync(configSettings)
            remoteConfig?.setDefaultsAsync(R.xml.remote_config_defaults)
            fetchUpdateVersionApp()
        }.getOrElse {
            it.printStackTrace()
            viewModel.makeLoginRequest()
        }
    }

    private fun fetchUpdateVersionApp() {
        kotlin.runCatching {
            remoteConfig?.fetchAndActivate()
                ?.addOnCompleteListener(this) { task ->
                    kotlin.runCatching {
                        if (task.isSuccessful) {
                            val updated = task.result
                            Timber.i("Config params updated: $updated")
                        } else {
                            Timber.i("Fetch failed")
                        }

                        val lastVersion: Double =
                            remoteConfig?.getDouble(ANDROID_LAST_VERSION_CODE_KEY) ?: -1.0
                        val forceVersion: Double =
                            remoteConfig?.getDouble(ANDROID_FORCE_VERSION_CODE_KEY) ?: -1.0

                        Timber.i("Config value: lastVersion ($lastVersion) - forceVersion ($forceVersion)")

                        messApplication.firebaseConfigManager.firebaseRemoteConfigLastVersionCode =
                            lastVersion
                        messApplication.firebaseConfigManager.firebaseRemoteConfigForceVersionCode =
                            forceVersion
                        // Trường hợp App cần cập nhật
                        if (isForceVersionApp) {
                            val newIntent = PlayCoreUpdateAct.getIntent(this)
                            startActivity(newIntent)
                            finish()
                        } else {
                            viewModel.makeLoginRequest()
                        }
                    }.getOrElse {
                        it.printStackTrace()
                        viewModel.makeLoginRequest()
                    }
                }
        }.getOrElse {
            it.printStackTrace()
            viewModel.makeLoginRequest()
        }
    }

    companion object {
        // Remote Config keys
        private const val ANDROID_LAST_VERSION_CODE_KEY = "android_last_version"
        private const val ANDROID_FORCE_VERSION_CODE_KEY = "android_force_version"
    }
}
