/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.common.utils.ktx

import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import kotlin.reflect.KClass

/**
 * @author ndn
 * Created by ndn
 * Created on 2019-12-03
 * com.halo.presentation.utils
 */
@MainThread
inline fun <reified VM : ViewModel> AppCompatActivity.viewModels(
        noinline ownerProducer: () -> ViewModelStoreOwner = { this },
        noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = createViewModelLazy(VM::class, { ownerProducer().viewModelStore }, factoryProducer)

/**
 * Helper method for creation of [ViewModelLazy], that resolves `null` passed as [factoryProducer]
 * to default factory.
 */
@MainThread
fun <VM : ViewModel> AppCompatActivity.createViewModelLazy(
        viewModelClass: KClass<VM>,
        storeProducer: () -> ViewModelStore,
        factoryProducer: (() -> ViewModelProvider.Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: {
        defaultViewModelProviderFactory
    }
    return ViewModelLazy(viewModelClass, storeProducer, factoryPromise)
}