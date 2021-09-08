/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.response;

import com.giphy.sdk.core.models.Meta;
import com.giphy.sdk.core.models.Pagination;
import com.giphy.sdk.core.models.StickerPack;

import java.util.List;

public class ListStickerPacksResponse implements GenericResponse {
    private List<StickerPack> data;
    public Pagination pagination;
    public Meta meta;

    public List<StickerPack> getData() {
        return data;
    }

    public void setData(List<StickerPack> data) {
        this.data = data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
