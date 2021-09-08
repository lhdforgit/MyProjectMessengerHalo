/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.editor.util;

import com.google.common.base.Optional;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Create by ndn
 * Create on 4/24/20
 * com.halo.editor.workmanager
 */
public class OptionalTypeAdapter implements JsonDeserializer<Optional<?>>, JsonSerializer<Optional<?>> {

    @Override
    public Optional deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        final Object value = context.deserialize(json, ((ParameterizedType) typeOfT).getActualTypeArguments()[0]);
        return Optional.of(value);
    }

    @Override
    public JsonElement serialize(Optional<?> src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.orNull());
    }
}
