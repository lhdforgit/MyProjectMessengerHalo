/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.repository

import com.halo.widget.repository.sticker.StickerRepository
import com.halo.widget.repository.sticker.StickerRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 5/16/18.
 */
@Module
abstract class StickerRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindsStickerRepository(repository: StickerRepositoryImpl?): StickerRepository?
}