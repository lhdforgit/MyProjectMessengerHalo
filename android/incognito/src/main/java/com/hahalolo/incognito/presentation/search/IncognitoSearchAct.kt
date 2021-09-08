package com.hahalolo.incognito.presentation.search

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoSearchActBinding
import com.hahalolo.incognito.presentation.base.AbsIncActivity
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.di.ActivityScoped
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/26/21
 * com.hahalolo.incognito.presentation.main
 */
@ActivityScoped
class IncognitoSearchAct
@Inject constructor() : AbsIncActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<IncognitoSearchActBinding>()
    val viewModel: IncognitoSearchViewModel by viewModels { factory }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.incognito_search_act)
    }

    override fun initializeLayout() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                finish()
            }
        }
    }
}