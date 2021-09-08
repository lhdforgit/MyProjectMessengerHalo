/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;

import com.giphy.sdk.core.models.BottleData;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApi;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.engine.ApiException;
import com.giphy.sdk.core.network.response.MediaResponse;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GifByIdTest {
    GPHApiClient imp;

    @Before
    public void setUp() throws Exception {
        imp = new GPHApiClient("dc6zaTOxFJmzC");
    }

    /**
     * Test if gif is returned using id
     *
     * @throws Exception
     */
    @Test
    public void testBase() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.gifById("darAMUceRAs0w", new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(MediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue("darAMUceRAs0w".equals(result.getData().getId()));
                Assert.assertTrue(result.getData().getType() == MediaType.gif);
                Assert.assertTrue("tesla GIF".equals(result.getData().getTitle()));
                Assert.assertNotNull(result.getData().getId());

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if gif is returned using id
     *
     * @throws Exception
     */
    @Test
    public void testGifNotFound() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.gifById("darAMUceRAs0w_ttttttttt", new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(MediaResponse result, Throwable e) {
                Assert.assertNull(result);
                Assert.assertNotNull(e);
                Assert.assertTrue(e instanceof ApiException);
                Assert.assertNotNull(((ApiException) e).getErrorResponse());
                Assert.assertNotNull(((ApiException) e).getErrorResponse().getMeta());
                Assert.assertEquals(((ApiException) e).getErrorResponse().getMeta().getStatus(), HttpURLConnection.HTTP_NOT_FOUND);

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

        imp.gifById("darAMUceRAs0w", new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(MediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getData());

                Assert.assertNotNull(result.getMeta());
                Assert.assertTrue(result.getMeta().getStatus() == 200);
                Assert.assertEquals(result.getMeta().getMsg(), "OK");
                Assert.assertNotNull(result.getMeta().getResponseId());

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test invalid api key
     *
     * @throws Exception
     */
    @Test
    public void testInvalidApiKey() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        final GPHApi client = new GPHApiClient("invalid_api_key");
        client.gifById("darAMUceRAs0w", new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(MediaResponse result, Throwable e) {
                Assert.assertNull(result);
                Assert.assertNotNull(e);
                Assert.assertTrue(e instanceof ApiException);
                Assert.assertNotNull(((ApiException) e).getErrorResponse());
                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test bottle_data
     *
     * @throws Exception
     */
    @Test
    public void testBottleData() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.gifById("darAMUceRAs0w", new CompletionHandler<MediaResponse>() {
            @Override
            public void onComplete(MediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getData());

                Assert.assertNotNull(result.getMeta());
                Assert.assertTrue(result.getMeta().getStatus() == 200);
                Assert.assertEquals(result.getMeta().getMsg(), "OK");
                Assert.assertNotNull(result.getMeta().getResponseId());

                BottleData bottleData = new BottleData();
                List<String> tags = new ArrayList<>();
                tags.add("https://test1.giphy.com");
                tags.add("https://test2.giphy.com");
                tags.add("https://test3.giphy.com");
                bottleData.setTid("testtid");
                bottleData.setTags(tags);
                result.getData().setBottleData(bottleData);

                final String str1 = Utils.GSON_INSTANCE_TEST.toJson(result.getData());
                final Media obj1 = Utils.GSON_INSTANCE_TEST.fromJson(str1, Media.class);
                final String str2 = Utils.GSON_INSTANCE_TEST.toJson(obj1);
                Assert.assertEquals(str1, str2);
                Assert.assertNotNull(obj1.getBottleData());
                Assert.assertEquals(obj1.getBottleData().getTid(), bottleData.getTid());

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }
}
