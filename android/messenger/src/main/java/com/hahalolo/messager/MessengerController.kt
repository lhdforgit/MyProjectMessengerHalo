package com.hahalolo.messager

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.table.AttachmentTable
import com.halo.data.entities.media.MediaEntity
import java.util.*

interface MessengerController{

    /*Room Detail Callback START*/
    fun updateAvatarGroupChat(roomId: String?)

    fun updateNickNameMember(roomId: String?)

    fun createGroupChat(memberEntity: MemberEntity?)

    fun navigateGallery(roomId: String?, tabIndex: Int)

    fun addMemberToGroup(roomId: String?)

    fun viewMemberOfGroup(roomId: String?)
    /*Room Detail Callback END*/


    /*Room Messages Callback */
    fun navigateViewAvatar(targetView: ImageView?, medias: MutableList<MediaEntity>)
    fun navigateChatMessageMember(roomId: String)
    fun navigateChatMessage(userId: String, context: Context?=null )
    fun navigatePersonalWall(userId: String)
    fun navigateOpenMedia(listImage: MutableList<AttachmentTable>?, position: Int)
    fun navigateChangeNickName(roomId: String)
    fun navigateChangeGroupAvatar(roomId: String)
    fun navigateChangeGroupName(roomId: String)
    fun navigateAddMemberInGroup(roomId: String)
    fun navigateShareMedia(roomId: String)
    fun navigateTakeImageCamera(roomId: String)
    fun navigateSetting(roomId: String)
    fun navigateTakeFileExternal(roomId: String)
    fun navigateSearchMain()
    fun navigateOpenWebView(url: String)
    fun navigateDownloadFile(attachmentTable: AttachmentTable)
    /*Room Messages Callback END*/

    fun startAct(activity: Activity): Boolean

    fun navigateOpenMediaEntity(
        arrayList: ArrayList<MediaEntity>,
        i: Int,
        currentView: View? = null
    )

    fun navigateSignIn()
    fun navigateOpenAvatar(avatar: ImageView? = null)

    val oauthToken: String
    val ownerId: String
    val ownerName: String
    val ownerAvatar: String

    fun appLang(): String
    fun getFileSize(uri: Uri, context: Context): Long

    //Friend
    fun navigateAppSetting()
}