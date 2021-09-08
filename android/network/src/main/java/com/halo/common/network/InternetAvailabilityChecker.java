/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.network;

import android.content.Context;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author ngannd
 * Create by ngannd on 07/12/2018
 * <p>
 * When use {@link InternetAvailabilityChecker} user must initation
 * <p>
 * Inside {@link android.app.Application} user must override
 * ----@Override public void onCreate() {
 * --------super.onCreate();
 * --------InternetAvailabilityChecker.init(this);
 * ----}
 *
 * <p>
 * When application call Low Memory, User need remove all internet connectivity listener
 * ----@Override public void onLowMemory() {
 * --------super.onLowMemory();
 * --------InternetAvailabilityChecker.getInstance().removeAllInternetConnectivityChangeListeners();
 * ----}
 * <p>
 * Inside {@link android.app.Activity} user must declare {@link InternetAvailabilityChecker}
 * and implements {@link InternetConnectivityListener}
 * <p>
 * ----public class MainActivity extends AppCompatActivity implements InternetConnectivityListener {
 * <p>
 * --------private InternetAvailabilityChecker mInternetAvailabilityChecker;
 * <p>
 * --------@Override protected void onCreate(Bundle savedInstanceState) {
 * ------------super.onCreate(savedInstanceState);
 * <p>
 * ------------mInternetAvailabilityChecker = InternetAvailabilityChecker.getInstance();
 * ------------mInternetAvailabilityChecker.addInternetConnectivityListener(this);
 * --------}
 * <p>
 * --------@Override protected void onDestroy() {
 * ------------super.onDestroy();
 * ------------mInternetAvailabilityChecker.removeInternetConnectivityChangeListener(this);
 * --------}
 * <p>
 * --------@Override public void onInternetConnectivityChanged(boolean isConnected) {
 * ------------if (isConnected) {
 * ----------------// Do something when internet connected
 * ------------} else {
 * ----------------// Do something
 * ------------}
 * --------}
 * --------}
 */
public final class InternetAvailabilityChecker implements NetworkChangeReceiver.NetworkChangeListener {

    private static final Object LOCK = new Object();
    private static volatile InternetAvailabilityChecker sInstance;

    private WeakReference<Context> mContextWeakReference;
    private List<WeakReference<InternetConnectivityListener>> mInternetConnectivityListenersWeakReferences;
    private NetworkChangeReceiver mNetworkChangeReceiver;
    private boolean mIsNetworkChangeRegistered = false;
    private boolean mIsInternetConnected = false;

    private TaskFinished<Boolean> mCheckConnectivityCallback;

    private static final String CONNECTIVITY_CHANGE_INTENT_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    private InternetAvailabilityChecker(Context context) {
        Context appContext = context.getApplicationContext();
        mContextWeakReference = new WeakReference<>(appContext);
        mInternetConnectivityListenersWeakReferences = new ArrayList<>();
    }

    /**
     * Call this function in application class to do initial setup. it returns singleton instance.
     *
     * @param context need to register for Connectivity broadcast
     * @return instance of InternetConnectivityHelper
     */
    public static InternetAvailabilityChecker init(Context context) {
        if (context == null) {
            throw new NullPointerException("context can not be null");
        }

        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new InternetAvailabilityChecker(context);
                }
            }
        }
        return sInstance;
    }

    public static InternetAvailabilityChecker getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("call init(Context) in your application class before calling getInstance()");
        }
        return sInstance;
    }

    /**
     * Sdd InternetConnectivityListener only if it's not added. It keeps a weak reference to the listener.
     * So user should have a strong reference to that listener otherwise that will be garbage collected
     */
    public void addInternetConnectivityListener(InternetConnectivityListener internetConnectivityListener) {
        if (internetConnectivityListener == null) {
            return;
        }
        mInternetConnectivityListenersWeakReferences.add(new WeakReference<>(internetConnectivityListener));
        if (mInternetConnectivityListenersWeakReferences.size() == 1) {
            registerNetworkChangeReceiver();
            return;
        }
        publishInternetAvailabilityStatus(mIsInternetConnected);
    }

    /**
     * remove the weak reference to the listener
     */
    public void removeInternetConnectivityChangeListener(InternetConnectivityListener internetConnectivityListener) {
        if (internetConnectivityListener == null) {
            return;
        }

        if (mInternetConnectivityListenersWeakReferences == null) {
            return;
        }

        Iterator<WeakReference<InternetConnectivityListener>> iterator = mInternetConnectivityListenersWeakReferences.iterator();
        while (iterator.hasNext()) {

            //if weak reference is null then remove it from iterator
            WeakReference<InternetConnectivityListener> reference = iterator.next();
            if (reference == null) {
                iterator.remove();
                continue;
            }

            //if listener referenced by this weak reference is garbage collected then remove it from iterator
            InternetConnectivityListener listener = reference.get();
            if (listener == null) {
                reference.clear();
                iterator.remove();
                continue;
            }

            //if listener to be removed is found then remove it
            if (listener == internetConnectivityListener) {
                reference.clear();
                iterator.remove();
                break;
            }
        }

        //if all listeners are removed then unregister NetworkChangeReceiver
        if (mInternetConnectivityListenersWeakReferences.size() == 0) {
            unregisterNetworkChangeReceiver();
        }
    }

    public void removeAllInternetConnectivityChangeListeners() {
        if (mInternetConnectivityListenersWeakReferences == null) {
            return;
        }

        Iterator<WeakReference<InternetConnectivityListener>> iterator = mInternetConnectivityListenersWeakReferences.iterator();
        while (iterator.hasNext()) {
            WeakReference<InternetConnectivityListener> reference = iterator.next();
            if (reference != null) {
                reference.clear();
            }
            iterator.remove();
        }
        unregisterNetworkChangeReceiver();
    }

    /**
     * registers a NetworkChangeReceiver if not registered already
     */
    private void registerNetworkChangeReceiver() {
        Context context = mContextWeakReference.get();
        if (context != null && !mIsNetworkChangeRegistered) {
            mNetworkChangeReceiver = new NetworkChangeReceiver();
            mNetworkChangeReceiver.setNetworkChangeListener(this);
            context.registerReceiver(mNetworkChangeReceiver, new IntentFilter(CONNECTIVITY_CHANGE_INTENT_ACTION));
            mIsNetworkChangeRegistered = true;
        }
    }

    /**
     * unregisters the already registered NetworkChangeReceiver
     */
    private void unregisterNetworkChangeReceiver() {
        Context context = mContextWeakReference.get();
        if (context != null && mNetworkChangeReceiver != null && mIsNetworkChangeRegistered) {
            try {
                context.unregisterReceiver(mNetworkChangeReceiver);
            } catch (IllegalArgumentException exception) {
                //consume this exception
            }
            mNetworkChangeReceiver.removeNetworkChangeListener();
        }
        mNetworkChangeReceiver = null;
        mIsNetworkChangeRegistered = false;
        mCheckConnectivityCallback = null;
    }

    @Override
    public void onNetworkChange(boolean isNetworkAvailable) {
        if (isNetworkAvailable) {
            mCheckConnectivityCallback = isInternetAvailable -> {
                mCheckConnectivityCallback = null;
                publishInternetAvailabilityStatus(isInternetAvailable);
            };
            new CheckInternetTask(mCheckConnectivityCallback).execute();
        } else {
            publishInternetAvailabilityStatus(false);
        }
    }

    private void publishInternetAvailabilityStatus(boolean isInternetAvailable) {
        mIsInternetConnected = isInternetAvailable;
        if (mInternetConnectivityListenersWeakReferences == null) {
            return;
        }

        Iterator<WeakReference<InternetConnectivityListener>> iterator = mInternetConnectivityListenersWeakReferences.iterator();
        while (iterator.hasNext()) {
            WeakReference<InternetConnectivityListener> reference = iterator.next();

            if (reference == null) {
                iterator.remove();
                continue;
            }

            InternetConnectivityListener listener = reference.get();
            if (listener == null) {
                iterator.remove();
                continue;
            }

            listener.onInternetConnectivityChanged(isInternetAvailable);
        }

        if (mInternetConnectivityListenersWeakReferences.size() == 0) {
            unregisterNetworkChangeReceiver();
        }
    }
}

