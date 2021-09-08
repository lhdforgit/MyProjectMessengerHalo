/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;

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

public class RandomTest {
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

        imp.random("cats dogs", MediaType.gif, null, new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(MediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getData());

                lock.countDown();

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

        imp.random("cats dogs", MediaType.gif, RatingType.pg, new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(MediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getData());

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test gif fields
     *
     * @throws Exception
     */
    @Test
    public void testFields() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.random("cats dogs", MediaType.gif, null, new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(MediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);

                Assert.assertNotNull(result.getData());
                Assert.assertNotNull(result.getData().getId());

                Assert.assertNotNull(result.getData().getImages());

                Assert.assertNotNull(result.getData().getImages().getOriginal());
                Assert.assertNotNull(result.getData().getImages().getOriginal().getGifUrl());

                Assert.assertNotNull(result.getData().getImages().getFixedHeightDownsampled());
                Assert.assertNotNull(result.getData().getImages().getFixedHeightDownsampled().getGifUrl());

                Assert.assertNotNull(result.getData().getImages().getFixedWidthDownsampled());
                Assert.assertNotNull(result.getData().getImages().getFixedWidthDownsampled().getGifUrl());

                Assert.assertNotNull(result.getData().getImages().getFixedHeightSmall());
                Assert.assertNotNull(result.getData().getImages().getFixedHeightSmall().getGifUrl());

                Assert.assertNotNull(result.getData().getImages().getFixedWidthSmall());
                Assert.assertNotNull(result.getData().getImages().getFixedWidthSmall().getGifUrl());

                Assert.assertNotNull(result.getData().getImages().getFixedHeightSmallStill());
                Assert.assertNotNull(result.getData().getImages().getFixedHeightSmallStill().getGifUrl());

                Assert.assertNotNull(result.getData().getImages().getFixedWidthSmallStill());
                Assert.assertNotNull(result.getData().getImages().getFixedWidthSmallStill().getGifUrl());

                lock.countDown();

            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if 2 consecutive requests return different gifs
     *
     * @throws Exception
     */
    @Test
    public void testDifferentResults() throws Exception {
        final CountDownLatch lock = new CountDownLatch(2);

        imp.random("cats dogs", MediaType.gif, RatingType.pg, new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(final MediaResponse result1, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result1);
                Assert.assertNotNull(result1.getData());

                imp.random("cats dogs", MediaType.gif, RatingType.pg, new CompletionHandler<MediaResponse>() {
                    @Override
                    public void onComplete(MediaResponse result2, Throwable e) {
                        Assert.assertNull(e);
                        Assert.assertNotNull(result2);
                        Assert.assertNotNull(result2.getData());

                        // The two gifs should be different
                        Assert.assertFalse(result1.getData().getId().equals(result2.getData().getId()));
                        lock.countDown();
                    }
                });
                lock.countDown();

            }
        });
        lock.await(Utils.MEDIUM_DELAY, TimeUnit.MILLISECONDS);
    }
}
