/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.response;

import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.Meta;

public class MediaResponse implements GenericResponse {
    private Media data;
    public Meta meta;

    public Media getData() {
        return data;
    }

    public void setData(Media data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
