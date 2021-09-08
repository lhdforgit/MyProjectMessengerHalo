package com.halo.data.entities.mess.oauth

import androidx.annotation.NonNull
import androidx.room.Entity
import com.google.gson.Gson
import com.halo.data.entities.user.User

/**
 * Create by ndn
 * Create on 6/4/21
 * com.halo.data.entities.oauth
 */
@Entity(tableName = "MessOauth", primaryKeys = ["token"])
data class MessOauth(
    @NonNull
    var token: String = "",
    var refreshToken: String? = null,
    var info: String? = null
) {
    val oauthInfo: OauthInfo?
        get() = kotlin.runCatching {
            info?.run {
                Gson().fromJson(this, OauthInfo::class.java)
            }
        }.getOrElse {
            it.printStackTrace()
            null
        }
}

data class OauthInfo(
    var idUser: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var avatar: String? = null,
    val provider: String? = null,
    val providerId: String? = null
) {
    val isIncognito: Boolean
        get() = provider == PROVIDER_MESS
    val isHahalolo: Boolean
        get() = provider == PROVIDER_HAHALOLO

    companion object {
        fun mapperOauthInfo(user: User): OauthInfo {
            return OauthInfo(
                idUser = user.userId,
                provider = user.provider,
                providerId = user.providerId,
                firstName = user.firstName,
                lastName = user.lastName,
                avatar = user.avatar
            )
        }
    }
}

const val PROVIDER_MESS = "messenger"
const val PROVIDER_HAHALOLO = "hahalolo"