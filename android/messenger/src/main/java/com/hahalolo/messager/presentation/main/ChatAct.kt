/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IntDef
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.bubble.BubbleAct
import com.hahalolo.messager.presentation.base.AbsMessActivity
import com.hahalolo.messager.presentation.group.create.ChatGroupCreateAct
import com.hahalolo.messager.presentation.group.member.ChatGroupMemberAct
import com.hahalolo.messager.presentation.main.contacts.owner.ChatOwnerAct
import com.hahalolo.messager.presentation.main.MessBottomItem.Companion.CONTACTS_ITEM
import com.hahalolo.messager.presentation.main.MessBottomItem.Companion.GROUP_ITEM
import com.hahalolo.messager.presentation.main.MessBottomItem.Companion.HOME_ITEM
import com.hahalolo.messager.presentation.main.contacts.ChatContactsFr
import com.hahalolo.messager.presentation.main.conversation.ConversationListFr
import com.hahalolo.messager.presentation.main.dialog.MenuConversationDialog
import com.hahalolo.messager.presentation.main.dialog.MenuConversationListener
import com.hahalolo.messager.presentation.main.group.ChatHomeGroupFr
import com.hahalolo.messager.presentation.message.ChatMessageAct
import com.hahalolo.messager.presentation.message.ChatMessageResultAction
import com.hahalolo.messager.presentation.search.ChatSearchAct
import com.hahalolo.messager.presentation.suggest.launcherContactSuggest
import com.hahalolo.messager.presentation.write_message.ChatWriteMessageAct
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.entity.ChannelEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatActBinding
import com.hahalolo.pickercolor.util.setVisibility
import com.hahalolo.socket.SocketState
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.KeyboardUtils
import com.halo.common.utils.Strings
import com.halo.common.utils.ktx.notNull
import com.halo.common.utils.ktx.viewModels
import com.halo.data.common.resource.StatusNetwork
import com.halo.fragnav.FragNavController
import com.halo.fragnav.FragNavLogger
import com.halo.fragnav.FragNavSwitchController
import com.halo.fragnav.FragNavTransactionOptions
import com.halo.fragnav.tabhistory.UniqueTabHistoryStrategy
import com.halo.widget.anim.ViewAnim
import com.halo.widget.dialog.HaloDialogCustom
import dagger.Lazy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/23/2018.
 */
@com.halo.di.ActivityScoped
class ChatAct : AbsMessActivity(),
        FragNavController.RootFragmentListener, ChatActListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var providerHomeFr: Lazy<ConversationListFr>

    @Inject
    lateinit var providerContactsFr: Lazy<ChatContactsFr>

    @Inject
    lateinit var providerGroupFr: Lazy<ChatHomeGroupFr>

    private var binding by notNull<ChatActBinding>()

    private val viewModel by viewModels<ChatViewModel> { factory }

    private var fragNavController: FragNavController? = null

    private var disposables: CompositeDisposable? = null

    private val requestManager: RequestManager by lazy { Glide.with(this) }

    companion object {


       val HOME_ITEM_ID = R.id.home_item
       val CONTACTS_ITEM_ID = R.id.contact_item
       val GROUP_ITEM_ID = R.id.group_item

        var isStarting = false

        private const val USER_ID_ARGS = "ChatAct-UserSocket-USER_ID_ARGS"
        private const val CHANEL_ID_ARGS = "ChatAct-UserSocket-CHANEL_ID_ARGS"
        private const val REQUEST_MESSAGE = 111
        private const val REQUEST_WRITE_MESSAGE = 112

        fun getIntent(context: Context): Intent {
            val intent = Intent(context, ChatAct::class.java)
            return intent
        }

        // chat với user
        fun getIntent(context: Context, userId: String?): Intent {
            val intent = Intent(context, ChatAct::class.java)
            intent.putExtra(USER_ID_ARGS, userId)
            return intent
        }

        // chat với 1 group id (chanelId)
        fun getIntentChanel(context: Context, chanelId: String?): Intent {
            chanelId?.takeIf { it.isNotBlank() && isStarting }?.run {
                return ChatMessageAct.getIntentChannel(context, this)
            } ?: run {
                val intent = Intent(context, ChatAct::class.java)
                intent.putExtra(CHANEL_ID_ARGS, chanelId)
                return intent
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_MESSAGE,
                REQUEST_WRITE_MESSAGE -> {
                    data?.getBooleanExtra(ChatMessageResultAction.FINISH_CHAT, false)?.takeIf { it }?.run {
                        finish()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isStarting = true
        initHeader()
        initBottomNavigation(savedInstanceState)
        // Show chat message if user id is not null
        if (intent != null) {
            // start chat with friend
            val userId = intent.getStringExtra(USER_ID_ARGS) ?: ""
            userId.takeIf { it.isNotBlank() && !TextUtils.equals(it, appController.ownerId) }?.run {
                navigateChatMessageWithFriend(this)
            }
            // start chat to chanel id
            val chanelId = intent.getStringExtra(CHANEL_ID_ARGS)
            chanelId?.takeIf { it.isNotBlank() }?.run {
                navigateChatMessage(this)
            }
        }
        bindAction()
        checkPermissionBubbleChat()
        initHandleSocketState()
    }

    private fun initHandleSocketState(){
        viewModel.socketState.observe(this, Observer {
            when(it){
                SocketState.CONNECTING->{
//                    Strings.log("initHandleSocketState CONNECTING " )
                }
                SocketState.CONNECTED->{
//                    Strings.log("initHandleSocketState CONNECTED " )
                }
                SocketState.DISCONNECT->{
//                    Strings.log("initHandleSocketState DISCONNECT " )
                }
                SocketState.AUTHEN->{
//                    Strings.log("initHandleSocketState AUTHEN " )
                    onAuthen()
                }
                else->{
//                    Strings.log("initHandleSocketState NONE " )
                }
            }
            binding.socketErrorStateTv.setVisibility(it== SocketState.DISCONNECT)
            binding.socketConnectedStateTv.setVisibility(it==SocketState.CONNECTED)
            binding.socketConnecting.setVisibility(it==SocketState.CONNECTING)
            ViewAnim.Builder.createAl(binding.stateGr)
                .showDown()
                .duration(if(it==SocketState.CONNECTED) 300 else 0 )
                .show(it != SocketState.CONNECTED)
        })
    }

    private fun onAuthen() {
        HaloDialogCustom.Builder().apply {
            setTitle("Đăng nhập lại")
            setDescription("Tài khoản đã hết hạn, Bạn cần phải đăng nhập lại!!")
            setTextPrimary("Đồng ý")
            setOnClickCancel {
                appController.navigateSignIn()
            }
            setOnClickPrimary {
                appController.navigateSignIn()
            }.build().apply {
                show(supportFragmentManager, "ChatAct")
            }
        }
    }

    private fun initHeader(){
        binding.apply {
            GlideRequestBuilder.getCircleCropRequest(requestManager)
                .load(appController.ownerAvatar)
                .error(R.drawable.community_avatar_holder)
                .placeholder(R.drawable.community_avatar_holder)
                .into(this.avatarIv)
            avatarIv.setOnClickListener {
                navigateMainSetting()
            }
            searchMessIv.setOnClickListener {
                navigateMainSearch()
            }
        }
    }

    private fun navigateMainSetting(){
        startActivity(ChatOwnerAct.getIntent(this))
    }

    private fun navigateMainSearch(){
        startActivity(ChatSearchAct.getIntent(this))
    }

    /**
     * Check permission on the first time open chat
     * @param isPermission : permission over draw
     * @param isShowRemindBubble : check the first time open chat == true else false
     * @return showDialog : isHavePermission == false or isBubbleSettingOpen == false
     */
    private fun checkPermissionBubbleChat() {
        viewModel.apply {
            if (isShowRemindBubble){
                BubbleAct.isHavePermission(this@ChatAct).let { isHavePermission ->
                    if (!isHavePermission || !isBubbleSettingOpen){
                        handleDialogPermission()
                    }
                }
            }
        }
    }

    private fun handleDialogPermission() {
        HaloDialogCustom.Builder().apply {
            setTitle(getString(R.string.chat_message_bubble_permission_title))
            setDescription(getString(R.string.chat_message_bubble_permission_des))
            setTextPrimary(getString(R.string.chat_message_bubble_permission_primary_text))
            setTextCancel(getString(R.string.chat_message_bubble_permission_cancel_text))
            setOnClickCancel {
                handleDialogDismissPermission()
            }
            setOnClickPrimary {
                appController.navigateAppSetting()
            }.build().apply {
                isCancelable = false
                show(supportFragmentManager, "ChatAct")
            }
        }
        viewModel.turnOffBubblePermissionRemind()
    }

    private fun handleDialogDismissPermission() {
        HaloDialogCustom.Builder().apply {
            setTitle(getString(R.string.chat_message_bubble_permission_title))
            setDescription(getString(R.string.chat_message_bubble_permission_skip_des))
            setTextPrimary(getString(R.string.ok))
            setOnClickPrimary {
            }.build().show(supportFragmentManager, "ChatAct")
        }
    }

    private fun bindAction() {
        binding.floatingBtn.setOnClickListener {
            when (binding.bottomNavigation.selectedItemId) {
                HOME_ITEM_ID -> {
                    navigateWriteMessage()
                }
                CONTACTS_ITEM_ID -> {
                    navigateSuggestContact()
                }
                GROUP_ITEM_ID -> {
                    navigateCreateGroup()
                }
            }
        }
    }


    override fun onBackPressed() {
        fragNavController?.apply {
            // Pop to root fragment
            if (!isRootFragment) {
                popFragment()
                return
            }
            try {
                currentFrag?.let {
                    // Pop to HomePageFr when current fragment is not HomePageFr
                    if (it is ConversationListFr) {
                        super.onBackPressed()
                    } else {
                        switchTab(HOME_ITEM)
                        binding.bottomNavigation.selectedItemId = HOME_ITEM_ID
                    }
                    return
                } ?: super.onBackPressed()
            } catch (e: Exception) {
                e.printStackTrace()
                super.onBackPressed()
            }
        }
        super.onBackPressed()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragNavController?.onSaveInstanceState(outState)
    }

    override fun isLightTheme(): Boolean {
        return true
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.chat_act)
    }

    override fun initializeLayout() {
        initObserver()
    }

    private fun initObserver() {
//        viewModel.removeRoomResponse.observe(this, {
//            when (it?.statusNetwork) {
//                StatusNetwork.LOADING -> {
//
//                }
//                StatusNetwork.SUCCESS -> {
//                    Toast.makeText(
//                            this@ChatAct,
//                            getString(R.string.chat_message_delete_conversation_success),
//                            Toast.LENGTH_SHORT
//                    ).show()
//                }
//                else -> {
//                    errorNetwork()
//                }
//            }
//        })
    }

    /*
     *  FragNavController.RootFragmentListener
     */
    /**
     * Init [com.google.android.material.bottomnavigation.BottomNavigationView]
     * Init [FragNavController]
     *
     * @param savedInstanceState [Bundle] bundle for [FragNavController]
     */
    private fun initBottomNavigation(savedInstanceState: Bundle?) {
        fragNavController = FragNavController(supportFragmentManager, R.id.main_fr).apply {
            rootFragmentListener = this@ChatAct
            createEager = true
            fragNavLogger = object : FragNavLogger {
                override fun error(message: String, throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
            fragmentHideStrategy = FragNavController.DETACH_ON_NAVIGATE_HIDE_ON_SWITCH
            navigationStrategy = UniqueTabHistoryStrategy(object : FragNavSwitchController {
                override fun switchTab(index: Int, transactionOptions: FragNavTransactionOptions?) {
                    binding.bottomNavigation.apply {
                        selectedItemId = when (index) {
                            HOME_ITEM -> HOME_ITEM_ID
                            CONTACTS_ITEM -> CONTACTS_ITEM_ID
                            GROUP_ITEM -> GROUP_ITEM_ID
                            else -> HOME_ITEM_ID
                        }
                    }
                }
            })
            initialize(HOME_ITEM, savedInstanceState)
            if (savedInstanceState == null) {
                binding.bottomNavigation.selectedItemId = HOME_ITEM_ID
            }
            executePendingTransactions()
            binding.bottomNavigation.apply {
                itemIconTintList = null
                setOnNavigationItemSelectedListener { item: MenuItem ->
                    switchFloatButton(item.itemId)
                    when (item.itemId) {
                        HOME_ITEM_ID -> {
                            switchTab(HOME_ITEM)
                            return@setOnNavigationItemSelectedListener true
                        }
                        CONTACTS_ITEM_ID -> {
                            switchTab(CONTACTS_ITEM)
                            return@setOnNavigationItemSelectedListener true
                        }
                        GROUP_ITEM_ID -> {
                            switchTab(GROUP_ITEM)
                            return@setOnNavigationItemSelectedListener true
                        }
                    }
                    false
                }
                setOnNavigationItemReselectedListener { item: MenuItem ->
                    when (item.itemId) {
                        HOME_ITEM_ID -> {
                        }
                        CONTACTS_ITEM_ID -> {
                        }
                        GROUP_ITEM_ID -> {
                        }
                    }
                    clearStack()
                }
            }
        }
    }

    private fun switchFloatButton(itemId: Int) {
        when (itemId) {
            HOME_ITEM_ID -> {
                binding.titleTv.setText(R.string.chat_message_home_message_title)
                binding.floatingBtn.setImageResource(R.drawable.ic_msg_write_msg)
            }
            CONTACTS_ITEM_ID -> {
                binding.titleTv.setText(R.string.chat_message_contact_title)
                binding.floatingBtn.setImageResource(R.drawable.ic_msg_add_friend)
            }
            GROUP_ITEM_ID -> {
                binding.titleTv.setText(R.string.chat_message_group_title)
                binding.floatingBtn.setImageResource(R.drawable.ic_msg_create_group)
            }
        }
    }

    /**
     * Dynamically create the Fragment that will go on the bottom of the stack
     *
     * @param index the index that the root of the stack Fragment needs to go
     * @return the new Fragment
     */
    override fun getRootFragment(index: Int): Fragment {
        when (index) {
            HOME_ITEM -> return providerHomeFr.get()
            CONTACTS_ITEM -> return providerContactsFr.get()
            GROUP_ITEM -> return providerGroupFr.get()
        }
        throw IllegalStateException("Need to send an index that we know: $index")
    }

    override val numberOfRootFragments: Int
        get() = 3

    override fun onResume() {
        viewModel.onResumeChatAct()
        super.onResume()
        hideKeyboard()
        hideKeyboardDelay()
    }

    override fun onPause() {
        viewModel.onPauseChartAct()
        super.onPause()
    }

    override fun finish() {
        KeyboardUtils.hideSoftInput(this)
        isStarting = false
        viewModel.onFinishChatAct()
        super.finish()
    }

    private fun hideKeyboardDelay() {
        lifecycleScope.launch {
            delay(500)
            val imm =
                    this@ChatAct.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }
    }

    private fun hideKeyboard() {
        if (KeyboardUtils.isSoftInputVisible(this)) {
            KeyboardUtils.hideSoftInput(this)
        }
    }

    fun observeDelay(time: Long, longDisposableObserver: DisposableObserver<Long>) {
        if (disposables == null) disposables = CompositeDisposable()
        disposables?.add(
                Observable.timer(time, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(longDisposableObserver)
        )
    }

    override fun onDestroy() {
        disposables?.clear()
        clearCache()
        super.onDestroy()
    }

    private fun clearCache() {
        if (fragNavController != null) {
            fragNavController?.clearStack()
            fragNavController = null
        }
    }


    override fun onClickMenuMore(room: ChannelEntity) {
        handleMenuConversation(room)
    }

    override fun onClickMenuRemove(room: ChannelEntity) {
        room.channelId().takeIf { it.isNotEmpty() }?.let {
            handleDeleteConversation(it)
        } ?: kotlin.run {
            errorNetwork()
        }
    }

    private fun handleDeleteConversation(roomId: String) {
        HaloDialogCustom.BuilderDelete().apply {
            setTitle(getString(R.string.chat_message_delete_conversation_title))
            setDescription(getString(R.string.chat_message_delete_conversation_des))
            setTextWarning(getString(R.string.chat_message_remove))
            setOnClickCancel {}
            setOnClickWarning { v ->
                viewModel.removeConversation(roomId)
            }
        }.build().show(supportFragmentManager, "ChatAct")
    }


    private fun navigateChatMessageWithFriend(friendId: String) {
        startActivityForResult(ChatMessageAct.getIntent(this@ChatAct, friendId), REQUEST_MESSAGE)
    }

    private fun navigateChatMessage(groupId: String) {
        startActivityForResult(ChatMessageAct.getIntentChannel(this@ChatAct, groupId), REQUEST_MESSAGE)
    }

    private fun navigateWriteMessage() {
        startActivityForResult(ChatWriteMessageAct.getIntent(this), REQUEST_WRITE_MESSAGE)
    }

    private fun navigateSuggestContact() {
        this.launcherContactSuggest()
    }

    private fun navigateCreateGroup() {
        startActivity(ChatGroupCreateAct.getIntentCreate(this@ChatAct))
    }

    private fun handleSetNewOwner(roomId: String) {
        startActivity(
                ChatGroupMemberAct.getIntentLeaveChannel(
                        this@ChatAct,
                        roomId
                )
        )
    }

    private fun navigateGroupDetail(roomId: String) {
        startActivity(
                ChatGroupMemberAct.getIntentMember(
                        this@ChatAct,
                        roomId
                )
        )
    }

    private fun navigateAddFriend(roomId: String) {
        startActivity(ChatGroupCreateAct.getIntentAddMember(this@ChatAct, roomId))
    }

    private fun handleMenuConversation(channel: ChannelEntity) {
        MenuConversationDialog(this@ChatAct).apply {
            setListener(object : MenuConversationListener {
                override fun onNotification() {
                    channel.channelId().takeIf { it.isNotEmpty() }?.let {
//                        handleUpdateStatusNotify(channel.isEnableNotify(appController.ownerId), it)
                        //todo
                    } ?: kotlin.run {
                        errorNetwork()
                    }
                }

                override fun onMemberDetail() {
                    channel.channelId().takeIf { it.isNotEmpty() }?.let {
                        navigateGroupDetail(it)
                    } ?: kotlin.run {
                        errorNetwork()
                    }
                }

                override fun onAddMember() {
                    channel.channelId().takeIf { it.isNotEmpty() }?.let {
                        navigateAddFriend(it)

                    } ?: kotlin.run {
                        errorNetwork()
                    }
                }

                override fun onLeaveGroup() {
                    channel.channelId().takeIf { it.isNotEmpty() }?.let {
//                        handleLeaveGroup(
//                            channel.isOwner(appController.ownerId),
//                                getRandomMember(channel)?.userId(),
//                                it
//                        )
                        //todo update
                    } ?: kotlin.run {
                        errorNetwork()
                    }
                }

                override fun onRemoveConversation() {
                    channel.channelId().takeIf { it.isNotEmpty() }?.let {
                        handleDeleteConversation(it)
                    } ?: kotlin.run {
                        errorNetwork()
                    }
                }

                override fun isEnableNotify(): Boolean {
                    return false
                }

                override fun isGroup(): Boolean {
                    return channel.isGroup()
                }
            })
        }.show()
    }

    private fun handleLeaveGroup(isOwner: Boolean, newOwnerId: String?, roomId: String) {
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
            setTextWarning(getString(R.string.chat_message_leave_group_title))
            setOnClickCancel {}
            setOnClickWarning {
                if (isOwner) {
                    onActionLeaveGroup(newOwnerId, roomId)
                } else {
                    onActionLeaveGroup(null, roomId)
                }
            }
            if (isOwner) {
                setTextPrimary(getString(R.string.chat_message_set_new_owner_title))
                setOnClickPrimary {
                    handleSetNewOwner(roomId)
                }
            }
        }.build().show(supportFragmentManager, "ChatAct")
    }

    private fun getRandomMember(roomEntity: ChannelEntity): MemberEntity? {
        return null
    }

    private fun onActionLeaveGroup(newOwnerId: String?, roomId: String) {
        viewModel.onLeaveGroup(newOwnerId, roomId)?.observe(this@ChatAct, Observer {
            it?.run {
                if (statusNetwork == StatusNetwork.SUCCESS) {
                    data?.takeIf { it.isNotEmpty() }?.run {

                        return@Observer
                    } ?: kotlin.run {
                        errorNetwork()
                    }
                } else if (statusNetwork != StatusNetwork.LOADING) {
                    errorNetwork()
                }
            }
        })
    }

    private fun handleUpdateStatusNotify(currentStatus: Boolean, it: String) {
        viewModel.onUpdateStatusNotifyAction(!currentStatus, it)
                ?.observe(this@ChatAct, Observer {
                    when {
                        it.isLoading -> {
                        }
                        it.isError -> {
                            errorNetwork()
                        }
                        it.isSuccess -> {
                            it.data?.run {
//                                if (this.notification) {
//                                    Toast.makeText(
//                                            this@ChatAct,
//                                            getString(R.string.chat_message_status_notify_on_toast),
//                                            Toast.LENGTH_SHORT
//                                    ).show()
//                                } else {
//                                    Toast.makeText(
//                                            this@ChatAct,
//                                            getString(R.string.chat_message_status_notify_off_toast),
//                                            Toast.LENGTH_SHORT
//                                    ).show()
//                                }
                            }
                        }
                    }
                })
    }

}

@Retention(AnnotationRetention.SOURCE)
@IntDef(HOME_ITEM, CONTACTS_ITEM, GROUP_ITEM)
annotation class MessBottomItem {
    companion object {
        const val HOME_ITEM = FragNavController.TAB1
        const val CONTACTS_ITEM = FragNavController.TAB2
        const val GROUP_ITEM = FragNavController.TAB3
    }
}


