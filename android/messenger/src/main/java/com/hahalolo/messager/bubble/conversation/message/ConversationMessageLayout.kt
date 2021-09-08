package com.hahalolo.messager.bubble.conversation.message

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.LifecycleOwner
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.hahalolo.messager.bubble.BubbleService
import com.hahalolo.messager.bubble.BubbleServiceCallback
import com.hahalolo.messager.bubble.base.AbsLifecycleView
import com.hahalolo.messager.bubble.conversation.BubbleConversationCallback
import com.hahalolo.messager.chatkit.view.MessageGalleryInputHeaderView
import com.hahalolo.messager.chatkit.view.MessageGalleryInputView
import com.hahalolo.messager.chatkit.view.MessageInputView
import com.hahalolo.messager.chatkit.view.input.MentionSpannableEntity
import com.halo.data.room.entity.MessageEntity
import com.halo.data.room.table.AttachmentTable
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ConversationMessageLayoutBinding
import com.halo.common.utils.KeyboardUtils
import com.halo.data.entities.media.MediaEntity
import com.halo.data.entities.mongo.tet.GifCard
import com.halo.widget.felling.model.StickerEntity
import com.halo.widget.gallery.MediaListener
import com.halo.widget.gallery.MediaStoreData
import com.halo.widget.gallery.MediaStoreDataLoader
import com.halo.widget.model.GifModel
import com.halo.widget.repository.sticker.StickerRepository
import com.halo.widget.sticker.sticker_group.StickerGroupView
import java.util.*

class ConversationMessageLayout : AbsLifecycleView,
    LoaderManager.LoaderCallbacks<List<MediaStoreData>>,
    MediaListener {

    companion object {
        private const val MEDIA_MAX_SIZE_BYTE = 314572800
        private const val MAX_FILE_DEFAULT_MEGABITE = 30
        private const val MAX_FILE_SIZE = (MAX_FILE_DEFAULT_MEGABITE * 1024 * 1024).toLong()
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    lateinit var binding: ConversationMessageLayoutBinding
    private var viewModel: ConversationMessageViewModel? = null
    private var serviceCallback: BubbleServiceCallback? = null
    private var conversationCallback: BubbleConversationCallback? = null

    fun updateBubbleServiceCallback(serviceCallback: BubbleServiceCallback) {
        this.serviceCallback = serviceCallback
    }

    fun updateBubbleConversationCallback(conversationCallback: BubbleConversationCallback) {
        this.conversationCallback = conversationCallback
    }

    override fun initializeBinding() {
        binding = ConversationMessageLayoutBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun initializeViewModel() {
        viewModel =
            serviceCallback?.createViewModel(ConversationMessageViewModel::class) as? ConversationMessageViewModel
    }

    /*ABS_MESSENGER*/
    private var curentTime: Long = 0

    private var requestManager: RequestManager? = null

    private fun isLightTheme(): Boolean {
        return true
    }

    private fun isSpamMessage(): Boolean {
        val newTime = System.currentTimeMillis()
        if (newTime - curentTime > 600) {
            curentTime = newTime
            return true
        }
        return false
    }

    private fun getRequestManager(): RequestManager {
        if (requestManager == null) {
            requestManager = Glide.with(this)
        }
        return requestManager!!
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<List<MediaStoreData>> {
        return MediaStoreDataLoader(context, MEDIA_MAX_SIZE_BYTE.toLong())
    }

    override fun onLoadFinished(
        loader: Loader<List<MediaStoreData>>,
        mediaStoreData: List<MediaStoreData>?
    ) {
        binding.layoutGallery.setAdapter(getRequestManager(), mediaStoreData)
    }

    override fun onLoaderReset(loader: Loader<List<MediaStoreData>>) {

    }

    override fun onSendMedia(data: MediaStoreData) {
        if (fileIsMaxSize(data.uri)) {
            makeSnackbar(context.getString(R.string.chat_message_aller_max_size, MAX_FILE_SIZE))
        } else {
            val lists = mutableListOf<Uri>()
            lists.add(data.uri)
            onSendMessageMedia(lists)
        }
    }

    private fun fileIsMaxSize(uri: Uri?): Boolean {
        //TODO UPDATE
//        if (uri != null) {
//            val fileSize = PhotoMetadataUtils.getFileSize(uri, this)
//            return fileSize >= MAX_FILE_SIZE
//        }
        return true
    }


    //start motion Layout
    private fun initMotionLayout() {
        showLayoutGallery(false)
        showLayoutSticker(false)
        showLayoutGifCard(false)
        showLayoutKeyBroad(false)
        binding.layoutSticker.setRequestManager(getRequestManager())
        binding.chatMessageMotion.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout, i: Int, i1: Int) {
            }

            override fun onTransitionChange(motionLayout: MotionLayout, i: Int, i1: Int, v: Float) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout, i: Int) {
                if (i == R.id.start) {
                    enableTransitionGallery(true)
                }
                if (i == R.id.end) {
                    enableTransitionGallery(false)
                }
            }

            override fun onTransitionTrigger(m: MotionLayout, i: Int, b: Boolean, v: Float) {
            }
        })
    }

    private fun initAbsAction() {
        binding.layoutGallery.listener =
            object : MessageGalleryInputView.Listener {
                override fun onSendMedia(list: MutableList<MediaStoreData>) {
                    val listVideos = mutableListOf<Uri>()
                    val listImages = mutableListOf<Uri>()
                    list.takeIf { it.isNotEmpty() }?.forEach {
                        if (it.type == MediaStoreData.Type.VIDEO) {
                            listVideos.add(it.uri)
                        } else {
                            listImages.add(it.uri)
                        }
                    }
                    onExpandGalleryInput(false)
                    listVideos.takeIf { it.isNotEmpty() }?.run {
                        onSendMessageMedia(this)
                    }
                    listImages.takeIf { it.isNotEmpty() }?.run {
                        onSendMessageMedia(this)
                    }
                }

                override fun onTouchScroll(e: MotionEvent?): Boolean {
                    if (e?.action == MotionEvent.ACTION_MOVE
                        && e.y < 0
                        && !galleyInputIsExpanded()
                    ) {
                        val bottom: Float = binding.layoutGalleryCroller.run {
                            (bottom + top) / 2f
                        }
                        e.action = MotionEvent.ACTION_DOWN
                        e.setLocation(e.x, bottom)
                        return binding.chatMessageMotion.onTouchEvent(e)
                    }
                    return false
                }

                override fun onTouchEvent(e: MotionEvent) {
                    binding.chatMessageMotion.onTouchEvent(e)
                }
            }

        binding.layoutGalleryHeader.listener =
            object : MessageGalleryInputHeaderView.Listener {
                override fun onClose() {
                    onExpandGalleryInput(false)
                }
            }

        binding.layoutSticker.setListener(object : StickerGroupView.StickerGroupListener {
            override fun onClickStickerItem(stickerEntity: StickerEntity?) {
                stickerEntity?.takeIf { isSpamMessage() }?.run {
                    onSendStickerItem(stickerEntity)
                }
            }

            override fun onClickGifCardItem(gifCard: GifCard) {


            }

            override fun stickerRepository(): StickerRepository? {
                return null
            }

            override fun lifecycleOwner(): LifecycleOwner {
                return this@ConversationMessageLayout
            }
        })
    }

    private fun onResume() {
        if (binding.layoutSticker.visibility != View.VISIBLE
            && binding.layoutGifCard.visibility != View.VISIBLE
            && binding.layoutKeybroad.visibility == View.VISIBLE
        ) {
            showLayoutKeyBroad(false)
        }
    }

    private fun initMediaRec() {
        //TODO UPDATE
        BubbleService.instance?.run {
            Glide.get(context).setMemoryCategory(MemoryCategory.HIGH)
            LoaderManager.getInstance(this)
                .initLoader(R.id.loader_id_media_store_data, null, this@ConversationMessageLayout)
        }
    }

    private fun initAbsMessageInput() {
        binding.chatMessageInput.hideSelectGalleryBtn()
        binding.chatMessageInput.setMessageInputListener(object :
            MessageInputView.MessageInputListener {

            override fun layoutStickerIsShowing(): Boolean {
                return binding.layoutSticker.visibility == View.VISIBLE
            }

            override fun layoutGifCardIsShowing(): Boolean {
                return binding.layoutGifCard.visibility == View.VISIBLE
            }

            override fun onSelectImageCamera() {
                //Not thing , use in ACTIVITY
            }

            override fun onSelectImageCameraNotPer() {
            }

            override fun onSelectMediaGallery() {
                initMediaRec()
            }

            override fun onSubmit(
                input: CharSequence?,
                editMsgId: String?,
                mentions: MutableList<MentionSpannableEntity>?,
                quoteMsg: MessageEntity?
            ): Boolean {
                input?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let { content ->
                    onSendSubmitText(content, editMsgId, mentions, quoteMsg)
                }
                return true
            }

            override fun activityIsSoftInputVisible(): Boolean {
                //TODO UPDATE
//                return KeyboardUtils.isSoftInputVisible(context)
                return false
            }

            override fun onHahaClick() {
                if (isSpamMessage()) {
                    onSendHahaItem()
                }
            }


            override fun onSelectFileExternal() {
                //Notthing , use in Activity

            }

            override fun onSelectFileExternalNotPer() {
            }

            override fun stickerRepository(): StickerRepository? {
                return null
            }

            override fun lifecycleOwner(): LifecycleOwner? {
                return null
            }

            override fun token(): String? {
                return null
            }

            //todo new UPDATE
            override fun showLayoutKeyboad(show: Boolean) {
                if (!show && binding.layoutSticker.visibility != View.VISIBLE
                    && binding.layoutGifCard.visibility != View.VISIBLE
                ) {
                    binding.chatMessageInput.showLayoutTabSticker(false)
                }
                showLayoutKeyBroad(show)
            }

            override fun onUpdateLayoutKeybroadHeight() {
                binding.layoutKeybroad.updateHeight()
            }

            override fun onFocusInput() {
                showLayoutGifCard(false)
                showLayoutSticker(false)
                showLayoutGallery(false)
                //TODO UPDATE
                showLayoutKeyBroad(false)
            }

            override fun onShowGalleryInput() {
                showLayoutGifCard(false)
                showLayoutSticker(false)
                val isShowing = binding.layoutGallery.visibility == View.VISIBLE
                showLayoutGallery(!isShowing)
                showLayoutKeyBroad(!isShowing)
                showKeybroadSoft(false, null)
            }

            override fun onShowGifCardInput(show: Boolean) {
                showLayoutGallery(false)
                binding.chatMessageInput.showLayoutTabSticker(show)
                showLayoutSticker(false)
                showLayoutGifCard(show)
                showLayoutKeyBroad(show)
                showKeybroadSoft(false, null)
            }

            override fun onShowStickerInput(show: Boolean) {
                showLayoutGallery(false)
                binding.chatMessageInput.showLayoutTabSticker(show)
                showLayoutGifCard(false)
                showLayoutSticker(show)
                showLayoutKeyBroad(show)
                showKeybroadSoft(false, null)
            }

            override fun onShowStickerInput() {
                showLayoutGallery(false)
                showLayoutGifCard(false)
                val isShowing = binding.layoutSticker.visibility == View.VISIBLE
                binding.chatMessageInput.showLayoutTabSticker(!isShowing)
                showLayoutSticker(!isShowing)
                showLayoutKeyBroad(!isShowing)
                showKeybroadSoft(false, null)
            }

            override fun onShowGiphyInput() {
                showLayoutSticker(false)
                showLayoutGifCard(false)
                showLayoutGallery(false)
                //TODO UPDATE
                showLayoutKeyboad(false)
            }

            override fun onStartTyping() {
                super.onStartTyping()
                this@ConversationMessageLayout.onStartTyping()
            }

            override fun onStopTyping() {
                super.onStopTyping()
                this@ConversationMessageLayout.onStopTyping()
            }

            override fun onClickGifItem(media: GifModel?) {
                media?.run {
//                    onSendGifItem(this)
                }
            }
        })
    }


    override fun onBackPress(): Boolean {
        if (galleyInputIsExpanded()) {
            onExpandGalleryInput(false)
        } else if (binding.layoutKeybroad.visibility == View.VISIBLE) {
            binding.chatMessageInput.showLayoutTabSticker(false)
            binding.chatMessageInput.showLayoutGiphy(false)
            showLayoutGallery(false)
            showLayoutSticker(false)
            showLayoutGifCard(false)
            showKeybroadSoft(false, null)
            showLayoutKeyBroad(false)
            return true
        } else if (binding.chatMessageInput.giphyIsVisible()) {
            binding.chatMessageInput.showLayoutGiphy(false)
            showKeybroadSoft(false, null)
            showLayoutKeyBroad(false)
            return true
        }
        return super.onBackPress()
    }

    private fun makeSnackbar(idResource: Int) {
        makeSnackbar(context.getString(idResource))
    }

    private fun makeSnackbar(content: String) {
        binding.root.run {
            Snackbar.make(this, content, Snackbar.LENGTH_SHORT).show()
        }
    }
    /*ABS_MESSENGER END */

    /*ACTION LAYOUT*/
    private fun showLayoutGallery(show: Boolean) {
        binding.layoutGalleryCroller.visibility = if (show) View.VISIBLE else View.GONE
        binding.layoutGalleryHeader.visibility = if (show) View.VISIBLE else View.GONE
        binding.layoutGallery.visibility = if (show) View.VISIBLE else View.GONE
        enableTransitionGallery(show)
    }

    private fun showLayoutSticker(show: Boolean) {
        binding.layoutSticker.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showLayoutGifCard(show: Boolean) {
        binding.layoutGifCard.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showLayoutKeyBroad(show: Boolean) {
        val use = binding.layoutGallery.visibility == View.VISIBLE
                || binding.layoutSticker.visibility == View.VISIBLE
                || binding.layoutGifCard.visibility == View.VISIBLE
        binding.layoutKeybroad.visibility = if (show || use) View.VISIBLE else View.GONE
    }

    protected fun showKeybroadSoft(show: Boolean, target: View?) {
        if (show) {
            target?.run {
                KeyboardUtils.showSoftInput(this)
            } ?: run {
                KeyboardUtils.showSoftInput(this)
            }
        } else {
            target?.run {
                KeyboardUtils.hideSoftInput(this)
            } ?: run {
                KeyboardUtils.hideSoftInput(this)
            }
        }
    }
    //end motion Layout

    private fun galleyInputIsExpanded(): Boolean {
        return binding.chatMessageMotion.currentState == R.id.end
    }

    private fun onExpandGalleryInput(expand: Boolean) {
        if (expand) {
            binding.chatMessageMotion.transitionToEnd()
        } else {
            binding.chatMessageMotion.transitionToStart()
        }
    }

    private fun enableTransitionGallery(enable: Boolean) {
        binding.chatMessageMotion.getTransition(R.id.transition_gallery_expanable)
            ?.setEnable(enable)
    }

    /*ACTION LAYOUT END */


    /*ACTION SEND MESSAGE*/


    private fun onSendHahaItem() {

    }

    //   1 video  or list image
    private fun onSendMessageMedia(listUri: MutableList<Uri>) {

    }

    private fun onSendMessageFile(uri: Uri) {

    }

    private fun onSendGifItem(media: com.giphy.sdk.core.models.Media) {

    }

    private fun onSendSubmitText(
        content: String,
        editMsgId: String?,
        mentionTables: MutableList<MentionSpannableEntity>?,
        quoteMsg: MessageEntity?
    ) {

    }

    private fun onSendStickerItem(stickerEntity: StickerEntity) {

    }

    private fun onSendGifCard(gifCard: GifCard) {

    }

    private fun onStartTyping() {

    }

    private fun onStopTyping() {

    }
    /*ACTION SEND MESSAGE END */


    /*LAYOUT */

    override fun initializeLayout() {

    }

    /*NAVIGATE ACT */
    private fun navigateViewAvatar(targetView: ImageView?, medias: MutableList<MediaEntity>) {
        serviceCallback?.navigateViewAvatar(targetView, medias)
    }

    private fun navigateChatMessageMember(roomId: String, tag: Int) {
        serviceCallback?.navigateChatMessageMember(roomId, tag)
    }

    private fun navigateChatMessage(userId: String) {
        serviceCallback?.navigateChatMessage(userId)
    }

    private fun navigatePersonalWall(userId: String) {
    }

    private fun navigateOpenMedia(listImage: MutableList<AttachmentTable>?, position: Int) {
    }

    private fun navigateChangeNickName() {

    }


    fun takeMediaMessage(roomId: String?, medias: MutableList<String>?) {

    }

    fun takeFileMessage(roomId: String?, path: String) {

    }

    /*NAVIGATE ACT END*/

    fun updateInfor(bubbleId: String) {


    }
    fun onShowContent() {
    }

    fun onHideContent() {
        super.onDetachedFromWindow()
    }

    override fun invalidateLayout() {
        super.invalidateLayout()
    }
}