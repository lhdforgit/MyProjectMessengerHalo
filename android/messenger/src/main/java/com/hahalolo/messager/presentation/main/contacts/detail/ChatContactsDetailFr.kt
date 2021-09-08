package com.hahalolo.messager.presentation.main.contacts.detail

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.RequestOptions
import com.hahalolo.messager.presentation.base.AbsMessFragment
import com.hahalolo.messager.utils.Strings
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatContactDetailFrBinding
import com.halo.common.utils.GlideRequestBuilder
import com.halo.common.utils.ThumbImageUtils
import com.halo.data.entities.contact.Contact
import com.halo.widget.HaloEditText
import com.halo.widget.dialog.HaloDialogCustom
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatContactDetailFr
@Inject constructor() : AbsMessFragment(), ChatContactDetailViewInterface {

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var binding: ChatContactDetailFrBinding
    private lateinit var viewModel: ChatContactsDetailViewModel
    private val requestManager: RequestManager by lazy { Glide.with(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.chat_contact_detail_fr, container, false)
        return binding.root
    }

    override fun initializeViewModel() {
        viewModel = ViewModelProvider(this, factory).get(ChatContactsDetailViewModel::class.java)
        arguments?.getString(PersonalDetailPopup.PERSONAL_ID_ARG)?.takeIf { it.isNotEmpty() }
            ?.let { userId ->
                viewModel.personalId = userId
            }
        arguments?.getBoolean(PersonalDetailPopup.IS_CONTACT_DETAIL_ARG, false)?.let {
            viewModel.isContactDetail = it
            binding.changeName.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    override fun initializeLayout() {
        initObserver()
        initPersonalInfo()
        initAction()
    }

    private fun initPersonalInfo() {
        viewModel.isContactDetail.takeIf { it }?.run {
            initContactDetail()
        } ?: kotlin.run {
            initUserDetail()
        }
    }

    private fun initContactDetail() {
        lifecycleScope.launch {
           viewModel.contactDetail()
        }
    }

    private fun initUserDetail() {
        lifecycleScope.launch {
            viewModel.userDetail().collect {
                when {
                    it.isLoading -> {

                    }
                    it.isSuccess -> {
                        it.data?.let {
                            Contact.transformUserToContact(it).let {
                                updatePersonalInfo(it)
                            }
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    private fun initAction() {
        binding.apply {
            changeName.setOnClickListener {
                val editText = HaloEditText(
                    ContextThemeWrapper(
                        context,
                        R.style.Messenger_EditText_OneLine
                    )
                )
                ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    editText.layoutParams = this
                }
                editText.setText(viewModel.contactName)
                HaloDialogCustom.Builder().apply {
                    setIcon(R.drawable.ic_chat_edit_name_two)
                    setTitle(getString(R.string.chat_message_owner_update_name_reminiscent))
                    setTextPrimary(getString(R.string.chat_message_owner_save))
                    setTextCancel(getString(R.string.chat_message_owner_cancel))
                    setOnClickCancel {}
                    setOnClickPrimary {
                        editText.text?.toString()?.trim()?.let {
                            handleChangeName(it)
                        }
                    }
                    setCustomView(editText)
                }.build().show(childFragmentManager, "ChatContactDetailFr")
            }
        }
    }

    private fun handleChangeName(name: String) {
        lifecycleScope.launch {
            viewModel.updateContactAliasName(name)
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.contactResponse.observe(this@ChatContactDetailFr, {
                it?.let { response ->
                    updatePersonalInfo(response)
                }
            })
        }
    }

    private fun updatePersonalInfo(data: Contact) {
        binding.apply {
            isContact = true
            usernameTv.text = data.fullName ?: ""
            GlideRequestBuilder
                .getCircleCropRequest(requestManager)
                .load(ThumbImageUtils.thumbAvatar(data.avatar ?: ""))
                .error(R.drawable.ic_avatar_holder)
                .into(avatarIv)

            requestManager.load(coverThumb(data.cover))
                .apply(RequestOptions().error(R.drawable.chat_cover_holder))
                .apply(RequestOptions().placeholder(R.drawable.chat_cover_holder))
                .into(coverIv)
            executePendingBindings()

            nameTv.text = data.aliasName ?: ""
            genderTv.text = data.gender ?: ""
            birthdayTv.text = data.birthday ?: ""
            addressTv.text = data.address ?: ""
        }
    }

    private fun coverThumb(path: String?): String? {
        return ThumbImageUtils.thumb(
            ThumbImageUtils.Size.MEDIA_WIDTH_360,
            path ?: "",
            ThumbImageUtils.TypeSize._AUTO
        )
    }

    override fun onClickPersonalPage() {

    }
}

interface ChatContactDetailViewInterface {
    fun onClickPersonalPage()
}