package com.hahalolo.incognito.presentation.setting.invite.adapter

import com.halo.data.entities.contact.Contact

interface IncognitoInviteContactListener {
    fun onSearch(query: String)
    fun onGetLinkInvite()
    fun onInviteFriend(contact: Contact)
    fun isInvited(contactId: String) : Boolean
}