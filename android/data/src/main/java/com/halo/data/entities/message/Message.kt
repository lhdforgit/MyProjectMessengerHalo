package com.halo.data.entities.message

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.halo.data.entities.attachment.Attachment
import com.halo.data.entities.invite.InviteInfo
import com.halo.data.entities.reaction.Reactions
import com.halo.data.room.type.AttachmentType
import com.halo.data.room.type.ContentType
import com.halo.data.room.type.MessageType

class Message(
    @SerializedName("workspaceId")
    @Expose
    var workspaceId: String? = null,

    @SerializedName("channelId")
    @Expose
    var channelId: String? = null,

    @SerializedName("messageId")
    @Expose
    var messageId: String? = null,

    @SerializedName("ref")
    @Expose
    var ref: String? = null,

    @SerializedName("attachmentType")
    @Expose
    var attachmentType: String? = null,

    @SerializedName("attachments")
    @Expose
    var attachments: MutableList<Attachment>? = null,

    @SerializedName("bucket")
    @Expose
    var bucket: Int = 0,

    @SerializedName("content")
    @Expose
    var content: String? = null,

    @SerializedName("contentType")
    @Expose
    var contentType: Any? = null,

    @SerializedName("createdAt")
    @Expose
    var createdAt: String? = null,

    @SerializedName("embed")
    @Expose
    var embed: MutableList<Embed>? = null,

    @SerializedName("hiddenBy")
    @Expose
    var hiddenBy: MutableList<String>? = null,

    @SerializedName("isThread")
    @Expose
    var isThread: Any? = null,

    @SerializedName("mentions")
    @Expose
    var mentions: MutableList<String>? = null,

    @SerializedName("messageType")
    @Expose
    var messageType: String? = null,

    @SerializedName("metadata")
    @Expose
    var metadata: Metadata? = null,

    @SerializedName("originMessage")
    @Expose
    var originMessage: Any? = null,

    @SerializedName("reactions")
    @Expose
    var reactions: Reactions? = null,

    @SerializedName("revokedAt")
    @Expose
    var revokedAt: Any? = null,

    @SerializedName("revokedBy")
    @Expose
    var revokedBy: Any? = null,

    @SerializedName("sentAt")
    @Expose
    var sentAt: String? = null,

    @SerializedName("updatedAt")
    @Expose
    var updatedAt: String? = null,

    @SerializedName("userId")
    @Expose
    var userId: String? = null,

    @SerializedName("originMessageId")
    @Expose
    var originMessageId:String?=null

) {

    var inviteInfo: InviteInfo? = null

    fun isEnableQuote(): Boolean {
        return false
    }

    fun time(): Long {
        try {
            return createdAt?.toLong() ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun timeCreate(): Long? {
        return try {
            return createdAt?.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun timeUpdate(): Long? {
        return try {
            return updatedAt?.toLong()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isMessageContent(): Boolean {
        return true
    }

    fun messageContent(): String {
        return content ?: ""
    }

    fun isRevoked(): Boolean {
        return revokedAt != null && revokedBy != null
    }

    fun isHided(ownerId: String): Boolean {
        return hiddenBy?.find { TextUtils.equals(it, ownerId) } != null
    }

    fun isInviteMsg(): Boolean {
        return (TextUtils.equals(this.messageType, MessageType.SYSTEM)
                && TextUtils.equals(this.contentType?.toString(), ContentType.NOTIFICATION)
                && TextUtils.equals(this.attachmentType, AttachmentType.LINKS)
                && inviteCode().isNotEmpty()
                )
    }

    fun inviteCode(): String {
        return attachments?.firstOrNull()?.joinCode() ?: ""
    }

    fun isOwner(ownerId: String): Boolean {
        return TextUtils.equals(ownerId, userId)
    }

    fun actorName(): String {
        return metadata?.actor?.displayName ?: ""
    }

    inner class Embed(
        private var meta: String? = null,
        var provider: String? = null,
        var url: String? = null
    ) {

        fun meta(): Meta? {
            return takeIf { !meta.isNullOrEmpty() }?.runCatching {
                Gson().fromJson(meta, Meta::class.java)
            }?.getOrNull()?.takeIf {
                !it.title.isNullOrEmpty()
                        || !it.description.isNullOrEmpty()
                        || !it.image.isNullOrEmpty()
            }
        }

        inner class Meta(
            var title: String? = null,
            var description: String? = null,
            var image: String? = null,
            var date: String? = null,
            var author_name: String? = null
        )
    }
}