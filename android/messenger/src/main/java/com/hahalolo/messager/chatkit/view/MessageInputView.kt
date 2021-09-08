/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.hahalolo.messager.chatkit.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.chatkit.view.input.MentionEditText
import com.hahalolo.messager.chatkit.view.input.MentionSpannableEntity
import com.hahalolo.messager.chatkit.view.input.MessageInput.InputListener
import com.hahalolo.messager.chatkit.view.input.MessageInput.TypingListener
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.entity.MessageEntity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.LayoutMessageInputBinding
import com.halo.common.permission.RxPermissions
import com.halo.common.utils.HaloFileUtils
import com.halo.common.utils.HaloFileUtils.PerListener
import com.halo.data.cache.pref.utils.PrefUtilsImpl
import com.halo.widget.model.GifModel
import com.halo.widget.repository.sticker.StickerRepository
import com.halo.widget.sticker.Utils
import com.halo.widget.sticker.gif_gr.GiphyView.GiphyViewListener
import io.reactivex.Observer
import io.reactivex.disposables.Disposable

/**
 * @author BuiSon
 * Created by BuiSon
 * Created on 10/6/2018.
 */
class MessageInputView : FrameLayout {
    private val TIME_HIDE_ANIMATION = 100

    private lateinit var binding: LayoutMessageInputBinding
    private val requestManager: RequestManager? = null
    var onGlobalLayoutListener: OnGlobalLayoutListener? = null
    private var heightCurrent = -1
    var isKeybroadIsShow = false
    private var requestKeyBroad = false
    private var editMsgId: String? = ""
    var contextAct: Activity? = null
    private val REQUEST_IMAGE_CAPTURE = 100
    private val TIME_SHOW: Long = 200
    private var MAX = -1
    private var messageInputListener: MessageInputListener? = null
    private var enableGallery = true

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        initView()
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private val giphyViewListener = object : GiphyViewListener {
        override fun onClickGifItem(media: GifModel) {
            messageInputListener?.onClickGifItem(media)
            onCloseGiphyView()
        }

        override fun onCloseGiphyView() {
            showLayoutGiphy(false)
            showLayoutTabSticker(false)
            actionShowLayoutKeyBroad()
            actionShowKeyBroad()
        }

        override fun stickerRepository(): StickerRepository?{
           return messageInputListener?.stickerRepository()
        }

        override fun lifecycleOwner(): LifecycleOwner? {
            return messageInputListener?.lifecycleOwner()
        }

        override fun token(): String? {
            return messageInputListener?.token()
        }
    }

    private fun initView() {
        binding = LayoutMessageInputBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        try{
            contextAct = Utils.asActivity(context)
            contextAct?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            binding.selectImageGalleryBt.visibility = View.VISIBLE
        }catch (e: Exception){
            e.printStackTrace()
            //TODO BUBBLE
            binding.selectImageGalleryBt.visibility = View.GONE
        }

        heightKeyBroad = PrefUtilsImpl.getInstance(context).heightKeybroad
        binding.layoutHeader.layoutParams.height = heightKeyBroad
        binding.giphyView.setRequestManager(Glide.with(context))

        bindAction()
        bindGlobal()
        bindMessageInput()
    }

    fun hideSelectGalleryBtn(){
        enableGallery = false
        binding.selectImageGalleryBt.visibility = View.GONE
    }

    private fun bindMessageInput() {
        binding.messageInput.setInputListener(messageInputLis)
        binding.messageInput.setAttachmentsListener {
            messageInputListener?.onAddAttachments()
        }
        binding.messageInput.setTypingListener(messageInputTypingListener)
    }

    fun getInputEditText():EditText?{
        return binding.messageInput.inputEditText
    }

    private fun bindGlobal() {
        if (contextAct == null) return
        try {
            onGlobalLayoutListener = OnGlobalLayoutListener {
                val rect = Utils.windowVisibleDisplayFrame(contextAct!!)
                if (MAX < rect.bottom) {
                    MAX = rect.bottom
                }
                val heightDifference = MAX - rect.bottom
                if (heightDifference != heightCurrent) {
                    heightCurrent = heightDifference
                    updateHeightLayout(heightDifference)
                    if (heightDifference > 100) {
                        isKeybroadIsShow = true
                        heightKeyBroad = heightDifference
                        if (requestKeyBroad) {
                            requestKeyBroad = false
                            actionHideKeyBroad()
                        }
                        onShowKeyBroad()
                    } else {
                        if (isKeybroadIsShow) {
                            onHideKeyBroad()
                        }
                        isKeybroadIsShow = false
                    }
                }
            }
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }

    private fun bindAction() {
        binding.selectFileBt.setOnClickListener { selectFileFromExternal() }
        binding.selectImageCameraBt.setOnClickListener { selectImageFromCamera() }
        binding.selectImageGalleryBt.setOnClickListener { selectImageFromGallery() }

        binding.gifCardBtn.setOnClickListener {
            showLayoutGiphy(false)
            showLayoutTabSticker(true)
            showKeybroadSoftGiphy(false)
            messageInputListener?.onShowGifCardInput(true)
        }

        binding.stickerBt.setOnClickListener {
            showLayoutGiphy(false)
            showLayoutTabSticker(true)
            showKeybroadSoftGiphy(false)
            messageInputListener?.onShowStickerInput(true)
        }

        binding.giphyBt.setOnClickListener {
            showLayoutGiphy(true)
            showLayoutTabSticker(true)
            showKeybroadSoftGiphy(true)
            messageInputListener?.onShowGiphyInput()
        }

        binding.giphyView.setListener(giphyViewListener)

        binding.messageQuoteInput.updateListener(object : MessageQuoteInputView.Listener {
            override fun onClose() {
                onRemoveQuoteMessage()
                onRemoveEditMessage()
            }

            override fun onShow() {
                binding.quoteGr.visibility = View.VISIBLE
            }
        })
    }

    fun setEditMessage(editMsg: MessageEntity){
        onRemoveQuoteMessage()
        binding.messageQuoteInput.setMessageQuoteEntity(editMsg.quoteMessage)
        binding.messageInput.setMessageEdit(editMsg)
        this.editMsgId = editMsg.messageId()
        actionShowKeyBroad()
    }

    fun setQuoteMessage(messageEntity: MessageEntity) {
        onRemoveEditMessage()
        binding.messageQuoteInput.setQuoteMessage(messageEntity)
        binding.messageInput.clearMessageInput()
        actionShowKeyBroad()
    }

    private fun onRemoveEditMessage(){
        if (!editMsgId.isNullOrEmpty()){
            this.editMsgId = null
            onRemoveQuoteMessage()
        }
    }

    private fun onRemoveQuoteMessage() {
        binding.messageQuoteInput.setQuoteMessage(null)
        binding.messageQuoteInput.setMessageQuoteEntity(null)
        binding.quoteGr.visibility = View.GONE
        binding.messageInput.setSendEnable(false)
    }

    fun clearMessageInput() {
        onRemoveEditMessage()
        onRemoveQuoteMessage()
        binding.messageInput.clearMessageInput()
    }

    fun setMentionListener(listener: MentionEditText.MentionListener){
        binding.messageInput.setMentionListener(listener)
    }

     fun addMentions(member: MemberEntity) {
        binding.messageInput.addMentions(member)
    }

    private val messageInputLis: InputListener = object : InputListener {
        override fun onSubmit(
            input: CharSequence?,
            mentions: MutableList<MentionSpannableEntity>?
        ): Boolean {
            val submit = messageInputListener?.onSubmit(
                input, editMsgId,
                mentions,
                binding.messageQuoteInput.getQuoteMessage()
            ) ?: false
            onRemoveQuoteMessage()
            return submit
        }

        override fun onTectChanged(text: String, enableSend: Boolean) {
            if (enableSend || text.isNotEmpty()) {
                binding.selectImageGalleryBt.visibility = View.GONE
                binding.selectImageCameraBt.visibility = View.GONE
                binding.selectFileBt.visibility = View.GONE
            } else {
                onRemoveEditMessage()
                binding.selectImageGalleryBt.takeIf { enableGallery && contextAct is Activity}?.visibility = View.VISIBLE
                binding.selectImageCameraBt.visibility = View.VISIBLE
                binding.selectFileBt.visibility = View.VISIBLE
            }
            messageInputListener?.onTextChanged(text)
        }

        override fun onHahaClick() {
            messageInputListener?.onHahaClick()
        }

        override fun onEmojiClick() {
            showLayoutGiphy(false)
            showLayoutTabSticker(true)
            messageInputListener?.onShowStickerInput()
        }

        override fun onFocusInput() {

            actionShowLayoutKeyBroad()
            showLayoutTabSticker(false)
            showLayoutGiphy(false)
            actionShowKeyBroad()
            messageInputListener?.onFocusInput()
        }

        override fun onTextLimited() {
            Toast.makeText(
                context,
                "The number of characters exceeds the allowed limit",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private val messageInputTypingListener: TypingListener = object : TypingListener {
        override fun onStartTyping() {
            messageInputListener?.onStartTyping()
        }

        override fun onStopTyping() {
            messageInputListener?.onStopTyping()
        }
    }

    private fun onHideKeyBroad() {
        showLayoutGiphy(false)
        messageInputListener?.showLayoutKeyboad(false)
    }

    private fun onShowKeyBroad() {
        messageInputListener?.showLayoutKeyboad(true)
    }

    private fun updateHeightLayout(height: Int) {
        if (height > 0 && height != PrefUtilsImpl.getInstance(context).heightKeybroad) {
            PrefUtilsImpl.getInstance(context).insertHeightKeybroad(height)
            binding.layoutHeader.layoutParams.height = height
            messageInputListener?.onUpdateLayoutKeybroadHeight()
        }
    }

    public override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bindViews()
    }

    private fun bindViews() {
        rootView.viewTreeObserver?.addOnGlobalLayoutListener {
            val heightDiff = this.height - rootView.height
        }
        contextAct?.window?.decorView?.viewTreeObserver
            ?.addOnGlobalLayoutListener(onGlobalLayoutListener)

    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        try {
            contextAct?.window?.decorView?.run{
                Utils.removeOnGlobalLayoutListener(this, onGlobalLayoutListener)
            }
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        try {
            if (hasWindowFocus) {
                contextAct?.window?.decorView?.run {
                    Utils.removeOnGlobalLayoutListener(this, onGlobalLayoutListener)
                }

                if (messageInputListener?.activityIsSoftInputVisible() == false) {
                    if (messageInputListener?.layoutStickerIsShowing() == false
                        && messageInputListener?.layoutGifCardIsShowing() == false) {
                        showLayoutTabSticker(false)
                    }
                    if (binding.layoutHeader.visibility == View.VISIBLE) {
                        actionHideLayoutHeader()
                    }
                }
                contextAct?.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener(
                    onGlobalLayoutListener
                )
            } else {
                contextAct?.window?.decorView?.run{
                    Utils.removeOnGlobalLayoutListener(this, onGlobalLayoutListener)
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            //TODO BUBBLE
        }
    }

    private fun actionShowLayoutKeyBroad() {
        messageInputListener?.showLayoutKeyboad(true)
        actionHideLayoutHeader()
    }

    private fun actionShowLayoutHeader() {
        binding.layoutHeader.visibility = View.VISIBLE
        showLayoutTabSticker(true)
        actionHideLayoutKeyBroad()
    }

    private fun actionHideLayoutHeader() {
        binding.layoutHeader.visibility = View.GONE
    }

    private fun actionHideLayoutKeyBroad() {
        messageInputListener?.showLayoutKeyboad(false)
    }

    private fun actionShowKeyBroad() {
        binding.messageInput.inputEditText.requestFocus()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(
            binding.messageInput.inputEditText,
            InputMethodManager.SHOW_IMPLICIT
        )
    }

    private fun actionShowKeyBroadGif() {
        binding.giphyView.inputSearch.requestFocus()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(
            binding.giphyView.inputSearch,
            InputMethodManager.SHOW_IMPLICIT
        )
    }

    private fun actionHideKeyBroad() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun isBackpress(): Boolean {
        actionHideLayoutHeader()
        if (isKeybroadIsShow) {
            actionHideKeyBroad()
        } else {
            actionHideLayoutKeyBroad()
        }
        return false
    }

    private fun selectImageFromGallery() {
        try {
            HaloFileUtils.externalPermision(contextAct!!, object : PerListener {
                override fun onGranted() {
                    showLayoutTabSticker(false)
                    messageInputListener?.onSelectMediaGallery()
                    messageInputListener?.onShowGalleryInput()
                }

                override fun onDeny() {
                    Toast.makeText(
                        contextAct,
                        R.string.editor_permission_media_request_denied,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }catch (e: Exception){
            //TODO BUBBLE
        }
    }

    private fun selectFileFromExternal() {
        try {
            HaloFileUtils.externalPermision(contextAct!!, object : PerListener {
                override fun onGranted() {
                    messageInputListener?.onSelectFileExternal()
                }

                override fun onDeny() {
                    Toast.makeText(
                        contextAct,
                        R.string.editor_permission_media_request_denied,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }catch (e: java.lang.Exception){
            //TODO BUBBLE
            messageInputListener?.onSelectFileExternalNotPer()
        }
    }

    private fun selectImageFromCamera() {
        try {
            val rxPermissions = RxPermissions(contextAct!!)
            rxPermissions.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
                .subscribe(object : Observer<Boolean> {
                    override fun onSubscribe(d: Disposable) {
                        // Do nothing
                    }

                    override fun onNext(aBoolean: Boolean) {
                        if (aBoolean) {
                            messageInputListener?.onSelectImageCamera()
                        } else {
                            Toast.makeText(
                                contextAct,
                                R.string.editor_permission_media_request_denied,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    override fun onError(e: Throwable) {
                        // Do nothing
                    }

                    override fun onComplete() {
                        // Do nothing
                    }
                })
        }catch (e: Exception){
            //TODO BUBBLE
            messageInputListener?.onSelectImageCameraNotPer()
        }
    }

    fun setMessageInputListener(messageInputListener: MessageInputListener?) {
        this.messageInputListener = messageInputListener
    }

    //TODO UPDATE
    fun showLayoutHeader() {
        actionShowLayoutHeader()
    }

    private fun showKeybroadSoftGiphy(show: Boolean) {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (show) {
            binding.giphyView.inputSearch.requestFocus()
            inputMethodManager.showSoftInput(
                binding.giphyView.inputSearch,
                InputMethodManager.SHOW_IMPLICIT
            )
        } else {
            binding.giphyView.inputSearch.clearFocus()
            inputMethodManager.hideSoftInputFromWindow(
                windowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN
            )
        }
    }

    fun showLayoutTabSticker(show: Boolean) {
        binding.tabSticker.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun showLayoutGiphy(show: Boolean) {
        binding.giphyGr.visibility = if (show) View.VISIBLE else View.GONE
        binding.inputLayout.visibility = if (show) View.GONE else View.VISIBLE
    }

    fun enableGifCardButton(enable : Boolean) {
        binding.gifCardBtn.visibility = if (enable)View.VISIBLE else View.GONE
    }

    fun giphyIsVisible(): Boolean {
        return binding.giphyGr.visibility == View.VISIBLE
    }

    interface MessageInputListener {
        fun onFocusInput()
        fun onShowGalleryInput()
        fun onShowStickerInput()// if show -> gone / gone - show
        fun onShowStickerInput(show: Boolean) // show or gone
        fun onShowGifCardInput(show: Boolean) // show or gone
        fun onShowGiphyInput()
        fun showLayoutKeyboad(show: Boolean)
        fun onUpdateLayoutKeybroadHeight()
        fun layoutStickerIsShowing(): Boolean
        fun layoutGifCardIsShowing(): Boolean

        fun activityIsSoftInputVisible(): Boolean
        fun onSelectImageCamera()
        fun onSelectImageCameraNotPer()
        fun onSelectMediaGallery()
        fun onSubmit(
            input: CharSequence?,
            editMsgId: String?,
            mentions: MutableList<MentionSpannableEntity>?,
            quoteMsg: MessageEntity?
        ): Boolean
        fun onTextChanged(text: String?) {}
        fun onHahaClick()
        fun onAddAttachments() {}
        fun onStartTyping() {}
        fun onStopTyping() {}
        fun onClickGifItem(media: GifModel?)
        fun onSelectFileExternal()
        fun onSelectFileExternalNotPer()
        fun stickerRepository(): StickerRepository?
        fun lifecycleOwner(): LifecycleOwner?
        fun token(): String?
    }

    companion object {
        var heightKeyBroad = 500
    }
}