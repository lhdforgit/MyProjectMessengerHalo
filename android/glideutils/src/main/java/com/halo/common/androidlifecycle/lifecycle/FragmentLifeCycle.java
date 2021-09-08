/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle.lifecycle;

import androidx.annotation.NonNull;
import androidx.collection.ArraySet;

import com.halo.common.androidlifecycle.FragmentEvent;
import com.halo.common.androidlifecycle.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * Add {@link LifeCycleListener} to attached fragment lifecycle
 */

public class FragmentLifeCycle implements LifeCycle {
    /**
     * weak LifeCycleListener hashSet
     */
    private Map<FragmentEvent, Set<LifeCycleListener>> lifeCycleListener = new HashMap<>(8);

    @Override
    public void onStart() {
        for (LifeCycleListener listener : getLifeCycleListeners(FragmentEvent.START)) {
            listener.accept();
        }
    }

    @Override
    public void onResume() {
        for (LifeCycleListener listener : getLifeCycleListeners(FragmentEvent.RESUME)) {
            listener.accept();
        }
    }

    @Override
    public void onPause() {
        for (LifeCycleListener listener : getLifeCycleListeners(FragmentEvent.PAUSE)) {
            listener.accept();
        }
    }

    @Override
    public void onStop() {
        for (LifeCycleListener listener : getLifeCycleListeners(FragmentEvent.STOP)) {
            listener.accept();
        }
    }

    @Override
    public void onDestroyView() {
        for (LifeCycleListener listener : getLifeCycleListeners(FragmentEvent.DESTROY_VIEW)) {
            listener.accept();
        }
    }

    @Override
    public void onDestroy() {
        for (LifeCycleListener listener : getLifeCycleListeners(FragmentEvent.DESTROY)) {
            listener.accept();
        }
    }

    @NonNull
    private Set<LifeCycleListener> getDirtyLifeCycleListeners(FragmentEvent fragmentEvent) {
        Set<LifeCycleListener> lifeCycleListenerSet = lifeCycleListener.get(fragmentEvent);
        if (lifeCycleListenerSet == null) {
            lifeCycleListenerSet = new ArraySet<>();
            lifeCycleListener.put(fragmentEvent, lifeCycleListenerSet);
        }
        return lifeCycleListenerSet;
    }

    /**
     * only for iterate, should not be hold by other object as LifeCycleListener in this list hold context
     */
    @NonNull
    List<LifeCycleListener> getLifeCycleListeners(FragmentEvent fragmentEvent) {
        synchronized (this) {
            return Util.getSnapshot(getDirtyLifeCycleListeners(fragmentEvent));
        }
    }

    public boolean addLifeCycleListener(FragmentEvent fragmentEvent, LifeCycleListener listener) {
        synchronized (this) {
            return getDirtyLifeCycleListeners(fragmentEvent).add(listener);
        }
    }

    public boolean removeLifeCycleListener(FragmentEvent fragmentEvent, LifeCycleListener listener) {
        synchronized (this) {
            return getDirtyLifeCycleListeners(fragmentEvent).remove(listener);
        }
    }
}
