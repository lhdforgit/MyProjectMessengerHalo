/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.cache.pref.login;

import androidx.annotation.Nullable;

import com.halo.data.entities.mongo.login.LoginEntity;
import com.halo.data.entities.mongo.token.TokenEntity;

/**
 * @author ndn
 * Created by ndn
 * Created on 10/24/18.
 */
public interface LoginPref {

    /**
     * Insert target login to pref
     *
     * @param entity {@link com.halo.data.entities.mongo.login.LoginEntity} target login
     */
    void insertTargetLogin(LoginEntity entity);

    /**
     * Get target login from pref
     *
     * @return entity {@link com.halo.data.entities.mongo.login.LoginEntity} target login
     */
    @Nullable
    LoginEntity getTargetLogin();

    void insertTargetToken(TokenEntity tokenEntity);


    TokenEntity getTargetToken();
}
