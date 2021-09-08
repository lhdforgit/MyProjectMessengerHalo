/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;

import com.giphy.sdk.core.models.enums.LangType;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.response.MediaResponse;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TranslateTest {
    GPHApiClient imp;

    @Before
    public void setUp() throws Exception {
        imp = new GPHApiClient("dc6zaTOxFJmzC");
    }

    /**
     * Test if gif is returned
     *
     * @throws Exception
     */
    @Test
    public void testBase() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.translate("hungry", MediaType.gif, null, null, new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(MediaResponse result, Throwable e) {
                lock.countDown();

                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getData());

            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if gif is returned using the rating param
     *
     * @throws Exception
     */
    @Test
    public void testRating() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.translate("hungry", MediaType.gif, RatingType.pg, null, new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(MediaResponse result, Throwable e) {
                lock.countDown();

                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getData());

            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if gif is returned using the lang param
     *
     * @throws Exception
     */
    @Test
    public void testLang() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.translate("hungry", MediaType.gif, RatingType.pg, LangType.english, new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(MediaResponse result, Throwable e) {
                lock.countDown();

                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getData());

            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if gif returned using two terms are different
     *
     * @throws Exception
     */
    @Test
    public void testTwoTerms() throws Exception {
        final CountDownLatch lock = new CountDownLatch(2);

        imp.translate("people", MediaType.gif, RatingType.y, LangType.english, new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(final MediaResponse result1, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result1);
                Assert.assertNotNull(result1.getData());

                imp.translate("cats and dogs", MediaType.gif, RatingType.y, LangType.english, new CompletionHandler<MediaResponse>() {
                    @Override
                    public void onComplete(MediaResponse result2, Throwable e) {
                        Assert.assertNull(e);
                        Assert.assertNotNull(result2);
                        Assert.assertNotNull(result2.getData());

                        Assert.assertFalse(result2.getData().getId().equals(result1.getData().getId()));

                        lock.countDown();
                    }
                });

                lock.countDown();
            }
        });
        lock.await(Utils.MEDIUM_DELAY, TimeUnit.MILLISECONDS);
    }
}
