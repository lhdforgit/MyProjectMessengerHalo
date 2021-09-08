/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle.manager;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.halo.common.androidlifecycle.FragmentEvent;
import com.halo.common.androidlifecycle.InitSate;
import com.halo.common.androidlifecycle.lifecycle.FragmentLifeCycle;
import com.halo.common.androidlifecycle.lifecycle.LifeCycle;
import com.halo.common.androidlifecycle.lifecycle.LifeCycleListener;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * manage lifecycle listener for {@link FragmentEvent}
 */

public class FragmentLifeCycleManager implements LifeCycleManager {
    private final FragmentLifeCycle fragmentLifeCycle = new FragmentLifeCycle();

    @NonNull
    @Override
    public LifeCycle getLifeCycle() {
        return fragmentLifeCycle;
    }

    @AnyThread
    public FragmentLifeCycleManager listen(FragmentEvent fragmentEvent, LifeCycleListener listener) {
        fragmentLifeCycle.addLifeCycleListener(fragmentEvent, listener);
        return this;
    }

    @AnyThread
    public FragmentLifeCycleManager unListen(FragmentEvent fragmentEvent, LifeCycleListener listener) {
        fragmentLifeCycle.removeLifeCycleListener(fragmentEvent, listener);
        return this;
    }

    public static InitSate getParentState(Fragment fragment) {
        InitSate initState;
        if (fragment.isResumed()) {
            initState = InitSate.RESUMED;
        } else if (fragment.isVisible()) {
            initState = InitSate.STARTED;
        } else if (fragment.isAdded()) {
            initState = InitSate.CREATED;
        } else {
            initState = InitSate.NONE;
        }
        return initState;
    }

    public static InitSate getParentState(android.app.Fragment fragment) {
        InitSate initState;
        if (fragment.isResumed()) {
            initState = InitSate.RESUMED;
        } else if (fragment.isVisible()) {
            initState = InitSate.STARTED;
        } else if (fragment.isAdded()) {
            initState = InitSate.CREATED;
        } else {
            initState = InitSate.NONE;
        }
        return initState;
    }
}
