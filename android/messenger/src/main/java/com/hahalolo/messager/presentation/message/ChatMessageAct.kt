/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.message

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.IntDef
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.hahalolo.messager.bubble.conversation.dialog.MentionDialog
import com.hahalolo.messager.bubble.conversation.view.react_detail.ChatReactionDetailBottomSheet
import com.hahalolo.messager.bubble.conversation.view.reader_detail.ChatReaderDetailBottomSheet
import com.hahalolo.messager.bubble.conversation.view.reader_detail.ChatReaderDetailListener
import com.hahalolo.messager.chatkit.adapter.*
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.ChatHolderItem
import com.hahalolo.messager.chatkit.view.input.MentionEditText
import com.hahalolo.messager.chatkit.view.input.MentionSpannableEntity
import com.hahalolo.messager.chatkit.view.mention.ChatMentionView
import com.hahalolo.messager.presentation.forward_message.ForwardMessageBottomSheet
import com.hahalolo.messager.presentation.group.detail.ChatGroupDetailAct
import com.hahalolo.messager.worker.download.DownloadMessageMediaWorker
import com.halo.common.utils.*
import com.hahalolo.messager.presentation.group.member.view.ChatWarningDialogBuilder
import com.hahalolo.messager.presentation.message.ChatMessageAct.ChatAction.Companion.CHAT_ROOM
import com.hahalolo.messager.presentation.message.ChatMessageAct.ChatAction.Companion.CHAT_USER
import com.hahalolo.messenger.R
import com.hahalolo.messager.presentation.group.detail.view.AvatarGroupMenu
import com.hahalolo.messager.presentation.message.adapter.ChatMessageAdapter
import com.hahalolo.messager.presentation.message.adapter.ChatMessageRecycleView
import com.hahalolo.messager.utils.FileUtils
import com.hahalolo.messenger.databinding.DeleteMessageCustomViewBinding
import com.hahalolo.pickercolor.util.setVisibility
import com.hahalolo.socket.SocketState
import com.halo.data.HalomeConfig
import com.halo.data.common.paging.NetworkState
import com.halo.data.entities.invite.InviteInfo
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.entity.ChannelEntity
import com.halo.data.room.table.AttachmentTable
import com.halo.data.room.table.MentionTable
import com.halo.data.entities.message.MessageSendBody
import com.halo.data.entities.mongo.tet.GifCard
import com.halo.data.room.entity.ChannelDetailEntity
import com.halo.data.room.type.AttachmentType
import com.halo.data.worker.sendMessage.SendMessageWorker
import com.halo.widget.anim.ViewAnim
import com.halo.widget.dialog.HaloDialogCustom
import com.halo.widget.felling.model.StickerEntity
import com.halo.widget.model.GifModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.message_keybroad_view.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
class ChatMessageAct : AbsChatMessageAct(), AvatarGroupMenu.AvatarGroupMenuListener {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    internal var viewModel: ChatMessageViewModel? = null

    private var messageAdapter: ChatMessageAdapter? = null

    private val dialogMessage by lazy { com.halo.widget.materialdialogs.MaterialDialog(this) }

    private val seenMessageListener = object : SeenMessageListener {
        override fun onSeenMessage(messageId: String?, time: Long?) {
            Strings.log("seenMessageListener onSeenMessage 1 ")
        }

        override fun onSeenMessage(messageId: String?) {
            Strings.log("seenMessageListener onSeenMessage 2 ")
        }
    }

    var firstScroll = false
    private var typingJob: Job? = null

    //    Rx java for delay
    private var disposables: CompositeDisposable? = null

    private var disposablesStatus: CompositeDisposable? = null

    private val imageLoader: ImageLoader = object : ImageLoader {
        override fun loadSmallAvatar(imageView: ImageView?, url: String?) {

            imageView?.run {
                this@ChatMessageAct.getRequestManager()
                    .load(
                        ThumbImageUtils.thumb(
                            ThumbImageUtils.Size.AVATAR_NORMAL,
                            url,
                            ThumbImageUtils.TypeSize._1_1
                        )
                    )
                    .placeholder(R.drawable.community_avatar_holder)
                    .error(R.drawable.community_avatar_holder)
                    .circleCrop()
                    .into(imageView)
            }
        }

        override fun loadImage(
            imageView: ImageView?, url: String?,
            payload: Any?, requestListener: RequestListener<Drawable>?
        ) {
            imageView?.run {
                this@ChatMessageAct.getRequestManager()
                    .load(url)
                    .placeholder(R.color.img_holder)
                    .error(R.color.img_holder)
                    .error(R.color.img_holder)
                    .listener(requestListener)
                    .into(imageView)
            }
        }

        override fun loadSticker(
            imageView: ImageView?, url: String?, payload: Any?,
            requestListener: RequestListener<Drawable>?
        ) {
            imageView?.run {
                this@ChatMessageAct.getRequestManager()
                    .load(url)
                    .placeholder(R.color.img_holder)
                    .error(R.color.img_holder)
                    .error(R.color.img_holder)
                    .listener(requestListener)
                    .into(imageView)
            }
        }

        override fun loadGif(
            imageView: ImageView?, url: String?, payload: Any?,
            requestListener: RequestListener<GifDrawable>?
        ) {
            imageView?.run {
                this@ChatMessageAct.getRequestManager()
                    .asGif()
                    .load(url)
                    .placeholder(R.color.img_holder)
                    .error(R.color.img_holder)
                    .listener(requestListener)
                    .into(this)
            }
        }

        override fun loadHahaGif(
            imageView: ImageView?,
            url: String?,
            payload: Any?,
            requestListener: RequestListener<GifDrawable>?
        ) {
            imageView?.run {
                this@ChatMessageAct.getRequestManager()
                    .asGif()
                    .load(url)
                    .placeholder(R.color.img_holder)
                    .error(R.color.img_holder)
                    .listener(requestListener)
                    .into(this)
            }
        }

        override fun loadAvatar(imageView: ImageView?, userID: String?, url: String?) {
            imageView?.run {
                this@ChatMessageAct.getRequestManager()
                    .load(
                        ThumbImageUtils.thumb(
                            ThumbImageUtils.Size.AVATAR_NORMAL,
                            url,
                            ThumbImageUtils.TypeSize._1_1
                        )
                    )
                    .placeholder(R.drawable.ic_dummy_personal_circle)
                    .error(R.drawable.ic_dummy_personal_circle)
                    .centerCrop()
                    .into(imageView)
            }
        }

        override fun getRequestManager(): RequestManager {
            return this@ChatMessageAct.getRequestManager()
        }
    }

    private val mentionDialogListener = object : MentionDialog.Listener {
        override fun isOwner(userId: String): Boolean {
            return TextUtils.equals(userId, appController.ownerId)
        }

        override fun navigatePersonalWall(mention: MemberEntity) {
            this@ChatMessageAct.navigatePersonalWall(mention.userId())
        }

        override fun navigateChat(mention: MemberEntity) {
            navigateChatMessageWithUser(mention.userId())
        }
    }

    private val chatMessageListener = object : ChatListener {

        override fun openWebUrl(url: String) {
            kotlin.runCatching {
                val uri = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                ActivityUtils.startActivity(intent)
            }
        }

        override fun onReactionMessage(messageId: String?, type: String) {
            viewModel?.onReactionMessage(messageId, type)
        }

        override fun onDeleteReaction(messageId: String, emoji: String) {
            viewModel?.onDeleteReaction(messageId, emoji)
        }

        override fun onAddMemberInGroup() {
            navigateAddMemberInGroup()
        }

        override fun onChangeGroupName() {
            navigateChangeGroupName()
        }

        override fun onChangeGroupAvatar() {
            navigateChangeGroupAvatar()
        }

        override fun onChangeNickName() {
            navigateChangeNickName()
        }

        override fun onImageClickListener(listImage: MutableList<AttachmentTable>?, position: Int) {
            navigateMediaViewer(listImage, position)
        }

        override fun onShowMenuAction() {
            KeyboardUtils.hideSoftInput(this@ChatMessageAct)
            if (KeyboardUtils.isSoftInputVisible(this@ChatMessageAct)) {
                binding?.chatMessageInput?.showLayoutHeader()
            }
        }

        override fun detailReactionOfMsg(message: MessageEntity?) {
            message?.reactionEntity()?.let {
                lifecycleScope.launch {
                    val result = async { viewModel?.convertMemberEntityReaction(it) }
                    result.await()?.let {
                        ChatReactionDetailBottomSheet().newInstance()?.apply {
                            setReaction(it.toMutableList())
                            show(supportFragmentManager, "ChatMessageAct")
                        }
                    }
                }
            }
        }

        override fun observerRoomEntity(observer: Observer<ChannelEntity>) {

        }

        override fun removeObserverRoomEntity(observer: Observer<ChannelEntity>) {

        }

        override fun onClickMention(mention: MentionTable) {
            showKeybroadSoft(false, binding?.chatMessageInput?.getInputEditText())
            val dialog = MentionDialog(this@ChatMessageAct)

        }

        override fun onClickReaderDetail(readers: MutableList<MemberEntity>?) {
            readers?.takeIf { it.isNotEmpty() }?.let { readers ->
                val bottomSheetDialog = ChatReaderDetailBottomSheet()
                    .newInstance()
                bottomSheetDialog?.setListReader(readers)
                bottomSheetDialog?.setListener(object : ChatReaderDetailListener {
                    override fun onItemClick(userId: String?) {
                        userId?.takeIf { it.isNotEmpty() }?.let {
                            navigatePersonalWall(it)
                        } ?: kotlin.run {
                            errorNetwork()
                        }
                    }

                    override fun onDismissListener() {
                        showLayoutKeyBroad(false)
                    }
                })
                bottomSheetDialog?.show(supportFragmentManager, "Bottom Sheet Dialog Fragment")
            }
        }

        override fun onRemoveMessageError(messageEntity: MessageEntity) {
            viewModel?.onRemoveMessageError(messageEntity)
        }

        override fun onInvitedChannel(data: InviteInfo) {
            data.code?.run {
                viewModel?.onInvitedChannel(this)
            }
        }

        override fun onAvatarClick(view: View?, messageEntity: MessageEntity?) {
            view?.let {
                messageEntity?.let { entity ->
                    showPopupAvatarClick(it, entity)
                }
            }
        }

        override fun getLanguageCode(): String {
            return appController.appLang()
        }

        override fun ownerId(): String {
            return appController.ownerId
        }

        override fun onDeleteMessage(message: MessageEntity?) {
            val selectView = DeleteMessageCustomViewBinding.inflate(
                LayoutInflater.from(this@ChatMessageAct), null, false
            )
            HaloDialogCustom.BuilderDelete().apply {
                setTitle("Xóa tin nhắn")
                setCustomView(selectView.root)
                setTextWarning("Xóa")
                setOnClickWarning {
                    selectView.deleteBt.takeIf { it.isChecked }?.run {
                        viewModel?.onDeleteMessage(message?.messageId() ?: "")
                    } ?: kotlin.run {
                        viewModel?.onRevokeMessage(message?.messageId() ?: "")
                    }
                }
                setOnClickCancel {
                }
            }.build().show(supportFragmentManager, "ChatMessageAct")
        }

        override fun onRevokeMessage(message: MessageEntity?) {

        }

        override fun onQuoteMessage(message: MessageEntity?) {
            // todo qoute message
            message?.run {
                binding?.chatMessageInput?.setQuoteMessage(this)
                lifecycleScope.launch {
                    delay(350)
                    showKeybroadSoft(true, binding?.chatMessageInput?.getInputEditText())
                }
            }
        }

        override fun onEditMessage(message: MessageEntity?) {
            message?.run {
                binding?.chatMessageInput?.setEditMessage(this)
                lifecycleScope.launch {
                    delay(350)
                    showKeybroadSoft(true, binding?.chatMessageInput?.getInputEditText())
                }
            }
        }

        override fun onDownloadMedia(message: MessageEntity?) {
            message?.let {
                onDownloadMediaPer(it)
            }
        }

        override fun onRetrySendMessage(message: MessageEntity?) {
            message?.let {

            }
        }

        override fun onCancelMessageError(message: MessageEntity?) {
            viewModel?.onCancelMessageError(message)
        }

        override fun onForwardMessage(message: MessageEntity?) {
            ForwardMessageBottomSheet.startForwardMessage(
                supportFragmentManager,
                message?.messageId() ?: ""
            )
        }
    }


    private fun navigateChangeGroupAvatar() {

    }

    private fun navigateChangeGroupName() {

    }

    @IntDef(CHAT_USER, CHAT_ROOM)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class ChatAction {
        companion object {
            const val CHAT_USER = 0
            const val CHAT_ROOM = 1
        }
    }

    override fun observerGifCards(observer: Observer<MutableList<GifCard>>) {

    }

    //   1 video  or list image
    override fun onSendMessageMedia(listUri: MutableList<Uri>) {
        createMessageInWorker(
            MessageSendBody(
                token = token(),
                workspaceId = viewModel?.workspaceId() ?: "",
                channelId = viewModel?.channelId() ?: "",
                ownerId = ownerId(),
                attachments = listUri.mapNotNull { FileUtils.getPath(this, it) }.toMutableList(),
                attachmentType = AttachmentType.IMAGE
            )
        )
    }

    override fun onSendMessageFile(uri: Uri) {
        if (FilenameUtils.isDataFile(this, uri)) {
//            com.halo.data.common.utils.Strings.log("SendMessageWorker onSendMessageFile       : ", uri)
        } else {
            val list = mutableListOf<Uri>()
            list.add(uri)
            onSendMessageMedia(list)
        }
    }

    override fun onSendMessageSticker(stickerEntity: StickerEntity) {
        createMessageInWorker(
            MessageSendBody(
                token = token(),
                workspaceId = viewModel?.workspaceId() ?: "",
                channelId = viewModel?.channelId() ?: "",
                ownerId = ownerId(),
                fromUrl = stickerEntity.getLinkUrl(),
                attachmentType = AttachmentType.STICKER
            )
        )
    }

    override fun onSendMessageGif(gifCard: GifCard) {
        createMessageInWorker(
            MessageSendBody(
                token = token(),
                workspaceId = viewModel?.workspaceId() ?: "",
                channelId = viewModel?.channelId() ?: "",
                ownerId = ownerId(),
                fromUrl = gifCard.path ?: "",
                attachmentType = AttachmentType.GIF
            )
        )
    }

    override fun onSendMessageGif(media: GifModel) {
        createMessageInWorker(
            MessageSendBody(
                token = token(),
                workspaceId = viewModel?.workspaceId() ?: "",
                channelId = viewModel?.channelId() ?: "",
                ownerId = ownerId(),
                fromUrl = media.gif?.url ?: "",
                attachmentType = AttachmentType.GIF
            )
        )
    }

    override fun onSendMessageText(
        content: String,
        editMsgId: String?,
        mentionTables: MutableList<MentionSpannableEntity>?,
        quoteMsg: MessageEntity?
    ) {
        createMessageInWorker(
            MessageSendBody(
                token = token(),
                workspaceId = viewModel?.workspaceId() ?: "",
                channelId = viewModel?.channelId() ?: "",
                ownerId = ownerId(),
                content = content,
                editMsgId = editMsgId,
                mentions = mentionTables?.mapNotNull { it.member?.userId() }?.toMutableList(),
                quoteMessageId = quoteMsg?.messageId()
            )
        )
    }

    override fun onSendMessageHaha() {

    }

    override fun onStartTyping() {
        typingJob = lifecycleScope.launch {
            viewModel?.onStartTyping()
            delay(HalomeConfig.TYPING_TIME_SEND)
            onStartTyping()
        }
    }

    override fun onStopTyping() {
        typingJob?.cancel()
        viewModel?.onStopTyping()
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.chat_message_act_v2)
        viewModel = ViewModelProvider(this, factory).get(ChatMessageViewModel::class.java)
        binding?.lifecycleOwner = this
    }

    override fun initializeLayout() {
        super.initializeLayout()

        initHandleSocketState()

        initCreateRoomChat()
        initHandleRoomEntity()
        initHandleReactions()
        initHandleDeleteMessage()

        initMessageAdapter()
        initHandleMessages()
        initHandleCountMsgUnread()
        initHandleTyping()

        initHandleJoinOtherChannel()

        initMessageInput()
        initAction()
    }

    //Todo
    private fun handleRemoveAction() {
        ChatWarningDialogBuilder()
            .withTitle(getString(R.string.chat_message_reaction_detail_remove_title))
            .withDescription(getString(R.string.chat_message_reaction_detail_remove_des))
            .withSubmitTextButton(getString(R.string.chat_message_change_nick_name_remove))
            .withListener(object :
                ChatWarningDialogBuilder.ChatWarningDialogListener {
                override fun onSubmit(newContent: String) {

                }
            }).build().show(supportFragmentManager, "ChatMessageAct")
    }

    private fun initMessageAdapter() {
        messageAdapter = ChatMessageAdapter(
            appController.ownerId,
            chatMessageListener,
            imageLoader
        )

        messageAdapter?.setSeenMessageSListener(seenMessageListener)

        binding?.messageList?.initAdapter(messageAdapter!!)
        binding?.messageList?.updateListener(object : ChatMessageRecycleView.Listener {
            override fun onItemAtEndLoaded() {
                viewModel?.messagePaging?.value?.onItemAtEndLoaded()
            }
        })
    }

    private fun initHandleMessages() {
        viewModel?.messageState?.observe(this, {
            it.second?.run {
                //state loadmore

            } ?: it.first?.run {
                //state Refresh
                val isEmpty =
                    (it == NetworkState.LOADED && viewModel?.messagePaging?.value?.isEmpty == true)
                binding?.emptyLayout?.setVisibility(isEmpty)
                binding?.messageLoading?.setVisibility(it == NetworkState.LOADING)
            }
        })

        viewModel?.messageList?.observe(this, Observer {
            messageAdapter?.submitList(it ?: mutableListOf())
        })
    }

    private fun initHandleCountMsgUnread() {

    }

    private fun initHandleTyping() {
        lifecycleScope.launch {
            viewModel?.typingResponse?.collectLatest {
            }
        }
    }


    private fun initHandleJoinOtherChannel() {
        lifecycleScope.launch {
            viewModel?.inviteJoinReponse?.collectLatest {
                it.data?.channelId?.takeIf { it.isNotEmpty() }?.run {
                    startActivity(getIntentChannel(this@ChatMessageAct, this))
                }
            }
        }
    }

    private fun showUnreadMsgLayout(count: Int, firstItem: Int) {
        binding?.countNewMessageTv?.text = resources.getQuantityString(
            R.plurals.chat_message_there_are_count_unread_message,
            count,
            count
        )
        binding?.newMsg = count > 0
        ViewAnim.popupTop(binding?.layoutNewMessage, (firstItem > 0))
//        ViewAnim.popupTop(binding?.layoutNewMessage, (count > 0 && firstItem > 0))
    }

    private fun onDelayForScrollTop(time: Long) {
        observeDelay(time, object : DisposableObserver<Long>() {
            override fun onComplete() {
                binding?.messageList?.scrollToPosition(0)
                firstScroll = true
            }

            override fun onNext(t: Long) {
            }

            override fun onError(e: Throwable) {
            }
        })
    }

    private fun handleLayoutWave(empty: Boolean) {
        disposablesStatus?.clear()
        observeDelayStatus(250, object : DisposableObserver<Long>() {
            override fun onComplete() {
                binding?.emptyLayout?.visibility = if (empty) View.VISIBLE else View.GONE
            }

            override fun onNext(t: Long) {
            }

            override fun onError(e: Throwable) {
            }
        })
    }

    private fun initHandleSocketState() {
        viewModel?.socketState?.observe(this, {
            when (it) {
                SocketState.CONNECTING -> {
                }
                SocketState.CONNECTED -> {
                }
                SocketState.DISCONNECT -> {
                }
                SocketState.AUTHEN -> {
//                    onAuthen()
                }
                else -> {
                }
            }
            binding?.socketErrorStateTv?.setVisibility(it == SocketState.DISCONNECT)
            binding?.socketConnectedStateTv?.setVisibility(it == SocketState.CONNECTED)
            binding?.socketConnecting?.setVisibility(it == SocketState.CONNECTING)
            ViewAnim.Builder.createAl(binding?.stateGr)
                .showDown()
                .duration(if (it == SocketState.CONNECTED) 300 else 0)
                .show(it != SocketState.CONNECTED)
        })
    }

    private fun initCreateRoomChat() {
        if (intent != null) {
            intent?.getStringExtra(USER_ID_ARGS)?.takeIf { it.isNotEmpty() }?.run {
                viewModel?.friendId?.value = this
            }

            intent?.getStringExtra(CHANNEL_ID)?.takeIf { it.isNotEmpty() }?.run {
                viewModel?.updateChannelId(this)
            }
        }
    }

    private fun initHandleRoomEntity() {
        lifecycleScope.launch {
            viewModel?.friendChannel?.collectLatest {
                it.statusNetwork.takeIf { it==400 }?.run {
                    // not create channel
                    binding?.requestCreateView?.visibility = View.VISIBLE
                    binding?.createChannelBt?.setOnClickListener {
                        binding?.requestCreateView?.visibility = View.GONE
                        viewModel?.createFriendId?.value = viewModel?.friendId?.value?:""
                    }
                }?: it.data?.channelId?.takeIf { it.isNotEmpty() }?.run {
                    binding?.requestCreateView?.visibility = View.GONE
                    // have this channel - update channel id
                    viewModel?.updateChannelId(this)
                }?:run{
                    binding?.requestCreateView?.visibility = View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel?.createFriendChannel?.collectLatest {
                it.data?.channelId?.takeIf { it.isNotEmpty() }?.run {
                    //update channel id
                    viewModel?.updateChannelId(this)
                }
            }
        }

        viewModel?.channelDetailEntity?.observe(this) { entity ->
            Strings.log("mentionResult entity ", entity)
            entity?.run {
                viewModel?.loadMessageOfChannel(this.channelId())
                binding?.nameTv?.text = this.channelName()
                binding?.avatarIv?.setRequestManager(getRequestManager())
                binding?.avatarIv?.setImage(this.channelAvatar())
                binding?.statusTv?.setVisibility(this.isPrivateChannel())
                updateRoomStatus(this)
            }
        }

        viewModel?.mentionResult?.observe(this) {

        }
    }

    private fun updateRoomStatus(channel: ChannelDetailEntity) {
        try {
            if (channel.isPrivateChannel()) {
                binding?.statusTv?.setText(if (channel.isFriendOnline()) R.string.chat_message_user_oline else R.string.chat_message_user_offline)
                binding?.statusTv?.setTextColor(
                    ContextCompat.getColor(
                        this,
                        if (channel.isFriendOnline()) R.color.messenger_text_focus
                        else R.color.messenger_text_light
                    )
                )
            } else {
                when (val size = channel.memberCount()) {
                    0 -> getString(R.string.chat_message_group_no_members)
                    1 -> getString(R.string.chat_message_group_only_you)
                    else -> String.format(
                        getString(R.string.chat_message_group_count_members),
                        size
                    )
                }

            }
            binding?.statusTv?.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initAction() {

        binding?.navigationBt?.setOnClickListener {
            finish()
        }

        binding?.nameGr?.setOnClickListener {
            onAvatarClick()
        }

        binding?.avatarIv?.setOnClickListener {
            onAvatarClick()
        }

        binding?.chatSettingBt?.setOnClickListener {
            viewModel?.channelId()?.let { channelId ->
                startActivity(ChatGroupDetailAct.getIntent(this@ChatMessageAct, channelId))
            }
        }

        binding?.chatGalleryView?.setOnClickListener {
            navigateShareMedia()
        }

        binding?.sayHelloBt?.setOnClickListener {

        }

        binding?.countNewMessageTv?.setOnClickListener {
            onDelayForScrollTop(0)
        }

        binding?.scrollLastBt?.setOnClickListener {
            onDelayForScrollTop(0)
        }
    }

    private fun onAvatarClick() {

    }

    private fun showDialogAvatarGroup(isHaveAvatar: Boolean) {
        val menu = AvatarGroupMenu(this@ChatMessageAct, true, isHaveAvatar)
        menu.show(supportFragmentManager, "ChatMessageAct")
    }

    private fun initMessageInput() {

        binding?.chatMessageInput?.setMentionListener(object : MentionEditText.MentionListener {
            var queryJob: Job? = null

            override fun onQueryTagFriend(s: String?) {
                Strings.log("mentionResult onQueryTagFriend " + s)
                queryJob?.cancel()
                s?.run {
                    queryJob = this@ChatMessageAct.lifecycleScope.launch {
                        delay(300)
                        Strings.log("mentionResult query " + s)
                        viewModel?.mentionQuery?.value = s
                    }
                } ?: kotlin.run {
                    viewModel?.mentionQuery?.value = null
                }
            }

            override fun onTextChange(s: String?) {

            }

            override fun onInsertStickerFromKeybroad(sticker: Boolean, linkUri: Uri?) {
//                linkUri?.toString()?.takeIf { it.isNotEmpty() }?.run {
//                    val isGif = ThumbImageUtils.isGif(this)
//                    val model = if (isGif) SendMessageModel.gif(
//                        appController.ownerId,
//                        viewModel?.roomEntity?.value?.roomId(),
//                        this
//                    ) else SendMessageModel.sticker(
//                        appController.ownerId,
//                        viewModel?.roomEntity?.value?.roomId(),
//                        this
//                    )
//                    model?.run {
//                        createMessageInWorker(model)
//                    }
//                }
            }
        })

        binding?.chatMentionView?.setRequestManager(getRequestManager())
        binding?.chatMentionView?.setChatMentionListener(object :
            ChatMentionView.ChatMentionListener {
            override fun observerList(observer: Observer<MutableList<MemberEntity>>) {
                viewModel?.mentionResult?.observe(this@ChatMessageAct, observer)
            }

            override fun onMentionMember(member: MemberEntity) {
                Strings.log("mentionResult onMentionMember ", member)
                binding?.chatMessageInput?.addMentions(member)
            }

            override fun queryMention(): String? {
                return viewModel?.mentionQuery?.value
            }
        })
    }

    private fun showPopupAvatarClick(view: View, messageEntity: MessageEntity?) {
        val popup = PopupMenu(this@ChatMessageAct, view)
        popup.menuInflater.inflate(R.menu.chat_message_user_infor_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_message_bt -> {
                    navigateChatMessageWithUser(messageEntity?.userId() ?: "")
                }
                R.id.action_infor_bt -> {
                    navigatePersonalWall(messageEntity?.userId() ?: "")
                }
            }
            popup.dismiss()
            true
        }
        popup.show()
    }

    private fun onDownloadMediaPer(message: MessageEntity) {
        HaloFileUtils.externalPermision(this, object : HaloFileUtils.PerListener {
            override fun onGranted() {
//                message.attachmentTables?.firstOrNull()?.run {
//                    val id = DownloadMessageMediaWorker.downloadMedia(this@ChatMessageAct, this)
//                    initHandleDownloadMedia(id)
//                }
            }

            override fun onDeny() {

            }
        })
    }

    //create new message to worker
    private fun createMessageInWorker(body: MessageSendBody) {
        try {
            SendMessageWorker.sendMessage(
                this,
                body
            ).observe(this, Observer {

            })
            binding?.emptyLayout?.visibility = View.GONE
            binding?.messageList?.scrollToPosition(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initHandleReactions() {
        lifecycleScope.launch {
            viewModel?.reactionResponse?.collectLatest {
            }
        }
    }

    private fun initHandleDeleteMessage() {
        lifecycleScope.launch {
            viewModel?.deleteMsgResponse?.collectLatest {
            }
        }
    }

    private fun initHandleDownloadMedia(workerId: UUID) {
        WorkManager.getInstance(this)
            .getWorkInfoByIdLiveData(workerId)
            .observe(this, { workInfo ->
                try {
                    when (workInfo?.state) {
                        WorkInfo.State.FAILED -> {
                            makeSnackbar(R.string.chat_message_download_failed)
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            val path =
                                workInfo.outputData.getString(DownloadMessageMediaWorker.OUTPUT_DOWNLOAD_PATH)
                            path?.takeIf { it.isNotEmpty() }?.run {
                                makeSnackbar(getString(R.string.chat_message_save_to, this))
                            }
                        }
                        WorkInfo.State.RUNNING -> {
//                            val progress = workInfo.outputData.getInt(
//                                DownloadMessageMediaWorker.OUTPUT_DOWNLOAD_PROGRESS,
//                                0
//                            )
                        }
                        else -> {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
    }

    private fun showErrorLayout(error: Boolean) {
        // room is not exit , thong bao ko the tra loi tin nhan luc nay
        binding?.chatMessageInput?.visibility = if (error) View.INVISIBLE else View.VISIBLE
    }

    override fun onDestroy() {
        binding?.messageList?.takeIf { it.childCount > 0 }?.run {
            for (i in 0 until childCount) {
                this.getChildAt(i)?.let {
                    (this.getChildViewHolder(it) as? ChatHolderItem<*>)?.invalidateLayout(
                        getRequestManager()
                    )
                }
            }
        }

        viewModel?.clearRangeMessage()
        disposables?.clear()
        disposablesStatus?.clear()
        super.onDestroy()
    }

    private fun observeDelay(time: Long, longDisposableObserver: DisposableObserver<Long>) {
        if (disposables == null) disposables = CompositeDisposable()
        disposables?.add(
            Observable.timer(time, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(longDisposableObserver)
        )
    }

    private fun observeDelayStatus(time: Long, longDisposableObserver: DisposableObserver<Long>) {
        if (disposablesStatus == null) disposablesStatus = CompositeDisposable()
        disposablesStatus?.add(
            Observable.timer(time, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(longDisposableObserver)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isStarting = true
    }

    override fun onResume() {
        viewModel?.openRoom()
        viewModel?.onResumeChatAct()
        super.onResume()
    }

    override fun onPause() {
        viewModel?.closeRoom()
        viewModel?.onPauseChartAct()
        super.onPause()
    }

    override fun finish() {
        isStarting = false
        viewModel?.disableReceiverTyping()
        viewModel?.onFinishChatAct()
        super.finish()
    }

    /*NAVIGATE*/
    private fun navigateAddMemberInGroup() {

    }

    private fun navigateChangeNickName() {

    }

    private fun navigateChatMessageWithUser(friendId: String?) {

    }

    private fun navigateShareMedia() {

    }


    private fun navigatePersonalWall(userId: String) {

    }

    private fun navigateMediaViewer(listImage: MutableList<AttachmentTable>?, position: Int) {

    }

    override fun navigateAvatarView() {

    }

    override fun navigateUpdateAvatar() {
        navigateChangeGroupAvatar()
    }

    override fun navigateViewMember() {

    }
    /*NAVIGATE END */

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

        }
    }

    companion object {

        var isStarting = false

        private const val USER_ID_ARGS = "ChatMessageAct-UserSocket-Id-Args"
        private const val CHANNEL_ID = "CHANNEL_ID"
        private const val CHAT_ACTION = "CHAT_ACTION"

        private const val REQUEST_MESSAGE_SETTING = 111
        private const val REQUEST_MESSAGE = 112
        private const val REQUEST_GROUP_MEMBER = 113

        fun getIntent(context: Context, friendId: String): Intent {
            val intent = Intent(context, ChatMessageAct::class.java)
            intent.putExtra(USER_ID_ARGS, friendId)
            intent.putExtra(CHAT_ACTION, CHAT_USER)
            return intent
        }

        fun getIntentChannel(context: Context, channelId: String): Intent {
            val intent = Intent(context, ChatMessageAct::class.java)
            intent.putExtra(CHANNEL_ID, channelId)
            intent.putExtra(CHAT_ACTION, CHAT_ROOM)
            return intent
        }
    }
}