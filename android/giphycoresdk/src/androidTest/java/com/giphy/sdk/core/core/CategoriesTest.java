/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;

import android.os.Parcel;

import com.giphy.sdk.core.models.Category;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.response.ListCategoryResponse;
import com.google.gson.Gson;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CategoriesTest {
    GPHApiClient imp;

    @Before
    public void setUp() throws Exception {
        imp = new GPHApiClient("dc6zaTOxFJmzC");
    }

    /**
     * Test if categories are returned.
     *
     * @throws Exception
     */
    @Test
    public void testBase() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.categoriesForGifs(null, null, null, new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(ListCategoryResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 25);

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if categories are returned using limit & offset
     *
     * @throws Exception
     */
    @Test
    public void testLimitOffset() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.categoriesForGifs(15, 0, null, new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(final ListCategoryResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 15);

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if categories returned have the proper fields
     *
     * @throws Exception
     */
    @Test
    public void testFields() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.categoriesForGifs(15, 0, null, new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(final ListCategoryResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 15);

                Assert.assertNotNull(result.getData());

                for (Category category : result.getData()) {
                    Assert.assertNotNull(category.getName());
                    Assert.assertNotNull(category.getNameEncoded());
                    Assert.assertNotNull(category.getSubCategories());
                    Assert.assertNotNull(category.getGif());
                }

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if pagination is returned.
     *
     * @throws Exception
     */
    @Test
    public void testPagination() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.categoriesForGifs(null, null, null, new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(ListCategoryResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 25);

                Assert.assertNotNull(result.getPagination());
                Assert.assertTrue(result.getPagination().getTotalCount() == 25);
                Assert.assertTrue(result.getPagination().getCount() == 25);

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

        imp.categoriesForGifs(null, null, null, new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(ListCategoryResponse result, Throwable e) {
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
     * Test if categories are returned using sort
     *
     * @throws Exception
     */
    @Test
    public void testSort() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.categoriesForGifs(15, 0, "giphy", new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(final ListCategoryResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 15);

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if categories returned using two sort values are different
     *
     * @throws Exception
     */
    @Test
    public void testTwoSort() throws Exception {
        final CountDownLatch lock = new CountDownLatch(2);

        imp.categoriesForGifs(15, 0, "giphy", new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(final ListCategoryResponse result1, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result1);
                Assert.assertTrue(result1.getData().size() == 15);

                imp.categoriesForGifs(15, 0, "trending", new CompletionHandler<ListCategoryResponse>() {
                    @Override
                    public void onComplete(final ListCategoryResponse result2, Throwable e) {
                        Assert.assertNull(e);
                        Assert.assertNotNull(result2);
                        Assert.assertTrue(result2.getData().size() == 15);

                        Assert.assertTrue(!result1.getData().get(0).getName().equals(result2.getData().get(0).getName()));
                        lock.countDown();
                    }
                });

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

        imp.categoriesForGifs(15, 0, "giphy", new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(ListCategoryResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 15);

                Gson gson = new Gson();
                for (Category category : result.getData()) {
                    Parcel parcel = Parcel.obtain();
                    category.writeToParcel(parcel, 0);
                    parcel.setDataPosition(0);
                    Category parcelCategory = Category.CREATOR.createFromParcel(parcel);
                    // Compare the initial object with the one obtained from parcel
                    Assert.assertEquals(gson.toJson(parcelCategory), gson.toJson(category));
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

        imp.categoriesForGifs(15, 0, "giphy", new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(ListCategoryResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 15);

                for (Category category : result.getData()) {
                    final String str1 = Utils.GSON_INSTANCE_TEST.toJson(category);
                    final Category obj1 = Utils.GSON_INSTANCE_TEST.fromJson(str1, Category.class);
                    final String str2 = Utils.GSON_INSTANCE_TEST.toJson(obj1);
                    Assert.assertEquals(str1, str2);
                }

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }
}