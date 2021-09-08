package com.hahalolo.qrcode

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Camera
import android.net.Uri
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.common.base.Objects
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.hahalolo.qrcode.barcodedetection.BarcodeProcessor
import com.hahalolo.qrcode.camera.CameraSource
import com.hahalolo.qrcode.databinding.QrCodeReaderActBinding
import com.halo.common.permission.RxPermissions
import com.halo.common.utils.ClipbroadUtils
import com.halo.common.utils.EvenObserver
import com.halo.common.utils.UtilsVibrator
import com.halo.common.utils.ktx.viewModels
import com.hahalolo.playcore.split.DaggerSplitActivity
import com.halo.common.utils.ktx.notNull
import com.halo.di.ActivityScoped
import timber.log.Timber
import java.io.IOException
import java.util.*
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/19/21
 * com.hahalolo.qrcode
 */
@ActivityScoped
class QRCodeReaderAct
@Inject constructor() : com.hahalolo.playcore.split.DaggerSplitActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<QrCodeReaderActBinding>()
    val viewModel by viewModels<QRCodeReaderViewModel> {
        factory
    }

    private var cameraSource: CameraSource? = null
    private var currentWorkflowState: QRCodeReaderViewModel.WorkflowState? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Utils.REQUEST_CODE_PHOTO_LIBRARY && resultCode == Activity.RESULT_OK) {
            data?.data?.apply {
                imageFromPath(this@QRCodeReaderAct, this)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.qr_code_reader_act)
        binding.apply {
            cameraPreviewGraphicOverlay.apply {
                cameraSource = CameraSource(this)
            }

            flashButton.setOnClickListener {
                flashButton.let {
                    if (it.isSelected) {
                        it.isSelected = false
                        cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                    } else {
                        it.isSelected = true
                        cameraSource!!.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                    }
                }
            }

            myQrCode.setOnClickListener {
                /*QRCodeAct.navigateQrCode(
                    this@QRCodeReaderAct,
                    controller.userIdToken,
                    controller.avatar
                )*/
            }

            getQrCode.setOnClickListener {
                Utils.openImagePicker(this@QRCodeReaderAct)
            }

            closeButton.setOnClickListener {
                finish()
            }
        }

        setUpWorkflowModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.markCameraFrozen()
        currentWorkflowState = QRCodeReaderViewModel.WorkflowState.NOT_STARTED
        cameraSource?.setFrameProcessor(
            BarcodeProcessor(
                binding.cameraPreviewGraphicOverlay,
                viewModel
            )
        )
        viewModel.setWorkflowState(QRCodeReaderViewModel.WorkflowState.DETECTING)
    }

    override fun onPause() {
        super.onPause()
        currentWorkflowState = QRCodeReaderViewModel.WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraSource?.release()
        cameraSource = null
    }

    private fun startCameraPreview() {
        val workflowModel = this.viewModel
        val cameraSource = this.cameraSource ?: return
        if (!workflowModel.isCameraLive) {
            try {
                workflowModel.markCameraLive()
                binding.cameraPreview.start(cameraSource)
            } catch (e: IOException) {
                Timber.e("Failed to start camera preview! - $e")
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        val workflowModel = this.viewModel
        if (workflowModel.isCameraLive) {
            workflowModel.markCameraFrozen()
            binding.flashButton.isSelected = false
            binding.cameraPreview.stop()
        }
    }

    private fun setUpWorkflowModel() {
        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        viewModel.workflowState.observe(this, { workflowState ->
            if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                return@observe
            }

            currentWorkflowState = workflowState
            Timber.d("Current workflow state: ${currentWorkflowState?.name}")

            when (workflowState) {
                QRCodeReaderViewModel.WorkflowState.DETECTING,
                QRCodeReaderViewModel.WorkflowState.CONFIRMING -> {
                    startCameraPreview()
                }
                QRCodeReaderViewModel.WorkflowState.SEARCHING,
                QRCodeReaderViewModel.WorkflowState.DETECTED,
                QRCodeReaderViewModel.WorkflowState.SEARCHED -> {
                    stopCameraPreview()
                }
                else -> {
                }
            }
        })
        viewModel.detectedBarcode.observe(this, { barcode ->
            showQrCodeResult(barcode?.rawValue)
        })
    }

    private fun imageFromPath(context: Context, uri: Uri) {
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, uri)
            scanBarcodes(image)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun scanBarcodes(image: InputImage) {
        BarcodeScanning.getClient().process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    showQrCodeResult(barcode.rawValue)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
    }

    private fun showQrCodeResult(qrCode: String?) {
        kotlin.runCatching {
            qrCode?.apply {
                UtilsVibrator.startVibratorSmall(this@QRCodeReaderAct)
                if (URLUtil.isValidUrl(qrCode)) {
                    val uri = Uri.parse(qrCode)
                    // Trường hợp xác định được là link trang cá nhân
                    if (qrCode.contains("hahalolo.com") && uri.path?.contains("/u/") == true) {
                        val indexOf: Int = uri.path?.lastIndexOf("/") ?: -1
                        if (indexOf > -1) {
                            val userId = uri.path?.substring(indexOf + 1)
                            /*controller.navigationToPersonalWall(
                                this@QRCodeReaderAct,
                                userId
                            )*/
                        } else {
                            // Trường hợp lấy link trang cá nhân bị lỗi
                            openLinkQrCode(qrCode, uri)
                        }
                    } else {
                        openLinkQrCode(qrCode, uri)
                    }
                } else {
                    copyTextQrCode(qrCode)
                }
            } ?: kotlin.run {
                notifyQrCodeNotFound()
            }
        }.getOrElse {
            it.printStackTrace()
            notifyQrCodeNotFound()
        }
    }

    @SuppressLint("ShowToast")
    private fun copyTextQrCode(qrCode: String?) {
        qrCode?.apply {
            Snackbar.make(binding.wrapper, qrCode, Snackbar.LENGTH_LONG)
                .setAction(R.string.barcode_copy_keyboard) {
                    ClipbroadUtils.copyText(this@QRCodeReaderAct, qrCode)
                }
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        viewModel.setWorkflowState(QRCodeReaderViewModel.WorkflowState.DETECTING)
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }
                })
                .show()
        }
    }

    @SuppressLint("ShowToast")
    private fun openLinkQrCode(qrCode: String?, uri: Uri?) {
        qrCode?.apply {
            Snackbar.make(binding.wrapper, qrCode, Snackbar.LENGTH_LONG)
                .setAction(R.string.barcode_open_link) {
                    uri?.apply {
                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                }
                .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        super.onDismissed(transientBottomBar, event)
                        viewModel.setWorkflowState(QRCodeReaderViewModel.WorkflowState.DETECTING)
                    }

                    override fun onShown(transientBottomBar: Snackbar?) {
                        super.onShown(transientBottomBar)
                    }
                })
                .show()
        }
    }

    @SuppressLint("ShowToast")
    private fun notifyQrCodeNotFound() {
        Snackbar.make(binding.wrapper, R.string.barcode_qr_code_not_found, Snackbar.LENGTH_LONG)
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    viewModel.setWorkflowState(QRCodeReaderViewModel.WorkflowState.DETECTING)
                }

                override fun onShown(transientBottomBar: Snackbar?) {
                    super.onShown(transientBottomBar)
                }
            })
            .show()
    }

    companion object {
        @JvmStatic
        fun getIntent(
            context: Context
        ) = Intent(context, QRCodeReaderAct::class.java)

        @JvmStatic
        fun navigateQrCodeReader(
            context: Context
        ) {
            kotlin.runCatching {
                val rxPermissions = RxPermissions(context as Activity)
                rxPermissions.request(
                    Manifest.permission.CAMERA
                ).subscribe(object : EvenObserver<Boolean>() {
                    override fun onNext(t: Boolean) {
                        if (t) {
                            val intent = getIntent(context)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(
                                context,
                                R.string.editor_permission_media_request_denied,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                })
            }.getOrElse {
                it.printStackTrace()
            }
        }
    }
}