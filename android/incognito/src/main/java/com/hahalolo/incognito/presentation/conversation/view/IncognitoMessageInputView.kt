package com.hahalolo.incognito.presentation.conversation.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.giphy.sdk.core.models.Media
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.IncognitoMessageInputViewBinding
import com.hahalolo.incognito.presentation.conversation.view.sticker.IncognitoMessageStickerListener
import com.hahalolo.incognito.presentation.conversation.view.sticker.IncognitoMessageStickerType
import com.halo.data.cache.pref.utils.PrefUtilsImpl
import com.halo.widget.room.table.StickerPackTable
import com.halo.widget.room.table.StickerTable
import com.halo.widget.sticker.Utils

class IncognitoMessageInputView : FrameLayout {
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView()
    }

    private lateinit var binding: IncognitoMessageInputViewBinding
    private val inputState = MutableLiveData(IncognitoMessageInputState.NONE)
    private val stickerState = MutableLiveData(IncognitoMessageStickerType.STICKER)

    private var MAX = -1
    private var heightCurrent = -1
    private var contextAct: Activity? = null
    private var onGlobalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private var inputListener: IncognitoMessageInputListener? = null

    fun updateInputListener(inputListener: IncognitoMessageInputListener) {
        this.inputListener = inputListener
        bindLayout()
    }

    /*INIT LAYOUT*/

    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.incognito_message_input_view, this, false
        )
        addView(binding.root)

        try {
            contextAct = Utils.asActivity(context)
            contextAct?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        heightKeyBroad = PrefUtilsImpl.getInstance(context).heightKeybroad
        binding.layoutHeader.layoutParams.height = heightKeyBroad
        binding.layoutKeybroad.layoutParams.height = heightKeyBroad
        requestLayout()
        initGlobal()
        initActions()
    }

    private fun initGlobal() {
        if (contextAct == null) return
        try {
            onGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
                val rect = Utils.windowVisibleDisplayFrame(contextAct!!)
                if (MAX < rect.bottom) {
                    MAX = rect.bottom
                }
                val heightDifference = MAX - rect.bottom
                if (heightDifference != heightCurrent) {
                    heightCurrent = heightDifference
                    initHeightLayout(heightDifference)
                    if (heightDifference > 100) {
                        if (binding.messageInput.isFocused) {
                            //focus input layout
                            inputState.value = IncognitoMessageInputState.KEY_BROAD
                        } else {
                            //focus input search gif
                            inputState.value = IncognitoMessageInputState.GIF
                        }
                    } else {
                        if (inputState.value != IncognitoMessageInputState.STICKER) {
                            inputState.value = IncognitoMessageInputState.NONE
                        }
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun initHeightLayout(height: Int) {
        if (height > 0 && height != PrefUtilsImpl.getInstance(context).heightKeybroad) {
            PrefUtilsImpl.getInstance(context).insertHeightKeybroad(height)
            binding.layoutHeader.layoutParams.height = height
            binding.layoutKeybroad.layoutParams.height = height
            requestLayout()
        }
    }

    private fun initActions() {
        binding.btnSend.setOnClickListener {
            val result = inputListener?.onSendMessageText(binding.messageInput.getTextSend())
            if (result == true) binding.messageInput.clearTextInput()
        }

        binding.btnSticker.setOnClickListener {
            if (inputState.value == IncognitoMessageInputState.STICKER) {
                inputState.value = IncognitoMessageInputState.KEY_BROAD
                actionShowKeyBroad()
            } else {
                stickerState.value = IncognitoMessageStickerType.STICKER
                inputState.value = IncognitoMessageInputState.STICKER
                actionHideKeyBroad()
            }
        }
        binding.messageInput.setOnClickListener {
            inputState.value = IncognitoMessageInputState.KEY_BROAD
        }

        binding.messageInput.onFocusChangeListener =
            OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    inputState.value = IncognitoMessageInputState.KEY_BROAD
                }
            }
        binding.messageInput.addTextChangedListener {
            val content = it?.toString()
            if (content.isNullOrEmpty()) {
                binding.btnSend.visibility = View.GONE
                binding.btnFile.visibility = View.VISIBLE
                binding.btnSticker.visibility = View.VISIBLE
                binding.btnHaha.visibility = View.VISIBLE
            } else {
                binding.btnSend.visibility = View.VISIBLE
                binding.btnFile.visibility = View.GONE
                binding.btnSticker.visibility = View.GONE
                binding.btnHaha.visibility = View.GONE
            }
        }
    }
    /*INIT LAYOUT END*/

    /*EVENT LAYOUT*/
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        rootView.viewTreeObserver?.addOnGlobalLayoutListener {
            val heightDiff = this.height - rootView.height
        }
        contextAct?.window?.decorView?.viewTreeObserver?.addOnGlobalLayoutListener(
            onGlobalLayoutListener
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        try {
            contextAct?.window?.decorView?.run {
                Utils.removeOnGlobalLayoutListener(this, onGlobalLayoutListener)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    /*EVENT LAYOUT END*/

    /*BIND LAYOUT*/

    private val stickerListener = object : IncognitoMessageStickerListener {
        override fun onSelectType(type: IncognitoMessageStickerType) {
            stickerState.value = type
        }

        override fun observerStickerType(observer: Observer<IncognitoMessageStickerType>) {
            inputListener?.lifecycleOwner()?.run {
                stickerState.observe(this, observer)
            }
        }

        override fun observerSticker(
            packId: String,
            observer: Observer<MutableList<StickerTable>>
        ) {
            inputListener?.run {
                stickerRepository().stickers(token = null, packId = packId).observe(lifecycleOwner(), observer)
            }
        }

        override fun observerStickerPacks(observer: Observer<MutableList<StickerPackTable>>) {
            inputListener?.run {
                stickerRepository().stickerPacks().observe(lifecycleOwner(), observer)
            }
        }

        override fun observerEmojiPacks(observer: Observer<String>) {

        }

        override fun observerFavoritePacks(observer: Observer<MutableList<StickerTable>>) {
            inputListener?.run {
                stickerRepository().latestStickers().observe(lifecycleOwner(), observer)
            }
        }

        override fun onClickFavorite(data: StickerTable) {

        }

        override fun onClickSticker(stickerTable: StickerTable) {
            inputListener?.run {
                stickerRepository().clickSticker(stickerTable.id)
            }
        }


        override fun onClickGif(media: Media) {
            inputListener?.run {
                stickerRepository().clickGif(media)
            }
        }

        override fun onClickEmoji() {

        }

        override fun closeGif() {
            inputState.value = IncognitoMessageInputState.KEY_BROAD
            actionShowKeyBroad()
        }

        override fun focusEditText(): EditText {
            return binding.messageInput
        }
    }

    private fun bindLayout() {

        binding.headerSticker.updateListener(stickerListener)
        binding.stickerView.updateListener(stickerListener)
        binding.gifView.updateListener(stickerListener)

        inputListener?.lifecycleOwner()?.run {

            stickerState.observe(this, Observer {
                if (inputState.value == IncognitoMessageInputState.STICKER
                    || inputState.value == IncognitoMessageInputState.GIF
                ) {
                    if (it == IncognitoMessageStickerType.GIF) {
                        binding.gifView.actionShowKeyBroad()
                        inputState.value = IncognitoMessageInputState.GIF
                    } else {
                        binding.gifView.actionHideKeyBroad()
                        inputState.value = IncognitoMessageInputState.STICKER
                    }
                }
            })

            inputState.observe(this, Observer {
                binding.btnSticker.setImageResource(
                    if (it == IncognitoMessageInputState.STICKER
                        || it == IncognitoMessageInputState.GIF
                    )
                        R.drawable.ic_incognito_input_sticker_active
                    else R.drawable.ic_incognito_input_sticker
                )

                binding.stickerView.visibility = if (it == IncognitoMessageInputState.STICKER
                    || it == IncognitoMessageInputState.GIF
                ) View.VISIBLE else View.GONE
                binding.headerSticker.visibility = if (it == IncognitoMessageInputState.STICKER
                    || it == IncognitoMessageInputState.GIF
                ) View.VISIBLE else View.GONE


                binding.gifView.visibility = if (
                    it == IncognitoMessageInputState.GIF
                ) View.VISIBLE else View.GONE

                binding.inputLayout.visibility = if (
                    it == IncognitoMessageInputState.GIF
                ) View.GONE else View.VISIBLE

                when (it) {
                    IncognitoMessageInputState.NONE -> {
                        binding.messageInput.clearFocusWhenEmpty()
                        binding.layoutKeybroad.visibility = View.GONE
                    }
                    IncognitoMessageInputState.KEY_BROAD -> {
                        binding.layoutKeybroad.visibility = View.VISIBLE
                    }
                    IncognitoMessageInputState.GIF -> {
                        binding.messageInput.clearFocusWhenEmpty()
                        binding.layoutKeybroad.visibility = View.VISIBLE

                    }
                    IncognitoMessageInputState.STICKER -> {
                        binding.messageInput.clearFocusWhenEmpty()
                        binding.layoutKeybroad.visibility = View.VISIBLE

                    }
                    IncognitoMessageInputState.FILE -> {

                    }
                }
            })
        }
    }

    /*BIND LAYOUT END*/


    /*ACTION LAYOUT*/
    private fun actionShowKeyBroad() {
        binding.messageInput.requestFocus()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.messageInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun actionHideKeyBroad() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    fun isBackPressed(): Boolean {
        if (inputState.value == IncognitoMessageInputState.NONE
            || inputState.value == IncognitoMessageInputState.KEY_BROAD
        ) {
            return false
        } else if (inputState.value == IncognitoMessageInputState.STICKER) {
            inputState.value = IncognitoMessageInputState.NONE
        } else if (inputState.value == IncognitoMessageInputState.GIF) {
            inputState.value = IncognitoMessageInputState.NONE
        }
        return true
    }
    /*ACTION LAYOUT END */

    companion object {
        var heightKeyBroad = 700
    }
}