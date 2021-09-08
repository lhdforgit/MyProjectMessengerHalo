/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle.lifecycle;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 */

public interface LifeCycle {
    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroyView();

    void onDestroy();
}
