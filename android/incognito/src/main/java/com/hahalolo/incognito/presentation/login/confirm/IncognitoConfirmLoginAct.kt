package com.hahalolo.incognito.presentation.login.confirm

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoConfirmLoginActBinding
import com.hahalolo.incognito.presentation.base.AbsIncActivity
import com.hahalolo.incognito.presentation.controller.IncognitoController
import com.hahalolo.incognito.presentation.main.IncognitoMainAct
import com.halo.common.utils.ktx.launchActivity
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import com.halo.widget.otp.OTPListener
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/26/21
 * com.hahalolo.incognito.presentation.main
 */
@ActivityScoped
class IncognitoConfirmLoginAct
@Inject constructor() : AbsIncActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var controller: IncognitoController

    var binding by notNull<IncognitoConfirmLoginActBinding>()
    val viewModel: IncognitoConfirmLoginViewModel by viewModels { factory }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_confirm_login_act)
    }

    override fun initializeLayout() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is LatestNewUiState.Nothing -> {
                            binding.indicator = false
                        }
                        is LatestNewUiState.Loading -> {
                            binding.indicator = true
                        }
                        is LatestNewUiState.Error -> {
                            binding.indicator = false
                            showErrorLogin(uiState.error)
                        }
                        is LatestNewUiState.Success -> {
                            binding.indicator = false
                            launchActivity<IncognitoMainAct> {}
                            overridePendingTransition(
                                android.R.anim.fade_in,
                                android.R.anim.fade_out
                            )
                            finishAffinity()
                        }
                        is LatestNewUiState.ResendSuccess -> {
                            binding.indicator = false
                            // TODO: Bắt đầu đếm ngược
                        }
                    }
                }
            }
        }
        initActions()
    }

    private fun initActions() {
        binding.apply {
            otpView.requestFocusOTP()
            otpView.otpListener = object : OTPListener {
                override fun onInteractionListener() {
                }

                override fun onOTPComplete(otp: String) {
                    val phone = intent.getStringExtra(PHONE_ARGS)
                    phone?.apply {
                        viewModel.verify(phone, otp, this@IncognitoConfirmLoginAct)
                    }
                }
            }
            resentBt.setOnClickListener {
                viewModel.resend()
            }
        }
    }

    private fun showErrorLogin(mess: String?) {
        MaterialAlertDialogBuilder(this)
            .setMessage(mess)
            .setNegativeButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        const val PHONE_ARGS = "IncognitoConfirmLoginAct-Phone-Args"
        fun getIntent(context: Context, phone: String?): Intent {
            val intent = Intent(context, IncognitoConfirmLoginAct::class.java)
            intent.putExtra(PHONE_ARGS, phone)
            return intent
        }
    }
}