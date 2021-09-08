/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.cache.pref.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.halo.data.cache.serializer.Serializer;
import com.halo.data.entities.mongo.login.LoginEntity;
import com.halo.data.entities.mongo.token.TokenEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author ndn
 * Created by ndn
 * Created on 10/16/18.
 */
@Singleton
public class LoginPrefImpl implements LoginPref {

    private static final String LOGIN_TARGET_PREF = "login-target-pref";
    private static final String LOGIN_TARGET_PREF_KEY = "login-target-pref-key";
    private static final String TOKEN_TARGET_PREF_KEY = "login-target-pref-key";

    private SharedPreferences sharedPref;
    private Serializer serializer;

    @Inject
    LoginPrefImpl(Context context, Serializer serializer) {
        this.sharedPref = context.getSharedPreferences(LOGIN_TARGET_PREF, Context.MODE_PRIVATE);
        this.serializer = serializer;
    }

    public LoginPrefImpl(Context context) {
        this.sharedPref = context.getSharedPreferences(LOGIN_TARGET_PREF, Context.MODE_PRIVATE);
    }

    @Override
    public void insertTargetLogin(LoginEntity entity) {
        if (entity != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(LOGIN_TARGET_PREF_KEY, serializer.serialize(entity, LoginEntity.class));
            editor.apply();
        }
    }


    @Override
    public LoginEntity getTargetLogin() {
        String json = sharedPref.getString(LOGIN_TARGET_PREF_KEY, null);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        return serializer.deserialize(json, LoginEntity.class);
    }

    @Override
    public void insertTargetToken(TokenEntity entity) {
        if (entity != null) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(TOKEN_TARGET_PREF_KEY, new Gson().toJson(entity));
            editor.apply();
        }
    }


    @Override
    public TokenEntity getTargetToken() {
        String json = sharedPref.getString(TOKEN_TARGET_PREF_KEY, null);
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        return new Gson().fromJson(sharedPref.getString(TOKEN_TARGET_PREF_KEY, ""), TokenEntity.class);
    }


}
