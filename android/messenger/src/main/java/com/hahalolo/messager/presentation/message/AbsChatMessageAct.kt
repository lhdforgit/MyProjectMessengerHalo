package com.hahalolo.messager.presentation.message

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import com.bumptech.glide.Glide
import com.bumptech.glide.MemoryCategory
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.hahalolo.messager.chatkit.view.MessageGalleryInputHeaderView
import com.hahalolo.messager.chatkit.view.MessageGalleryInputView
import com.hahalolo.messager.chatkit.view.MessageInputView
import com.hahalolo.messager.chatkit.view.input.MentionSpannableEntity
import com.hahalolo.messager.presentation.base.AbsMessBackActivity
import com.hahalolo.messager.presentation.group.create.GroupConstant
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessageActV2Binding
import com.halo.common.utils.KeyboardUtils
import com.halo.constant.HaloConfig
import com.halo.data.entities.mongo.tet.GifCard
import com.halo.data.room.entity.MessageEntity
import com.halo.editor.mediasend.MediaSendActivity
import com.halo.editor.mediasend.MediaSendActivityResult
import com.halo.editor.util.MediaUtil
import com.halo.widget.felling.model.StickerEntity
import com.halo.widget.gallery.MediaListener
import com.halo.widget.gallery.MediaStoreData
import com.halo.widget.gallery.MediaStoreDataLoader
import com.halo.widget.model.GifModel
import com.halo.widget.repository.sticker.StickerRepository
import com.halo.widget.sticker.sticker_group.StickerGroupView
import javax.inject.Inject

abstract class AbsChatMessageAct : AbsMessBackActivity(),
    LoaderManager.LoaderCallbacks<List<MediaStoreData>>,
    MediaListener {

    private var curentTime: Long = 0

    private var requestManager: RequestManager? = null

    internal var binding: ChatMessageActV2Binding? = null

    @Inject
    lateinit var stickerRepository: StickerRepository

    private val stickerListener = object : StickerGroupView.StickerGroupListener {
        override fun onClickStickerItem(stickerEntity: StickerEntity?) {
            stickerEntity?.takeIf { isSpamMessage() }?.run {
                this@AbsChatMessageAct.onSendMessageSticker(stickerEntity)
            }
        }

        override fun onClickGifCardItem(gifCard: GifCard) {
            gifCard.takeIf { isSpamMessage() }?.run {
                this@AbsChatMessageAct.onSendMessageGif(gifCard)
            }
        }

        override fun stickerRepository(): StickerRepository {
            return stickerRepository
        }

        override fun lifecycleOwner(): LifecycleOwner {
            return this@AbsChatMessageAct
        }

        override fun token(): String {
            return this@AbsChatMessageAct.token()
        }
    }

    override fun isLightTheme(): Boolean {
        return true
    }

    protected fun isSpamMessage(): Boolean {
        val newTime = System.currentTimeMillis()
        if (newTime - curentTime > 600) {
            curentTime = newTime
            return true
        }
        return false
    }

    protected fun getRequestManager(): RequestManager {
        if (requestManager == null) {
            requestManager = Glide.with(this)
        }
        return requestManager!!
    }

    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<List<MediaStoreData>> {
        return MediaStoreDataLoader(this, HaloConfig.MEDIA_MAX_SIZE_BYTE.toLong())
    }

    override fun onLoadFinished(
        loader: Loader<List<MediaStoreData>>,
        mediaStoreData: List<MediaStoreData>?
    ) {
        binding?.layoutGallery?.setAdapter(getRequestManager(), mediaStoreData)
    }

    override fun onLoaderReset(loader: Loader<List<MediaStoreData>>) {

    }

    override fun onSendMedia(data: MediaStoreData) {
        if (fileIsMaxSize(data.uri)) {
            makeSnackbar(getString(R.string.chat_message_aller_max_size, MAX_FILE_SIZE))
        } else {
            val lists = mutableListOf<Uri>()
            lists.add(data.uri)
            onSendMessageMedia(lists)
        }
    }

    private fun fileIsMaxSize(uri: Uri?): Boolean {
        if (uri != null) {
            val fileSize = appController.getFileSize(uri, this)
            return fileSize >= MAX_FILE_SIZE
        }
        return false
    }

    override fun initializeLayout() {
        initMotionLayout()
        initMessageInput()
        initGifCardInput()
        initAction()
    }

    private fun initGifCardInput() {
        binding?.layoutGifCard?.setListener(stickerListener)

        observerGifCards(androidx.lifecycle.Observer {
            it?.takeIf { it.isNotEmpty() }?.run {
                binding?.chatMessageInput?.enableGifCardButton(true)
                binding?.layoutGifCard?.updateGifCards(this)
            } ?: run {
                binding?.chatMessageInput?.enableGifCardButton(false)
            }
        })
    }

    //start motion Layout
    private fun initMotionLayout() {
        showLayoutGallery(false)
        showLayoutSticker(false)
        showLayoutGifCard(false)
        showLayoutKeyBroad(false)
        binding?.layoutSticker?.setRequestManager(getRequestManager())
        binding?.chatMessageMotion?.setTransitionListener(object : MotionLayout.TransitionListener {
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


    private fun showLayoutGallery(show: Boolean) {
        binding?.layoutGalleryCroller?.visibility = if (show) View.VISIBLE else View.GONE
        binding?.layoutGalleryHeader?.visibility = if (show) View.VISIBLE else View.GONE
        binding?.layoutGallery?.visibility = if (show) View.VISIBLE else View.GONE
        enableTransitionGallery(show)
    }

    private fun showLayoutSticker(show: Boolean) {
        binding?.layoutSticker?.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showLayoutGifCard(show: Boolean) {
        binding?.layoutGifCard?.visibility = if (show) View.VISIBLE else View.GONE
    }

    protected fun showLayoutKeyBroad(show: Boolean) {
        val use = binding?.layoutGallery?.visibility == View.VISIBLE
                || binding?.layoutSticker?.visibility == View.VISIBLE
                || binding?.layoutGifCard?.visibility == View.VISIBLE
        binding?.takeIf { !(show || use) }?.chatMessageInput?.isKeybroadIsShow = false
        binding?.layoutKeybroad?.visibility = if (show || use) View.VISIBLE else View.GONE
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
        return binding?.chatMessageMotion?.currentState == R.id.end
    }

    private fun onExpandGalleryInput(expand: Boolean) {
        if (expand) {
            binding?.chatMessageMotion?.transitionToEnd()
        } else {
            binding?.chatMessageMotion?.transitionToStart()
        }
    }

    private fun enableTransitionGallery(enable: Boolean) {
        binding?.chatMessageMotion?.getTransition(R.id.transition_gallery_expanable)
            ?.setEnable(enable)
    }

    private fun initAction() {
        binding?.layoutGallery?.listener =
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
                        val bottom: Float = binding?.layoutGalleryCroller?.run {
                            (bottom + top) / 2f
                        } ?: 0.0f
                        e.action = MotionEvent.ACTION_DOWN
                        e.setLocation(e.x, bottom)
                        val result = binding?.chatMessageMotion?.onTouchEvent(e)
                        return result ?: false
                    }
                    return false
                }

                override fun onTouchEvent(e: MotionEvent) {
                    binding?.chatMessageMotion?.onTouchEvent(e)
                }
            }

        binding?.layoutGalleryHeader?.listener =
            object : MessageGalleryInputHeaderView.Listener {
                override fun onClose() {
                    onExpandGalleryInput(false)
                }
            }

        binding?.layoutSticker?.setListener(stickerListener)
    }

    override fun onResume() {
        super.onResume()
        if (binding?.layoutSticker?.visibility != View.VISIBLE
            && binding?.layoutGifCard?.visibility != View.VISIBLE
            && binding?.layoutKeybroad?.visibility == View.VISIBLE
        ) {
            showLayoutKeyBroad(false)
        }
    }

    private fun initMessageInput() {
        binding?.chatMessageInput?.setMessageInputListener(object :
            MessageInputView.MessageInputListener {

            override fun layoutStickerIsShowing(): Boolean {
                return binding?.layoutSticker?.visibility == View.VISIBLE
            }

            override fun layoutGifCardIsShowing(): Boolean {
                return binding?.layoutGifCard?.visibility == View.VISIBLE
            }

            override fun onSelectImageCamera() {
                startActivityForResult(
                    MediaSendActivity.buildGalleryIntent(this@AbsChatMessageAct),
                    REQUEST_IMAGE_CAPTURE
                )
            }

            override fun onSelectImageCameraNotPer() {
                // Not thing
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
                    this@AbsChatMessageAct.onSendMessageText(content, editMsgId, mentions, quoteMsg)
                }
                return true
            }

            override fun activityIsSoftInputVisible(): Boolean {
                return KeyboardUtils.isSoftInputVisible(this@AbsChatMessageAct)
            }

            override fun onHahaClick() {
                if (isSpamMessage()) {
                    this@AbsChatMessageAct.onSendMessageHaha()
                }
            }

            override fun onSelectFileExternal() {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = getString(R.string.chat_message_all_type)
                val mimetypes = resources.getStringArray(R.array.allowed_file_mime_types)
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                try {
                    startActivityForResult(
                        Intent.createChooser(
                            intent,
                            getString(R.string.chat_message_select_a_file_to_upload)
                        ),
                        REQUEST_FILE_EXTERNAL
                    )
                } catch (ex: ActivityNotFoundException) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(
                        this@AbsChatMessageAct,
                        getString(R.string.chat_message_please_install_a_file_manager),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onSelectFileExternalNotPer() {
                //Use for Bubble
            }

            override fun stickerRepository(): StickerRepository {
                return stickerRepository
            }

            override fun lifecycleOwner(): LifecycleOwner {
                return this@AbsChatMessageAct
            }

            override fun token(): String {
                return this@AbsChatMessageAct.token()
            }

            //todo new UPDATE
            override fun showLayoutKeyboad(show: Boolean) {
                if (!show && binding?.layoutSticker?.visibility != View.VISIBLE
                    && binding?.layoutGifCard?.visibility != View.VISIBLE
                ) {
                    binding?.chatMessageInput?.showLayoutTabSticker(false)
                }
                this@AbsChatMessageAct.showLayoutKeyBroad(show)
            }

            override fun onUpdateLayoutKeybroadHeight() {
                binding?.layoutKeybroad?.updateHeight()
            }

            override fun onFocusInput() {
                showLayoutGifCard(false)
                showLayoutSticker(false)
                showLayoutGallery(false)
                showLayoutKeyBroad(true)
            }

            override fun onShowGalleryInput() {
                showLayoutGifCard(false)
                showLayoutSticker(false)
                val isShowing = binding?.layoutGallery?.visibility == View.VISIBLE
                showLayoutGallery(!isShowing)
                showLayoutKeyBroad(!isShowing)
                showKeybroadSoft(false, null)
            }

            override fun onShowGifCardInput(show: Boolean) {
                showLayoutGallery(false)
                binding?.chatMessageInput?.showLayoutTabSticker(show)
                showLayoutSticker(false)
                showLayoutGifCard(show)
                showLayoutKeyBroad(show)
                showKeybroadSoft(false, null)
            }

            override fun onShowStickerInput(show: Boolean) {
                showLayoutGallery(false)
                binding?.chatMessageInput?.showLayoutTabSticker(show)
                showLayoutGifCard(false)
                showLayoutSticker(show)
                showLayoutKeyBroad(show)
                showKeybroadSoft(false, null)
            }

            override fun onShowStickerInput() {
                showLayoutGallery(false)
                showLayoutGifCard(false)
                val isShowing = binding?.layoutSticker?.visibility == View.VISIBLE
                binding?.chatMessageInput?.showLayoutTabSticker(!isShowing)
                showLayoutSticker(!isShowing)
                showLayoutKeyBroad(!isShowing)
                showKeybroadSoft(false, null)
            }

            override fun onShowGiphyInput() {
                showLayoutSticker(false)
                showLayoutGifCard(false)
                showLayoutGallery(false)
                showLayoutKeyboad(true)
            }

            override fun onStartTyping() {
                super.onStartTyping()
                this@AbsChatMessageAct.onStartTyping()
            }

            override fun onStopTyping() {
                super.onStopTyping()
                this@AbsChatMessageAct.onStopTyping()
            }

            override fun onClickGifItem(media: GifModel?) {
                media?.run {
                    this@AbsChatMessageAct.onSendMessageGif(this)
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> if (data != null) {
                    val medias: MediaSendActivityResult? =
                        data.getParcelableExtra(MediaSendActivity.EXTRA_RESULT)
                    medias?.run {
                        val listImages = mutableListOf<Uri>()
                        val listVideos = mutableListOf<Uri>()
                        uploadedMedia?.forEach {
                            it.uri?.run {
                                if (MediaUtil.isVideoType(it.mimeType)) {
                                    listVideos.add(this)
                                } else {
                                    listImages.add(this)
                                }
                            }
                        }
                        listVideos.takeIf { it.isNotEmpty() }?.run {
                            onSendMessageMediaList(this)
                        }
                        listImages.takeIf { it.isNotEmpty() }?.run {
                            onSendMessageMediaList(this)
                        }
                    }
                }
                REQUEST_FILE_EXTERNAL -> data?.data?.run {
                    if (!fileIsMaxSize(this)) {
                        onSendMessageFile(this)
                    } else {
                        makeSnackbar(
                            getString(
                                R.string.chat_message_aller_max_size,
                                MAX_FILE_DEFAULT_MEGABITE
                            )
                        )
                    }
                }
                REQUEST_MESSAGE_SETTING -> {
                    data?.getBooleanExtra(GroupConstant.STATUS_LEAVE_GROUP, false)?.let {
                        if (it) {
                            finish()
                        }
                    }
                    data?.getBooleanExtra(GroupConstant.STATUS_DELETE_CONVERSATION, false)
                        ?.takeIf { it }
                        ?.let {
                            finish()
                        }
                }
                else -> {
                    makeSnackbar(R.string.chat_message_aller_file_not_available)
                }
            }
        }
    }

    private fun onSendMessageMediaList(listUri: MutableList<Uri>) {
        var start = 0
        var end = MAX_MEDIA_FILE
        while ((listUri.size - start) > MAX_MEDIA_FILE) {
            val subList = listUri.subList(start, end)
            onSendMessageMedia(subList)
            start += MAX_MEDIA_FILE
            end += MAX_MEDIA_FILE
        }
        if (listUri.size > start) {
            val subList = listUri.subList(start, listUri.size)
            onSendMessageMedia(subList)
        }
    }

    abstract fun observerGifCards(observer: Observer<MutableList<GifCard>>)

    abstract fun onSendMessageMedia(listUri: MutableList<Uri>)

    abstract fun onSendMessageFile(uri: Uri)

    abstract fun onSendMessageSticker(stickerEntity: StickerEntity)

    abstract fun onSendMessageGif(gifCard: GifCard)

    abstract fun onSendMessageText(
        content: String,
        editMsgId: String?,
        mentionTables: MutableList<MentionSpannableEntity>?,
        quoteMsg: MessageEntity?
    )

    abstract fun onSendMessageGif(media: GifModel)

    abstract fun onSendMessageHaha()

    abstract fun onStartTyping()
    abstract fun onStopTyping()

    private fun initMediaRec() {
        Glide.get(this).setMemoryCategory(MemoryCategory.HIGH)
        LoaderManager.getInstance(this).initLoader(R.id.loader_id_media_store_data, null, this)
    }

    override fun initActionBar() {
        setSupportActionBar(binding?.toolbar)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }

    override fun onBackPressed() {
        if (galleyInputIsExpanded()) {
            onExpandGalleryInput(false)
        } else if (binding?.layoutKeybroad?.visibility == View.VISIBLE) {
            binding?.chatMessageInput?.showLayoutTabSticker(false)
            binding?.chatMessageInput?.showLayoutGiphy(false)
            showLayoutGallery(false)
            showLayoutSticker(false)
            showLayoutGifCard(false)
            showKeybroadSoft(false, null)
            showLayoutKeyBroad(false)
        } else {
            super.onBackPressed()
        }
    }

    protected fun makeSnackbar(idResource: Int) {
        makeSnackbar(getString(idResource))
    }

    protected fun makeSnackbar(content: String) {
        binding?.root?.run {
            Snackbar.make(this, content, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val REQUEST_MESSAGE_SETTING = 111
        private const val REQUEST_IMAGE_CAPTURE = 112
        private const val REQUEST_FILE_EXTERNAL = 113

        private const val MAX_MEDIA_FILE = 5

        private const val MAX_FILE_DEFAULT_MEGABITE = 30
        private const val MAX_FILE_SIZE = (MAX_FILE_DEFAULT_MEGABITE * 1024 * 1024).toLong()
    }
}