/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;

import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.response.ListMediaResponse;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GifsByCategoryTest {
    GPHApiClient imp;

    @Before
    public void setUp() throws Exception {
        imp = new GPHApiClient("dc6zaTOxFJmzC");
    }

    /**
     * Test if search without params returns 25 gifs and not exception.
     *
     * @throws Exception
     */
    @Test
    public void testBase() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.gifsByCategory("animals", "dragon", null, null, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 25);

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if the 11th gif from the request with offset 0 is the same as the first gif from the
     * request with offset 10
     *
     * @throws Exception
     */
    @Test
    public void testLimitOffset() throws Exception {
        final CountDownLatch lock = new CountDownLatch(2);

        imp.gifsByCategory("animals", "cats", 20, 0, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(final ListMediaResponse result1, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result1);
                Assert.assertTrue(result1.getData().size() == 20);

                imp.gifsByCategory("animals", "cats", 20, 10, null, null, new CompletionHandler<ListMediaResponse>() {
                    @Override
                    public void onComplete(ListMediaResponse result2, Throwable e) {
                        Assert.assertNull(e);
                        Assert.assertNotNull(result2);
                        Assert.assertTrue(result2.getData().size() == 20);

                        Utils.checkOffsetWorks(result1.getData(), result2.getData(), 1);

                        lock.countDown();
                    }
                });

                lock.countDown();
            }
        });
        lock.await(Utils.MEDIUM_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if pagination is returned.
     *
     * @throws Exception
     */
    @Test
    public void testPagination() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.gifsByCategory("animals", "dragon", 13, 12, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 13);

                Assert.assertNotNull(result.getPagination());
                Assert.assertTrue(result.getPagination().getCount() == 13);
                Assert.assertTrue(result.getPagination().getOffset() == 12);
                Assert.assertTrue(result.getPagination().getTotalCount() > 100);

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if meta is returned.
     *
     * @throws Exception
     */
    @Test
    public void testMeta() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.gifsByCategory("animals", "dragon", null, null, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 25);

                Assert.assertNotNull(result.getMeta());
                Assert.assertTrue(result.getMeta().getStatus() == 200);
                Assert.assertEquals(result.getMeta().getMsg(), "OK");
                Assert.assertNotNull(result.getMeta().getResponseId());

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }
}