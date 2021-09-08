/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;

import android.os.Parcel;

import com.giphy.sdk.core.models.TermSuggestion;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApi;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.response.ListTermSuggestionResponse;
import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TermSuggestionTest {
    GPHApi imp;

    @Before
    public void setUp() throws Exception {
        imp = new GPHApiClient("4OMJYpPoYwVpe");
    }

    /**
     * Test if term suggestion endpoint returns data
     *
     * @throws Exception
     */
    @Test
    public void testBase() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.termSuggestions("come", new CompletionHandler<ListTermSuggestionResponse>() {
            @Override
            public void onComplete(ListTermSuggestionResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() > 0);

                for (TermSuggestion termSuggestion : result.getData()) {
                    Assert.assertNotNull(termSuggestion.getTerm());
                }

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if parcelable is implemeted correctly for the models
     *
     * @throws Exception
     */
    @Test
    public void testParcelable() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.termSuggestions("come", new CompletionHandler<ListTermSuggestionResponse>() {
            @Override
            public void onComplete(ListTermSuggestionResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);

                Gson gson = new Gson();
                for (TermSuggestion termSuggestion : result.getData()) {
                    Parcel parcel = Parcel.obtain();
                    termSuggestion.writeToParcel(parcel, 0);
                    parcel.setDataPosition(0);
                    TermSuggestion parcelTermSuggestion = TermSuggestion.CREATOR.createFromParcel(parcel);
                    // Compare the initial object with the one obtained from parcel
                    Assert.assertEquals(gson.toJson(parcelTermSuggestion), gson.toJson(termSuggestion));
                }

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }
}
