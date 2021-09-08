/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.entities.media;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@StringDef({TypeMedia.IMG, TypeMedia.VID})
@Retention(RetentionPolicy.SOURCE)
public @interface TypeMedia {
    String IMG = "img";
    String VID = "vid";
}
