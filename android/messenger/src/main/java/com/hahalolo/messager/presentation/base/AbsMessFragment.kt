/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.hahalolo.messager.presentation.base

import android.content.Context
import android.os.Bundle
import android.view.View
import com.hahalolo.messager.MessengerController
import com.halo.common.glide.transformations.face.GlideFaceDetector
import dagger.android.support.DaggerFragment
import timber.log.Timber
import javax.inject.Inject

/**
 * @author ndn
 * Created by ndn
 * Created on 5/24/18.
 */
abstract class AbsMessFragment : DaggerFragment() {

    @Inject
    lateinit var appController: MessengerController

    val userIdToken:String by lazy {
        appController.ownerId
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Timber.i("Lifecycle ${javaClass.simpleName} Attach fragment")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.i("Lifecycle ${javaClass.simpleName} create")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.i("Lifecycle ${javaClass.simpleName} view created")
        initializeViewModel()
        initializeLayout()
    }

    override fun onStart() {
        super.onStart()
        Timber.i("Lifecycle ${javaClass.simpleName} start")
    }

    override fun onResume() {
        super.onResume()
        Timber.i("Lifecycle ${javaClass.simpleName} resume")
    }

    override fun onStop() {
        super.onStop()
        Timber.i("Lifecycle ${javaClass.simpleName} stop")
    }

    override fun onPause() {
        super.onPause()
        Timber.i("Lifecycle ${javaClass.simpleName} pause")
    }

    override fun onDestroyView() {
        invalidateLayoutOnDestroy()
        super.onDestroyView()
        Timber.i("Lifecycle ${javaClass.simpleName} destroy view")
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("Lifecycle ${javaClass.simpleName} destroy")
    }

    override fun onDetach() {
        super.onDetach()
        Timber.i("Lifecycle ${javaClass.simpleName} fragment")
    }

    /**
     * https://stackoverflow.com/questions/7575921/illegalstateexception-can-not-perform-this-action-after-onsaveinstancestate-wit
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE")
        super.onSaveInstanceState(outState)
    }

    open fun initializeViewModel() {}

    protected abstract fun initializeLayout()

    /**
     * Clear cache, Leak memory when destroy view
     */
    protected open fun invalidateLayoutOnDestroy() {
        GlideFaceDetector.releaseDetector()
    }

    open fun errorNetwork() {


    }

}