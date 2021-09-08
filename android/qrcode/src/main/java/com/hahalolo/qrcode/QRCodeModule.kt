package com.hahalolo.qrcode

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Create by ndn
 * Create on 5/19/21
 * com.hahalolo.qrcode
 */
@Module
abstract class QRCodeModule {

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun qrCodeAct(): QRCodeAct

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun qrCodeReaderAct(): QRCodeReaderAct

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(QRCodeViewModel::class)
    abstract fun bindQRCodeViewModel(viewModel: QRCodeViewModel): ViewModel

    @Binds
    @IntoMap
    @com.halo.di.ViewModelKey(QRCodeReaderViewModel::class)
    abstract fun bindQRCodeReaderViewModel(viewModel: QRCodeReaderViewModel): ViewModel
}