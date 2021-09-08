/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.response;

import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.Meta;
import com.giphy.sdk.core.models.Pagination;

import java.util.List;

public class ListMediaResponse implements GenericResponse {
    private List<Media> data;
    public Pagination pagination;
    public Meta meta;

    public List<Media> getData() {
        return data;
    }

    public void setData(List<Media> data) {
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
