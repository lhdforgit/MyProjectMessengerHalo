/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.halo.common.androidlifecycle.lifemap.ActivityLifecycleMap;
import com.halo.common.androidlifecycle.manager.ActivityLifeCycleManager;
import com.halo.common.androidlifecycle.manager.FragmentLifeCycleManager;
import com.halo.common.androidlifecycle.manager.ViewLifeCycleManager;
import com.halo.common.androidlifecycle.util.Util;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * A singleton to present a simple static interface for building lifecycle manager with context, activity, fragment
 */

public class AndroidLifeCycle {
    private static final String TAG = "AndroidLifeCycle";
    private static volatile AndroidLifeCycle instance;

    private AndroidLifeCycleImpl lifeCycleImpl = new AndroidLifeCycleImpl();

    /**
     * Get the singleton.
     *
     * @return the singleton
     */
    public static AndroidLifeCycle get() {
        if (instance == null) {
            synchronized (AndroidLifeCycle.class) {
                if (instance == null) {
                    instance = new AndroidLifeCycle();
                }
            }
        }
        return instance;
    }

    /**
     * Get a LifeCycleManager by passing in a context.
     *
     * @param context activity, or wrapped activity, will not be retained
     * @return A LifeCycleManager to listen
     */
    @NonNull
    public static ActivityLifeCycleManager with(@Nullable Context context) {
        Util.assertMainThread();
        if (context == null) {
            throw new IllegalArgumentException("You cannot start a load on a null Context");
        }
        if (context instanceof FragmentActivity) {
            return with((FragmentActivity) context);
        } else if (context instanceof Activity) {
            return with((Activity) context);
        } else if (context instanceof ContextWrapper) {
            return with(((ContextWrapper) context).getBaseContext());
        }

        throw new IllegalArgumentException("Illegal type of context:" + context.toString());
    }

    /**
     * Get a LifeCycleManager by passing in a {@link FragmentActivity}.
     *
     * @param activity listened activity, will not be retained
     * @return A LifeCycleManager to listen {@link ActivityEvent}
     */
    @NonNull
    public static ActivityLifeCycleManager with(@NonNull FragmentActivity activity) {
        return get().lifeCycleImpl.with(activity, ActivityLifecycleMap.getState(activity));
    }

    /**
     * Get a LifeCycleManager by passing in a {@link Activity}.
     *
     * @param activity listened activity, will not be retained
     * @return A LifeCycleManager to listen {@link ActivityEvent}
     */
    @NonNull
    public static ActivityLifeCycleManager with(@NonNull Activity activity) {
        return get().lifeCycleImpl.with(activity, ActivityLifecycleMap.getState(activity));
    }

    /**
     * Get a LifeCycleManager by passing in a {@link Fragment}.
     *
     * <p>Auto init fragment state</p>
     *
     * @param fragment listened fragment, will not be retained
     * @return A LifeCycleManager to listen {@link FragmentEvent}
     */
    @NonNull
    public static FragmentLifeCycleManager with(@NonNull Fragment fragment) {
        return get().lifeCycleImpl.with(fragment, FragmentLifeCycleManager.getParentState(fragment));
    }

    /**
     * Get a LifeCycleManager by passing in a {@link android.app.Fragment}.
     *
     * <p>Only used after JELLY_BEAN_MR1(api 17), as fragment after this could add child fragment</p>
     * <p>Auto init fragment state</p>
     *
     * @param fragment listened fragment, will not be retained
     * @return A LifeCycleManager to listen {@link FragmentEvent}
     */
    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static FragmentLifeCycleManager with(@NonNull android.app.Fragment fragment) {
        return get().lifeCycleImpl.with(fragment, FragmentLifeCycleManager.getParentState(fragment));
    }

    /**
     * Get a LifeCycleManager by passing in a {@link View}.
     *
     * <p>If {@link #bindFragment(View, Fragment)}, listen to bound fragment, else listen to {@link View#getContext()}</p>
     *
     * @param view listened view, will not be retained
     * @return LifeCycleManager to listen bound fragment or context
     */
    public static ViewLifeCycleManager with(@NonNull View view) {
        return ViewLifeCycleManager.get(view);
    }

    public static void bindFragment(View view, Fragment fragment) {
        ViewLifeCycleManager.bindFragment(view, fragment);
    }

    public static void bindFragment(View view, android.app.Fragment fragment) {
        ViewLifeCycleManager.bindFragment(view, fragment);
    }

    public static boolean loggable() {
        return AndroidLifeCycleImpl.loggable;
    }
}
