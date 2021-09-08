/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.models.enums;

import com.google.gson.annotations.SerializedName;

public enum RatingType {
    r("r"),
    y("y"),
    g("g"),
    pg("pg"),
    @SerializedName("pg-13")
    pg13("pg-13"),
    unrated("unrated"),
    nsfw("nsfw");

    private final String rating;

    private RatingType(String rating) {
        this.rating = rating;
    }

    public String toString() {
        return this.rating;
    }
}
