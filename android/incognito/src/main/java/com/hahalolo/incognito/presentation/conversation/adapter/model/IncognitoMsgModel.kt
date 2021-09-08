package com.hahalolo.incognito.presentation.conversation.adapter.model

import java.util.*

class IncognitoMsgModel(
    var data: String? = null,
    before: String?,
    after: String?
) {

    var groupType: IncognitoMsgGroupType = IncognitoMsgGroupType.ONLY

    init {
        val beforIsGr = isOfGroup(before, data)
        val afterIsGr = isOfGroup(after, data)
        if (beforIsGr && afterIsGr) {
            groupType = IncognitoMsgGroupType.CENTER
        } else if (beforIsGr) {
            groupType = IncognitoMsgGroupType.TOP
        } else if (afterIsGr) {
            groupType = IncognitoMsgGroupType.LAST
        } else {
            groupType = IncognitoMsgGroupType.ONLY
        }
    }

    private fun isOfGroup(m: String?, m2: String?): Boolean {
        if (m != null && m2 != null     // 2 tin nhanc khac null
            && isOneUserSend(m, m2)     // cung la do  1 nguoi gui
            && isMessageContent(m)     // tin nhan 2 cung la tin nhan noi dung
            && isMessageContent(m2)     // tin nhan 2 cung la tin nhan noi dung
            && isIntimeGroup(m, m2)     // 2 tin gửi ko cách nhau 1 phút
        ) {
            return true
        }
        return false
    }

    private fun isOneUserSend(m: String?, m2: String?): Boolean {
        // cùng 1 người gửi
        return false
    }

    private fun isIntimeGroup(m: String?, m2: String?): Boolean {
        // trong cùng 1 khoảng thời gian
        return false
    }

    private fun isMessageContent(m: String): Boolean {
        //Cùng là tin nhắn nội dung
        return false
    }


    fun viewType():Int {
        return 1
    }

    fun isOutComing(): Boolean{
        return false
    }

    fun dataContentsTheSame(data: String?): Boolean {
        return false
    }

    /*DATA CONTENT */
    fun id():String{
        return data?:""
    }

    fun timeId(): Long {
        val calender = Calendar.getInstance()
        calender.timeInMillis = System.currentTimeMillis() //this time
        val day = calender.get(Calendar.DAY_OF_MONTH)
        val month = calender.get(Calendar.MONTH)
        val year = calender.get(Calendar.YEAR)
        return (day * 1000000 + month * 10000 + year).toLong()
    }

    fun userId():String{
        return ""
    }

    fun messageId():String{
        return ""
    }
}