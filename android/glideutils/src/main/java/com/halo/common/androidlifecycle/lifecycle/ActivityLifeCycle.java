/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle.lifecycle;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import com.halo.common.androidlifecycle.ActivityEvent;
import com.halo.common.androidlifecycle.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * Add {@link LifeCycleListener} to attached activity lifecycle
 */
public class ActivityLifeCycle implements LifeCycle {
    /**
     * weak LifeCycleListener hashSet
     */
    private Map<ActivityEvent, Set<LifeCycleListener>> lifeCycleListener = new HashMap<>(8);

    @Override
    public void onStart() {
        for (LifeCycleListener listener : getLifeCycleListeners(ActivityEvent.START)) {
            listener.accept();
        }
    }

    @Override
    public void onResume() {
        for (LifeCycleListener listener : getLifeCycleListeners(ActivityEvent.RESUME)) {
            listener.accept();
        }
    }

    @Override
    public void onPause() {
        for (LifeCycleListener listener : getLifeCycleListeners(ActivityEvent.PAUSE)) {
            listener.accept();
        }
    }

    @Override
    public void onStop() {
        for (LifeCycleListener listener : getLifeCycleListeners(ActivityEvent.STOP)) {
            listener.accept();
        }
    }

    @Override
    public void onDestroyView() {

    }

    @Override
    public void onDestroy() {
        for (LifeCycleListener listener : getLifeCycleListeners(ActivityEvent.DESTROY)) {
            listener.accept();
        }
        lifeCycleListener.clear();
    }

    @NonNull
    private Set<LifeCycleListener> getDirtyLifeCycleListeners(ActivityEvent activityEvent) {
        Set<LifeCycleListener> lifeCycleListenerSet = lifeCycleListener.get(activityEvent);
        if (lifeCycleListenerSet == null) {
            lifeCycleListenerSet = new ArraySet<>();
            lifeCycleListener.put(activityEvent, lifeCycleListenerSet);
        }
        return lifeCycleListenerSet;
    }

    /**
     * only for iterate, should not be hold by other object as LifeCycleListener in this list hold context
     */
    @NonNull
    List<LifeCycleListener> getLifeCycleListeners(ActivityEvent activityEvent) {
        synchronized (this) {
            return Util.getSnapshot(getDirtyLifeCycleListeners(activityEvent));
        }
    }

    @AnyThread
    public boolean addLifeCycleListener(ActivityEvent activityEvent, LifeCycleListener listener) {
        synchronized (this) {
            return getDirtyLifeCycleListeners(activityEvent).add(listener);
        }
    }

    @AnyThread
    public boolean removeLifeCycleListener(ActivityEvent activityEvent, LifeCycleListener listener) {
        synchronized (this) {
            return getDirtyLifeCycleListeners(activityEvent).remove(listener);
        }
    }
}
