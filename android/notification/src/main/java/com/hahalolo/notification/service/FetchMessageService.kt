package com.hahalolo.notification.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import javax.inject.Inject

/**
 * Create by ndn
 * Create on 6/11/21
 * com.hahalolo.notification.service
 * Thực hiện hành động tải dữ liệu tin nhắn mới từ server, khi nhận được thông báo.
 */
class FetchMessageService
@Inject constructor() : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        val mess = intent?.getStringExtra(MESSAGE_DATA)
        Log.i("FetchMessageService", "onBind: $mess")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.w("FetchMessageService", "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.w("FetchMessageService", "onDestroy")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.w("FetchMessageService", "onLowMemory")
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.w("FetchMessageService", "onRebind")
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.w("FetchMessageService", "onUnbind")
        return super.onUnbind(intent)
    }

    companion object {
        private const val MESSAGE_DATA = "FetchMessageService-Data"

        fun getIntent(context: Context, message: String?): Intent {
            val intent = Intent(context, FetchMessageService::class.java)
            intent.putExtra(MESSAGE_DATA, message)
            return intent
        }
    }
}