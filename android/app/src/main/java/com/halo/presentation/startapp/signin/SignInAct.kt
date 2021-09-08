/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.startapp.signin

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import com.hahalolo.messager.presentation.main.ChatAct
import com.halo.R
import com.halo.common.utils.KeyboardUtils
import com.halo.common.utils.ktx.launchActivity
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.setSafeOnClickListener
import com.halo.common.utils.ktx.transparentStatusNavigationBar
import com.halo.data.common.utils.Strings
import com.halo.data.entities.mongo.login.LoginEntity
import com.halo.databinding.SigninActBinding
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
 *
 *
 * TODO: Always show error 401. Click ok but not dismiss this
 */
class SignInAct : AbsActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var hahaloloAppManager: HahaloloAppManager

    var binding by notNull<SigninActBinding>()
    val viewModel: SignInViewModel by viewModels { factory }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.signin_act)
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
            // Hide keyboard when user click outside
            wrapper.setOnClickListener { KeyboardUtils.hideSoftInput(this@SignInAct) }
            wrapper.setOnTouchListener { _: View?, event: MotionEvent ->
                if (event.action == MotionEvent.ACTION_DOWN
                    || event.action == MotionEvent.ACTION_MOVE
                ) {
                    KeyboardUtils.hideSoftInput(this@SignInAct)
                    return@setOnTouchListener true
                }
                false
            }

            loginBt.setSafeOnClickListener { doSignIn() }
            passwordEdt.imeOptions = EditorInfo.IME_ACTION_DONE
            passwordEdt.setOnEditorActionListener { _: TextView?, i: Int, _: KeyEvent? ->
                if (i == EditorInfo.IME_ACTION_DONE) {
                    doSignIn()
                    return@setOnEditorActionListener true
                }
                false
            }

            closeBt.setOnClickListener {
                finish()
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                when (uiState) {
                    is LatestNewUiState.Loading -> {
                        binding.indicator = true
                        return@collect
                    }
                    is LatestNewUiState.Error -> {
                        binding.indicator = false
                        showErrorLogin()
                        return@collect
                    }
                    is LatestNewUiState.Success -> {
                        binding.indicator = false
                        launchActivity<ChatAct> {}
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finishAffinity()
                        return@collect
                    }
                    else -> {
                        binding.indicator = false
                        return@collect
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

    /**
     * Do sign in
     * Check username pattern
     * Check password pattern
     * Hide keyboard
     * UpdateChat sign in observer
     */
    private fun doSignIn() {
        try {
            binding.usernameEdt.error = null
            binding.passwordEdt.error = null
            if (binding.usernameEdt.text == null
                    || TextUtils.isEmpty(binding.usernameEdt.text.toString())) {
                Toast.makeText(this@SignInAct, getString(R.string.sign_in_username_error_null), Toast.LENGTH_LONG).show()
                return
            } else if (!Strings.isValidPhone(binding.usernameEdt.text.toString())) {
                if (!Strings.isValidEmail(binding.usernameEdt.text.toString())) {
                    Toast.makeText(this@SignInAct, getString(R.string.sign_in_username_error_phone_email_regex), Toast.LENGTH_LONG).show()
                    return
                }
            }
            if (binding.passwordEdt.text == null || TextUtils.isEmpty(binding.passwordEdt.text.toString())) {
                Toast.makeText(this@SignInAct, getString(R.string.sign_in_password_error_null), Toast.LENGTH_LONG).show()
                return
            }
            KeyboardUtils.hideSoftInput(this)
            viewModel.updateSignIn(
                LoginEntity(
                    binding.usernameEdt.text.toString(),
                    binding.passwordEdt.text.toString()
                ),
                this
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context?): Intent {
            return Intent(context, SignInAct::class.java)
        }
    }
}