package com.halo.data.entities.contact

data class ContactBody(
    var aliasName: String? = null,
    var status: String =  ContactType.CONTACTED
)