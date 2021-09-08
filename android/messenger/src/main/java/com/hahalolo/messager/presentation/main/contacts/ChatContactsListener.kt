package com.hahalolo.messager.presentation.main.contacts

import com.halo.data.entities.contact.Contact

interface ChatContactsListener {
    fun detailUser(user: String?, contact: String?)
    fun onMessage(contact: Contact)
}