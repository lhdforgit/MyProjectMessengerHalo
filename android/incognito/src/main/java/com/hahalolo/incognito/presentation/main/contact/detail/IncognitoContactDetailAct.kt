package com.hahalolo.incognito.presentation.main.contact.detail

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoContactDetailActBinding
import com.hahalolo.incognito.presentation.base.AbsIncBackActivity
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class IncognitoContactDetailAct
@Inject constructor() : AbsIncBackActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoContactDetailActBinding>()
    val viewModel: IncognitoContactDetailViewModel by viewModels { factory }

    override fun initActionBar() {
        setSupportActionBar(binding.toolbar)
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_contact_detail_act)
    }

    override fun initializeLayout() {
        initAction()
    }

    private fun initAction() {
        binding.apply {

        }
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, IncognitoContactDetailAct::class.java)
        }
    }
}