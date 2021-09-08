/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.widget.jsoup.internal;

import java.util.Locale;

/**
 * Util methods for normalizing strings. Jsoup internal use only, please don't depend on this API.
 */
public final class Normalizer {

    public static String lowerCase(final String input) {
        return input != null ? input.toLowerCase(Locale.ENGLISH) : "";
    }

    public static String normalize(final String input) {
        return lowerCase(input).trim();
    }
}
