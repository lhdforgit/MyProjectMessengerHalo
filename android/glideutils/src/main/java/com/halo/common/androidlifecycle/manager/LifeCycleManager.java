/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle.manager;

import androidx.annotation.Nullable;

import com.halo.common.androidlifecycle.lifecycle.LifeCycle;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * manage lifecycle listener
 */

public interface LifeCycleManager {

    @Nullable
    LifeCycle getLifeCycle();
}
