/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;

import android.os.Bundle;
import android.os.Parcel;

import com.giphy.sdk.core.BuildConfig;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.LangType;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.models.enums.RatingType;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.response.ListMediaResponse;
import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by bogdantmm on 4/21/17.
 */

public class SearchTest {
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

        imp.search("hack", MediaType.gif, null, null, null, null, null, new CompletionHandler<ListMediaResponse>() {
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
     * Test a search that has no results.
     *
     * @throws Exception
     */
    @Test
    public void testNoResults() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.search("jjhjhhjhhhjjhhh", MediaType.gif, null, null, null, null, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 0);

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if limit returns the exact amount of gifs
     *
     * @throws Exception
     */
    @Test
    public void testLimit() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.search("cats", MediaType.gif, 13, null, null, null, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 13);

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if rating returns gifs
     *
     * @throws Exception
     */
    @Test
    public void testRating() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.search("cats", MediaType.gif, 20, null, RatingType.pg, null, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 20);

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if rating returns gifs
     *
     * @throws Exception
     */
    @Test
    public void testRatingY() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.search("cars", MediaType.gif, 20, null, RatingType.y, null, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 20);

                Assert.assertTrue(result.getData().get(0).getRating() == RatingType.y);

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if rating 'unrated' returns gifs
     *
     * @throws Exception
     */
    @Test
    public void testRatingUnrated() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.search("cars", MediaType.gif, 20, null, RatingType.unrated, null, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 20);

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if rating 'nsfw' returns gifs
     *
     * @throws Exception
     */
    @Test
    public void testRatingNsfw() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.search("cars", MediaType.gif, 20, null, RatingType.nsfw, null, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 20);

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if languages returns gifs
     *
     * @throws Exception
     */
    @Test
    public void testLang() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.search("cars", MediaType.gif, 20, null, null, LangType.chineseTraditional, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 20);

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
    public void testOffset() throws Exception {
        final CountDownLatch lock = new CountDownLatch(2);

        imp.search("cats", MediaType.gif, 7, 0, RatingType.pg, null, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(final ListMediaResponse result1, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result1);
                Assert.assertTrue(result1.getData().size() == 7);

                imp.search("cats", MediaType.gif, 6, 4, RatingType.pg, null, "test", new CompletionHandler<ListMediaResponse>() {
                    @Override
                    public void onComplete(ListMediaResponse result2, Throwable e) {
                        Assert.assertNull(e);
                        Assert.assertNotNull(result2);
                        Assert.assertTrue(result2.getData().size() == 6);

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

        imp.search("hack", MediaType.gif, 13, 12, null, null, "test", new CompletionHandler<ListMediaResponse>() {
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

        imp.search("test", MediaType.gif, null, null, null, null, null, new CompletionHandler<ListMediaResponse>() {
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

    /**
     * Test cancelation
     *
     * @throws Exception
     */
    @Test
    public void testCancelation() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        final Future task = imp.search("hack", MediaType.gif, null, null, null, null, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                // Ignore this test on travis
                if (!BuildConfig.TRAVIS) {
                    // If we get here, the test will fail, since it wasn't properly canceled
                    Assert.assertTrue(false);
                }

                lock.countDown();
            }
        });
        // Cancel imediately
        task.cancel(true);

        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test cancelation with some delay
     *
     * @throws Exception
     */
    @Test
    public void testCancelationWithDelay() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        final Future task = imp.search("hack", MediaType.gif, 2, null, null, null, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                // If we get here, the test will fail, since it wasn't properly canceled
                Assert.assertTrue(false);

                lock.countDown();
            }
        });

        // Cancel after a small period of time. Enough for the network request to start, but less
        // than it takes to complete
        lock.await(15, TimeUnit.MILLISECONDS);
        task.cancel(true);

        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if images have width & height & frames
     *
     * @throws Exception
     */
    @Test
    public void testWidthHeight() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.search("hack", MediaType.gif, null, null, null, null, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 25);

                for (Media media : result.getData()) {
                    Assert.assertTrue(media.getImages().getOriginal().getHeight() > 0);
                    Assert.assertTrue(media.getImages().getOriginal().getWidth() > 0);
                    Assert.assertTrue(media.getImages().getOriginal().getFrames() > 0);
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

        imp.search("hack", MediaType.gif, 7, null, null, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 7);

                for (Media media : result.getData()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("test", "test2");
                    media.setUserDictionary(bundle);
                }

                Gson gson = new Gson();
                for (Media media : result.getData()) {
                    Parcel parcel = Parcel.obtain();
                    media.writeToParcel(parcel, 0);
                    parcel.setDataPosition(0);
                    Media parcelMedia = Media.CREATOR.createFromParcel(parcel);
                    // Compare the initial object with the one obtained from parcel
                    Assert.assertEquals(gson.toJson(parcelMedia), gson.toJson(media));
                    Assert.assertNotNull(media.getUserDictionary());
                    Assert.assertEquals(media.getUserDictionary().getString("test"), "test2");
                }

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if json serialization&deserialization is implemeted correctly for the models
     *
     * @throws Exception
     */
    @Test
    public void testJson() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.search("hack", MediaType.gif, 7, null, null, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 7);

                for (Media media : result.getData()) {
                    final String str1 = Utils.GSON_INSTANCE_TEST.toJson(media);
                    final Media obj1 = Utils.GSON_INSTANCE_TEST.fromJson(str1, Media.class);
                    final String str2 = Utils.GSON_INSTANCE_TEST.toJson(obj1);
                    Assert.assertEquals(str1, str2);
                }

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }
}