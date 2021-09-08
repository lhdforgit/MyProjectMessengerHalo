package com.hahalolo.messager.bubble.conversation.dialog

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.RequestManager
import com.hahalolo.messenger.databinding.HaloDialogCustomViewBinding
import com.halo.common.utils.KeyboardUtils
import com.halo.common.utils.ThumbImageUtils
import com.halo.widget.R

class HaloDialogCustomView : FrameLayout {
    private var binding: HaloDialogCustomViewBinding? = null

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = HaloDialogCustomViewBinding.inflate(inflater, this, true)
    }

    fun showDialog() {
        visibility = View.VISIBLE
    }

    fun hideDialog() {
        try {
            visibility = View.GONE
            binding?.apply {
                primaryBt.setOnClickListener(null)
                warningBt.setOnClickListener(null)
                successBt.setOnClickListener(null)
                cancelBt.setOnClickListener(null)
                actionChatBt.setOnClickListener(null)
                inputEdt.visibility = View.GONE
            }
            KeyboardUtils.hideSoftInput(this@HaloDialogCustomView)

        } catch (e: Exception) {

        }
    }

    fun setTitle(title: CharSequence) {
        binding?.titleTv?.text = title
        binding?.titleTv?.visibility = View.VISIBLE
    }

    fun setDescription(description: CharSequence) {
        binding?.descriptionTv?.text = description
        binding?.descriptionTv?.visibility = View.VISIBLE
    }

    fun setTextInput(text: CharSequence) {
        binding?.inputEdt?.setText(text)
        binding?.inputEdt?.visibility = View.VISIBLE
    }

    fun getTextInput(): String {
        return binding?.inputEdt?.text?.toString() ?: ""
    }

    fun setIcon(icon: Int) {
        binding?.logoImg?.setImageResource(icon)
    }

    fun setPrimaryText(text: CharSequence) {
        binding?.primaryBt?.text = text
    }

    fun setSuccessText(text: CharSequence) {
        binding?.successBt?.text = text
    }

    fun setWarningText(text: CharSequence) {
        binding?.warningBt?.text = text
    }

    fun setCancelText(text: CharSequence) {
        binding?.cancelBt?.text = text
    }

    fun setPrimaryClick(onClickListener: OnClickListener) {
        binding?.primaryBt?.setOnClickListener(onClickListener)
    }

    fun setSuccessClick(onClickListener: OnClickListener) {
        binding?.successBt?.setOnClickListener(onClickListener)
    }

    fun setWarningClick(onClickListener: OnClickListener) {
        binding?.warningBt?.setOnClickListener(onClickListener)
    }

    fun setOnSentMessageClick(onClickListener: OnClickListener) {
        binding?.actionChatBt?.setOnClickListener(onClickListener)
    }

    fun hideCancel(){
        binding?.cancelBt?.visibility = View.GONE
    }

    fun buildWarning(
        title: CharSequence,
        description: CharSequence,
        textAction: CharSequence,
        onClickListener: OnClickListener
    ) {
        binding?.apply {
            logoImg.setImageResource(R.drawable.ic_chat_dialog_warning)
            primaryBt.text = textAction
            primaryBt.setOnClickListener(onClickListener)
            build(title, description)
        }
    }

    fun buildSuccess(
        title: CharSequence,
        description: CharSequence,
        textAction: CharSequence,
        onClickListener: OnClickListener
    ) {
        binding?.apply {
            logoImg.setImageResource(R.drawable.ic_dialog_header_success)
            successBt.text = textAction
            successBt.setOnClickListener(onClickListener)
            build(title, description)
        }
    }

    fun buildUpdate(
        title: CharSequence,
        description: CharSequence,
        textAction: CharSequence,
        onClickListener: OnClickListener
    ) {
        binding?.apply {
            logoImg.setImageResource(R.drawable.ic_chat_dialog_edit)
            primaryBt.text = textAction
            primaryBt.setOnClickListener(onClickListener)
            buildUpdate(title, description)
        }
    }

    fun buildCancel(
        title: CharSequence,
        description: CharSequence,
        textAction: CharSequence,
        onClickListener: OnClickListener
    ) {
        binding?.apply {
            logoImg.setImageResource(R.drawable.ic_request_failed)
            warningBt.text = textAction
            warningBt.setOnClickListener(onClickListener)
            build(title, description)
        }

    }

    fun buildDelete(
        title: CharSequence,
        description: CharSequence,
        textAction: CharSequence,
        onClickListener: OnClickListener
    ) {
        binding?.apply {
            logoImg.setImageResource(R.drawable.ic_chat_dialog_delete)
            warningBt.text = textAction
            warningBt.setOnClickListener(onClickListener)
            build(title, description)
        }
    }

    fun buildMenu(title: CharSequence, listener: DialogMenuListener?) {
        binding?.apply {
            isGroup = listener?.isGroup() ?: false
            isEnableNotify = listener?.isEnableNotify() ?: false
            logoImg.setImageResource(R.drawable.ic_conversation_dialog_header)
            descriptionTv.visibility = View.GONE
            menuGroup.visibility = View.VISIBLE
            titleTv.text = title
            groupAction.visibility = View.GONE
            containerLayout.setOnClickListener { hideDialog() }
            listMemberTv.setOnClickListener {
                listener?.onClickMemberList()
            }
            addMemberTv.setOnClickListener {
                listener?.onClickAddMember()
            }
            leaveGroupTv.setOnClickListener {
                listener?.onClickLeaveGroup()
            }
            removeConversationTv.setOnClickListener {
                listener?.onClickDeleteConversation()
            }
            notiConversationTv.setOnClickListener {
                listener?.onClickNotification()
                hideDialog()
            }
            showDialog()
        }
    }

    private fun build(title: CharSequence, description: CharSequence) {
        binding?.apply {
            titleTv.text = title
            descriptionTv.text = description
            descriptionTv.visibility = View.VISIBLE
            titleTv.visibility = View.VISIBLE
            groupAction.visibility = View.VISIBLE
            inputEdt.visibility = View.GONE
            build()
        }
    }

    private fun buildUpdate(title: CharSequence, description: CharSequence) {
        binding?.apply {
            titleTv.text = title
            descriptionTv.text = description
            inputEdt.visibility = View.VISIBLE
            build()
        }
    }

    private fun buildActionButton() {
        binding?.apply {
            groupAction.visibility = View.VISIBLE
            actionChatBt.visibility = if (actionChatBt.hasOnClickListeners()) View.VISIBLE else View.GONE
            primaryBt.visibility = if (primaryBt.hasOnClickListeners()) View.VISIBLE else View.GONE
            successBt.visibility = if (successBt.hasOnClickListeners()) View.VISIBLE else View.GONE
            warningBt.visibility = if (warningBt.hasOnClickListeners()) View.VISIBLE else View.GONE
            cancelBt.setOnClickListener { hideDialog() }
            cancelBt.visibility = View.VISIBLE
            haloDialogView.setOnClickListener { }
        }
    }

    fun build() {
        binding?.apply {
            buildActionButton()
            containerLayout.setOnClickListener { hideDialog() }
            menuGroup.visibility = View.GONE
            showDialog()
        }
    }

    fun buildDisable() {
        binding?.apply {
            buildActionButton()
            containerLayout.setOnClickListener { }
            showDialog()
        }
    }

    fun buildMentionDetail(
        userName: String?,
        avatar: String?,
        requestManager: RequestManager?,

        ) {
        binding?.apply {
            requestManager?.apply {
                load(
                    ThumbImageUtils.thumb(
                        ThumbImageUtils.Size.AVATAR_NORMAL,
                        avatar,
                        ThumbImageUtils.TypeSize._1_1
                    )
                )
                    .placeholder(com.hahalolo.messenger.R.drawable.ic_dummy_personal_circle)
                    .error(com.hahalolo.messenger.R.drawable.ic_dummy_personal_circle)
                    .circleCrop()
                    .into(logoImg)

            }
            titleTv.text = userName ?: ""
            inputEdt.visibility = View.GONE
            descriptionTv.visibility = View.GONE
            build()
            hideCancel()
        }
    }
}