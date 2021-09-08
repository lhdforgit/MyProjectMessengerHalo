/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle.manager;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;

import com.halo.common.androidlifecycle.ActivityEvent;
import com.halo.common.androidlifecycle.lifecycle.ActivityLifeCycle;
import com.halo.common.androidlifecycle.lifecycle.LifeCycle;
import com.halo.common.androidlifecycle.lifecycle.LifeCycleListener;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * manage lifecycle listener for {@link ActivityEvent}
 */

public class ActivityLifeCycleManager implements LifeCycleManager {
    private final ActivityLifeCycle activityLifeCycle = new ActivityLifeCycle();

    @NonNull
    @Override
    public LifeCycle getLifeCycle() {
        return activityLifeCycle;
    }

    @AnyThread
    public ActivityLifeCycleManager listen(ActivityEvent activityEvent, LifeCycleListener listener) {
        activityLifeCycle.addLifeCycleListener(activityEvent, listener);
        return this;
    }

    @AnyThread
    public ActivityLifeCycleManager unListen(ActivityEvent activityEvent, LifeCycleListener listener) {
        activityLifeCycle.removeLifeCycleListener(activityEvent, listener);
        return this;
    }
}
