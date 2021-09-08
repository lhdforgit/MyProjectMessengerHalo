/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.network;

/**
 * @author ngannd
 * Create by ngannd on 07/12/2018
 */
interface TaskFinished<T> {
    void onTaskFinished(T data);
}