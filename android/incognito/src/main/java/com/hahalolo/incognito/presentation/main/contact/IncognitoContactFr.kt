package com.hahalolo.incognito.presentation.main.contact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoContactFrBinding
import com.hahalolo.incognito.presentation.base.AbsIncFragment
import com.hahalolo.incognito.presentation.main.contact.adapter.IncognitoContactAdapter
import com.hahalolo.incognito.presentation.main.contact.adapter.IncognitoContactListener
import com.hahalolo.incognito.presentation.main.contact.detail.IncognitoContactDetailAct
import com.halo.common.utils.ktx.autoCleared
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 6/1/21
 * com.hahalolo.incognito.presentation.main.contact
 */
class IncognitoContactFr
@Inject constructor(): AbsIncFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private var binding by autoCleared<IncognitoContactFrBinding>()
    private val viewModel: IncognitoContactFrViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.incognito_contact_fr,
            container, false
        )
        return binding.root
    }

    override fun initializeLayout() {
        initRecycleView()
    }

    private fun initRecycleView() {
        binding.recyclerView.adapter = IncognitoContactAdapter(object: IncognitoContactListener{
            override fun onClick() {
                navigateContactDetail()
            }
        })
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }

    private fun navigateContactDetail(){
        startActivity(IncognitoContactDetailAct.getIntent(requireContext()))
    }
}