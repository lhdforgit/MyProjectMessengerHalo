/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.network.api;

import android.net.Uri;

public class Constants {
    public static final Uri SERVER_URL = Uri.parse("https://api.giphy.com");
    public static final Uri MOBILE_API_URL = Uri.parse("https://x.giphy.com");

    public static final String API_KEY = "api_key";

    public static class Paths {
        public static final String SEARCH = "v1/%s/search";
        public static final String TRENDING = "v1/%s/trending";
        public static final String RANDOM = "v1/%s/random";
        public static final String TRANSLATE = "v1/%s/translate";
        public static final String CATEGORIES = "v1/gifs/categories";
        public static final String SUBCATEGORIES = "v1/gifs/categories/%s";
        public static final String GIFS_BY_CATEGORY = "v1/gifs/categories/%s/%s";
        public static final String GIF_BY_ID = "v1/gifs/%s";
        public static final String GIF_BY_IDS = "v1/gifs";
        public static final String TERM_SUGGESTIONS = "v1/queries/suggest/%s";
        public static final String STICKER_PACKS = "v1/stickers/packs";
        public static final String STICKER_PACK_BY_ID = "v1/stickers/packs/%s";
        public static final String STICKER_PACK_CHILDREN = "v1/stickers/packs/%s/children";
        public static final String STICKERS_BY_PACK_ID = "v1/stickers/packs/%s/stickers";
        public static final String GIFS_BY_CHANNEL_ID = "v1/channels/%s/%s";
    }
}
