/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.permission;

import androidx.annotation.NonNull;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author ndn
 * Created by ndn
 * Created on 5/25/18.
 */
public class Permission {

    public final String name;
    final boolean granted;
    private final boolean shouldShowRequestPermissionRationale;

    Permission(String name, boolean granted) {
        this(name, granted, false);
    }

    Permission(String name, boolean granted, boolean shouldShowRequestPermissionRationale) {
        this.name = name;
        this.granted = granted;
        this.shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale;
    }

    Permission(List<Permission> permissions) {
        name = combineName(permissions);
        granted = combineGranted(permissions);
        shouldShowRequestPermissionRationale = combineShouldShowRequestPermissionRationale(permissions);
    }

    @Override
    @SuppressWarnings("SimplifiableIfStatement")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Permission that = (Permission) o;

        if (granted != that.granted) return false;
        if (shouldShowRequestPermissionRationale != that.shouldShowRequestPermissionRationale)
            return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (granted ? 1 : 0);
        result = 31 * result + (shouldShowRequestPermissionRationale ? 1 : 0);
        return result;
    }

    @NonNull
    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", granted=" + granted +
                ", shouldShowRequestPermissionRationale=" + shouldShowRequestPermissionRationale +
                '}';
    }

    private String combineName(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .map(permission -> permission.name).collectInto(new StringBuilder(), (s, s2) -> {
                    if (s.length() == 0) {
                        s.append(s2);
                    } else {
                        s.append(", ").append(s2);
                    }
                }).blockingGet().toString();
    }

    private Boolean combineGranted(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .all(permission -> permission.granted).blockingGet();
    }

    private Boolean combineShouldShowRequestPermissionRationale(List<Permission> permissions) {
        return Observable.fromIterable(permissions)
                .any(permission -> permission.shouldShowRequestPermissionRationale).blockingGet();
    }
}
