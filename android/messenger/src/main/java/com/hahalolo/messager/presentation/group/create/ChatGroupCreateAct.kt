/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.create

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.LoadType
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendChooseListener
import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendSelectData
import com.hahalolo.messager.presentation.adapter.paging.MessengerLoadStateAdapter
import com.hahalolo.messager.presentation.adapter.paging.MessengerLoadStateCallback
import com.hahalolo.messager.presentation.base.AbsMessBackActivity
import com.hahalolo.messager.presentation.group.create.adapter.ChannelContactListener
import com.hahalolo.messager.presentation.group.create.adapter.ChannelContactPageListAdapter
import com.hahalolo.messager.presentation.message.ChatMessageAct
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatGroupCreateActBinding
import com.halo.common.utils.RecyclerViewUtils
import com.halo.common.utils.list.ListUtils
import com.halo.data.entities.channel.ChannelBody
import com.halo.data.worker.channel.CreateChannelWorker
import com.halo.widget.HaloLinearLayoutManager
import com.halo.widget.anim.ViewAnim
import kotlinx.android.synthetic.main.message_keybroad_view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatGroupCreateAct : AbsMessBackActivity() {
    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private var binding: ChatGroupCreateActBinding? = null
    private var viewModel: ChatGroupCreateViewModel? = null

    private var adapter: ChannelContactPageListAdapter? = null

    private val requestManager by lazy { Glide.with(this) }


    private val listener = object : ChannelContactListener {
        override fun isChecked(userId: String): Boolean {
            return viewModel?.isChecked(userId) ?: false
        }

        override fun onClickItem(data: FriendSelectData, checked: Boolean) {
            if (checked) {
                viewModel?.addContactSelected(data)
                viewModel?.addContactSelected(data)
            } else {
                viewModel?.removeContactSelected(data)
            }
        }

        override fun isMember(userId: String?): Boolean {
            return false
        }

        override fun onSearchFriend(query: String?) {

        }

        override fun onRetryNetwork() {

        }
    }

    override fun initActionBar() {

    }

    override fun isLightTheme(): Boolean {
        return true
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.chat_group_create_act)
        viewModel = ViewModelProvider(this, factory).get(ChatGroupCreateViewModel::class.java)
        intent.getStringExtra(GROUP_ID_ADD_MEMBER_PARAMS)?.takeIf { it.isNotEmpty() }?.run {
            viewModel?.channelId = this
            binding?.isAddMember = viewModel?.channelId?.isNotEmpty() ?: false
        }
        intent?.getParcelableExtra<FriendSelectData>(FRIEND_SELECT)?.let {
            viewModel?.addContactSelected(it)
        }
    }

    override fun initializeLayout() {
        initView()
        initUserSearch()
        initObserver()
    }

    private fun isFullUserInGroup(data: FriendSelectData): Boolean {
        val newCount = viewModel?.listFriendChoose?.value?.size ?: 0
        val curentCount = viewModel?.listMembers?.value?.size ?: 0
        var maxCount = MAX_MEMBER_IN_GROUP
        binding?.isAddMember?.takeIf { it }?.run {
            maxCount = MAX_MEMBER_IN_GROUP
        } ?: kotlin.run {
            maxCount = MAX_MEMBER_IN_GROUP - 1
        }
        val full = newCount + curentCount >= maxCount
        if (full) {
            showWarningMaxNumber()
            updateItemFriendsChanged(data)
        }
        return full
    }

    private fun showWarningMaxNumber() {
        binding?.root?.run {
            Snackbar.make(
                this,
                getString(
                    R.string.chat_message_max_member,
                    MAX_MEMBER_IN_GROUP
                ),
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateItemFriendsChanged(data: FriendSelectData?) {
        val index = ListUtils.getPosition(adapter?.snapshot()) { input ->
            TextUtils.equals(data?.userId, input?.userId)
        }
        if (index >= 0) {
            adapter?.updateStatusCheck(index)
        }
        binding?.userSearchResult?.apply {
            currentList?.let { snapshot ->
                val indexSearch = ListUtils.getPosition(snapshot) { input ->
                    TextUtils.equals(data?.userId, input?.userId)
                }
                if (indexSearch >= 0) {
                    notifyItemChanged(indexSearch)
                }
            }
        }

    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel?.run {
                listContact().collect {
                    adapter?.submitData(it)
                }
            }
        }
        viewModel?.listFriendChoose?.observe(this@ChatGroupCreateAct, Observer {
            binding?.groupSelectView?.bind(it)
            ViewAnim.Builder.create(binding?.groupSelectView)
                .hideDown()
                .duration(500)
                .show(!it.isNullOrEmpty())
        })
    }

    private fun initUserSearch() {
        lifecycleScope.launch {
            viewModel?.userSearch?.collect {
                when {
                    it.isLoading -> {

                    }
                    it.isSuccess -> {
                        it.data?.let {
                            it.takeIf { it.isNotEmpty() }?.run {
                                binding?.isSearch = true
                                binding?.userSearchResult?.submitList(it)
                            }
                        }
                    }
                    else -> {
                        binding?.isSearch = false
                    }
                }
            }
        }
    }


    private fun initView() {
        adapter = ChannelContactPageListAdapter(
            listener,
            requestManager
        )
        binding?.apply {
            // init Search user
            isSearch = false
            userSearchResult.initView(listener)

            navigation.setOnClickListener {
                finish()
            }
            usersRec.layoutManager = HaloLinearLayoutManager(this@ChatGroupCreateAct)
            RecyclerViewUtils.optimization(usersRec, this@ChatGroupCreateAct)

            usersRec.adapter = adapter?.withLoadStateFooter(
                footer = MessengerLoadStateAdapter(
                    object :
                        MessengerLoadStateCallback {
                        override fun retry() {
                            adapter?.retry()
                        }
                    })
            )
            adapter?.run {
                addLoadStateListener {
                    if (it.append.endOfPaginationReached) {
                        lifecycleScope.launch {
                            delay(500)
                            adapter?.empty = adapter?.differ?.itemCount == 0
                        }
                    }
                    it.source.forEach { loadType, loadState ->
                        when (loadType) {
                            LoadType.APPEND -> {
                            }
                            LoadType.PREPEND -> {

                            }
                            LoadType.REFRESH -> {
                                lifecycleScope.launch {
                                    delay(500)
                                    adapter?.error = loadState is LoadState.Error
                                }
                            }
                        }
                    }
                }
            }
            groupSelectView.run {
                isAddMember = viewModel?.channelId?.isNotEmpty() ?: false
                this.requestManager = this@ChatGroupCreateAct.requestManager
                listener = object : FriendChooseListener {
                    override fun onRemoveFriendSelected(position: Int, data: FriendSelectData) {
                        lifecycleScope.launch {
                            delay(100)
                            viewModel?.removeContactSelected(data)
                            updateItemFriendsChanged(data)
                        }
                    }

                    override fun onCreateGroup(groupName: String) {
                        if (isAddMember) {
                            onAddMemberGroupChat()
                        } else {
                            groupName.takeIf { it.isEmpty() }?.run {
                                Toast.makeText(
                                    this@ChatGroupCreateAct,
                                    "Tên nhóm là bắt buộc.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } ?: kotlin.run {
                                onCreateGroupChat(groupName)
                            }
                        }
                    }
                }
            }

            this.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    query?.takeIf { it.isNotEmpty() }?.let {
                        viewModel?.keywordData?.value = it
                    } ?: kotlin.run {
                        binding?.isSearch = false
                    }
                    return false
                }
            })

        }
    }

    private fun onCreateGroupChat(groupName: String) {
        viewModel?.apply {
            val body = ChannelBody(
                name = groupName,
                avatar = null,
                userIds = listContactId()
            )
            lifecycleScope.launch {
                val idWorker = createChannel(this@ChatGroupCreateAct, body)
                WorkManager.getInstance(this@ChatGroupCreateAct).getWorkInfoByIdLiveData(idWorker)
                    .observe(this@ChatGroupCreateAct, { workInfo ->
                        workInfo?.let {
                            when (it.state) {
                                WorkInfo.State.RUNNING -> {
                                    showIndicator()
                                }
                                WorkInfo.State.SUCCEEDED -> {
                                    dismissIndicator()
                                    finish()
                                    it.outputData.getString(CreateChannelWorker.CHANNEL_ID_RESULT)
                                        ?.let { channelId ->
                                            navigateChannel(channelId)
                                        }
                                }
                                WorkInfo.State.FAILED -> {
                                    dismissIndicator()
                                }
                                else -> {
                                    dismissIndicator()
                                }
                            }
                        }
                    })
            }
        }
    }

    private fun navigateChannel(channelId: String?) {
        startActivity(ChatMessageAct.getIntentChannel(this, channelId ?: ""))
    }

    private fun onAddMemberGroupChat() {
        lifecycleScope.launch {
            viewModel?.inviteJoinChannel(this@ChatGroupCreateAct)?.let { idWorker ->
                WorkManager.getInstance(this@ChatGroupCreateAct).getWorkInfoByIdLiveData(idWorker)
                    .observe(this@ChatGroupCreateAct, { workInfo ->
                        workInfo?.let {
                            when (it.state) {
                                WorkInfo.State.RUNNING -> {
                                    showIndicator()
                                }
                                WorkInfo.State.SUCCEEDED -> {
                                    dismissIndicator()
                                    finish()
                                }
                                WorkInfo.State.FAILED -> {
                                    dismissIndicator()
                                }
                                else -> {
                                    dismissIndicator()
                                }
                            }
                        }
                    })
            }
        }
    }

    companion object {
        private const val REQUEST_MESSAGE = 111
        private const val MAX_MEMBER_IN_GROUP = 150   // Max member in group is 150
        private const val FRIEND_SELECT = "CreateGroupAct_FRIEND_SELECT"

        private const val GROUP_ID_ADD_MEMBER_PARAMS = "CreateGroupAct_GROUP_ID"

        fun getIntentAddMember(context: Context, roomId: String): Intent {
            val intent = Intent(context, ChatGroupCreateAct::class.java)
            intent.putExtra(GROUP_ID_ADD_MEMBER_PARAMS, roomId)
            return intent
        }

        fun getIntentCreate(context: Context): Intent {
            return Intent(context, ChatGroupCreateAct::class.java)
        }

        fun getIntentCreateWith(
            context: Context,
            friendSelect: FriendSelectData
        ): Intent {
            val intent = Intent(context, ChatGroupCreateAct::class.java)
            intent.putExtra(FRIEND_SELECT, friendSelect)
            return intent
        }
    }
}