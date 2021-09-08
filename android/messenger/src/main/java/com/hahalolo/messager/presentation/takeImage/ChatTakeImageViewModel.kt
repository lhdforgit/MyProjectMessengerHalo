package com.hahalolo.messager.presentation.takeImage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class ChatTakeImageViewModel@Inject
constructor() : ViewModel(){
    var roomTakeImageId  = MutableLiveData<String>()
    var roomMessageTakeMedia = MutableLiveData<String>()
    var roomMessageTakeFile = MutableLiveData<String>()

}