package com.hahalolo.call.client.activities

import com.hahalolo.call.client.fragments.AudioConversationFragment
import com.hahalolo.call.client.fragments.IncomeCallFragment
import com.hahalolo.call.client.fragments.PreviewFragment
import com.hahalolo.call.client.fragments.VideoConversationFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Create by ndn
 * Create on 6/18/21
 * com.hahalolo.call.client.activities
 */
@Module
abstract class CallFrModule {

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun audio(): AudioConversationFragment

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun income(): IncomeCallFragment

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun preview(): PreviewFragment

    @com.halo.di.FragmentScoped
    @ContributesAndroidInjector
    internal abstract fun video(): VideoConversationFragment
}