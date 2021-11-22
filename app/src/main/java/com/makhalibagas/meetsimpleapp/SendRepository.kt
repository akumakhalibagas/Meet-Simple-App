package com.makhalibagas.meetsimpleapp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.await

class SendRepository(private val firebaseService: FirebaseService) {

    fun sendRemoteMessage(remoteMessageBody: String): LiveData<String> {
        val result = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resultMessage =
                    firebaseService.sendRemoteMessage(getRemoteMessageHeaders(), remoteMessageBody)
                        .await()
                result.postValue(resultMessage)
                Log.d("resultsendmsg", resultMessage)
            } catch (e: Exception) {
            }
        }

        Log.d("resultsend", result.toString())
        return result
    }
}