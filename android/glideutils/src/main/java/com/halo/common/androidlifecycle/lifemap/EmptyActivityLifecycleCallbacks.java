/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidlifecycle.lifemap;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * Empty impl of {@link android.app.Application.ActivityLifecycleCallbacks}
 */

class EmptyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
    EmptyActivityLifecycleCallbacks() {
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    public void onActivityStarted(Activity activity) {
    }

    public void onActivityResumed(Activity activity) {
    }

    public void onActivityPaused(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }
}
