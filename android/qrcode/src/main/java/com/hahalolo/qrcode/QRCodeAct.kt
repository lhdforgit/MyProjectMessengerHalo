package com.hahalolo.qrcode

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hahalolo.qrcode.databinding.QrCodeActBinding
import com.halo.common.utils.SizeUtils
import com.halo.common.utils.ktx.viewModels
import com.hahalolo.playcore.split.DaggerSplitActivity
import com.halo.common.utils.ktx.lightTheme
import com.halo.common.utils.ktx.notNull
import com.halo.di.ActivityScoped
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 5/19/21
 * com.hahalolo.qrcode
 */
@ActivityScoped
class QRCodeAct
@Inject constructor() : com.hahalolo.playcore.split.DaggerSplitActivity() {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    var binding by notNull<QrCodeActBinding>()
    val viewModel by viewModels<QRCodeViewModel> {
        factory
    }

    private var userAvatarBitmap: Bitmap? = null
    var qrCodeBitmap: Bitmap? = null

    private val dateFormatter = SimpleDateFormat(
        "yyyy.MM.dd 'at' HH:mm:ss z",
        Locale.getDefault()
    )

    private val disposables = CompositeDisposable()

    private val observable: Single<Bitmap>
        get() = Single.create { emitter ->
            userAvatarBitmap?.run {
                val bitmap = viewModel.overlay(viewModel.genQrCode(), this)
                bitmap?.run {
                    emitter.onSuccess(bitmap)
                } ?: emitter.onError(Exception("Bitmap cannot created"))
            }
        }
    private val observer: SingleObserver<Bitmap>
        get() = object : SingleObserver<Bitmap> {

            override fun onSubscribe(d: Disposable) {
                disposables.add(d)
            }

            override fun onSuccess(t: Bitmap) {
                binding.qrCodeImg.setImageBitmap(t)
                qrCodeBitmap = t
                disposables.clear()
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                disposables.clear()
            }
        }

    private val observableSave: Single<String>
        get() = Single.create { emitter ->
            qrCodeBitmap?.run {
                val url = saveQrCodeToFile(this)
                emitter.onSuccess(url)
            }
        }

    private val observerSave: SingleObserver<String>
        get() = object : SingleObserver<String> {

            override fun onSubscribe(d: Disposable) {
                disposables.add(d)
            }

            override fun onSuccess(t: String) {
                Toast.makeText(this@QRCodeAct, R.string.barcode_qr_code_saved, Toast.LENGTH_LONG).show()
                disposables.clear()
            }

            override fun onError(e: Throwable) {
                e.printStackTrace()
                disposables.clear()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lightTheme()
        binding = DataBindingUtil.setContentView(this, R.layout.qr_code_act)

        intent?.apply {
            viewModel.userId = this.getStringExtra(USER_ID_ARGS)
            viewModel.avatar = this.getStringExtra(USER_AVATAR_ARGS)
        }
        viewModel.genQrCode()?.apply {
            qrCodeBitmap = this
            binding.qrCodeImg.setImageBitmap(this)
            Glide.with(this@QRCodeAct)
                .asBitmap()
                .load(viewModel.avatar)
                .circleCrop()
                .override(SizeUtils.dp2px(32.0f))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        userAvatarBitmap = resource
                        observable
                            // Run on a background thread
                            .subscribeOn(Schedulers.computation())
                            // Be notified on the main thread
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(observer)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        }

        binding.apply {
            navigationBt.setOnClickListener {
                finish()
            }
            readQrCode.setOnClickListener {
                QRCodeReaderAct.navigateQrCodeReader(this@QRCodeAct)
            }
            saveQrCode.setOnClickListener {
                observableSave
                    // Run on a background thread
                    .subscribeOn(Schedulers.computation())
                    // Be notified on the main thread
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observerSave)
            }
        }
    }

    fun saveQrCodeToFile(bitmap: Bitmap?): String {
        val resolver = applicationContext.contentResolver
        return kotlin.runCatching {
            return bitmap?.run {
                val imageUrl = MediaStore.Images.Media.insertImage(
                    resolver, bitmap, "Hahalolo QR Code", dateFormatter.format(Date())
                )
                return if (!imageUrl.isNullOrEmpty()) {
                    Timber.i("Writing to MediaStore: $imageUrl")
                    imageUrl
                } else {
                    Timber.e("Writing to MediaStore failed")
                    ""
                }
            } ?: ""
        }.getOrElse {
            it.printStackTrace()
            ""
        }
    }

    companion object {
        private const val USER_ID_ARGS = "User-Id-Args"
        private const val USER_AVATAR_ARGS = "User-Avatar-Args"

        @JvmStatic
        fun getIntent(
            context: Context,
            userId: String?,
            avatar: String?
        ): Intent {
            val intent = Intent(context, QRCodeAct::class.java)
            intent.putExtra(USER_ID_ARGS, userId)
            intent.putExtra(USER_AVATAR_ARGS, avatar)
            return intent
        }

        fun navigateQrCode(
            context: Context,
            userId: String?,
            avatar: String?
        ) {
            val intent = getIntent(context, userId, avatar)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(intent)
        }
    }
}