/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.cache.database;

import android.text.TextUtils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * @author ndn
 * Created by ndn
 * Created on 6/7/18.
 */
public class TypeConverters {

    private TypeConverters() {
        throw new AssertionError("Instantiating utility class.");
    }

    @TypeConverter
    public static List<Object> stringToListObject(String value) {
        if (TextUtils.isEmpty(value)) {
            return Collections.emptyList();
        }
        try {
            Type listType = new TypeToken<List<Object>>() {
            }.getType();
            return new Gson().fromJson(value, listType);
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @TypeConverter
    public static String listObjectToString(List<Object> list) {
        if (list == null) {
            return "";
        }
        return new Gson().toJson(list);
    }
}
