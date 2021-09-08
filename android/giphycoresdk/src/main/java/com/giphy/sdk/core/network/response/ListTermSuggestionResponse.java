/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.response;

import com.giphy.sdk.core.models.Meta;
import com.giphy.sdk.core.models.TermSuggestion;

import java.util.List;

public class ListTermSuggestionResponse implements GenericResponse {
    private List<TermSuggestion> data;
    private Meta meta;

    public List<TermSuggestion> getData() {
        return data;
    }

    public void setData(List<TermSuggestion> data) {
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }
}
