/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.detail

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.hahalolo.messager.bubble.BubbleAct
import com.hahalolo.messager.bubble.BubbleService
import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendSelectData
import com.hahalolo.messager.bubble.conversation.detail.gallery.adapter.ChatGalleryListener
import com.hahalolo.messager.bubble.model.BubbleInfo
import com.hahalolo.messager.presentation.base.AbsMessBackActivity
import com.hahalolo.messager.presentation.group.create.ChatGroupCreateAct
import com.hahalolo.messager.presentation.group.create.GroupConstant
import com.hahalolo.messager.presentation.group.detail.ChatGroupDetailAct.RoomAction.Companion.ROOM_AVATAR
import com.hahalolo.messager.presentation.group.detail.ChatGroupDetailAct.RoomAction.Companion.ROOM_NAME
import com.hahalolo.messager.presentation.group.detail.adapter.ChatGroupDetailGalleryAdapter
import com.hahalolo.messager.presentation.group.detail.view.AvatarGroupMenu
import com.hahalolo.messager.presentation.group.gallery.AttachmentListener
import com.hahalolo.messager.presentation.group.gallery.ChatRoomGalleryAct
import com.hahalolo.messager.presentation.group.gallery.adapter.MessengerManagerFileModel
import com.hahalolo.messager.presentation.group.member.ChatGroupMemberAct
import com.hahalolo.messager.presentation.main.ChatAct
import com.hahalolo.messager.presentation.main.contacts.detail.PersonalDetailPopup
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageGeneralSettingActBinding
import com.halo.common.permission.RxPermissions
import com.halo.common.utils.*
import com.halo.data.entities.channel.Channel
import com.halo.data.entities.media.MediaEntity
import com.halo.data.entities.media.TypeMedia
import com.halo.editor.mediasend.AvatarSelectionActivity
import com.halo.widget.HaloEditText
import com.halo.widget.HaloGridLayoutManager
import com.halo.widget.dialog.HaloDialogCustom
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import javax.inject.Inject


class ChatGroupDetailAct : AbsMessBackActivity(), AvatarGroupMenu.AvatarGroupMenuListener {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(ROOM_NAME, ROOM_AVATAR)
    annotation class RoomAction {
        companion object {
            const val ROOM_NAME = 1
            const val ROOM_AVATAR = 2
        }
    }

    companion object {
        private const val CHANEL_ID = "ChatMessageGeneralSettingAct_group_or_friend_id"
        private const val ROOM_ACTION = "ChatMessageGeneralSettingAct_ROOM_ACTION"

        private const val ADD_MEMBER_REQUEST_CODE = 111
        private const val REQUEST_IMAGE_CAPTURE = 112
        private const val REQUEST_CODE_CROP_AVATAR = 113
        private const val REQUEST_CODE_SET_OWNER = 114

        @JvmStatic
        fun getIntent(context: Context, idObject: String): Intent {
            val intent = Intent(context, ChatGroupDetailAct::class.java)
            intent.putExtra(CHANEL_ID, idObject)
            return intent
        }

        @JvmStatic
        fun getIntent(context: Context, idObject: String, action: Int?): Intent {
            val intent = Intent(context, ChatGroupDetailAct::class.java)
            intent.putExtra(CHANEL_ID, idObject)
            intent.putExtra(ROOM_ACTION, action)
            return intent
        }

    }

    private var adapter: ChatGroupDetailGalleryAdapter? = null

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    private val binding by lazy<ChatMessageGeneralSettingActBinding> {
        DataBindingUtil.setContentView(this, R.layout.chat_message_general_setting_act)
    }

    private val viewModel by lazy {
        ViewModelProvider(viewModelStore, factory).get(
            ChatGroupDetailViewModel::class.java
        )
    }

    private val requestManager by lazy { Glide.with(this) }


    override fun initActionBar() {
    }

    override fun isLightTheme(): Boolean {
        return true
    }

    override fun initializeBindingViewModel() {
        intent?.apply {
            getStringExtra(CHANEL_ID)?.let {
                viewModel.channelId = it
            }
            getIntExtra(ROOM_ACTION, -1).let {

            }
        }
    }

    private fun handleAction(action: Int) {
        when (action) {
            ROOM_NAME -> {
                navigateChangeName()
            }
            ROOM_AVATAR -> {
                selectChangeAvatarGroup()
            }
            else -> {

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.appBarLayout.addOnOffsetChangedListener(
            AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
                updateViews(Math.abs(verticalOffset / appBarLayout.totalScrollRange.toFloat()))
            })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

    }

    private fun updateViews(offset: Float) {
        when (offset) {
            in 0.15F..1F -> {
                binding.nameGroupTv.apply {
                    if (visibility != View.VISIBLE) visibility = View.VISIBLE
                    alpha = (1 - offset) * 0.35F
                }
                binding.statusUser.apply {
                    if (visibility != View.VISIBLE) visibility = View.VISIBLE
                    alpha = (1 - offset) * 0.35F
                }
                binding.avatarGr.apply {
                    if (visibility != View.VISIBLE) visibility = View.VISIBLE
                    alpha = (1 - offset) * 0.35F
                }
                binding.subGroupNameTv.apply {
                    alpha = offset
                }
                if (offset > 0.8F) {
                    binding.subGroupNameTv.apply {
                        if (visibility != View.VISIBLE) visibility = View.VISIBLE
                    }
                } else if (offset < 0.5F) {
                    binding.subGroupNameTv.apply {
                        if (visibility != View.GONE) visibility = View.GONE
                    }
                }
            }
            in 0F..0.15F -> {
                binding.nameGroupTv.alpha = (1f)
                binding.statusUser.alpha = (1f)
                binding.avatarGr.alpha = (1f)
                binding.subGroupNameTv.apply {
                    if (visibility != View.GONE) visibility = View.GONE
                }
            }

        }
    }

    override fun initializeLayout() {
        initView()
        initObserver()
        initData()
        initActionView()
    }

    private fun initData() {
        lifecycleScope.launch {
            viewModel.channelDetail()
            viewModel.getAttachments()
            delay(100)
        }
    }

    private val attachmentListener = object : AttachmentListener {

        override fun onClickAttachment(position: Int, mediaModel: MessengerManagerFileModel?) {
            openMedia(position)
        }

        override fun onClickLastMedia() {
            navigateShareMedia()
        }
    }

    private fun initActionView() {
        binding.apply {
            //openBubbleBt.visibility = View.GONE
            navigationBt.setOnClickListener { onBackPressed() }
            addUserBt.setOnClickListener { navigateAddMember() }
            viewMembersBt.setOnClickListener { navigateViewAllMembers() }
            shareMediaBt.setOnClickListener { navigateShareMedia() }
            shareFileBt.setOnClickListener { navigateShareFile() }
            shareLinkBt.setOnClickListener { navigateShareLink() }
            changeNameBt.setOnClickListener { navigateChangeName() }
            nickNameChatBt.setOnClickListener { navigateNickNameMember() }
            nameGroupTv.setOnClickListener { navigateChangeName() }
            viewMoreTv.setOnClickListener { navigateShareMedia() }
            deleteConversationBt.setOnClickListener { handleDeleteConversation() }
            changeAvatarBt.setOnClickListener { selectChangeAvatarGroup() }
            openBubbleBt.setOnClickListener { handleOpenBubbleChat() }
        }
    }

    private fun showAvatarMenu() {
        val avatarMenu = AvatarGroupMenu(this@ChatGroupDetailAct, true, viewModel.isHaveAvatar)
        avatarMenu.show(supportFragmentManager, "ChatGroupDetailAct")
    }

    private fun handleOpenBubbleChat() {
        BubbleAct.isHavePermission(this@ChatGroupDetailAct).let { permision ->
            // Nếu chưa có permission over draw OR đã tắt chức năng bubble -> navigate SettingAct
            if (!permision || !viewModel.isOpenBubble) {
                showDialogAcceptOpenBubble()
            } else {
                val bubbleInfo = BubbleInfo(
                    "roomId",
                    "Open bubble chat",
                    "",
                    "",
                    false,
                    "roomId"
                )
                BubbleService.openBubbleConversation(this@ChatGroupDetailAct, bubbleInfo)
                initResultBackHome()
            }
        }
    }

    private fun initResultBackHome() {
        setResult(RESULT_OK, Intent().apply {
            putExtra(ChatGroupDetailResultAction.BACK_HOME, true)
        })
        finish()
    }

    private fun showDialogAcceptOpenBubble() {
        HaloDialogCustom.Builder().apply {
            setTitle(getString(R.string.chat_message_bubble_permission_title))
            setDescription(getString(R.string.chat_message_bubble_accept_open_des))
            setTextPrimary(getString(R.string.chat_message_bubble_permission_primary_text))
            setTextCancel(getString(R.string.chat_message_bubble_permission_cancel_text))
            setOnClickCancel {
            }
            setOnClickPrimary {
                appController.navigateAppSetting()
            }.build().apply {
                show(supportFragmentManager, "ChatAct")
            }
        }
    }

    private fun handleDeleteConversation() {
        HaloDialogCustom.BuilderDelete().apply {
            setTitle(getString(R.string.chat_message_delete_conversation_title))
            setDescription(getString(R.string.chat_message_delete_conversation_des))
            setTextWarning(getString(R.string.chat_message_remove))
            setOnClickCancel {}
            setOnClickWarning { v ->

            }
        }.build().show(supportFragmentManager, "ChatGroupDetailAct")
    }

    private fun initView() {
        adapter = ChatGroupDetailGalleryAdapter(requestManager, attachmentListener)
        binding.mediaRec.layoutManager = HaloGridLayoutManager(this@ChatGroupDetailAct, 3)
        RecyclerViewUtils.optimization(binding.mediaRec, this@ChatGroupDetailAct)
        binding.mediaRec.adapter = adapter
        binding.size = 0
    }

    private fun openMedia(position: Int) {
        /*
        val listData = convertListMediaEntity(this)
        appController.navigateOpenMediaEntity(
            listData as ArrayList<MediaEntity>,
            if (position < listData.size) position else 0
        )
        */
    }

    private fun convertListMediaEntity(listImage: MutableList<AttachmentTable>?): List<MediaEntity> {
        val listData = ArrayList<MediaEntity>()
        listImage?.takeIf { !it.isNullOrEmpty() }?.forEach {
            val mediaEntity = MediaEntity()
            mediaEntity.id = UUID.randomUUID().toString()
            mediaEntity.thumb = it.thumbnail
            mediaEntity.path = it.getAttachmentUrl()
            mediaEntity.type = if (it.isVideo()) TypeMedia.VID else TypeMedia.IMG
            listData.add(mediaEntity)
        }
        return listData
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ADD_MEMBER_REQUEST_CODE -> {
                    data?.getBooleanExtra(GroupConstant.STATUS_LEAVE_GROUP, false)?.takeIf { it }
                        ?.run {
                            initResultLeaveGroup()
                        }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    data?.getParcelableExtra<Uri>(AvatarSelectionActivity.EXTRA_IMAGE_URI)?.path?.let { avatar ->
                        handleChangeAvatar(avatar)
                    } ?: kotlin.run {
                        errorNetwork()
                    }
                }
                REQUEST_CODE_SET_OWNER -> {
                    data?.getBooleanExtra(GroupConstant.STATUS_LEAVE_GROUP, false)?.takeIf { it }
                        ?.run {
                            initResultLeaveGroup()
                        }
                }
            }
        }
    }

    private fun navigateNickNameMember() {
        viewModel.channelId?.let {
            startActivity(
                ChatGroupMemberAct.getIntentNickName(
                    this@ChatGroupDetailAct,
                    it
                )
            )
        }
    }

    private fun handleCreateGroup(channel: Channel) {
        channel.recipient?.apply {
            val friendSelect = FriendSelectData(userId, this.userName(), avatar)
            startActivity(
                ChatGroupCreateAct.getIntentCreateWith(
                    this@ChatGroupDetailAct,
                    friendSelect
                )
            )
        } ?: kotlin.run {
            errorNetwork()
        }
    }

    private fun navigateChangeName() {
        val editText = HaloEditText(
            ContextThemeWrapper(
                this@ChatGroupDetailAct,
                R.style.Messenger_EditText_OneLine
            )
        )
        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
            editText.layoutParams = this
        }
        editText.setText(viewModel.channelName)
        HaloDialogCustom.Builder().apply {
            setIcon(R.drawable.ic_chat_dialog_edit)
            setTitle(getString(R.string.chat_message_change_group_name))
            setDescription(getString(R.string.chat_message_change_group_name_description))
            setTextPrimary(getString(R.string.chat_message_room_change_name_save_title))
            setOnClickCancel {}
            setOnClickPrimary {
                editText.text?.toString()?.trim()?.let {
                    handleChangeName(it)
                }
            }
            setCustomView(editText)
        }.build().show(supportFragmentManager, "ChatGroupDetailAct")
    }

    private fun navigateShareMedia() {
        viewModel.channelId?.takeIf { it.isNotEmpty() }?.let { id ->
            startActivity(ChatRoomGalleryAct.getIntent(this@ChatGroupDetailAct, id, 0))
        } ?: kotlin.run {
            errorNetwork()
        }
    }

    private fun navigateShareLink() {
        viewModel.channelId?.takeIf { it.isNotEmpty() }?.let { id ->
            startActivity(ChatRoomGalleryAct.getIntent(this@ChatGroupDetailAct, id, 1))
        } ?: kotlin.run {
            errorNetwork()
        }
    }

    private fun navigateShareFile() {
        viewModel.channelId?.takeIf { it.isNotEmpty() }?.let { id ->
            startActivity(ChatRoomGalleryAct.getIntent(this@ChatGroupDetailAct, id, 2))
        } ?: kotlin.run {
            errorNetwork()
        }
    }

    private fun navigateViewAllMembers() {
        startActivity(
            ChatGroupMemberAct.getIntentMember(
                this@ChatGroupDetailAct,
                viewModel.channelId
            )
        )
    }

    private fun navigateAddMember() {
        viewModel.channelId.takeIf { it.isNotEmpty() }?.let { id ->
            startActivity(ChatGroupCreateAct.getIntentAddMember(this@ChatGroupDetailAct, id))
        } ?: kotlin.run {
            errorNetwork()
        }
    }

    private fun initResultLeaveGroup() {
        val returnIntent = Intent()
        returnIntent.putExtra(GroupConstant.STATUS_LEAVE_GROUP, true)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun initResultDeleteConversation() {
        val returnIntent = Intent()
        returnIntent.putExtra(GroupConstant.STATUS_DELETE_CONVERSATION, true)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.apply {
                channelResponse.observe(this@ChatGroupDetailAct, {
                    it?.let { response ->
                        updateLayout(response)
                    }
                })
                uiState.collect { state ->
                    when (state) {
                        is ChannelUiState.Loading -> {
                            showIndicator()
                        }
                        is ChannelUiState.Success -> {
                            dismissIndicator()
                        }
                        is ChannelUiState.ChannelDetail -> {
                            dismissIndicator()
                        }
                        is ChannelUiState.ChannelDelete -> {
                            ActivityUtils.finishAllActivities()
                            ActivityUtils.startActivity(ChatAct.getIntent(this@ChatGroupDetailAct))
                        }
                        is ChannelUiState.ChannelMedia -> {
                            state.data?.let {
                                adapter?.updateData(it)
                            }
                        }
                        is ChannelUiState.ChannelLeave -> {
                            ActivityUtils.finishAllActivities()
                            ActivityUtils.startActivity(ChatAct.getIntent(this@ChatGroupDetailAct))
                        }
                        else -> {
                            errorNetwork()
                            dismissIndicator()
                        }
                    }
                }
            }
        }
    }

    private fun updateLayout(channel: Channel) {
        binding.apply {
            isGroup = channel.isGroupChannel()

            avatarGr.updateListImage(channel.channelAvatars(), requestManager)
            nameGroupTv.text = channel.channelName()
            subGroupNameTv.text = channel.channelName()
            createGroupChatBt.setOnClickListener { handleCreateGroup(channel) }

            leaveGroupBt.visibility = if (channel.isOwner(ownerId())) View.VISIBLE else View.GONE
            leaveGroupBt.setOnClickListener { showDialogLeaveGroup(channel.isOwner(ownerId())) }

            if (channel.isGroupChannel()) {
                nameGroupTv.setOnClickListener { navigateChangeName() }
            }

            if (!channel.isSystemAccount()) {
                avatarGr.setOnClickListener {
                    if (channel.isPrivateChannel()) {
                        channel.recipient?.userId?.takeIf { it.isNotEmpty() }?.let { contactId ->
                            PersonalDetailPopup.startUserDetail(
                                fragmentManager = supportFragmentManager,
                                contactId
                            )
                        }
                    } else {
                        showAvatarMenu()
                    }
                }
            }
            contentLayout.visibility = View.VISIBLE
            executePendingBindings()
        }
    }

    private fun showDialogLeaveGroup(isOwner: Boolean) {

        viewModel.apply {
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
                        handleDeleteGroup()
                    } else {
                        onLeaveGroup(null)
                    }
                }
                setOnClickPrimary(if (isOwner) View.OnClickListener { handleSetNewOwner() } else null)
            }.build().show(supportFragmentManager, "ChatGroupDetailAct")
        }
    }

    private fun handleDeleteGroup() {
        HaloDialogCustom.BuilderDelete().apply {
            setTitle(getString(R.string.chat_message_delete_group_title))
            setDescription(getString(R.string.chat_message_delete_group_description))
            setTextWarning(getString(R.string.chat_message_gallery_menu_delete))
            setOnClickCancel {}
            setOnClickWarning {
                onDeleteGroup()
            }.build().show(supportFragmentManager, "ChatGroupDetailAct")
        }
    }

    private fun onDeleteGroup() {
        lifecycleScope.launch {
            viewModel.deleteChannel()
        }
    }

    private fun onLeaveGroup(newOwnerId: String?) {
        lifecycleScope.launch {
            viewModel.leaveChannel(newOwnerId)
        }
    }

    private fun handleSetNewOwner() {
        startActivity(
            ChatGroupMemberAct.getIntentLeaveChannel(
                this@ChatGroupDetailAct,
                viewModel.channelId
            )
        )
    }

    private fun selectChangeAvatarGroup() {
        val rxPermissions = RxPermissions(this)
        rxPermissions.request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).subscribe(object : EvenObserver<Boolean>() {
            override fun onNext(t: Boolean) {
                if (t) {
                    startActivityForResult(
                        AvatarSelectionActivity.getIntentForUriResult(this@ChatGroupDetailAct),
                        REQUEST_IMAGE_CAPTURE
                    )
                } else {
                    Toast.makeText(
                        this@ChatGroupDetailAct,
                        R.string.editor_permission_media_request_denied,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }


    private fun handleChangeName(name: String) {
        lifecycleScope.launch {
            viewModel.updateChannelName(name)
        }
    }

    private fun handleChangeAvatar(avatar: String) {
        lifecycleScope.launch {
            viewModel.updateChannelAvatar(avatar)
        }
    }

    private fun navigateMediaViewer(url: String) {
        val entities = ArrayList<MediaEntity>()
        entities.add(
            MediaEntity(
                ThumbImageUtils.replaceThumbUrl(url),
                appController.ownerId
            )
        )
        appController.navigateOpenMediaEntity(entities, 0)
    }

    override fun navigateAvatarView() {
        navigateMediaViewer(viewModel.avatarChannel)
    }

    override fun navigateUpdateAvatar() {
        selectChangeAvatarGroup()
    }

    override fun navigateViewMember() {
        startActivity(
            ChatGroupMemberAct.getIntentMember(
                this@ChatGroupDetailAct,
                viewModel.channelId
            )
        )
    }
}