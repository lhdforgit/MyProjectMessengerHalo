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

public class SubcategoriesTest {
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

        imp.subCategoriesForGifs("actions", null, null, null, new CompletionHandler<ListCategoryResponse>() {
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
     * Test if subcategories are returned using limit & offset
     *
     * @throws Exception
     */
    @Test
    public void testLimitOffset() throws Exception {
        final CountDownLatch lock = new CountDownLatch(2);

        imp.subCategoriesForGifs("animals", 15, 0, null, new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(final ListCategoryResponse result1, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result1);
                Assert.assertTrue(result1.getData().size() == 15);

                imp.subCategoriesForGifs("animals", 15, 5, null, new CompletionHandler<ListCategoryResponse>() {
                    @Override
                    public void onComplete(final ListCategoryResponse result2, Throwable e) {
                        Assert.assertNull(e);
                        Assert.assertNotNull(result2);
                        Assert.assertTrue(result2.getData().size() == 15);

                        Assert.assertTrue(result2.getData().get(0).getName().equals(result1.getData().get(5).getName()));
                        lock.countDown();
                    }
                });

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if subcategories returned have the proper fields
     *
     * @throws Exception
     */
    @Test
    public void testFields() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.subCategoriesForGifs("animals", 15, 0, null, new CompletionHandler<ListCategoryResponse>() {
            @Override
            public void onComplete(final ListCategoryResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertTrue(result.getData().size() == 15);

                Assert.assertNotNull(result.getData());

                for (Category category : result.getData()) {
                    Assert.assertNotNull(category.getName());
                    Assert.assertNotNull(category.getNameEncoded());
                    Assert.assertNotNull(category.getGif());
                    Assert.assertNotNull(category.getEncodedPath());
                    Assert.assertEquals(category.getEncodedPath(), "animals/" + category.getNameEncoded());
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

        imp.subCategoriesForGifs("animals", 15, 0, null, new CompletionHandler<ListCategoryResponse>() {
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
}