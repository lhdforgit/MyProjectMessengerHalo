/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.androidautodispose;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.halo.common.androidlifecycle.ActivityEvent;
import com.halo.common.androidlifecycle.FragmentEvent;
import com.halo.common.androidlifecycle.ViewEvent;
import com.uber.autodispose.LifecycleEndedException;
import com.uber.autodispose.LifecycleScopeProvider;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @author ndn
 * Created by ndn
 * Created on 9/13/18
 * A {@link LifecycleScopeProvider} that can provide scoping for Android {@link Fragment}, {@link Activity}, {@link View} classes.
 */
public class AndroidLifeCycleScopeProvider implements LifecycleScopeProvider<AndroidRxEvent> {

    private static final Function<AndroidRxEvent, AndroidRxEvent> CORRESPONDING_EVENTS =
            new Function<AndroidRxEvent, AndroidRxEvent>() {
                @Override
                public AndroidRxEvent apply(AndroidRxEvent lastEvent) throws Exception {
                    switch (lastEvent) {
                        case START:
                            return AndroidRxEvent.DISPOSE;
                        default:
                            throw new LifecycleEndedException("Fragment or activity is detached!");
                    }
                }
            };

    private final Observable<AndroidRxEvent> lifecycle;

    AndroidLifeCycleScopeProvider(Fragment fragment, FragmentEvent event) {
        lifecycle = new FragmentEventObservable(fragment, event);
    }

    AndroidLifeCycleScopeProvider(android.app.Fragment fragment, FragmentEvent event) {
        lifecycle = new FragmentEventObservable(fragment, event);
    }

    AndroidLifeCycleScopeProvider(Activity activity, ActivityEvent event) {
        lifecycle = new ActivityEventObservable(activity, event);
    }

    AndroidLifeCycleScopeProvider(FragmentActivity activity, ActivityEvent event) {
        lifecycle = new ActivityEventObservable(activity, event);
    }

    AndroidLifeCycleScopeProvider(Context context, ActivityEvent event) {
        lifecycle = new ActivityEventObservable(context, event);
    }

    AndroidLifeCycleScopeProvider(View view, ViewEvent event) {
        lifecycle = new ViewEventObservable(view, event);
    }

    @Override
    public Observable<AndroidRxEvent> lifecycle() {
        return lifecycle;
    }

    @Override
    public Function<AndroidRxEvent, AndroidRxEvent> correspondingEvents() {
        return CORRESPONDING_EVENTS;
    }

    @Override
    public AndroidRxEvent peekLifecycle() {
        return AndroidRxEvent.START;
    }
}
