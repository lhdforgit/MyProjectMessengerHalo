package com.hahalolo.playcore.language

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.playcore.R
import com.hahalolo.playcore.controller.PlayCoreController
import com.hahalolo.playcore.databinding.PlayCoreLanguageActBinding
import com.hahalolo.playcore.split.DaggerSplitActivity
import com.hahalolo.playcore.split.SplitLanguageAction
import com.hahalolo.playcore.split.SplitLanguageUtils
import com.hahalolo.playcore.split.SplitLanguageUtils.Split.Companion.create
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@ActivityScoped
class PlayCoreLanguageAct : DaggerSplitActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var controller: PlayCoreController

    var binding by notNull<PlayCoreLanguageActBinding>()
    val viewModel: PlayCoreLanguageViewModel by viewModels { factory }

    private val callback = object : SplitLanguageAction {
        override fun installFailure(message: String) {
            Timber.i("Update Language Install Failure: $message")
            binding.caption.text = message
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                finish()
            }
        }

        override fun showProcessUpdateLanguage(max: Int, currValue: Int, message: Int) {
            Timber.i("Progress Update Language: $currValue")
            binding.indicator.max = max
            binding.indicator.progress = currValue
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.play_core_language_act)

        intent?.apply {
            val lang = getStringExtra(LANGUAGE_ARGS) ?: LanguageApp.ENGLISH
            try {
                SplitLanguageUtils.changeLanguage(
                    create(this@PlayCoreLanguageAct, callback),
                    lang
                ) {
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(1000)
                        finish()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                binding.caption.text = "${e.message}"
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1000)
                    finish()
                }
            }
        }

        binding.apply {
            title.text = getString(R.string.playcore_caption_loading)
        }
    }

    companion object {
        const val LANGUAGE_ARGS = "PlayCoreLanguageAct-Lang-Args"
        fun changeLanguage(lang: String, context: Context) {
            val intent = Intent(context, PlayCoreLanguageAct::class.java)
            intent.putExtra(LANGUAGE_ARGS, lang)
            context.startActivity(intent)
        }
    }
}