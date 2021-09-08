/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.network;

import android.os.AsyncTask;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author ngannd
 * Create by ngannd on 07/12/2018
 * <p>
 * this async task tries to create a socket connection with google.com.
 * If succeeds then return true otherwise false
 */
class CheckInternetTask extends AsyncTask<Void, Void, Boolean> {

    private WeakReference<TaskFinished<Boolean>> mCallbackWeakReference;

    CheckInternetTask(TaskFinished<Boolean> callback) {
        mCallbackWeakReference = new WeakReference<>(callback);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            //parse url. if url is not parsed properly then return
            URL url;
            try {
                url = new URL("https://clients3.google.com/generate_204");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            }

            //open connection. If fails return false
            HttpURLConnection urlConnection;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            urlConnection.setRequestProperty("User-Agent", "Android");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(1500);
            urlConnection.connect();
            return urlConnection.getResponseCode() == 204 && urlConnection.getContentLength() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean isInternetAvailable) {
        TaskFinished<Boolean> callback = mCallbackWeakReference.get();
        if (callback != null) {
            callback.onTaskFinished(isInternetAvailable);
        }
    }
}
