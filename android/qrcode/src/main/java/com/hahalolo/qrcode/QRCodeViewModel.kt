package com.hahalolo.qrcode

import android.graphics.*
import androidx.lifecycle.ViewModel
import net.glxn.qrgen.android.QRCode
import javax.inject.Inject


/**
 * Create by ndn
 * Create on 5/19/21
 * com.hahalolo.qrcode
 */
class QRCodeViewModel
@Inject constructor() : ViewModel() {

    var userId: String? = null
    var avatar: String? = null

    fun genQrCode(): Bitmap? {
        return kotlin.runCatching {
            return userId?.run {
                QRCode.from(/*HaloConfig.PERSONAL_WALL_URL + */this).withSize(512, 512).bitmap()
            }
        }.getOrElse {
            it.printStackTrace()
            null
        }
    }

    fun overlay(bmp1: Bitmap?, bmp2: Bitmap?): Bitmap? {
        if (bmp1 == null || bmp2 == null) {
            return null
        }
        return kotlin.runCatching {
            val bitmap2Border = getRoundedCornerBitmap(bmp2)
            bitmap2Border?.run {
                val bitmapWithOverlay = Bitmap.createBitmap(bmp1.width, bmp1.height, bmp1.config)
                val canvas = Canvas(bitmapWithOverlay)
                canvas.drawBitmap(bmp1, Matrix(), null)
                canvas.drawBitmap(
                    bitmap2Border,
                    ((bmp1.width - bitmap2Border.width) / 2).toFloat(),
                    ((bmp1.height - bitmap2Border.height) / 2).toFloat(),
                    null
                )
                bitmapWithOverlay
            } ?: bmp1
        }.getOrElse {
            it.printStackTrace()
            bmp1
        }
    }

    private fun getRoundedCornerBitmap(bitmap: Bitmap?): Bitmap? {
        return bitmap?.run {
            return kotlin.runCatching {
                val w: Int = bitmap.width
                val h: Int = bitmap.height

                val radius = (h / 2).coerceAtMost(w / 2)
                val output = Bitmap.createBitmap(w + 8, h + 8, bitmap.config)

                val p = Paint()
                p.isAntiAlias = true

                val c = Canvas(output)
                c.drawARGB(0, 0, 0, 0)
                p.style = Paint.Style.FILL

                c.drawCircle((w / 2 + 4).toFloat(), (h / 2 + 4).toFloat(), radius.toFloat(), p)

                p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)

                c.drawBitmap(bitmap, 4.0f, 4.0f, p)
                p.xfermode = null
                p.style = Paint.Style.STROKE
                p.color = Color.WHITE
                p.strokeWidth = 4.0f
                c.drawCircle((w / 2 + 4).toFloat(), (h / 2 + 4).toFloat(), radius.toFloat(), p)
                return output
            }.getOrElse {
                it.printStackTrace()
                null
            }
        }
    }
}

