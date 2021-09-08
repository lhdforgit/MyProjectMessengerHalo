/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;

import android.os.Parcel;

import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.StickerPack;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.response.ListMediaResponse;
import com.giphy.sdk.core.network.response.ListStickerPacksResponse;
import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class StickerPacksTest {
    GPHApiClient imp;
    private static final String TEST_PACK_ID = "3138";

    @Before
    public void setUp() throws Exception {
        imp = new GPHApiClient("dc6zaTOxFJmzC");
    }

    /**
     * Test if there are stickers packs being returned and that the data is valid.
     *
     * @throws Exception
     */
    @Test
    public void testBase() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.stickerPacks(new CompletionHandler<ListStickerPacksResponse>() {
            @Override
            public void onComplete(ListStickerPacksResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(!result.getData().isEmpty());

                for (StickerPack stickerPack : result.getData()) {
                    Assert.assertNotNull(stickerPack.getId());
                    Assert.assertNotNull(stickerPack.getFeaturedGif());
                    Assert.assertNotNull(stickerPack.getSlug());
                    Assert.assertNotNull(stickerPack.getType());
                    Assert.assertNotNull(stickerPack.getDescription());
                    Assert.assertNotNull(stickerPack.getDisplayName());
                }

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test getting a sticker pack and then its stickers
     *
     * @throws Exception
     */
    @Test
    public void testStickersByPackId() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.stickerPacks(new CompletionHandler<ListStickerPacksResponse>() {
            @Override
            public void onComplete(ListStickerPacksResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(!result.getData().isEmpty());

                StickerPack stickerPack = result.getData().get(0);

                imp.stickersByPackId(stickerPack.getId(), null, null,
                        new CompletionHandler<ListMediaResponse>() {
                            @Override
                            public void onComplete(ListMediaResponse result, Throwable e) {
                                Assert.assertNull(e);
                                Assert.assertNotNull(result);
                                Assert.assertTrue(!result.getData().isEmpty());

                                for (Media sticker : result.getData()) {
                                    Assert.assertNotNull(sticker);
                                    Assert.assertNotNull(sticker.getId());
                                    Assert.assertNotNull(sticker.getImages());
                                }

                                lock.countDown();
                            }
                        });


            }
        });

        lock.await(Utils.MEDIUM_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test getting a sticker pack and then its stickers with a limit and offset
     *
     * @throws Exception
     */
    @Test
    public void testStickersByPackIdWithOffset() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);
        final int limitSize = 1;

        imp.stickerPacks(new CompletionHandler<ListStickerPacksResponse>() {
            @Override
            public void onComplete(ListStickerPacksResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(!result.getData().isEmpty());

                final StickerPack stickerPack = result.getData().get(0);

                imp.stickersByPackId(stickerPack.getId(), limitSize, 0,
                        new CompletionHandler<ListMediaResponse>() {
                            @Override
                            public void onComplete(ListMediaResponse result, Throwable e) {
                                Assert.assertNull(e);
                                Assert.assertNotNull(result);
                                Assert.assertTrue(!result.getData().isEmpty());
                                Assert.assertEquals(result.getData().size(), limitSize);

                                final Media loneStickerInResult = result.getData().get(0);

                                imp.stickersByPackId(stickerPack.getId(), limitSize, limitSize,
                                        new CompletionHandler<ListMediaResponse>() {
                                            @Override
                                            public void onComplete(ListMediaResponse result, Throwable e) {
                                                Assert.assertNull(e);
                                                Assert.assertNotNull(result);
                                                Assert.assertTrue(!result.getData().isEmpty());
                                                Assert.assertEquals(result.getData().size(), limitSize);

                                                Media stickerInOffsetResult = result.getData().get(0);

                                                Assert.assertNotSame(
                                                        loneStickerInResult.getId(),
                                                        stickerInOffsetResult.getId()
                                                );

                                                lock.countDown();
                                            }
                                        });
                            }
                        });
            }
        });

        lock.await(Utils.MEDIUM_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test getting a sticker pack's child
     *
     * @throws Exception
     */
    @Test
    public void testStickerChildren() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.stickerPackChildren(TEST_PACK_ID,
                new CompletionHandler<ListStickerPacksResponse>() {
                    @Override
                    public void onComplete(ListStickerPacksResponse result, Throwable e) {
                        Assert.assertNull(e);
                        Assert.assertNotNull(result);
                        Assert.assertTrue(!result.getData().isEmpty());

                        lock.countDown();
                    }
                });


        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if parcelable is implemented correctly for the models
     *
     * @throws Exception
     */
    @Test
    public void testParcelable() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.stickerPacks(new CompletionHandler<ListStickerPacksResponse>() {
            @Override
            public void onComplete(ListStickerPacksResponse result, Throwable e) {

                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(!result.getData().isEmpty());

                Gson gson = new Gson();
                for (StickerPack stickerPack : result.getData()) {
                    Parcel parcel = Parcel.obtain();
                    stickerPack.writeToParcel(parcel, 0);
                    parcel.setDataPosition(0);
                    StickerPack parcelStickerPack = StickerPack.CREATOR.createFromParcel(parcel);

                    // Compare the initial object with the one obtained from parcel
                    String expected = gson.toJson(parcelStickerPack);
                    String actual = gson.toJson(stickerPack);
                    Assert.assertEquals(expected, actual);
                }

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }
}