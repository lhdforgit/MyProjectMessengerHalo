/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import io.reactivex.subjects.PublishSubject;

/**
 * @author ndn
 * Created by ndn
 * Created on 5/25/18.
 * <p>
 * Create a RxPermissions instance :
 * RxPermissions rxPermissions = new RxPermissions(this); // where this is an Activity instance
 * <p>
 * Example : request the CAMERA permission (with Retrolambda for brevity, but not required)
 * // Must be done during an initialization phase like onCreate
 * rxPermissions
 * .request(Manifest.permission.CAMERA)
 * .subscribe(granted -> {
 * if (granted) { // Always true pre-M
 * // I can control the camera now
 * } else {
 * // Oups permission denied
 * }
 * });
 * <p>
 * If multiple permissions at the same time, the result is combined :
 * rxPermissions
 * .request(Manifest.permission.CAMERA,
 * Manifest.permission.READ_PHONE_STATE)
 * .subscribe(granted -> {
 * if (granted) {
 * // All requested permissions are granted
 * } else {
 * // At least one permission is denied
 * }
 * });
 * <p>
 * You can also observe a detailed result with requestEach or ensureEach :
 * rxPermissions
 * .requestEach(Manifest.permission.CAMERA,
 * Manifest.permission.READ_PHONE_STATE)
 * .subscribe(permission -> { // will emit 2 Permission objects
 * if (permission.granted) {
 * // `permission.name` is granted !
 * } else if (permission.shouldShowRequestPermissionRationale) {
 * // Denied permission without ask never again
 * } else {
 * // Denied permission with ask never again
 * // Need to go to the settings
 * }
 * });
 * <p>
 * You can also get combined detailed result with requestEachCombined or ensureEachCombined :
 * rxPermissions
 * .requestEachCombined(Manifest.permission.CAMERA,
 * Manifest.permission.READ_PHONE_STATE)
 * .subscribe(permission -> { // will emit 1 Permission object
 * if (permission.granted) {
 * // All permissions are granted !
 * } else if (permission.shouldShowRequestPermissionRationale)
 * // At least one denied permission without ask never again
 * } else {
 * // At least one denied permission with ask never again
 * // Need to go to the settings
 * }
 * });
 */
public class RxPermissions {

    static final String TAG = "RxPermissions";
    static final Object TRIGGER = new Object();

    RxPermissionsFragment mRxPermissionsFragment;

    public RxPermissions(@NonNull Activity activity) {
        mRxPermissionsFragment = getRxPermissionsFragment(activity);
    }

    private RxPermissionsFragment getRxPermissionsFragment(Activity activity) {
        RxPermissionsFragment rxPermissionsFragment = findRxPermissionsFragment(activity);
        boolean isNewInstance = rxPermissionsFragment == null;
        if (isNewInstance) {
            rxPermissionsFragment = new RxPermissionsFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager
                    .beginTransaction()
                    .add(rxPermissionsFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return rxPermissionsFragment;
    }

    private RxPermissionsFragment findRxPermissionsFragment(Activity activity) {
        return (RxPermissionsFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    public void setLogging(boolean logging) {
        mRxPermissionsFragment.setLogging(logging);
    }

    /**
     * Map emitted items from the source observable into {@code true} if permissions in parameters
     * are granted, or {@code false} if not.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    @SuppressWarnings("WeakerAccess")
    public <T> ObservableTransformer<T, Boolean> ensure(final String... permissions) {
        return new ObservableTransformer<T, Boolean>() {
            @Override
            public ObservableSource<Boolean> apply(Observable<T> o) {
                return request(o, permissions)
                        // Transform Observable<Permission> to Observable<Boolean>
                        .buffer(permissions.length)
                        .flatMap((Function<List<Permission>, ObservableSource<Boolean>>) permissions1 -> {
                            if (permissions1.isEmpty()) {
                                // Occurs during orientation change, when the subject receives onComplete.
                                // In that case we don't want to propagate that empty list to the
                                // subscriber, only the onComplete.
                                return Observable.empty();
                            }
                            // Return true if all permissions are granted.
                            for (Permission p : permissions1) {
                                if (!p.granted) {
                                    return Observable.just(false);
                                }
                            }
                            return Observable.just(true);
                        });
            }
        };
    }

    /**
     * Map emitted items from the source observable into {@link Permission} objects for each
     * permission in parameters.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    @SuppressWarnings("WeakerAccess")
    public <T> ObservableTransformer<T, Permission> ensureEach(final String... permissions) {
        return o -> request(o, permissions);
    }

    /**
     * Map emitted items from the source observable into one combined {@link Permission} object. Only if all permissions are granted,
     * permission also will be granted. If any permission has {@code shouldShowRationale} checked, than result also has it checked.
     * <p>
     * If one or several permissions have never been requested, invoke the related framework method
     * to ask the user if he allows the permissions.
     */
    public <T> ObservableTransformer<T, Permission> ensureEachCombined(final String... permissions) {
        return o -> request(o, permissions)
                .buffer(permissions.length)
                .flatMap((Function<List<Permission>, ObservableSource<Permission>>) permissions1 -> {
                    if (permissions1.isEmpty()) {
                        return Observable.empty();
                    }
                    return Observable.just(new Permission(permissions1));
                });
    }

    /**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public Observable<Boolean> request(final String... permissions) {
        return Observable.just(TRIGGER).compose(ensure(permissions));
    }

    /**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     */
    @SuppressWarnings({"WeakerAccess", "unused"})
    public Observable<Permission> requestEach(final String... permissions) {
        return Observable.just(TRIGGER).compose(ensureEach(permissions));
    }

    /**
     * Request permissions immediately, <b>must be invoked during initialization phase
     * of your application</b>.
     */
    public Observable<Permission> requestEachCombined(final String... permissions) {
        return Observable.just(TRIGGER).compose(ensureEachCombined(permissions));
    }

    private Observable<Permission> request(final Observable<?> trigger, final String... permissions) {
        if (permissions == null || permissions.length == 0) {
            throw new IllegalArgumentException("RxPermissions.request/requestEach requires at least one input permission");
        }
        return oneOf(trigger, pending(permissions))
                .flatMap((Function<Object, Observable<Permission>>) o -> requestImplementation(permissions));
    }

    private Observable<?> pending(final String... permissions) {
        for (String p : permissions) {
            if (!mRxPermissionsFragment.containsByPermission(p)) {
                return Observable.empty();
            }
        }
        return Observable.just(TRIGGER);
    }

    private Observable<?> oneOf(Observable<?> trigger, Observable<?> pending) {
        if (trigger == null) {
            return Observable.just(TRIGGER);
        }
        return Observable.merge(trigger, pending);
    }

    @TargetApi(Build.VERSION_CODES.M)
    Observable<Permission> requestImplementation(final String... permissions) {
        List<Observable<Permission>> list = new ArrayList<>(permissions.length);
        List<String> unrequestedPermissions = new ArrayList<>();

        // In case of multiple permissions, we create an Observable for each of them.
        // At the end, the observables are combined to have a unique response.
        for (String permission : permissions) {
            mRxPermissionsFragment.log("Requesting permission " + permission);
            if (isGranted(permission)) {
                // Already granted, or not Android M
                // Return a granted Permission object.
                list.add(Observable.just(new Permission(permission, true, false)));
                continue;
            }

            if (isRevoked(permission)) {
                // Revoked by a policy, return a denied Permission object.
                list.add(Observable.just(new Permission(permission, false, false)));
                continue;
            }

            PublishSubject<Permission> subject = mRxPermissionsFragment.getSubjectByPermission(permission);
            // Create a new subject if not exists
            if (subject == null) {
                unrequestedPermissions.add(permission);
                subject = PublishSubject.create();
                mRxPermissionsFragment.setSubjectForPermission(permission, subject);
            }

            list.add(subject);
        }

        if (!unrequestedPermissions.isEmpty()) {
            String[] unrequestedPermissionsArray = unrequestedPermissions.toArray(new String[unrequestedPermissions.size()]);
            requestPermissionsFromFragment(unrequestedPermissionsArray);
        }
        return Observable.concat(Observable.fromIterable(list));
    }

    /**
     * Invokes Activity.shouldShowRequestPermissionRationale and wraps
     * the returned value in an observable.
     * <p>
     * In case of multiple permissions, only emits true if
     * Activity.shouldShowRequestPermissionRationale returned true for
     * all revoked permissions.
     * <p>
     * You shouldn't call this method if all permissions have been granted.
     * <p>
     * For SDK &lt; 23, the observable will always emit false.
     */
    @SuppressWarnings("WeakerAccess")
    public Observable<Boolean> shouldShowRequestPermissionRationale(final Activity activity, final String... permissions) {
        if (!isMarshmallow()) {
            return Observable.just(false);
        }
        return Observable.just(shouldShowRequestPermissionRationaleImplementation(activity, permissions));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean shouldShowRequestPermissionRationaleImplementation(final Activity activity, final String... permissions) {
        for (String p : permissions) {
            if (!isGranted(p) && !activity.shouldShowRequestPermissionRationale(p)) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissionsFromFragment(String[] permissions) {
        mRxPermissionsFragment.log("requestPermissionsFromFragment " + TextUtils.join(", ", permissions));
        mRxPermissionsFragment.requestPermissions(permissions);
    }

    /**
     * Returns true if the permission is already granted.
     * <p>
     * Always true if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isGranted(String permission) {
        return !isMarshmallow() || mRxPermissionsFragment.isGranted(permission);
    }

    /**
     * Returns true if the permission has been revoked by a policy.
     * <p>
     * Always false if SDK &lt; 23.
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isRevoked(String permission) {
        return isMarshmallow() && mRxPermissionsFragment.isRevoked(permission);
    }

    boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    void onRequestPermissionsResult(String permissions[], int[] grantResults) {
        mRxPermissionsFragment.onRequestPermissionsResult(permissions, grantResults, new boolean[permissions.length]);
    }
}
