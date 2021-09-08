/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle.lifemap;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import com.halo.common.androidlifecycle.InitSate;

import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * Record activity state
 */
public class ActivityLifecycleMap {
    private static AtomicBoolean sInitialized = new AtomicBoolean(false);
    private static DispatcherActivityCallback callback;

    static void init(Context context) {
        if (!sInitialized.getAndSet(true)) {
            callback = new DispatcherActivityCallback();
            ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(callback);
        }
    }

    /**
     * Get activity state
     */
    @NonNull
    public static InitSate getState(Activity activity) {
        InitSate state = null;
        if (callback != null) {
            state = callback.lifecycleMap.get(activity);
        }
        if (state == null) {
            state = InitSate.NONE;
        }
        return state;
    }

    @VisibleForTesting
    private static class DispatcherActivityCallback extends EmptyActivityLifecycleCallbacks {
        private final WeakHashMap<Activity, InitSate> lifecycleMap = new WeakHashMap<>();

        DispatcherActivityCallback() {
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            lifecycleMap.put(activity, InitSate.CREATED);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            lifecycleMap.put(activity, InitSate.STARTED);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            lifecycleMap.put(activity, InitSate.RESUMED);
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            lifecycleMap.remove(activity);
        }
    }


}
