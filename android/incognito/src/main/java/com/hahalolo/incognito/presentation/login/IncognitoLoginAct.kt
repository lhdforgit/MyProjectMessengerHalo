package com.hahalolo.incognito.presentation.login

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoLoginActBinding
import com.hahalolo.incognito.presentation.base.AbsIncActivity
import com.hahalolo.incognito.presentation.controller.IncognitoController
import com.hahalolo.incognito.presentation.login.confirm.IncognitoConfirmLoginAct
import com.halo.common.utils.CountryCodeUtil
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.setSafeOnClickListener
import com.halo.common.utils.ktx.updateStateButton
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/26/21
 * com.hahalolo.incognito.presentation.main
 */
@ActivityScoped
class IncognitoLoginAct
@Inject constructor() : AbsIncActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var controller: IncognitoController

    var binding by notNull<IncognitoLoginActBinding>()
    val viewModel: IncognitoLoginViewModel by viewModels { factory }

    private val iosDefault by lazy { CountryCodeUtil.getDefaultCountryCode(this) }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_login_act)
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
                            startActivity(
                                IncognitoConfirmLoginAct.getIntent(
                                    this@IncognitoLoginAct,
                                    binding.countryCode.fullNumber
                                )
                            )
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

        binding.apply {
            startBtn.setSafeOnClickListener {
                val phoneNumber = countryCode.fullNumber
                viewModel.login(phoneNumber, null)
            }

            countryCode.setDefaultCountryUsingNameCode(/*iosDefault*/"vn")
            countryCode.resetToDefaultCountry()
            countryCode.registerCarrierNumberEditText(phoneEdt)
            countryCode.setPhoneNumberValidityChangeListener { isValidNumber ->
                if (isValidNumber) {
                    startBtn.updateStateButton(true)
                } else {
                    startBtn.updateStateButton(false)
                }
            }

            phoneEdt.imeOptions = EditorInfo.IME_ACTION_DONE
            phoneEdt.setOnEditorActionListener { _: TextView?, i: Int, _: KeyEvent? ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (countryCode.isValidFullNumber) {
                        val phoneNumber = countryCode.fullNumber
                        viewModel.login(phoneNumber, null)
                        return@setOnEditorActionListener true
                    } else {
                        return@setOnEditorActionListener false
                    }
                }
                false
            }

            notMeBt.setSafeOnClickListener {
                controller.startLogin()
                finishAffinity()
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
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoLoginAct::class.java)
        }
    }
}