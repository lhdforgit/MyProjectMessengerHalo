/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.engine.NetworkSession;
import com.giphy.sdk.core.network.response.GenericResponse;
import com.giphy.sdk.core.network.response.ListMediaResponse;
import com.giphy.sdk.core.threading.ApiTask;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpIntegrationTest {
    GPHApiClient imp;

    @Before
    public void setUp() throws Exception {
        imp = new GPHApiClient("dc6zaTOxFJmzC", new OkHttptNetworkSession());
    }

    /**
     * Test if trending without params returns 25 gifs and not exception.
     *
     * @throws Exception
     */
    @Test
    public void testTrending() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.trending(MediaType.gif, null, null, null, new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getData());
                Assert.assertTrue(result.getData().size() == 25);

                for (Media media : result.getData()) {
                    Assert.assertNotNull(media.getId());
                    Assert.assertNotNull(media.getImages());
                }
                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Test if search without params returns 25 gifs and not exception.
     *
     * @throws Exception
     */
    @Test
    public void testSearch() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        imp.search("hack", MediaType.gif, null, null, null, null, "test", new CompletionHandler<ListMediaResponse>() {
            @Override
            public void onComplete(ListMediaResponse result, Throwable e) {
                Assert.assertNull(e);
                Assert.assertNotNull(result);
                Assert.assertNotNull(result.getData());
                Assert.assertTrue(result.getData().size() == 25);

                for (Media media : result.getData()) {
                    Assert.assertNotNull(media.getId());
                    Assert.assertNotNull(media.getImages());
                    Assert.assertNotNull(media.getType());
                }

                lock.countDown();
            }
        });
        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    static class OkHttptNetworkSession implements NetworkSession {
        @Override
        public <T extends GenericResponse> ApiTask<T> queryStringConnection(@NonNull final Uri serverUrl,
                                                                            @NonNull final String path,
                                                                            @NonNull final String method,
                                                                            @NonNull final Class<T> responseClass,
                                                                            @Nullable final Map<String, String> queryStrings,
                                                                            @Nullable final Map<String, String> headers) {
            return new ApiTask<>(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    final Uri.Builder uriBuilder = serverUrl.buildUpon().appendEncodedPath(path);

                    if (queryStrings != null) {
                        for (Map.Entry<String, String> query : queryStrings.entrySet()) {
                            uriBuilder.appendQueryParameter(query.getKey(), query.getValue());
                        }
                    }

                    final URL url = new URL(uriBuilder.build().toString());

                    final Request.Builder requestBuilder = new Request.Builder()
                            .url(url);

                    if (headers != null) {
                        for (Map.Entry<String, String> header : headers.entrySet()) {
                            requestBuilder.addHeader(header.getKey(), header.getValue());
                        }
                    }

                    final Request request = requestBuilder.build();

                    final OkHttpClient client = new OkHttpClient();
                    final Response response = client.newCall(request).execute();
                    // Deserialize HTTP response to concrete type.

                    final ResponseBody body = response.body();
                    return Utils.GSON_INSTANCE_TEST.fromJson(body.string(), responseClass);
                }
            });
        }

        @Override
        public <T extends GenericResponse> ApiTask<T> postStringConnection(@NonNull final Uri serverUrl,
                                                                           @NonNull final String path,
                                                                           @NonNull final String method,
                                                                           @NonNull final Class<T> responseClass,
                                                                           @Nullable final Map<String, String> queryStrings,
                                                                           @Nullable final Map<String, String> headers,
                                                                           @Nullable final Object requestBody) {
            return new ApiTask<>(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    final Uri.Builder uriBuilder = serverUrl.buildUpon().appendEncodedPath(path);

                    if (queryStrings != null) {
                        for (Map.Entry<String, String> query : queryStrings.entrySet()) {
                            uriBuilder.appendQueryParameter(query.getKey(), query.getValue());
                        }
                    }

                    final URL url = new URL(uriBuilder.build().toString());

                    RequestBody requestBody1 = RequestBody.create(okhttp3.MediaType.parse("application/json"), Utils.GSON_INSTANCE_TEST.toJson(requestBody));
                    final Request.Builder requestBuilder = new Request.Builder()
                            .method(method, requestBody1)
                            .url(url);

                    if (headers != null) {
                        for (Map.Entry<String, String> header : headers.entrySet()) {
                            requestBuilder.addHeader(header.getKey(), header.getValue());
                        }
                    }

                    final Request request = requestBuilder.build();

                    final OkHttpClient client = new OkHttpClient();
                    final Response response = client.newCall(request).execute();
                    // Deserialize HTTP response to concrete type.

                    final ResponseBody body = response.body();
                    return Utils.GSON_INSTANCE_TEST.fromJson(body.string(), responseClass);
                }
            });
        }
    }
}
