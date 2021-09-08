/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */
package com.halo.widget.api

import com.halo.data.api.utils.ServiceGenerator
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author ndn
 * Created by ndn
 * Created on 5/16/18.
 */
@Module
class StickerApiModule {

    @Singleton
    @Provides
    fun bindStickerService(): StickerService {
        return ServiceGenerator.createMessService(serviceClass = StickerService::class.java)
    }

    @Singleton
    @Provides
    fun bindGifService(): GifService {
        return ServiceGenerator.createMessService(serviceClass = GifService::class.java)
    }
}