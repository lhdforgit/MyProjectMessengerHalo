/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.giphy.sdk.core.core;


import android.os.Looper;

import com.giphy.sdk.core.models.enums.MediaType;
import com.giphy.sdk.core.network.api.CompletionHandler;
import com.giphy.sdk.core.network.api.GPHApiClient;
import com.giphy.sdk.core.network.engine.DefaultNetworkSession;
import com.giphy.sdk.core.network.response.MediaResponse;
import com.giphy.sdk.core.threading.ApiTask;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadingTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testAsync() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        new ApiTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Assert.assertTrue(Looper.getMainLooper().getThread() != Thread.currentThread());
                return "test";
            }
        }).executeAsyncTask(new CompletionHandler<String>() {
            @Override
            public void onComplete(String result, Throwable e) {
                Assert.assertTrue(Looper.getMainLooper().getThread() == Thread.currentThread());
                Assert.assertNotNull(result);
                Assert.assertNull(e);

                Assert.assertEquals(result, "test");
                lock.countDown();
            }
        });

        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testApiTaskWithCustomExecutor() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        // Execute a dummy instruction
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // Get the thread id and check if it's equal to the thread on which the
                // ApiTask is executed
                final Thread thread = Thread.currentThread();

                new ApiTask<String>(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        Assert.assertTrue(Looper.getMainLooper().getThread() != Thread.currentThread());
                        // Check if the current thread is the same as the initial thread
                        Assert.assertTrue(thread == Thread.currentThread());
                        return "test";
                    }
                }, executorService, ApiTask.getCompletionExecutor()).executeAsyncTask(new CompletionHandler<String>() {
                    @Override
                    public void onComplete(String result, Throwable e) {
                        Assert.assertTrue(Looper.getMainLooper().getThread() == Thread.currentThread());
                        Assert.assertNotNull(result);
                        Assert.assertNull(e);

                        Assert.assertEquals(result, "test");
                        lock.countDown();
                    }
                });
            }
        });

        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testApiClientWithCustomExecutor() throws Exception {
        final int iterations = 5;
        final CountDownLatch lock = new CountDownLatch(iterations);

        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        final Executor completionExecutor = ApiTask.getCompletionExecutor();
        final GPHApiClient apiClient = new GPHApiClient("dc6zaTOxFJmzC", new DefaultNetworkSession(executorService, completionExecutor));

        class A {
            public boolean triggered = false;
        }
        ;
        for (int i = 0; i < iterations; i++) {
            final A testObj = new A();
            // Job 1
            apiClient.gifById("darAMUceRAs0w", new CompletionHandler<MediaResponse>() {
                @Override
                public void onComplete(MediaResponse result, Throwable e) {
                    testObj.triggered = true;
                    junit.framework.Assert.assertNull(e);
                    junit.framework.Assert.assertNotNull(result);
                    junit.framework.Assert.assertTrue("darAMUceRAs0w".equals(result.getData().getId()));
                    junit.framework.Assert.assertTrue(result.getData().getType() == MediaType.gif);
                    junit.framework.Assert.assertTrue("tesla GIF".equals(result.getData().getTitle()));
                    junit.framework.Assert.assertNotNull(result.getData().getId());
                }
            });

            // Job 2
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    completionExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            Assert.assertTrue(testObj.triggered);
                            lock.countDown();
                        }
                    });
                }
            });
        }

        // This test passes if Job 1 is always finished before Job 2, because both run on the same
        // executor and MAIN_LOOP_HANDLER maintains the order of the callbacks.
        // This is not an ideal testcase, but should be enough to test this functionality
        lock.await(Utils.SMALL_DELAY * iterations, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testAsyncAndBackgroundCompletionExecutor() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        // Execute callbacks on a background thread
        final ExecutorService networkRequestExecutor = Executors.newSingleThreadExecutor();
        final ExecutorService completionExecutor = Executors.newSingleThreadExecutor();

        completionExecutor.execute(new Runnable() {
            @Override
            public void run() {
                // Get the thread id and check if it's equal to the thread on which the
                // completion handler is executed
                final Thread thread = Thread.currentThread();

                new ApiTask<String>(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        Assert.assertTrue(Looper.getMainLooper().getThread() != Thread.currentThread());
                        return "test 2";
                    }
                }, networkRequestExecutor, completionExecutor).executeAsyncTask(new CompletionHandler<String>() {
                    @Override
                    public void onComplete(String result, Throwable e) {
                        // This should be run on a background thread
                        Assert.assertTrue(Looper.getMainLooper().getThread() != Thread.currentThread());
                        Assert.assertNotNull(result);
                        Assert.assertNull(e);

                        Assert.assertEquals(result, "test 2");

                        // Check if the current thread is the same as the initial thread
                        Assert.assertTrue(thread == Thread.currentThread());
                        lock.countDown();
                    }
                });
            }
        });

        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testAsyncError() throws Exception {
        final CountDownLatch lock = new CountDownLatch(1);

        new ApiTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new Exception("Test error");
            }
        }).executeAsyncTask(new CompletionHandler<String>() {
            @Override
            public void onComplete(String result, Throwable e) {
                Assert.assertTrue(Looper.getMainLooper().getThread() == Thread.currentThread());
                Assert.assertNotNull(e);
                Assert.assertNull(result);

                Assert.assertEquals(e.getMessage(), "Test error");

                lock.countDown();
            }
        });

        lock.await(Utils.SMALL_DELAY, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSync() throws Exception {
        String test = new ApiTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Assert.assertTrue(Looper.getMainLooper().getThread() != Thread.currentThread());
                return "test";
            }
        }).executeImmediately();
        Assert.assertEquals(test, "test");
    }

    @Test
    public void testSyncError() throws Exception {
        try {
            String test = new ApiTask<String>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    throw new Exception("Test error");
                }
            }).executeImmediately();
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "Test error");
            return;
        }
        // If code reaches here, force fail
        Assert.assertTrue(false);
    }
}
