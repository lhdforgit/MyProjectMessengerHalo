/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

@file:Suppress("unused")

package com.hahalolo.player

import kotlin.annotation.AnnotationRetention.SOURCE

/**
 * @author ndn (2018/07/30).
 */

@Retention(SOURCE)
annotation class Draft(val message: String = "")

@Retention(SOURCE)
annotation class Experiment

@Retention(SOURCE)
annotation class Beta(val message: String = "")

@Retention(SOURCE)
annotation class Stable
