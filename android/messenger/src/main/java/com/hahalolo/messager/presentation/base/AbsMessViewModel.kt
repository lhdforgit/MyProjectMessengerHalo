package com.hahalolo.messager.presentation.base

import androidx.lifecycle.ViewModel
import com.hahalolo.messager.MessengerController
import javax.inject.Inject

abstract class AbsMessViewModel(protected var appController: MessengerController) : ViewModel() {

    fun userIdToken(): String {
        return appController.ownerId
    }

    fun token():String{
        return appController.oauthToken
    }
}