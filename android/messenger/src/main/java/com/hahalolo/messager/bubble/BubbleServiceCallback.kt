package com.hahalolo.messager.bubble

import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.table.AttachmentTable
import com.halo.data.entities.media.MediaEntity
import kotlin.reflect.KClass

interface BubbleServiceCallback {
    fun collapse()

    fun getLifecycle(): LifecycleOwner

    fun getViewModelStoreOwner(): ViewModelStoreOwner

    fun <T : Any> createViewModel(kClass: KClass<T>): Any?

    /*Room Detail Callback */
    fun updateAvatarGroupChat(roomId: String?)

    fun createGroupWith(memberEntity: MemberEntity?, tag : Int)

    fun navigateGallery(roomId: String?, tabIndex: Int)
    /*Room Detail Callback END*/


    /*Room Messages Callback */
    fun navigateViewAvatar(targetView: ImageView?, medias: MutableList<MediaEntity>)
    fun navigateChatMessageMember(roomId: String, tag : Int?)
    fun navigateChatMessage(userId: String)
    fun navigatePersonalWall(userId: String)
    fun navigateOpenMedia(listImage: MutableList<AttachmentTable>?, position: Int)
    fun navigateChangeNickName(roomId: String, tag : Int)
    fun navigateUpdateOwnerMember(roomId: String, tag : Int)
    fun navigateChangeGroupAvatar(roomId: String)
    fun navigateChangeGroupName(roomId: String)
    fun navigateAddMemberInGroup(roomId: String, tag : Int)
    fun navigateShareMedia(roomId: String)
    fun navigateTakeImageCamera(roomId: String)
    fun navigateSetting(roomId: String)
    fun navigateTakeFileExternal(roomId: String)
    fun navigateSearchMain()
    fun navigateGalleryMedia(roomId: String, tabIndex: Int)
    fun navigateOpenWebView(url : String)
    fun navigateDownloadFile(attachmentTable: AttachmentTable)
    /*Room Messages Callback END*/

    fun getAppLang() : String

    fun getAvatar() : String

    /*Home Action*/
    fun onOpenConversation(roomId: String)
    /*Home Action End*/
}