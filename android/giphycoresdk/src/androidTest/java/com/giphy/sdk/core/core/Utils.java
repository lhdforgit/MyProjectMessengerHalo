/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;

import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.json.BooleanDeserializer;
import com.giphy.sdk.core.models.json.DateSerializer;
import com.giphy.sdk.core.models.json.IntDeserializer;
import com.giphy.sdk.core.models.json.MainAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import junit.framework.Assert;

import java.util.Date;
import java.util.List;

public class Utils {
    public static long SMALL_DELAY = 2000;
    public static long MEDIUM_DELAY = 3000;

    public static final Gson GSON_INSTANCE_TEST = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, new TestDateDeserializer())
            .registerTypeHierarchyAdapter(Date.class, new DateSerializer())
            .registerTypeHierarchyAdapter(boolean.class, new BooleanDeserializer())
            .registerTypeHierarchyAdapter(int.class, new IntDeserializer())
            .registerTypeAdapterFactory(new MainAdapterFactory())
            .create();

    public static void checkOffsetWorks(List<Media> result1, List<Media> result2) {
        checkOffsetWorks(result1, result2, 20);
    }

    /**
     * Test if result2 is offseted compared to result1. Because the endpoint is not deterministic,
     * we ignore the offset value
     *
     * @param result1
     * @param result2
     * @param maxLength
     * @return
     */
    public static void checkOffsetWorks(List<Media> result1, List<Media> result2, int maxLength) {
        // We first find the exact offset
        int offset = 0;
        for (int i = 0; i < result1.size(); i++) {
            if (result1.get(i).getId().equals(result2.get(0).getId())) {
                offset = i;
                break;
            }
        }
        Assert.assertTrue(offset != 0);

        // Check if all results starting from offset match with result2
        for (int i = 0; i < result2.size() && i + offset < result1.size() && i < maxLength; i++) {
            Assert.assertEquals(result1.get(i + offset).getId(), result2.get(i).getId());
        }
    }
}
