package com.hahalolo.messager.presentation.main.contacts

import com.hahalolo.messager.presentation.main.contacts.detail.ChatContactDetailFr
import com.hahalolo.messager.presentation.main.contacts.detail.PersonalDetailPopup
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatContactsModule {
    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    abstract fun chatContactsDetailBottomSheet(): PersonalDetailPopup

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    abstract fun chatContactDetailFr(): ChatContactDetailFr
}