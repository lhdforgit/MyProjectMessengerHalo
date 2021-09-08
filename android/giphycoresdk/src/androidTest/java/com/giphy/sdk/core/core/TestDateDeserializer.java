/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TestDateDeserializer implements JsonDeserializer<Date> {
    private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private final DateFormat dateFormatStories = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);

    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        Date date = null;
        try {
            date = dateFormat.parse(json.getAsJsonPrimitive().getAsString());
        } catch (Exception e) {
            try {
                date = dateFormatStories.parse(json.getAsJsonPrimitive().getAsString());
            } catch (Exception e2) {
            }
        }
        return date;
    }
}