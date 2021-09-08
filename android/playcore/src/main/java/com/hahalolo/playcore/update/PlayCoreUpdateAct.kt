/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.playcore.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.playcore.R
import com.hahalolo.playcore.databinding.PlayCoreUpdateAppActBinding
import com.hahalolo.playcore.split.DaggerSplitActivity
import com.halo.common.utils.ktx.notNull
import javax.inject.Inject

/**
 * @author ndn
 * Created by ndn
 * Created on 6/11/18.
 */
class PlayCoreUpdateAct : DaggerSplitActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<PlayCoreUpdateAppActBinding>()
    val viewModel: PlayCoreUpdateViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.play_core_update_app_act)
        binding.apply {
            toolbar.setNavigationOnClickListener {
                finish()
            }
            updateBt.setOnClickListener {
                launchGooglePlay()
            }
        }
    }

    private fun launchGooglePlay() {
        kotlin.runCatching {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.hahalolo.android.social"
                )
                setPackage("com.android.vending")
            }
            startActivity(intent)
            finish()
        }.getOrElse {
            it.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context?): Intent {
            return Intent(context, PlayCoreUpdateAct::class.java)
        }
    }
}