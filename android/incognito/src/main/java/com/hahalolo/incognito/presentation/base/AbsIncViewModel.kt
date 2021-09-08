package com.hahalolo.incognito.presentation.base

import androidx.lifecycle.ViewModel
import com.hahalolo.incognito.presentation.controller.IncognitoController
import javax.inject.Inject

abstract class AbsIncViewModel  :ViewModel(){
    @Inject
    lateinit var appController: IncognitoController


}