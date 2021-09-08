/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * View lifecycle events.
 * No onCreate event because we could only listen to context lifecycle event after create
 */
public enum ViewEvent {
    /**
     * before onStart
     */
    START,
    /**
     * after onResume
     */
    RESUME,
    /**
     * before onPause
     */
    PAUSE,
    /**
     * before onStop
     */
    STOP,
    /**
     * before onDestroy
     */
    DESTROY
}
