package com.makhalibagas.meetsimpleapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class SendViewModel(private val sendRepository: SendRepository) : ViewModel() {

    fun sendRemoteMessage(remoteMessageBody: String): LiveData<String> =
        sendRepository.sendRemoteMessage(remoteMessageBody)

}