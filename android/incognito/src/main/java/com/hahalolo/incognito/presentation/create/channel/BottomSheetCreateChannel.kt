package com.hahalolo.incognito.presentation.create.channel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.RequestManager
import com.hahalolo.incognito.R
import com.hahalolo.incognito.databinding.BottomSheetCreateChannelBinding
import com.halo.common.permission.RxPermissions
import com.halo.common.utils.EvenObserver
import com.halo.common.utils.GlideRequestBuilder
import com.halo.editor.mediasend.AvatarSelectionActivity
import com.halo.widget.dialog.BottomSheetDialogBase

class BottomSheetCreateChannel(
    context: Context,
    val requestManager: RequestManager,
    val listener: CreateChannelListener
) :
    BottomSheetDialogBase(context) {

    lateinit var binding: BottomSheetCreateChannelBinding
    private var avatarPath = ""

    override fun initializeCustomView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetCreateChannelBinding.inflate(inflater, container, false)
        initAction()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_PICK_AVATAR -> {
                    data?.getParcelableExtra<Uri>(AvatarSelectionActivity.EXTRA_IMAGE_URI)
                        ?.let { avatarUri ->
                            avatarPath = avatarUri.path ?: ""
                            updateAvatar(avatarUri)
                        }
                }
            }
        }
    }


    private fun updateAvatar(uri: Uri) {
        GlideRequestBuilder
            .getCenterCropRequest(requestManager)
            .load(uri)
            .into(binding.avatarImg)
    }

    private fun openPickAvatar() {
        val rxPermissions = RxPermissions(requireActivity())
        rxPermissions.request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).subscribe(object : EvenObserver<Boolean>() {
            override fun onNext(t: Boolean) {
                if (t) {
                    startActivityForResult(
                        AvatarSelectionActivity.getIntentForUriResult(requireActivity()),
                        REQUEST_PICK_AVATAR
                    )
                } else {
                    Toast.makeText(
                        requireActivity(),
                        R.string.editor_permission_media_request_denied,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    fun initAction() {
        binding.apply {
            pickAvatarBt.setOnClickListener {
                openPickAvatar()
            }
            createBt.setOnClickListener {
                groupNameEdt.text?.toString()?.trim()?.takeIf { it.isNotEmpty() }?.let { name ->
                    listener.onCreateGroup(name, avatarPath)
                    dismiss()
                } ?: kotlin.run {
                    groupNameEdt.error = "Nhập tên nhóm"
                    groupNameEdt.requestFocus()
                }
            }
            cancelBt.setOnClickListener { dismiss() }
            setEnableDismiss(false)
        }
    }

    override fun setUpDialog(dialog: CustomDialog) {

    }

    companion object {
        const val REQUEST_PICK_AVATAR = 111
    }
}

interface CreateChannelListener {
    fun onPickAvatar()
    fun onCreateGroup(name: String, avatar: String)
}
