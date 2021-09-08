/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.startapp.haslogin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.hahalolo.messager.presentation.main.ChatAct
import com.halo.R
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.ktx.launchActivity
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.setSafeOnClickListener
import com.halo.common.utils.ktx.transparentStatusNavigationBar
import com.halo.databinding.SigninHasLoginActBinding
import com.halo.presentation.HahaloloAppManager
import com.halo.presentation.base.AbsActivity
import com.halo.widget.materialdialogs.MaterialDialog
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author ndn
 * Created by ndn
 * Created on 5/14/18.
 */
class SignInHasLoginAct : AbsActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var hahaloloAppManager: HahaloloAppManager

    var binding by notNull<SigninHasLoginActBinding>()
    val viewModel: SignInHasLoginViewModel by viewModels { factory }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.signin_has_login_act)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transparentStatusNavigationBar()
    }

    override fun isLightTheme(): Boolean {
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initializeLayout() {
        binding.apply {
            notMeBt.setSafeOnClickListener {
                viewModel.notMe()
            }
            loginBt.setSafeOnClickListener {
                viewModel.signIn()
            }
            GlideRequestBuilder.getCircleCropRequest(Glide.with(this@SignInHasLoginAct))
                .load(hahaloloAppManager.dataMess?.avatar)
                .error(com.hahalolo.messenger.R.drawable.community_avatar_holder)
                .placeholder(com.hahalolo.messenger.R.drawable.community_avatar_holder)
                .into(avatar)
            hahaloloAppManager.dataMess?.name?.apply {
                usernameTv.text = this
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is LatestNewUiState.Loading -> {
                            binding.indicator = true
                        }
                        is LatestNewUiState.Success -> {
                            binding.indicator = false
                            launchActivity<ChatAct> {}
                            overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                            finishAffinity()
                        }
                        is LatestNewUiState.Error -> {
                            binding.indicator = false
                            showErrorLogin()
                        }
                        else -> {
                            binding.indicator = false
                        }
                    }
                }
            }
        }
    }

    private fun showErrorLogin() {
        val dialog = MaterialDialog(this)
        dialog.message(R.string.sign_in_error_login_message, null)
        dialog.cancelable(true)
        dialog.positiveButton(R.string.sign_in_error_login_negative, null) {
            dialog.dismiss()
        }
        dialog.show()
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context?): Intent {
            return Intent(context, SignInHasLoginAct::class.java)
        }
    }
}