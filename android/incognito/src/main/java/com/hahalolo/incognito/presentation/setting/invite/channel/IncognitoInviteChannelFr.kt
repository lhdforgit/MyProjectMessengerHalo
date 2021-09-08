package com.hahalolo.incognito.presentation.setting.invite.channel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.hahalolo.incognito.databinding.IncognitoInviteChannelFrBinding
import com.hahalolo.incognito.presentation.base.AbsIncFragment
import com.hahalolo.incognito.presentation.setting.invite.IncognitoInviteFriendViewModel
import com.hahalolo.incognito.presentation.setting.invite.adapter.IncognitoInviteContactAdapter
import com.hahalolo.incognito.presentation.setting.invite.adapter.IncognitoInviteContactListener
import com.hahalolo.incognito.presentation.setting.invite.adapter.InviteContactLoadStateAdapter
import com.hahalolo.incognito.presentation.setting.invite.adapter.LoadStateCallback
import com.hahalolo.incognito.presentation.setting.invite.link.IncognitoInviteUrlBottomSheet
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.ktx.autoCleared
import com.halo.common.utils.list.ListUtils
import com.halo.data.entities.contact.Contact
import com.halo.widget.HaloLinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class IncognitoInviteChannelFr @Inject constructor() : AbsIncFragment(),
    IncognitoInviteContactListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var viewModel: IncognitoInviteFriendViewModel? = null
    private var binding by autoCleared<IncognitoInviteChannelFrBinding>()
    private var adapter: IncognitoInviteContactAdapter? = null
    private val requestManager by lazy { Glide.with(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = IncognitoInviteChannelFrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initializeViewModel() {
        viewModel = ViewModelProviders.of(requireActivity(), factory)
            .get(IncognitoInviteFriendViewModel::class.java)
    }

    override fun initializeLayout() {
        initRec()
        initObserver()
    }

    private fun initRec() {
        binding.apply {
            adapter = IncognitoInviteContactAdapter(this@IncognitoInviteChannelFr, requestManager)
            contactRec.layoutManager = HaloLinearLayoutManager(requireContext())
            RecyclerViewUtils.optimization(contactRec, activity)

            contactRec.adapter = adapter?.withLoadStateFooter(
                footer = InviteContactLoadStateAdapter(
                    object :
                        LoadStateCallback {
                        override fun retry() {
                            adapter?.retry()
                        }
                    })
            )
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel?.apply {
                listContact().collect {
                    adapter?.submitData(it)
                }
            }
        }
    }

    override fun onSearch(query: String) {

    }

    override fun onGetLinkInvite() {
        val memu = IncognitoInviteUrlBottomSheet(requireContext())
        memu.show(childFragmentManager, "IncognitoInviteChannelFr")
    }

    override fun onInviteFriend(contact: Contact) {
        viewModel?.apply {
            contact.contactId?.let {
                listId.add(it)
            }
        }
    }

    override fun isInvited(contactId: String): Boolean {
        viewModel?.apply {
            return ListUtils.isDataExits(listId) { input -> TextUtils.equals(input, contactId) }
        }
        return false
    }

    private fun navigateShareMore(
        activity: Activity,
        embed: String?
    ) {
        try {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, embed)
            sendIntent.type = "text/plain"

            val shareIntent = Intent.createChooser(
                sendIntent,
                ""
            )
            activity.startActivity(shareIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}