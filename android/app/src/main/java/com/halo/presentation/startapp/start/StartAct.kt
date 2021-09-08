/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.presentation.startapp.start

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.presentation.login.IncognitoLoginAct
import com.hahalolo.playcore.language.LanguageApp
import com.hahalolo.playcore.language.PlayCoreLanguageAct
import com.hahalolo.playcore.split.SplitLanguageAction
import com.hahalolo.playcore.split.SplitLanguageUtils
import com.halo.R
import com.halo.common.utils.KeyboardUtils
import com.halo.common.utils.ktx.launchActivity
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.transparentStatusNavigationBar
import com.halo.databinding.StartActBinding
import com.halo.presentation.appLang
import com.halo.presentation.base.AbsActivity
import com.halo.presentation.common.language.v2.DialogShowPercentDowloadLanguage
import com.halo.presentation.startapp.signin.SignInAct
import timber.log.Timber
import javax.inject.Inject


/**
 * @author ndn
 * Created by ndn
 * Created on 6/11/18.
 */
class StartAct : AbsActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<StartActBinding>()
    val viewModel: StartViewModel by viewModels { factory }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.start_act)
    }

    override fun isLightTheme(): Boolean {
        return false
    }

    override fun initializeLayout() {
        transparentStatusNavigationBar(false)
        analytics.logEventStartApp()
        binding.apply {
            hahaloloBt.setOnClickListener {
                launchActivity<SignInAct> { }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
            incognitoBt.setOnClickListener {
                launchActivity<IncognitoLoginAct> { }
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finishAffinity()
            }
            langVi.setOnClickListener {
                PlayCoreLanguageAct.changeLanguage(LanguageApp.VIETNAMESE, this@StartAct)
            }
            langEn.setOnClickListener {
                PlayCoreLanguageAct.changeLanguage(LanguageApp.ENGLISH, this@StartAct)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            KeyboardUtils.hideSoftInput(this)
            updateLanguageState()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateLanguageState() {
        binding.apply {
            if (appLang == "en") {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    langEn.setTextColor(resources.getColor(R.color.primary, null))
                    langVi.setTextColor(resources.getColor(R.color.text_body, null))
                } else {
                    langEn.setTextColor(resources.getColor(R.color.primary))
                    langVi.setTextColor(resources.getColor(R.color.text_body))
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    langVi.setTextColor(resources.getColor(R.color.primary, null))
                    langEn.setTextColor(resources.getColor(R.color.text_body, null))
                } else {
                    langVi.setTextColor(resources.getColor(R.color.primary))
                    langEn.setTextColor(resources.getColor(R.color.text_body))
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context?): Intent {
            return Intent(context, StartAct::class.java)
        }
    }
}