package com.hahalolo.socket

import androidx.lifecycle.LiveData
import com.halo.data.entities.mess.oauth.MessOauth


/**
 * Create by ndn
 * Create on 5/27/21
 * com.hahalolo.socket
 */
interface SocketManager {

    //Khởi tạo socket with token Entity
    fun initSocket(userToken: MessOauth?)

    fun getSocketState(): LiveData<Int>

    //Kiểm tra và duy trì kết nối
    fun onResume()

    //Tạm dừng kết nối socket
    fun onPause()

    //Xóa thông tin và ngắt kết nối socket
    fun disconnect()
}