package com.halo.presentation.controller.module

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import com.hahalolo.messager.MessengerController
import com.hahalolo.messager.bubble.conversation.detail.friend.select.adapter.FriendSelectData
import com.hahalolo.messager.presentation.download.ChatDownloadFileAct
import com.hahalolo.messager.presentation.group.create.ChatGroupCreateAct
import com.hahalolo.messager.presentation.group.detail.ChatGroupDetailAct
import com.hahalolo.messager.presentation.group.gallery.ChatRoomGalleryAct
import com.hahalolo.messager.presentation.group.member.ChatGroupMemberAct
import com.hahalolo.messager.presentation.message.ChatMessageAct
import com.hahalolo.messager.presentation.takeImage.ChatTakeImageAct
import com.halo.data.room.entity.MemberEntity
import com.halo.data.room.table.AttachmentTable
import com.halo.common.utils.ActivityUtils
import com.halo.data.entities.media.MediaEntity
import com.halo.data.entities.media.TypeMedia
import com.halo.presentation.gallery.utils.PhotoMetadataUtils
import com.halo.presentation.mediaviewer.MediaViewerAct
import com.halo.presentation.messApplication
import com.halo.presentation.oauth
import com.halo.presentation.oauthInfo
import com.halo.presentation.search.general.SearchAct
import com.halo.presentation.setting.SettingAct
import com.halo.presentation.startapp.start.StartAct
import java.util.*
import javax.inject.Inject

class MessengerControllerImpl @Inject constructor(
    val application: Application
) : MessengerController {

    /*Room Detail Callback START*/
    override fun updateAvatarGroupChat(roomId: String?) {
        if (isStarted()) {
            ActivityUtils.startActivity(
                ChatGroupDetailAct.getIntent(
                    application,
                    roomId ?: "",
                    ChatGroupDetailAct.RoomAction.ROOM_AVATAR
                )
            )
        } else {
            restartAct(updateAvatarRoomId = roomId)
        }
    }

    override fun updateNickNameMember(roomId: String?) {
        if (isStarted()) {
            ActivityUtils.startActivity(
                ChatGroupMemberAct.getIntentNickName(
                    application, roomId
                        ?: ""
                )
            )
        } else {
            restartAct(updateNicknameRoomId = roomId)
        }
    }

    override fun createGroupChat(memberEntity: MemberEntity?) {

    }

    private fun createGroupChat(data: FriendSelectData) {
        ActivityUtils.startActivity(ChatGroupCreateAct.getIntentCreateWith(application, data))
    }

    override fun navigateGallery(roomId: String?, tabIndex: Int) {
        if (isStarted()) {
            ActivityUtils.startActivity(
                ChatRoomGalleryAct.getIntent(
                    application, roomId
                        ?: "", tabIndex
                )
            )
        } else {
            restartAct(
                galleryRoomId = roomId,
                galleryRoomTab = tabIndex
            )
        }
    }

    override fun addMemberToGroup(roomId: String?) {
        if (isStarted() && !roomId.isNullOrEmpty()) {
            ActivityUtils.startActivity(ChatGroupCreateAct.getIntentAddMember(application, roomId))
        } else {
            restartAct(addMemberToRoomId = roomId)
        }
    }

    override fun viewMemberOfGroup(roomId: String?) {
        if (isStarted()) {
            ActivityUtils.startActivity(
                ChatGroupMemberAct.getIntentMember(
                    application, roomId
                        ?: ""
                )
            )
        } else {
            restartAct(memberOfRoomId = roomId)
        }
    }

    override fun navigateViewAvatar(targetView: ImageView?, medias: MutableList<MediaEntity>) {
        if (isStarted()) {
            ActivityUtils.startActivity(
                MediaViewerAct.getIntent(
                    application,
                    medias as ArrayList<MediaEntity>, 0
                )
            )
        } else {
            restartAct(viewAvatars = medias)
        }
    }

    override fun navigateChatMessageMember(roomId: String) {
        if (isStarted()) {
            ActivityUtils.startActivity(ChatGroupMemberAct.getIntentMember(application, roomId))
        } else {
            restartAct(chatMessageRoomId = roomId)
        }
    }

    override fun navigateChatMessage(userId: String, context: Context?) {
        if (isStarted()) {
            ActivityUtils.startActivity(
                ChatMessageAct.getIntent(
                    context ?: messApplication,
                    userId
                )
            )
        } else {
            restartAct(chatMessageFriendId = userId)
        }
    }

    override fun navigatePersonalWall(userId: String) {
        if (isStarted()) {
//            ActivityUtils.startActivity(PersonalWallAct.getIntent(application, userId))
        } else {
            restartAct(personalUserId = userId)
        }
    }

    override fun navigateOpenMedia(listImage: MutableList<AttachmentTable>?, position: Int) {
        fun convertListMediaEntity(listImage: MutableList<AttachmentTable>?): List<MediaEntity> {
            val lists = ArrayList<MediaEntity>()
            listImage?.takeIf { !it.isNullOrEmpty() }?.forEach {
                val mediaEntity = MediaEntity()
                mediaEntity.id = UUID.randomUUID().toString()
                mediaEntity.thumb = it.thumbnail
                mediaEntity.path = it.getAttachmentUrl()
                mediaEntity.type = if (it.isVideo())
                    TypeMedia.VID
                else
                    TypeMedia.IMG
                lists.add(mediaEntity)
            }
            return lists
        }

        listImage?.run {
            val listData = convertListMediaEntity(this)
            if (isStarted()) {
                navigateOpenMedias(listData, position)
            } else {
                restartAct(openMedias = listData, openMediaPosition = position)
            }
        }
    }

    private fun navigateOpenMedias(listData: List<MediaEntity>, position: Int) {
        ActivityUtils.startActivity(
            MediaViewerAct.getIntent(
                application,
                listData as ArrayList<MediaEntity>,
                if (position < listData.size) position else 0
            )
        )
    }

    override fun navigateChangeNickName(roomId: String) {
        if (isStarted()) {
            ActivityUtils.startActivity(
                ChatGroupMemberAct.getIntentNickName(
                    application,
                    roomId
                )
            )
        } else {
            restartAct(changeNickNameRoomId = roomId)
        }
    }

    override fun navigateChangeGroupAvatar(roomId: String) {
        if (isStarted()) {
            val intent = ChatGroupDetailAct.getIntent(
                application,
                roomId,
                ChatGroupDetailAct.RoomAction.ROOM_AVATAR
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityUtils.startActivity(intent)
        } else {
            restartAct(changeAvatarRoomId = roomId)
        }
    }

    override fun navigateChangeGroupName(roomId: String) {
        if (isStarted()) {
            val intent = ChatGroupDetailAct.getIntent(
                application,
                roomId,
                ChatGroupDetailAct.RoomAction.ROOM_NAME
            )
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            ActivityUtils.startActivity(intent)
        } else {
            restartAct(changeNameRoomId = roomId)
        }
    }

    override fun navigateAddMemberInGroup(roomId: String) {
        if (isStarted()) {
            ActivityUtils.startActivity(ChatGroupCreateAct.getIntentAddMember(application, roomId))
        } else {
            restartAct(addMemberToRoomId = roomId)
        }
    }

    override fun navigateShareMedia(roomId: String) {
        if (isStarted()) {
            ActivityUtils.startActivity(ChatRoomGalleryAct.getIntent(application, roomId, 0))
        } else {
            restartAct(shareMediaRoomId = roomId)
        }
    }

    override fun navigateTakeImageCamera(roomId: String) {
        ActivityUtils.startActivity(ChatTakeImageAct.getIntentMsgTakeMedia(application, roomId))
    }

    override fun navigateSetting(roomId: String) {
        if (isStarted()) {
            ActivityUtils.startActivity(ChatGroupDetailAct.getIntent(application, roomId))
        } else {
            restartAct(detailRoomId = roomId)
        }
    }

    override fun navigateTakeFileExternal(roomId: String) {
        ActivityUtils.startActivity(ChatTakeImageAct.getIntentMsgTakeFile(application, roomId))
    }

    override fun navigateSearchMain() {
        if (isStarted()) {
            ActivityUtils.startActivity(SearchAct.getIntent(application))
        } else {
            restartAct(searchMain = true)
        }
    }

    override fun navigateOpenWebView(url: String) {
        kotlin.runCatching {
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            ActivityUtils.startActivity(intent)
        }
    }

    override fun navigateDownloadFile(attachmentTable: AttachmentTable) {
        kotlin.runCatching {
            if (isStarted()) {
                ActivityUtils.startActivity(
                    ChatDownloadFileAct.startDownload(
                        application,
                        attachmentTable
                    )
                )
            } else {
                restartAct(downloadFileAttachment = attachmentTable)
            }
        }
    }
    /*Room Detail Callback END*/


    /*RESTART APP*/
    private var updateAvatarRoomId: String? = null
    private var updateNicknameRoomId: String? = null
    private var createGroupChat: FriendSelectData? = null
    private var galleryRoomId: String? = null
    private var galleryRoomTab: Int? = null
    private var memberOfRoomId: String? = null
    private var viewAvatars: MutableList<MediaEntity>? = null
    private var chatMessageRoomId: String? = null
    private var chatMessageFriendId: String? = null
    private var personalUserId: String? = null
    private var openMedias: List<MediaEntity>? = null
    private var openMediaPosition: Int? = null
    private var changeNickNameRoomId: String? = null
    private var changeAvatarRoomId: String? = null
    private var changeNameRoomId: String? = null
    private var addMemberToRoomId: String? = null
    private var shareMediaRoomId: String? = null
    private var detailRoomId: String? = null
    private var searchMain: Boolean? = null
    private var downloadFileAttachment: AttachmentTable? = null

    private fun isStarted(): Boolean {
        return oauth?.token?.isNotEmpty() == true
    }

    //save id and wait for restart
    private fun restartAct(
        updateAvatarRoomId: String? = null,
        updateNicknameRoomId: String? = null,
        createGroupChat: FriendSelectData? = null,
        galleryRoomId: String? = null,
        galleryRoomTab: Int? = null,
        memberOfRoomId: String? = null,
        viewAvatars: MutableList<MediaEntity>? = null,
        chatMessageRoomId: String? = null,
        chatMessageFriendId: String? = null,
        personalUserId: String? = null,
        openMedias: List<MediaEntity>? = null,
        openMediaPosition: Int? = null,
        changeNickNameRoomId: String? = null,
        changeAvatarRoomId: String? = null,
        changeNameRoomId: String? = null,
        addMemberToRoomId: String? = null,
        shareMediaRoomId: String? = null,
        detailRoomId: String? = null,
        searchMain: Boolean? = null,
        downloadFileAttachment: AttachmentTable? = null
    ) {

        //SAVE ID
        this.updateAvatarRoomId = updateAvatarRoomId
        this.updateNicknameRoomId = updateNicknameRoomId
        this.createGroupChat = createGroupChat
        this.galleryRoomId = galleryRoomId
        this.galleryRoomTab = galleryRoomTab
        this.memberOfRoomId = memberOfRoomId
        this.viewAvatars = viewAvatars
        this.chatMessageRoomId = chatMessageRoomId
        this.chatMessageFriendId = chatMessageFriendId
        this.personalUserId = personalUserId
        this.openMedias = openMedias
        this.openMediaPosition = openMediaPosition
        this.changeNickNameRoomId = changeNickNameRoomId
        this.changeAvatarRoomId = changeAvatarRoomId
        this.changeNameRoomId = changeNameRoomId
        this.addMemberToRoomId = addMemberToRoomId
        this.shareMediaRoomId = shareMediaRoomId
        this.detailRoomId = detailRoomId
        this.searchMain = searchMain

        //RESTART APP
        ActivityUtils.startActivity(StartAct.getIntent(application).apply {
            this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        ActivityUtils.finishAllActivities()
    }

    //Start Act when app restarted
    override fun startAct(activity: Activity): Boolean {

        return this.updateAvatarRoomId?.takeIf { it.isNotEmpty() }?.run {
            updateAvatarGroupChat(this)
            true
        } ?: this.updateNicknameRoomId?.takeIf { it.isNotEmpty() }?.run {
            updateNickNameMember(this)
            true
        } ?: this.createGroupChat?.run {
            createGroupChat(this)
            true
        } ?: this.galleryRoomId?.takeIf { it.isNotEmpty() }?.run {
            navigateGallery(this, galleryRoomTab ?: 0)
            true
        } ?: this.memberOfRoomId?.takeIf { it.isNotEmpty() }?.run {
            viewMemberOfGroup(this)
            true
        } ?: this.viewAvatars?.takeIf { it.isNotEmpty() }?.run {
            navigateViewAvatar(null, this)
            true
        } ?: this.chatMessageRoomId?.takeIf { it.isNotEmpty() }?.run {
            navigateChatMessageMember(this)

            true
        } ?: this.chatMessageFriendId?.takeIf { it.isNotEmpty() }?.run {
            navigateChatMessage(this)

            true
        } ?: this.personalUserId?.takeIf { it.isNotEmpty() }?.run {
            navigatePersonalWall(this)
            true
        } ?: this.openMedias?.takeIf { it.isNotEmpty() }?.run {
            navigateOpenMedias(this, openMediaPosition ?: 0)
            true
        } ?: this.changeNickNameRoomId?.takeIf { it.isNotEmpty() }?.run {
            navigateChangeNickName(this)
            true
        } ?: this.changeAvatarRoomId?.takeIf { it.isNotEmpty() }?.run {
            navigateChangeGroupAvatar(this)
            true
        } ?: this.changeNameRoomId?.takeIf { it.isNotEmpty() }?.run {
            navigateChangeGroupName(this)
            true
        } ?: this.addMemberToRoomId?.takeIf { it.isNotEmpty() }?.run {
            addMemberToGroup(this)
            true
        } ?: this.shareMediaRoomId?.takeIf { it.isNotEmpty() }?.run {
            navigateShareMedia(this)
            true
        } ?: this.detailRoomId?.takeIf { it.isNotEmpty() }?.run {
            navigateSetting(this)
            true
        } ?: this.searchMain?.takeIf { it }?.run {
            navigateSearchMain()
            true
        } ?: this.downloadFileAttachment?.run {
            navigateDownloadFile(this)
            true
        } ?: false
    }

    override fun navigateOpenMediaEntity(
        arrayList: ArrayList<MediaEntity>,
        i: Int,
        currentView: View?
    ) {

    }

    override fun navigateSignIn() {
        ActivityUtils.startActivity(StartAct.getIntent(application).apply {
            this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        ActivityUtils.finishAllActivities()
    }

    override fun navigateOpenAvatar(avatar: ImageView?) {

    }

    override val oauthToken: String
        get() = oauth?.token?:""

    override val ownerId: String
        get() = oauthInfo?.idUser ?: ""
    override val ownerName: String
        get() = (oauthInfo?.firstName ?: "") + (oauthInfo?.lastName ?: "")
    override val ownerAvatar: String
        get() = oauthInfo?.avatar ?: ""

    override fun appLang(): String {
        return com.halo.presentation.appLang
    }

    override fun getFileSize(uri: Uri, context: Context): Long {
        return PhotoMetadataUtils.getFileSize(uri, context)
    }

    override fun navigateAppSetting() {
        SettingAct.launchSetting(application)
    }
    /*RESTART APP END*/
}
