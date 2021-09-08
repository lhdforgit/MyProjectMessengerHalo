/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.entities.mongo.login;


import com.google.common.base.Strings;

/**
 * @author ndn
 * Created by ndn
 * Created on 10/24/18.
 */
public class LoginEntity {

    private String username;
    private String password;

    public LoginEntity() {
    }

    public LoginEntity(String username, String password) {
        this.username = Strings.nullToEmpty(username).trim();
        this.password = Strings.nullToEmpty(password).trim();
    }

    public String getUsername() {
        return Strings.nullToEmpty(username).trim();
    }

    public void setUsername(String username) {
        this.username = Strings.nullToEmpty(username).trim();
    }

    public String getPassword() {
        return Strings.nullToEmpty(password).trim();
    }

    public void setPassword(String password) {
        this.password = Strings.nullToEmpty(password).trim();
    }
}
