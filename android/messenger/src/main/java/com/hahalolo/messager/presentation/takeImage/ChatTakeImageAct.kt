package com.hahalolo.messager.presentation.takeImage

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.hahalolo.messager.bubble.BubbleService
import com.hahalolo.messager.presentation.base.AbsMessBackActivity
import com.hahalolo.messenger.R
import com.hahalolo.messenger.databinding.ChatTakeImageActBinding
import com.halo.common.permission.RxPermissions
import com.halo.common.utils.HaloFileUtils
import com.halo.editor.mediasend.MediaSendActivity
import com.halo.editor.mediasend.MediaSendActivityResult
import com.halo.editor.util.MediaUtil
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class ChatTakeImageAct : AbsMessBackActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    internal var viewModel: ChatTakeImageViewModel? = null

    internal var binding: ChatTakeImageActBinding? = null

    companion object {
        private const val GROUP_DETAIL_TAKE_IMAGE =
            "ChatTakeImageAndroidAct-GROUP_DETAIL_TAKE_IMAGE"
        private const val GROUP_MESSAGE_TAKE_IMAGE =
            "ChatTakeImageAndroidAct-GROUP_MESSAGE_TAKE_IMAGE"
        private const val GROUP_MESSAGE_TAKE_FILE =
            "ChatTakeImageAndroidAct-GROUP_MESSAGE_TAKE_FILE"

        private const val REQUEST_IMAGE_CAPTURE = 111
        private const val REQUEST_FILE_EXTERNAL = 112
        private const val REQUEST_MESSAGE_SETTING = 113

        fun getIntentMsgTakeMedia(context: Context, roomId: String?): Intent {
            return Intent(context, ChatTakeImageAct::class.java).apply {
                this.putExtra(GROUP_MESSAGE_TAKE_IMAGE, roomId)
            }
        }

        fun getIntentMsgTakeFile(context: Context, roomId: String?): Intent {
            return Intent(context, ChatTakeImageAct::class.java).apply {
                this.putExtra(GROUP_MESSAGE_TAKE_FILE, roomId)
            }
        }

        fun getIntent(context: Context, roomId: String?): Intent {
            return Intent(context, ChatTakeImageAct::class.java).apply {
                this.putExtra(GROUP_DETAIL_TAKE_IMAGE, roomId)
            }
        }
    }

    override fun initActionBar() {

    }

    override fun initializeBindingViewModel() {
        binding = DataBindingUtil.setContentView(this, R.layout.chat_take_image_act)
        viewModel = ViewModelProvider(this, factory).get(ChatTakeImageViewModel::class.java)
        binding?.lifecycleOwner = this
    }

    override fun initializeLayout() {
        viewModel?.roomTakeImageId?.value = intent.getStringExtra(GROUP_DETAIL_TAKE_IMAGE)
        viewModel?.roomMessageTakeMedia?.value = intent.getStringExtra(GROUP_MESSAGE_TAKE_IMAGE)
        viewModel?.roomMessageTakeFile?.value = intent.getStringExtra(GROUP_MESSAGE_TAKE_FILE)

        //check permission and take media
        viewModel?.roomMessageTakeFile?.value?.takeIf { it.isNotEmpty() }?.run {
            navigateTakeFile()
        } ?: run {
            navigateTakeImage()
        }
    }

    private fun navigateTakeFile() {
        try {
            HaloFileUtils.externalPermision(this, object : HaloFileUtils.PerListener {
                override fun onGranted() {
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
                            ), REQUEST_FILE_EXTERNAL
                        )
                    } catch (ex: ActivityNotFoundException) {
                        // Potentially direct the user to the Market with a Dialog
                        Toast.makeText(
                            this@ChatTakeImageAct,
                            getString(R.string.chat_message_please_install_a_file_manager),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }

                override fun onDeny() {
                    Toast.makeText(
                        this@ChatTakeImageAct,
                        com.hahalolo.messenger.R.string.editor_permission_media_request_denied,
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            })
        } catch (e: java.lang.Exception) {
            //TODO BUBBLE
            finish()
        }
    }

    private fun navigateTakeImage() {
        val rxPermissions = RxPermissions(this)
        rxPermissions.request(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).subscribe(object : Observer<Boolean> {
            override fun onSubscribe(d: Disposable) {
                // Do nothing
            }

            override fun onNext(aBoolean: Boolean) {
                if (aBoolean) {
                    startActivityForResult(
                        MediaSendActivity.buildGalleryIntent(this@ChatTakeImageAct),
                        REQUEST_IMAGE_CAPTURE
                    )
                } else {
                    Toast.makeText(
                        this@ChatTakeImageAct,
                        R.string.editor_permission_media_request_denied,
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            }

            override fun onError(e: Throwable) {
                // Do nothing
            }

            override fun onComplete() {
                // Do nothing
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
                    onSendMessageFile(this)
                }
                REQUEST_MESSAGE_SETTING -> {
//                    data?.getBooleanExtra(GroupConstant.STATUS_LEAVE_GROUP, false)?.let {
//                        if (it) {
//                            finish()
//                        }
//                    }
//                    data?.getBooleanExtra(GroupConstant.STATUS_DELETE_CONVERSATION, false)
//                        ?.takeIf { it }
//                        ?.let {
//                            finish()
//                        }
                }
                else -> {
                    makeSnackbar(R.string.chat_message_aller_file_not_available)
                }
            }
        }
        finish()
    }

    private fun onSendMessageFile(uri: Uri) {
        viewModel?.roomMessageTakeFile?.value?.takeIf { it.isNotEmpty() }?.run {
            BubbleService.pushFileMessage(this@ChatTakeImageAct, this, uri.toString())
        }
    }

    private fun onSendMessageMediaList(uris: MutableList<Uri>) {
        viewModel?.roomMessageTakeMedia?.value?.takeIf { it.isNotEmpty() }?.run {
            BubbleService.pushMediaMessage(this@ChatTakeImageAct, this, uris)
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

}