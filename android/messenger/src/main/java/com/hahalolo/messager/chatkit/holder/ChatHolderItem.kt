/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.holder

import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hahalolo.messager.chatkit.adapter.ChatListener
import com.hahalolo.messager.chatkit.commons.ImageLoader
import com.hahalolo.messager.chatkit.holder.view.ChatMessageTextView
import com.hahalolo.messager.chatkit.messages.MessagesListStyle
import com.hahalolo.messager.chatkit.utils.ChatConstant
import com.hahalolo.messager.chatkit.utils.DateFormatter
import com.hahalolo.messager.chatkit.view.ChatReactionView
import com.hahalolo.messager.chatkit.view.MessageReaderItem
import com.hahalolo.messager.chatkit.view.media.ChatMutilImageView
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.entity.MessageBlockEntity
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.table.MentionTable
import com.hahalolo.messenger.R
import com.hahalolo.swipe.SwipeLayout
import com.halo.common.utils.*
import com.halo.data.room.type.MessageType
import com.halo.data.room.type.ReactionType
import com.halo.widget.felling.utils.FeelingUtils
import com.halo.widget.reactions.MessageMenuListener
import com.halo.widget.reactions.ReactionClickListener
import com.halo.widget.reactions.ReactionSelectedListener
import com.halo.widget.reactions.message.MessageReactionPopup
import com.halo.widget.reactions.message.MessageReactionUtils
import java.util.*
import kotlin.math.abs

open class ChatHolderItem<BINDING : ViewDataBinding>(
    val binding: BINDING,
    val chatListener: ChatListener,
    val holderListener: HolderListener,
    val imageLoader: ImageLoader,
    val style: MessagesListStyle
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        const val MARGIN_GROUP = 1f
        const val MARGIN_NON_GROUP = 8f
    }

    protected var time: TextView? = null
    protected var messageStatus: TextView? = null
    protected var messageReaderItem: MessageReaderItem? = null
    protected var messageGroup: ViewGroup? = null
    protected var reactionView: ChatReactionView? = null
    protected var messageBubble: View? = null
    protected var avLoading: View? = null

    private val requestListener: RequestListener<Drawable> = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            avLoading?.visibility = View.GONE
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            avLoading?.visibility = View.GONE
            return false
        }
    }

    private val requestGifListener: RequestListener<GifDrawable> =
        object : RequestListener<GifDrawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<GifDrawable>?,
                isFirstResource: Boolean
            ): Boolean {
                avLoading?.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: GifDrawable?,
                model: Any?,
                target: Target<GifDrawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                avLoading?.visibility = View.GONE
                return false
            }
        }

    protected open fun initView(binding: BINDING) {
        time = itemView.findViewById(R.id.messageTime)
        messageStatus = itemView.findViewById(R.id.messageStatus)
        messageGroup = itemView.findViewById(R.id.messageGroup)
        messageReaderItem = itemView.findViewById(R.id.messageReaderItem)
        reactionView = itemView.findViewById(R.id.messageReactionItem)
        messageBubble = itemView.findViewById(R.id.messageBubble)
        avLoading = itemView.findViewById(R.id.messageAvLoading)
    }

    init {
        this.initView(this.binding)
        this.applyStyle(style)
    }

    protected open fun applyStyle(style: MessagesListStyle) {

    }

    open fun onBind(iMessage: MessageEntity) {
        onBindReaction(iMessage)
        onBindTime(iMessage)
        onBindStatus(iMessage)
        onBindLastSeenMessage(iMessage)
        //bind action
        bindActionReactions(messageBubble, iMessage)
        bindAction(iMessage)
    }

    private fun bindAction(iMessage: MessageEntity) {
        reactionView?.setOnClickListener {
            chatListener.detailReactionOfMsg(iMessage)
        }
    }

    private fun onBindStatus(iMessage: MessageEntity) {
        messageStatus?.visibility = View.GONE
        iMessage.run {
//            val sp = SpanBuilderUtil()
//            if (isSeenMessage(chatListener.ownerId())) {
//                getListUserSeenMessage(chatListener.ownerId()).run {
//                    forEachIndexed { index, memberEntity ->
//                        sp.append(memberEntity.getUserName())
//                        if (index < size - 1) {
//                            sp.appendWithSpace(",")
//                        }
//                    }
//                    sp.append(binding.root.context.getString(R.string.chat_messenger_seen_message))
//                }
//            } else if (isSendding()) {
//                sp.append(binding.root.context.getString(R.string.chat_messenger_sending_message))
//            }
//            messageStatus?.text = sp.build()
        }
    }

    private fun onBindReaction(iMessage: MessageEntity) {
//        iMessage.reactions?.run {
//            reactionView?.visibility = View.VISIBLE
//            reactionView?.updateListReactions(this)
//        } ?: run {
//            reactionView?.visibility = View.GONE
//        }
    }

    private fun onBindTime(iMessage: MessageEntity) {
        time?.tag = iMessage.messageId()

        iMessage.messageTime()?.run {
            time?.visibility = View.VISIBLE
            time?.text = DateFormatter.format(Date(this), DateFormatter.Template.TIME)
        } ?: run {
            time?.visibility = View.GONE
        }
    }

    private fun onBindLastSeenMessage(iMessage: MessageEntity) {
//        iMessage.getListUserSeenMessage(chatListener.ownerId()).takeIf { !it.isNullOrEmpty() }
//            ?.run {
//                messageReaderItem?.visibility = View.VISIBLE
//                messageReaderItem?.setImageLoader(imageLoader)
//                messageReaderItem?.setMemberEntities(this)
//                messageReaderItem?.setOnClickListener { chatListener.onClickReaderDetail(this) }
//            } ?: run {
//            messageReaderItem?.visibility = View.GONE
//        }
    }

    fun onBindLastSeenMsgDeleted(mutableList: MutableList<MemberEntity>) {
        //todo update danh sách user đã xem khi tin nhan sau đó bị xóa
        mutableList
            .takeIf { !it.isNullOrEmpty() }
            ?.run {
                messageReaderItem?.visibility = View.VISIBLE
                messageReaderItem?.setImageLoader(imageLoader)
                messageReaderItem?.setMemberEntities(this)
            }
            ?: run {
                messageReaderItem?.visibility = View.GONE
            }
    }

    private fun onDropLikeAndShowTime(message: MessageEntity) {
        if (clickedMention) {
            clickedMention = false
        } else if (!message.canDropLike(chatListener.ownerId())) {
            chatListener.onReactionMessage(message.messageId(), ReactionType.LIKE)
        } else {
            holderListener.onShowTimeWhenClick(message.messageId(), time)
        }
    }

    fun bindDropLikeAndShowTime(message: MessageEntity, view: View?) {
        view?.setOnClickListener {
            onDropLikeAndShowTime(message)
        }
    }

    open fun onMessageGroupType(messageGroupType: MessageGroupType) {
        messageGroup?.run {
            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            when (messageGroupType) {
                MessageGroupType.START -> params.setMargins(
                    0,
                    SizeUtils.dp2px(MARGIN_NON_GROUP),
                    0,
                    SizeUtils.dp2px(MARGIN_GROUP)
                )
                MessageGroupType.LAST -> params.setMargins(
                    0,
                    SizeUtils.dp2px(MARGIN_GROUP),
                    0,
                    SizeUtils.dp2px(MARGIN_NON_GROUP)
                )
                MessageGroupType.ONLY -> params.setMargins(
                    0,
                    SizeUtils.dp2px(MARGIN_NON_GROUP),
                    0,
                    SizeUtils.dp2px(MARGIN_NON_GROUP)
                )
                MessageGroupType.MEDIUM -> params.setMargins(
                    0,
                    SizeUtils.dp2px(MARGIN_GROUP),
                    0,
                    SizeUtils.dp2px(MARGIN_GROUP)
                )
            }
            this.layoutParams = params
        }
    }

    protected fun configureLinksBehavior(text: TextView) {
        text.linksClickable = false
        text.movementMethod = object : LinkMovementMethod() {
            override fun onTouchEvent(
                widget: TextView,
                buffer: Spannable,
                event: MotionEvent
            ): Boolean {
                val result = false
//                if (!MessagesListAdapter.isSelectionModeEnabled) {
//                    result = super.onTouchEvent(widget, buffer, event)
//                }
                itemView.onTouchEvent(event)
                return result
            }
        }
    }

    private fun checkDownloaded(url: String?): Boolean {
        return HaloFileUtils.isDownLoaded(FilenameUtils.getName(url))
    }

    protected fun bindStickerMedia(
        image: ImageView?,
        imageUrl: String?
    ) {
        avLoading?.visibility = View.VISIBLE
        if (ThumbImageUtils.isGif(imageUrl)) {
            imageLoader.loadGif(image, imageUrl, null, requestGifListener)
        } else {
            imageLoader.loadSticker(image, imageUrl, null, requestListener)
        }
    }

    protected fun bindGifMedia(image: AppCompatImageView, imageUrl: String) {
        avLoading?.visibility = View.VISIBLE
        imageLoader.loadGif(image, imageUrl, null, requestGifListener)
    }

    protected fun bindWaveMedia(image: ImageView?) {
        avLoading?.visibility = View.VISIBLE
        val link = itemView.context.getString(R.string.chat_message_icon_say_hello)
        imageLoader.loadGif(image, link, null, requestGifListener)
    }

    protected fun bindHahaMedia(image: ImageView?) {
        avLoading?.visibility = View.VISIBLE
        //for gif
        val link = itemView.context.getString(R.string.chat_message_icon_haha)
        imageLoader.loadHahaGif(image, link, null, requestGifListener)
    }

    private fun getTypeReaction(position: Int): String {
        return when (position) {
            0 -> ReactionType.LIKE
            1 -> ReactionType.LOVE
            2 -> ReactionType.HAHA
            3 -> ReactionType.WOW
            4 -> ReactionType.CRY
            5 -> ReactionType.ANGRY
            else -> ""
        }
    }

    protected fun bindActionReactions(view: View?, message: MessageEntity) {
        if (message.isDeleted() || message.isRemoved() || message.isSendError() || message.isSending()) {
            view?.setOnTouchListener(null)
            view?.setOnLongClickListener(null)
            if (view is ChatMutilImageView) {
                view.disableReaction()
            }
            return
        }
        val reactionSelectedListener = object : ReactionSelectedListener {
            override fun invoke(position: Int): Boolean {
                chatListener.onReactionMessage(message.messageId(), getTypeReaction(position))
                return true
            }
        }
        val reactionClickListener = object : ReactionClickListener {
            override fun invoke(): Boolean {
                return false
            }
        }

        val messageMenuListener = object : MessageMenuListener {
            override fun enableEdit(): Boolean {
                return message.isEnableEdit() && message.isOwnerMessage(chatListener.ownerId())
            }

            override fun enableRevoke(): Boolean {
                return message.isOwnerMessage(chatListener.ownerId())
            }

            override fun enableQuote(): Boolean {
                // Disable quote in menu reaction
                return false
            }

            override fun enableQuoteBtn(): Boolean {
                return TextUtils.equals(
                    message.messageType(),
                    MessageType.TEXT
                ) && message.messageText().isNotEmpty()
            }

            override fun enableDelete(): Boolean {
                // tạm thời ẩn action delete message
                return false
            }

            override fun enableDownload(): Boolean {
//                return message.messageType() == MessageType.MEDIA &&
//                        (message.attachmentTables?.size == 1)
                return false
            }

            override fun enableCopy(): Boolean {
                return TextUtils.equals(
                    message.messageType(),
                    MessageType.TEXT
                ) && message.messageText().isNotEmpty()
            }

            override fun enableClearReaction(): Boolean {
                //TODO Pending clear reaction
                return false
            }

            override fun enableForward(): Boolean {
                return false
            }

            override fun haveReaction(): Boolean {
//                return message.reactions?.find {
//                    TextUtils.equals(it.userId, chatListener.ownerId())
//                } != null
                return false
            }

            override fun enableReaction(int: Int): Boolean {
//                return message.reactions?.find {
//                    TextUtils.equals(it.userId, chatListener.ownerId())
//                            && TextUtils.equals(getTypeReaction(int), it.type)
//                } != null
                return false
            }

            override fun onDeleteMessage() {
                chatListener.onDeleteMessage(message)
            }

            override fun onRevokeMessage() {
                chatListener.onRevokeMessage(message)
            }

            override fun onQuoteMessage() {
                chatListener.onQuoteMessage(message)
            }

            override fun onEditMessage() {
                chatListener.onEditMessage(message)
            }

            override fun onDownloadMedia() {
                chatListener.onDownloadMedia(message)
            }

            override fun onCopyMessage() {
                ClipbroadUtils.copyText(itemView.context, message.messageText())
            }

            override fun onClearReaction() {

            }

            override fun onForwardMessage() {

            }

            override fun onShowPopup() {
                chatListener.onShowMenuAction()
            }
        }
        reactionPopup.updateListener(
            view,
            reactionSelectedListener,
            reactionClickListener,
            messageMenuListener
        )
        if (view is ChatMutilImageView) {
            view.bindReaction(reactionSelectedListener, reactionClickListener, messageMenuListener)
        } else if (view is ChatMessageTextView) {
            view.bindReaction(reactionSelectedListener, reactionClickListener, messageMenuListener)
        }
    }

    private val reactionPopup: MessageReactionPopup by lazy {
        MessageReactionUtils.initPopup(itemView.context)
    }

    protected fun setBubbleSize() {
//        val WIDTH = (WIDTH_SCREEN * 2.5f / 4).toInt()
    }

    protected fun setImageSize(
        image: ImageView, imageWidth: Int, imageHeight: Int,
        WIDTH_SCREEN: Int, HEIGHT_SCREEN: Int
    ) {

        val WIDTH = (WIDTH_SCREEN * 2.5f / 4).toInt()
        var with = WIDTH
        var height = -2
        val scaleType = getScaleType(imageWidth, imageHeight)

        if (scaleType == ImageView.ScaleType.CENTER_CROP) {
            if (imageWidth > imageHeight) {
                height = WIDTH / 16 * 9
                with = WIDTH
            } else {
                height = WIDTH / 9 * 16
                with = WIDTH
            }
        } else {
            if (imageWidth > 0 && imageHeight > 0 && WIDTH > 0) {
                val t1 = imageHeight.toFloat() / imageWidth
                val t2 = HEIGHT_SCREEN.toFloat() / WIDTH_SCREEN
                if (t1 > t2) {
                    height = WIDTH_SCREEN
                    with = WIDTH_SCREEN * imageWidth / imageHeight
                } else {
                    with = WIDTH
                    height = WIDTH * imageHeight / imageWidth
                }
            }
        }
        image.scaleType = scaleType

        val param = image.layoutParams
        param?.width = with
        param?.height = height
        image.layoutParams = param
    }

    private fun getScaleType(width: Int, height: Int): ImageView.ScaleType {
        if (width > 0 && height > 0) {
            if (width / height > 16 / 9 || height / width > 16 / 9) {
                return ImageView.ScaleType.CENTER_CROP
            }
        }
        return ImageView.ScaleType.FIT_CENTER
    }

    protected fun setGiphySize(path:String?, imageView: ImageView, WIDTH_SCREEN: Int) {
        if(FeelingUtils.isStickerCard(path)){
            val WIDTH = (WIDTH_SCREEN * 2.5f / 4).toInt()
            imageView.layoutParams.width = WIDTH
            imageView.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        }else{
            val WIDTH = (WIDTH_SCREEN * 3f / 8).toInt()
            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            imageView.layoutParams.width = WIDTH
            imageView.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
        }
    }

    protected fun setStickerSize(imageUrl: String, imageView: ImageView, WIDTH_SCREEN: Int) {
        val WIDTH = (WIDTH_SCREEN * 2.5f / 4).toInt()
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        val link = imageUrl
        when {
            FeelingUtils.isStickerCard(imageUrl) -> {
                imageView.layoutParams.width = WIDTH
                imageView.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            link.contains(ChatConstant.HAHA_GIF) -> {
                imageView.layoutParams.width = WIDTH_SCREEN / 8
                imageView.layoutParams.height = WIDTH_SCREEN / 8
            }
            else -> {
                imageView.layoutParams.width = WIDTH_SCREEN / 4
                imageView.layoutParams.height = WIDTH_SCREEN / 4
            }
        }
    }

    protected fun setWaveSize(imageView: ImageView, WIDTH_SCREEN: Int) {
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        when {
            else -> {
                imageView.layoutParams.width = WIDTH_SCREEN / 4
                imageView.layoutParams.height = WIDTH_SCREEN / 4
            }
        }
    }

    protected fun setHahaSize(imageView: ImageView, WIDTH_SCREEN: Int) {
        imageView.scaleType = ImageView.ScaleType.FIT_CENTER
        when {
            else -> {
                imageView.layoutParams.width = WIDTH_SCREEN / 8
                imageView.layoutParams.height = WIDTH_SCREEN / 8
            }
        }
    }

    protected fun bindText(
        messageText: ChatMessageTextView,
        incoming: Boolean = false,
        message: MessageEntity
    ) {
        val messageContent = message.messageText()
        val msgBlocks: MutableList<MessageBlockEntity>? = null

        messageText.updateChatMessageTextListener(object :
            ChatMessageTextView.ChatMessageTextListener {
            override fun onClickSpan(mention: MentionTable) {
                if (!mention.isMentionAll()) {
                    clickedMention = true
                    chatListener.onClickMention(mention)
                }
            }

            override fun onClickTextView() {
                onDropLikeAndShowTime(message)
            }

            override fun onDoubleClickTextView() {
                ClipbroadUtils.copyText(itemView.context, message.messageText())
            }

            override fun onTripleClickTextView() {

            }
        })
//        messageText.updateMessageBlock(incoming, messageContent, msgBlocks)
    }

    protected fun bindSwipe(iMessage: MessageEntity) {
        binding.root.findViewById<SwipeLayout>(R.id.messageSwipe)?.apply {
            this.removeAllSwipeListener()
            this.isClickToClose = false
            this.setOnClickListener(null)
            this.isSwipeEnabled = iMessage.isEnableQuote()
            this.addSwipeListener(object : SwipeLayout.SwipeListener {
                private var surFaceLeft = 0
                private val SIZE_SWIPE_QUOTE = SizeUtils.dp2px(60f).toFloat()
                override fun onStartOpen(layout: SwipeLayout?) {
                }

                override fun onOpen(layout: SwipeLayout?) {
                }

                override fun onUpdate(layout: SwipeLayout?, leftOffset: Int, topOffset: Int) {
                    val left = abs(leftOffset)
                    val alpha = if (left < SIZE_SWIPE_QUOTE) left / SIZE_SWIPE_QUOTE else 1f
                    binding.root.findViewById<View>(R.id.messageSwipeQuote)?.run {
                        when (layout?.dragEdge) {
                            SwipeLayout.DragEdge.Right -> this.translationX =
                                this.width * (1f - alpha)
                            SwipeLayout.DragEdge.Left -> this.translationX =
                                this.width * (alpha - 1f)
                            else -> {
                            }
                        }
                        this.alpha = alpha
                    }
                }

                override fun onStartClose(layout: SwipeLayout?) {
                }

                override fun onClose(layout: SwipeLayout?) {
                    takeIf {
                        surFaceLeft >= SIZE_SWIPE_QUOTE
                                && iMessage.isEnableQuote()
                    }?.run {
                        chatListener.onQuoteMessage(message = iMessage)
                    }
                }

                override fun onHandRelease(layout: SwipeLayout?, xvel: Float, yvel: Float) {
                    surFaceLeft = abs(layout?.surfaceView?.left ?: 0)
                    close()
                }
            })
        }
    }


    var clickedMention = false

    open fun invalidateLayout(requestManager: RequestManager?) {
        messageReaderItem?.invalidateLayout(requestManager)
    }

    open fun getTargets(): MutableList<ImageView> {
        return mutableListOf()
    }
}