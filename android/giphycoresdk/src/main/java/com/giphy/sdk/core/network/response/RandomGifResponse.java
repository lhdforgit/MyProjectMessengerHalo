/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.response;

import com.giphy.sdk.core.models.Meta;
import com.giphy.sdk.core.models.RandomGif;

public class RandomGifResponse implements GenericResponse {
    private RandomGif data;
    private Meta meta;

    public MediaResponse toGifResponse() {
        final MediaResponse mediaResponse = new MediaResponse();
        mediaResponse.setData(data.toGif());
        mediaResponse.setMeta(meta);
        return mediaResponse;
    }

    public RandomGif getData() {
        return data;
    }

    public void setData(RandomGif data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
