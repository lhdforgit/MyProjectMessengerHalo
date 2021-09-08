/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation

import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Create by ndn
 * Create on 7/14/20
 * com.halo.presentation
 */
@Singleton
class HahaloloAppManager
@Inject constructor() {

    var dataMess: DataMess? = null

    val isOpenFromHahalolo: Boolean
        get() = dataMess != null

    /** Khi gọi từ ứng dụng khác (gọi từ Hahalolo) thì chỉ cần gọi theo Package
     * data là dữ liệu chuỗi Json
     * Intent intent = new Intent("com.hahalolo.mess");
     * intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     * intent.putExtra("DataMess","data string");
     * startActivity(intent);
     */
    fun getIntentDataFromHahaloloApp(intent: Intent?): DataMess? {
        intent?.apply {
            if (hasExtra("DataMess")) {
                kotlin.runCatching {
                    val data = getStringExtra("DataMess")
                    dataMess = Gson().fromJson(data, DataMess::class.java)
                }.getOrElse {
                    it.printStackTrace()
                }
            }
        }
        return dataMess
    }

    fun openHahaloloApp(context: Context, dataHahalolo: DataHahalolo?) {
        val intent = Intent("com.hahalolo.android.social")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        dataHahalolo?.apply {
            val data = Gson().toJson(dataHahalolo)
            intent.putExtra("HahaloloData", data)
        }
        context.startActivity(intent)
    }
}

data class DataMess(
    var userId: String? = null,
    var accessToken: String? = null,
    var roomId: String? = null,
    var avatar: String? = null,
    var name: String? = null
)

data class DataHahalolo(
    var userId: String? = null,
    var accessToken: String? = null,
    var roomId: String? = null
)