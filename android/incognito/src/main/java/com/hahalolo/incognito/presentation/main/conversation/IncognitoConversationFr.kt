package com.hahalolo.incognito.presentation.main.conversation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoConversationFrBinding
import com.hahalolo.incognito.presentation.base.AbsIncFragment
import com.hahalolo.incognito.presentation.conversation.IncognitoConversationAct
import com.hahalolo.incognito.presentation.main.conversation.adapter.IncognitoConversationAdapter
import com.hahalolo.incognito.presentation.main.conversation.adapter.IncognitoConversationListener
import com.halo.common.utils.ktx.autoCleared
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 6/1/21
 * com.hahalolo.incognito.presentation.main.conversation
 */
class IncognitoConversationFr
@Inject constructor() : AbsIncFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private var binding by autoCleared<IncognitoConversationFrBinding>()
    private val viewModel: IncognitoConversationFrViewModel by viewModels { factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.incognito_conversation_fr,
            container, false
        )
        return binding.root
    }

    override fun initializeLayout() {
        initRecycleView()
    }

    private fun initRecycleView() {

        binding.recyclerView.adapter = IncognitoConversationAdapter(object : IncognitoConversationListener{
            override fun onClick() {
                navigateConversation()
            }
        })

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            LinearLayoutManager.VERTICAL
        )
        ContextCompat.getDrawable(requireContext(), R.drawable.bg_incognito_conversation_divider)?.run {
            dividerItemDecoration.setDrawable(this)
        }
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun navigateConversation(){
        startActivity(IncognitoConversationAct.getIntent(requireContext()))
    }

}