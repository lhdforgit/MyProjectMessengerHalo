/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.mediasend

import com.halo.di.FragmentScoped
import com.halo.editor.camera.Camera1Fragment
import com.halo.editor.camera.CameraXFragment
import com.halo.editor.mediasend.picker.MediaPickerFolderFragment
import com.halo.editor.mediasend.picker.MediaPickerItemFragment
import com.halo.editor.mediasend.send.MediaSendFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author ndn
 * Created by ndn
 * Created on 3/20/20
 * com.halo.editor.mediasend
 */
@Module
abstract class MediaSendModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun mediaSendFr(): MediaSendFragment?

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun camera1Fr(): Camera1Fragment?

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun cameraxFr(): CameraXFragment?

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun pickerFolderFr(): MediaPickerFolderFragment?

    @FragmentScoped
    @ContributesAndroidInjector
    abstract fun pickerItemFr(): MediaPickerItemFragment?
}