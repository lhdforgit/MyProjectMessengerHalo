/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.presentation

import android.app.Activity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Create by ndn
 * Create on 7/14/20
 * com.halo.presentation
 */
@Singleton
class DeeplinkManager
@Inject constructor() {

    /*Deep link Tour*/
    var idTour: String? = null

    private fun removeTourId() {
        this.idTour = null
    }

    /*Deep Links Personal Wall Detail*/
    var idPersonalWall: String? = null

    private fun removeIdPersonalWall() {
        idPersonalWall = null
    }

    /*Deep Links Page Wall Detail*/
    var idPageWall: String? = null

    fun removeIdPageWall() {
        idPageWall = null
    }

    var idPost: String? = null
    fun removePost() {
        idPost = null
    }

    fun deepLink(activity: Activity): Boolean {

        return false
    }
}