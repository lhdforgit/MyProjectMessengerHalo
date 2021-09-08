/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.models.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;

public class BooleanDeserializer implements JsonDeserializer<Boolean> {
    public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        final JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
        if (jsonPrimitive.isBoolean()) {
            return json.getAsBoolean();
        } else if (jsonPrimitive.isNumber()) {
            return json.getAsInt() != 0;
        }
        return false;
    }
}