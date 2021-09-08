/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.member

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.filter
import com.bumptech.glide.Glide
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.hahalolo.messager.presentation.adapter.paging.MessengerLoadStateAdapter
import com.hahalolo.messager.presentation.adapter.paging.MessengerLoadStateCallback
import com.hahalolo.messager.presentation.adapter.paging.MessengerPreloadHolder
import com.hahalolo.messager.presentation.base.AbsMessBackActivity
import com.hahalolo.messager.presentation.group.create.ChatGroupCreateAct
import com.hahalolo.messager.presentation.group.create.GroupConstant
import com.hahalolo.messager.presentation.group.member.adapter.MessengerMemberAdapter
import com.hahalolo.messager.presentation.group.member.adapter.MessengerMemberDiffCallback
import com.hahalolo.messager.presentation.group.member.adapter.MessengerMemberListener
import com.hahalolo.messager.presentation.main.ChatAct
import com.hahalolo.messager.presentation.main.contacts.detail.PersonalDetailPopup
import com.hahalolo.messager.presentation.message.ChatMessageAct
import com.hahalolo.messager.presentation.message.ChatMessageResultAction
import com.halo.data.room.entity.MemberEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageMemberActBinding
import com.halo.common.utils.ActivityUtils
import com.halo.common.utils.RecyclerViewUtils
import com.halo.data.common.utils.Strings
import com.halo.data.entities.member.MemberChannel
import com.halo.widget.HaloEditText
import com.halo.widget.HaloLinearLayoutManager
import com.halo.widget.HaloTypefaceSpan
import com.halo.widget.dialog.HaloDialogCustom
import com.halo.widget.recyclerview.RecyclerViewPreloaderFlexbox
import kotlinx.android.synthetic.main.chat_group_create_act.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.Flags

class ChatGroupMemberAct : AbsMessBackActivity(), MessengerMemberListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private var binding: ChatMessageMemberActBinding? = null

    private var viewModel: ChatGroupMemberViewModel? = null

    private var adapter: MessengerMemberAdapter? = null

    private var startChat: Long = 0

    private var startWall: Long = 0

    private val requestManager by lazy { Glide.with(this) }


    override fun isLightTheme(): Boolean {
        return true
    }

    override fun initActionBar() {
        setSupportActionBar(binding?.toolbar)
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.chat_message_member_act)
        viewModel = ViewModelProvider(this, factory).get(ChatGroupMemberViewModel::class.java)
        intent?.apply {
            getBooleanExtra(IS_MEMBER, true).let {
                binding?.isMember = it
            }
            getBooleanExtra(IS_LEAVE_GROUP, false)?.let {
                binding?.isLeave = it
                viewModel?.isShowOwner = !it
            }
            getStringExtra(CHANNEL_ID)?.takeIf { it.isNotEmpty() }?.let {
                viewModel?.channelId?.value = it
            }
        }
    }

    override fun initializeLayout() {
        initRec()
        initObserver()
        getRoleMemberChannel()
        getMemberLogin()
        bindActions()
    }

    private fun initObserver() {
        viewModel?.apply {
            channelId.observe(this@ChatGroupMemberAct, Observer {
                lifecycleScope.launch {
                    memberPaging.collect {
                        if (isShowOwner) {
                            adapter?.submitData(it)
                        } else {
                            it.filter { !it.isOwner }.let {
                                adapter?.submitData(it)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun getRoleMemberChannel() {
        lifecycleScope.launch {
            viewModel?.getRoleChannel()
        }
    }

    private fun getMemberLogin() {
        lifecycleScope.launch {
            viewModel?.getMemberLogin()
        }
    }


    private fun bindActions() {
        binding?.addUserBt?.setOnClickListener { navigateAddMember() }
        binding?.navigationBt?.setOnClickListener { onBackPressed() }
    }

    private fun navigateAddMember() {
        viewModel?.channelId?.value?.takeIf { it.isNotEmpty() }?.let {
            startActivity(ChatGroupCreateAct.getIntentAddMember(this@ChatGroupMemberAct, it))
        } ?: kotlin.run {
            errorNetwork()
        }
    }

    private fun showDialogLeaveGroup(member: MemberChannel) {
        HaloDialogCustom.Builder().apply {
            setIcon(R.drawable.ic_chat_dialog_logout)
            setTitle(getString(R.string.chat_message_lear_group))
            setDescription(
                Strings.getStringStyle(
                    getString(R.string.chat_message_set_new_owner_select_title),
                    member.displayName,
                    HaloTypefaceSpan.BOLD(this@ChatGroupMemberAct)
                )
            )
            setTextWarning(getString(R.string.chat_message_ok_leave_group))
            setOnClickCancel {}
            setOnClickWarning {
                initLeaveChannel(member.userId ?: "")
            }
        }.build().show(supportFragmentManager, "ChatGroupMemberAct")
    }

    private fun initRec() {
        binding?.apply {
            val preloadSizeProvider = ViewPreloadSizeProvider<MemberChannel>()
            listMemberRec.layoutManager = HaloLinearLayoutManager(this@ChatGroupMemberAct)
            RecyclerViewUtils.optimization(binding?.listMemberRec, this@ChatGroupMemberAct)
            adapter = MessengerMemberAdapter(
                isMember ?: true,
                diffCallback = MessengerMemberDiffCallback(),
                preloadSizeProvider = preloadSizeProvider,
                requestManager = requestManager,
                listener = this@ChatGroupMemberAct
            )
            adapter?.let {
                val preloader = RecyclerViewPreloaderFlexbox(
                    requestManager,
                    it,
                    preloadSizeProvider,
                    5
                )
                listMemberRec.addOnScrollListener(preloader)
            }
            listMemberRec.setRecyclerListener { viewHolder ->
                kotlin.runCatching {
                    if (viewHolder is MessengerPreloadHolder) {
                        (viewHolder as MessengerPreloadHolder).invalidateLayout(requestManager)
                    }
                }
            }

            listMemberRec.adapter = adapter?.withLoadStateFooter(
                footer = MessengerLoadStateAdapter(
                    object : MessengerLoadStateCallback {
                        override fun retry() {
                            adapter?.retry()
                        }
                    })
            )
        }
    }

    private fun showPopupMenuMember(member: MemberChannel, view: View) {
        val userIdLogin = appController.ownerId
        val wrapper: Context = ContextThemeWrapper(this@ChatGroupMemberAct, R.style.PopupMenu)
        val popup = PopupMenu(wrapper, view)
        //Inflating the Popup using xml file
        popup.menuInflater
            .inflate(R.menu.chat_message_member_menu, popup.menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.gravity = Gravity.CENTER_HORIZONTAL
        }
        val menu = popup.menu
        val nicknameBt = menu.findItem(R.id.action_set_nickname)
        val removeAdminBt = menu.findItem(R.id.action_remove_admin)
        val setAdminBt = menu.findItem(R.id.action_set_admin)
        val removeMemberBt = menu.findItem(R.id.action_remove_from_group)
        val personalWallBt = menu.findItem(R.id.action_personal_wall)
        val sendMessageBt = menu.findItem(R.id.action_send_message)
        val leaveChannelBt = menu.findItem(R.id.action_leave_group)

        val leaveChannelTitle = SpannableString(getString(R.string.chat_message_leave_group))
        leaveChannelTitle.setSpan(ForegroundColorSpan(Color.RED), 0, leaveChannelTitle.length, 0)
        leaveChannelBt.title = leaveChannelTitle


        val memberIsOwnerRole = member.isOwner
        val memberIsAdminRole = member.isAdmin

        viewModel?.apply {
            when {
                memberLoginIsOwner -> {
                    when {
                        memberIsOwnerRole -> {
                            removeAdminBt.isVisible = false
                            setAdminBt.isVisible = false
                            removeMemberBt.isVisible = false
                        }
                        memberIsAdminRole -> {
                            removeAdminBt.isVisible = true
                            setAdminBt.isVisible = false
                            removeMemberBt.isVisible = true
                        }
                        else -> {
                            removeAdminBt.isVisible = false
                            setAdminBt.isVisible = true
                            removeMemberBt.isVisible = true
                        }
                    }
                }
                memberLoginIsAdmin -> {
                    when {
                        memberIsOwnerRole -> {
                            removeAdminBt.isVisible = false
                            setAdminBt.isVisible = false
                            removeMemberBt.isVisible = false
                        }
                        memberIsAdminRole -> {
                            removeAdminBt.isVisible = false
                            setAdminBt.isVisible = false
                            removeMemberBt.isVisible = false
                        }
                        else -> {
                            removeAdminBt.isVisible = false
                            setAdminBt.isVisible = false
                            removeMemberBt.isVisible = false
                        }
                    }
                }
                memberLoginIsMember -> {
                    removeAdminBt.isVisible = false
                    setAdminBt.isVisible = false
                    removeMemberBt.isVisible = false
                }

                // set enable/ disable

                //registering popup with OnMenuItemClickListener
            }
        }

        nicknameBt.isVisible = true
        leaveChannelBt.isVisible = member.isUserLogin(ownerId())
        sendMessageBt.isVisible = !member.isUserLogin(ownerId())
        personalWallBt.isVisible = !member.isUserLogin(ownerId())

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_remove_admin -> {
                    onActionRemoveAdmin(member)
                }
                R.id.action_set_admin -> {
                    onActionSetAdmin(member)
                }
                R.id.action_remove_from_group -> {
                    onActionRemoveFromGroup(member)
                }
                R.id.action_personal_wall -> {
                    navigatePersonalWall(member.userId)
                }
                R.id.action_send_message -> {
                    onActionSendMessage(member)
                }
                R.id.action_leave_group -> {
                    onActionLeaveGroup(member)
                }
                R.id.action_set_nickname -> {
                    onSetNickNameMember(member)
                }
            }
            true
        }

        popup.show()

    }

    private fun onSetNickNameMember(member: MemberChannel) {
        val memberId = member.userId ?: ""
        val currentName = member.displayName
        val editText = HaloEditText(
            ContextThemeWrapper(
                this@ChatGroupMemberAct,
                R.style.Messenger_EditText_OneLine
            )
        )
        ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            editText.layoutParams = this
        }
        editText.setText(currentName)
        HaloDialogCustom.Builder().apply {
            setIcon(R.drawable.ic_chat_dialog_edit)
            setTitle(getString(R.string.chat_message_change_group_name))
            setDescription(getString(R.string.chat_message_change_group_name_description))
            setTextPrimary(getString(R.string.chat_message_room_change_name_save_title))
            setOnClickCancel {}
            setOnClickPrimary {
                editText.text?.toString()?.trim()?.let { nickName ->
                    lifecycleScope.launchWhenCreated {
                        viewModel?.updateMemberNickname(
                            userId = memberId,
                            nickName = nickName
                        )
                    }
                }
            }
            setCustomView(editText)
        }.build().show(supportFragmentManager, "ChatGroupMemberAct")
    }

    private fun initLeaveChannel(newOwnerId: String?) {
        lifecycleScope.launch {
            viewModel?.leaveChannel(newOwnerId)?.collect {
                when {
                    it.isSuccess -> {
                        it.data?.run {
                            initResultLeaveGroup()
                        } ?: kotlin.run {
                            errorNetwork()
                        }
                    }
                    it.isLoading -> {

                    }
                    else -> {
                        errorNetwork()
                    }
                }
            }
        }
    }

    private fun onActionLeaveGroup(member: MemberChannel) {
        viewModel?.apply {
            val isOwner = member.isOwner
            HaloDialogCustom.Builder().apply {
                setIcon(R.drawable.ic_chat_dialog_logout)
                setTitle(
                    if (isOwner) getString(R.string.chat_message_leave_group_owner_title) else getString(
                        R.string.chat_message_lear_group
                    )
                )
                setDescription(
                    if (isOwner) getString(R.string.chat_message_leave_group_owner_description) else getString(
                        R.string.chat_message_you_really_want_to_leave_group
                    )
                )
                setTextWarning(
                    if (isOwner) getString(R.string.chat_message_delete_group_title) else getString(
                        R.string.chat_message_lear_group
                    )
                )
                setTextPrimary(getString(R.string.chat_message_set_new_owner_title))
                setOnClickCancel {}
                setOnClickWarning {
                    if (isOwner) {
                        initDeleteChannel()
                    } else {
                        initLeaveChannel(null)
                    }
                }
                setOnClickPrimary(if (isOwner) View.OnClickListener { handleSetNewOwner() } else null)

            }.build().show(supportFragmentManager, "ChatGroupMemberAct")
        }
    }

    private fun initDeleteChannel() {
        lifecycleScope.launch {
            viewModel?.deleteChannel()?.collect {
                when {
                    it.isSuccess -> {
                        initResultLeaveGroup()
                    }
                    it.isLoading -> {

                    }
                    else -> {
                        errorNetwork()
                    }
                }
            }
        }
    }

    private fun handleSetNewOwner() {
        viewModel?.channelId?.value?.let {
            startActivityForResult(
                getIntentLeaveChannel(
                    this@ChatGroupMemberAct,
                    it
                ), REQUEST_CODE_SET_OWNER
            )
        } ?: kotlin.run {
            errorNetwork()
        }
    }


    private fun onActionRemoveFromGroup(member: MemberChannel) {
        HaloDialogCustom.BuilderDelete().apply {
            setTitle(getString(R.string.chat_message_remove_member_title))
            setDescription(getString(R.string.chat_message_remove_member_description))
            setTextWarning(getString(R.string.chat_message_remove))
            setOnClickCancel {}
            setOnClickWarning {
                member.userId?.takeIf { it.isNotEmpty() }?.let { userId ->
                    lifecycleScope.launch {
                        viewModel?.deleteMember(userId)
                    }
                } ?: kotlin.run {
                    errorNetwork()
                }
            }
        }.build().show(supportFragmentManager, "ChatGroupMemberAct")
    }

    private fun onActionSendMessage(member: MemberChannel) {
        member.userId?.takeIf { it.isNotEmpty() }?.let {
            startActivity(ChatMessageAct.getIntent(this@ChatGroupMemberAct, it))
        } ?: kotlin.run {
            errorNetwork()
        }
    }

    private fun navigatePersonalWall(userId: String?) {
        userId?.takeIf { it.isNotEmpty() }?.let {
            PersonalDetailPopup.startUserDetail(supportFragmentManager, userId)
        } ?: kotlin.run {
            errorNetwork()
        }
    }

    private fun initResultLeaveGroup() {
        val intent = Intent(this@ChatGroupMemberAct, ChatAct::class.java)
        ActivityUtils.finishAllActivities()
        ActivityUtils.startActivity(intent)
    }


    private fun onActionSetAdmin(member: MemberChannel) {
        HaloDialogCustom.Builder().apply {
            setTitle(getString(R.string.chat_message_set_admin_role_title))
            setDescription(getString(R.string.chat_message_set_admin_role_description))
            setOnClickCancel {}
            setOnClickPrimary {
                lifecycleScope.launch {
                    member.userId?.takeIf { it.isNotEmpty() }?.let {
                        viewModel?.setAdminMember(it)
                    } ?: kotlin.run {
                        errorNetwork()
                    }
                }
            }
        }.build().show(supportFragmentManager, "ChatGroupMemberAct")
    }

    private fun onActionRemoveAdmin(member: MemberChannel) {
        HaloDialogCustom.BuilderDelete().apply {
            setTitle(getString(R.string.chat_message_remove_admin_role_title))
            setDescription(getString(R.string.chat_message_remove_admin_role_description))
            setTextWarning(getString(R.string.chat_message_change_nick_name_remove))
            setOnClickCancel {}
            setOnClickWarning {
                lifecycleScope.launch {
                    member.userId?.takeIf { it.isNotEmpty() }?.let {
                        viewModel?.deleteAdminMember(it)
                    } ?: kotlin.run {
                        errorNetwork()
                    }
                }
            }
        }.build().show(supportFragmentManager, "ChatGroupMemberAct")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_MESSAGE -> {
                    initResultFinish()
                }
            }
        }
    }

    private fun initResultFinish() {
        setResult(RESULT_OK, Intent().apply {
            this.putExtra(ChatMessageResultAction.FINISH_CHAT, true)
        })
        finish()
    }

    companion object {
        private const val REQUEST_MESSAGE = 111
        private const val CHANNEL_ID = "ChatMessageMemberAct_CHANNE_ID"
        private const val IS_MEMBER =
            "ChatMessageMemberAct_IS_MEMBER"  // isMember : true : MemberAct , false : NickNameAct
        private const val IS_LEAVE_GROUP = "ChatMessageMemberAct_IS_LEAVE_GROUP"
        private const val REQUEST_CODE_SET_OWNER = 114

        fun getIntentMember(context: Context, channelId: String): Intent {
            val intent = Intent(context, ChatGroupMemberAct::class.java)
            intent.putExtra(CHANNEL_ID, channelId)
            intent.putExtra(IS_MEMBER, true)
            return intent
        }


        fun getIntentNickName(context: Context, channelId: String): Intent {
            val intent = Intent(context, ChatGroupMemberAct::class.java)
            intent.putExtra(CHANNEL_ID, channelId)
            intent.putExtra(IS_MEMBER, false)
            return intent
        }

        fun getIntentLeaveChannel(context: Context, channelId: String): Intent {
            val intent = Intent(context, ChatGroupMemberAct::class.java)
            intent.putExtra(CHANNEL_ID, channelId)
            intent.putExtra(IS_MEMBER, false)
            intent.putExtra(IS_LEAVE_GROUP, true)
            return intent
        }
    }

    override fun onMenuClick(item: MemberChannel, view: View) {
        showPopupMenuMember(item, view)
    }

    override fun onAvatarClick(userId: String) {
        userId.takeIf { it.isNotEmpty() }?.let {
            PersonalDetailPopup.startUserDetail(supportFragmentManager, userId)
        } ?: kotlin.run {
            errorNetwork()
        }
    }

    override fun onItemClick(item: MemberChannel) {
        if (binding?.isLeave == true) {
            showDialogLeaveGroup(item)
        } else {
            onSetNickNameMember(item)
        }
    }
}
