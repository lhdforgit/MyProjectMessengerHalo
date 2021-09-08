package com.hahalolo.messager.presentation.search.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hahalolo.messager.presentation.base.AbsMessFragment
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.databinding.ChatSearchUserFrBinding
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.ktx.autoCleared
import com.halo.widget.HaloLinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class ChatSearchUserFr @Inject constructor() : AbsMessFragment() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var viewModel: ChatSearchUserViewModel? = null
    private var binding by autoCleared<ChatSearchUserFrBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ChatSearchUserFrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initializeViewModel() {
        super.initializeViewModel()
        viewModel = ViewModelProvider(this, factory).get(ChatSearchUserViewModel::class.java)
    }

    override fun initializeLayout() {
        initObserver()
        initAction()
    }

    private fun initAction() {
        binding.apply {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    query?.takeIf { it.isNotEmpty() }?.let { keyword ->
                        updateSearch(keyword)
                    } ?: kotlin.run {
                        binding.userResult.submitList(mutableListOf())
                    }
                    return false
                }
            })
        }
    }

    private fun updateSearch(keyword: String) {
        viewModel?.keywordData?.value = keyword
    }

    private fun initObserver() {
        lifecycleScope.launchWhenCreated {
            viewModel?.users?.collectLatest {
                when {
                    it.isLoading -> {

                    }
                    it.isSuccess -> {
                        it.data?.let { users ->
                            binding.userResult.submitList(users)
                        } ?: kotlin.run {

                        }
                    }
                    else -> {
                        errorNetwork()
                    }
                }
            }
        }
    }

}