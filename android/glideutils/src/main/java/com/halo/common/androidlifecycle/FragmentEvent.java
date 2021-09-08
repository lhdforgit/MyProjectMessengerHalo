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
 * Fragment lifecycle events
 * No onAttach and onCreate event because we could only listen to fragment lifecycle event after fragment attach to host
 */
public enum FragmentEvent {
    /**
     * after onStart
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
     * before onDestroyView
     */
    DESTROY_VIEW,
    /**
     * before onDestroy
     */
    DESTROY
}
