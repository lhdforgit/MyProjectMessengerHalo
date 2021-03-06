/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.google.common.base.Preconditions;
import com.halo.common.androidlifecycle.manager.ActivityLifeCycleManager;
import com.halo.common.androidlifecycle.manager.FragmentLifeCycleManager;
import com.halo.common.androidlifecycle.manager.LifeCycleManagerFragment;
import com.halo.common.androidlifecycle.manager.LifeCycleManagerSupportFragment;
import com.halo.common.androidlifecycle.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * delegate of {@link AndroidLifeCycle}
 */
@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public class AndroidLifeCycleImpl implements Handler.Callback {
    private static final String TAG = "AndroidLifeCycle";
    private static final String FRAGMENT_TAG = "me.ykrank.androidlifecycle.manager";
    static boolean loggable = false;

    private static final int ID_REMOVE_FRAGMENT_MANAGER = 1;

    /**
     * Pending adds for RequestManagerFragments.
     */
    final Map<android.app.FragmentManager, LifeCycleManagerFragment> pendingLifeCycleManagerFragments =
            new HashMap<>();

    /**
     * Main thread handler to handle cleaning up pending fragment maps.
     */
    private final Handler handler = new Handler(Looper.getMainLooper(), this /* Callback */);

    @NonNull
    ActivityLifeCycleManager with(@NonNull FragmentActivity activity, InitSate initSate) {
        Util.assertMainThread();
        assertNotDestroyed(activity);
        FragmentManager fm = activity.getSupportFragmentManager();
        LifeCycleManagerSupportFragment current = getLifeCycleManagerSupportFragment(fm, null);
        ActivityLifeCycleManager lifeCycleManager = (ActivityLifeCycleManager) current.getLifeCycleManager();
        if (lifeCycleManager == null) {
            current.setInitState(initSate);
            lifeCycleManager = new ActivityLifeCycleManager();
            current.setLifeCycleManager(lifeCycleManager);
        }
        return (ActivityLifeCycleManager) Preconditions.checkNotNull(current.getLifeCycleManager());
    }

    @NonNull
    ActivityLifeCycleManager with(@NonNull Activity activity, InitSate initSate) {
        Util.assertMainThread();
        assertNotDestroyed(activity);
        android.app.FragmentManager fm = activity.getFragmentManager();

        LifeCycleManagerFragment current = getLifeCycleManagerFragment(fm, null);
        ActivityLifeCycleManager lifeCycleManager = (ActivityLifeCycleManager) current.getLifeCycleManager();
        if (lifeCycleManager == null) {
            current.setInitState(initSate);
            lifeCycleManager = new ActivityLifeCycleManager();
            current.setLifeCycleManager(lifeCycleManager);
        }
        return (ActivityLifeCycleManager) Preconditions.checkNotNull(current.getLifeCycleManager());
    }

    @NonNull
    FragmentLifeCycleManager with(@NonNull Fragment fragment, InitSate initSate) {
        if (loggable) {
        }
        Preconditions.checkNotNull(fragment.getActivity(),
                "You cannot start a load on a fragment before it is attached or after it is destroyed");
        Util.assertMainThread();
        FragmentManager fm = fragment.getChildFragmentManager();
        LifeCycleManagerSupportFragment current = getLifeCycleManagerSupportFragment(fm, fragment);
        FragmentLifeCycleManager lifeCycleManager = (FragmentLifeCycleManager) current.getLifeCycleManager();
        if (lifeCycleManager == null) {
            current.setInitState(initSate);
            lifeCycleManager = new FragmentLifeCycleManager();
            current.setLifeCycleManager(lifeCycleManager);
        }
        return (FragmentLifeCycleManager) Preconditions.checkNotNull(current.getLifeCycleManager());
    }

    @NonNull
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    FragmentLifeCycleManager with(@NonNull android.app.Fragment fragment, InitSate initSate) {
        if (loggable) {
            Log.d(TAG, "isAdded:" + fragment.isAdded() + ",isResumed:" + fragment.isResumed() + ",isVisible:" + fragment.isVisible());
        }
        Preconditions.checkNotNull(fragment.getActivity(),
                "You cannot start a load on a fragment before it is attached or after it is destroyed");
        Util.assertMainThread();
        android.app.FragmentManager fm = fragment.getChildFragmentManager();
        LifeCycleManagerFragment current = getLifeCycleManagerFragment(fm, fragment);
        FragmentLifeCycleManager lifeCycleManager = (FragmentLifeCycleManager) current.getLifeCycleManager();
        if (lifeCycleManager == null) {
            current.setInitState(initSate);
            lifeCycleManager = new FragmentLifeCycleManager();
            current.setLifeCycleManager(lifeCycleManager);
        }
        return (FragmentLifeCycleManager) Preconditions.checkNotNull(current.getLifeCycleManager());
    }

    @NonNull
    LifeCycleManagerFragment getLifeCycleManagerFragment(
            @NonNull final android.app.FragmentManager fm, @Nullable android.app.Fragment parentHint) {
        LifeCycleManagerFragment current = (LifeCycleManagerFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = pendingLifeCycleManagerFragments.get(fm);
            if (current == null) {
                current = new LifeCycleManagerFragment();
                current.setParentFragmentHint(parentHint);
                pendingLifeCycleManagerFragments.put(fm, current);
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
                handler.obtainMessage(ID_REMOVE_FRAGMENT_MANAGER, fm).sendToTarget();
            }
        }
        return current;
    }

    @NonNull
    LifeCycleManagerSupportFragment getLifeCycleManagerSupportFragment(
            @NonNull final FragmentManager fm, @Nullable Fragment parentHint) {
        LifeCycleManagerSupportFragment current = (LifeCycleManagerSupportFragment) fm.findFragmentByTag(FRAGMENT_TAG);
        if (current == null) {
            current = new LifeCycleManagerSupportFragment();
            current.setParentFragmentHint(parentHint);
            fm.beginTransaction().add(current, FRAGMENT_TAG).commitNowAllowingStateLoss();
        }
        return current;
    }

    @Override
    public boolean handleMessage(Message msg) {
        boolean handled = true;
        Object removed = null;
        Object key = null;
        switch (msg.what) {
            case ID_REMOVE_FRAGMENT_MANAGER:
                android.app.FragmentManager fm = (android.app.FragmentManager) msg.obj;
                key = fm;
                removed = pendingLifeCycleManagerFragments.remove(fm);
                break;
            default:
                handled = false;
                break;
        }
        if (handled && removed == null && Log.isLoggable(TAG, Log.WARN)) {
            Log.w(TAG, "Failed to remove expected request manager fragment, manager: " + key);
        }
        return handled;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static void assertNotDestroyed(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed()) {
            throw new IllegalArgumentException("You cannot start a load for a destroyed activity");
        }
    }
}
