package com.hahalolo.messager.presentation.main.contacts.owner

import android.content.Context
import android.content.Intent
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hahalolo.messager.presentation.base.AbsMessBackActivity
import com.hahalolo.messager.presentation.main.contacts.owner.dialog.OwnerStatusDialog
import com.hahalolo.messager.presentation.main.contacts.owner.dialog.OwnerStatusListener
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatMessengerUserAboutSettingBinding
import com.hahalolo.pickercolor.MaterialColorPickerDialog
import com.hahalolo.pickercolor.model.ColorShape
import com.halo.common.utils.ActivityUtils
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.ThumbImageUtils
import com.halo.common.utils.ktx.transparentStatusNavigationBar
import com.halo.common.utils.ktx.viewModels
import com.halo.data.entities.contact.Contact
import com.halo.widget.HaloEditText
import com.halo.widget.dialog.HaloDialogCustom
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatOwnerAct : AbsMessBackActivity() {
    private var binding: ChatMessengerUserAboutSettingBinding? = null
    private val requestManager: RequestManager by lazy { Glide.with(this) }

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private val viewModel by viewModels<ChatOwnerViewModel> { factory }

    override fun initActionBar() {

    }

    override fun initializeLayout() {
        transparentStatusNavigationBar(false)
        initView()
        initHandleOwner()
        initAction()
    }

    private fun initHandleOwner() {
        lifecycleScope.launch {
            viewModel.owner().collect {
                when {
                    it.isLoading -> {

                    }
                    it.isSuccess -> {
                        it.data?.let {
                            updateOwner(it)
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun updateOwner(owner: Contact) {
        binding?.apply {
            usernameTv.text = owner.fullName
            nameTv.text = owner.fullName

            GlideRequestBuilder
                .getCircleCropRequest(requestManager)
                .load(ThumbImageUtils.thumbAvatar(owner.avatar))
                .error(R.drawable.ic_avatar_holder)
                .into(avatarIv)

            genderTv.text = owner.gender
            birthdayTv.text = owner.birthday
            addressTv.text = owner.address
        }
    }

    private fun initAction() {
        binding?.apply {
            toolBar.setOnClickListener { onBackPressed() }
            shareQrTv.setOnClickListener { handleShareQR() }
            statusTv.setOnClickListener { handleStatus() }
            displayTv.setOnClickListener { handlePickColor() }
            logOutTv.setOnClickListener { handleLogout() }
        }
    }

    private fun handleStatus() {
        OwnerStatusDialog(this).apply {
            setListener(object : OwnerStatusListener {
                override fun onClickActive() {

                }

                override fun onClickAbsent() {

                }

                override fun onClickBusy() {

                }

                override fun onClickUpdateStatus() {
                    val editText = HaloEditText(
                        ContextThemeWrapper(
                            this@ChatOwnerAct,
                            R.style.Messenger_EditText_OneLine
                        )
                    )
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        editText.layoutParams = this
                    }
                    //                editText.setText(viewModel.channelName)
                    HaloDialogCustom.Builder().apply {
                        setIcon(R.drawable.ic_post_status)
                        setTitle(getString(R.string.chat_message_owner_status_update))
                        setTextPrimary(getString(R.string.chat_message_owner_save))
                        setTextCancel(getString(R.string.chat_message_owner_cancel))
                        setOnClickCancel {}
                        setOnClickPrimary {
                            editText.text?.toString()?.trim()?.let {
                            }
                        }
                        setCustomView(editText)
                    }.build().show(supportFragmentManager, "ChatOwnerAct")
                }
            })
        }.show()
    }

    private fun handleShareQR() {

    }

//    private fun handleSupportCenter() {
//        CustomTab.openUrl(
//            this@ChatMessageSettingAct,
//            if (appController.appLang() == HaloConfig.LANGUAGE_DEFAULT) HaloConfig.HELP
//            else HaloConfig.HELP_EN,
//            "Trung tâm hỗ trợ"
//        )
//    }

    private fun handlePickColor() {
        kotlin.runCatching {
            MaterialColorPickerDialog
                .Builder(this@ChatOwnerAct)
                .setTitle("Chỉnh màu")
                .setColors(resources.getStringArray(R.array.color_chat_setting))
                .setColorShape(ColorShape.CIRCLE)
                .setColorListener { color, colorHex ->

                }
                .build()
                .show()
        }
    }

    private fun handleLogout() {
        HaloDialogCustom.Builder().apply {
            setTitle("Đăng xuất")
            setDescription("Bạn có chắc chắn muốn đăng xuất tài khoản khỏi ứng dụng?")
            setTextPrimary("Đồng ý")
            setTextCancel("Hủy")
            setOnClickCancel {
            }
            setOnClickPrimary {
                appController.navigateSignIn()
            }.build().apply {
                show(supportFragmentManager, "ChatAct")
            }
        }
    }

    private fun handleLogoutSuccess() {
        /*PushNotifyWrapperLifecycle.deRegisterMessenger(this@ChatMessageSettingAct)*/
        appController.navigateSignIn()
        ActivityUtils.finishAllActivities()
    }

    private fun initView() {
//        binding?.apply {
//            usernameTv.text = appController.ownerName
//            nameTv.text = appController.ownerName
//
//            GlideRequestBuilder
//                .getCircleCropRequest(requestManager)
//                .load(ThumbImageUtils.thumbAvatar(appController.ownerAvatar))
//                .error(R.drawable.ic_avatar_holder)
//                .into(avatarIv)
//        }
    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(
            this@ChatOwnerAct,
            R.layout.chat_messenger_user_about_setting
        )
    }

    companion object {
        @JvmStatic
        fun getIntent(context: Context): Intent {
            return Intent(context, ChatOwnerAct::class.java)
        }
    }
}