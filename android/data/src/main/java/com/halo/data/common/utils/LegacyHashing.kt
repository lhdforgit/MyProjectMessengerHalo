/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.common.utils

import com.google.common.hash.HashFunction
import com.google.common.hash.Hashing

/**
 * @author ndn
 * Created by ndn
 * Created on 8/1/18
 * Provides an md5 and sha1 hash function without producing deprecation warnings.
 */
class LegacyHashing private constructor() {

    init {
        throw AssertionError("Instantiating utility class.")
    }

    companion object {

        fun md5(): HashFunction {
            return Hashing.md5()
        }

        fun sha1(): HashFunction {
            return Hashing.sha1()
        }
    }
}