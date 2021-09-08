/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.group.gallery

import com.hahalolo.messager.presentation.group.gallery.doc.ChatRoomGalleryDocFr
import com.hahalolo.messager.presentation.group.gallery.link.ChatRoomGalleryLinkFr
import com.hahalolo.messager.presentation.group.gallery.photo.ChatRoomGalleryPhotoFr
import com.halo.di.FragmentChildScoped
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ChatRoomGalleryModule {
    @FragmentChildScoped
    @ContributesAndroidInjector
    abstract fun chatRoomGalleryPhotoFr(): ChatRoomGalleryPhotoFr

    @FragmentChildScoped
    @ContributesAndroidInjector
    abstract fun chatRoomGalleryLinkFr(): ChatRoomGalleryLinkFr

    @FragmentChildScoped
    @ContributesAndroidInjector
    abstract fun chatRoomGalleryDocFr(): ChatRoomGalleryDocFr
}