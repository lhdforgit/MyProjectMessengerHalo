/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

import android.net.Uri;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Create by ndn
 * Create on 4/24/20
 * com.halo.editor.workmanager
 * <p>
 * Serialize/Deserialize Android's {@link Uri} class.
 * You can register this by
 * {@code GsonBuilder().registerTypeHierarchyAdapter(Uri.class, new UriTypeHierarchyAdapter())}.
 * <p>
 * https://gist.github.com/ypresto/3607f395ac4ef2921a8de74e9a243629
 */
public class UriTypeHierarchyAdapter implements JsonDeserializer<Uri>, JsonSerializer<Uri> {
    @Override
    public Uri deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Uri.parse(json.getAsString());
    }

    @Override
    public JsonElement serialize(Uri src, Type typeOfSrc, JsonSerializationContext context) {
        // Note that Uri is abstract class.
        return new JsonPrimitive(src.toString());
    }
}
