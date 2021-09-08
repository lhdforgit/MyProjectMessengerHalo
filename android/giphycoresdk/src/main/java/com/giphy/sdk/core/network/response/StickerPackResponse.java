/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.response;

import com.giphy.sdk.core.models.Meta;
import com.giphy.sdk.core.models.StickerPack;

public class StickerPackResponse implements GenericResponse {
    private StickerPack data;
    public Meta meta;

    public StickerPack getData() {
        return data;
    }

    public void setData(StickerPack data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
