/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle.lifecycle;

import androidx.annotation.MainThread;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * When accept lifecycle event
 */

public interface LifeCycleListener {
    @MainThread
    void accept();
}
