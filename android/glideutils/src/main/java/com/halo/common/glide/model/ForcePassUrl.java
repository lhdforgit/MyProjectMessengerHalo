/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.glide.model;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;

import java.net.URL;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * force download url
 */

public class ForcePassUrl extends GlideUrl {
    public ForcePassUrl(URL url) {
        super(url);
    }

    public ForcePassUrl(String url) {
        super(url);
    }

    public ForcePassUrl(URL url, Headers headers) {
        super(url, headers);
    }

    public ForcePassUrl(String url, Headers headers) {
        super(url, headers);
    }
}
