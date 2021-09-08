package com.hahalolo.call.client

import com.hahalolo.call.client.activities.CallActivity
import com.hahalolo.call.client.activities.CallFrModule
import com.hahalolo.call.client.activities.PermissionsActivity
import com.hahalolo.call.client.services.CallService
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Create by ndn
 * Create on 6/18/21
 * com.hahalolo.call.client
 */
@Module
abstract class CallModule {

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector(modules = [CallFrModule::class])
    abstract fun callAct(): CallActivity

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun permissionAct(): PermissionsActivity

    @com.halo.di.ActivityScoped
    @ContributesAndroidInjector
    abstract fun callService(): CallService
}