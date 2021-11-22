package com.makhalibagas.meetsimpleapp

import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d("fcm", "token" + p0)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
//        Log.d("remoteMessageData", remoteMessage.data["type"]!!)
//
//        Log.d("dataaaaaa", remoteMessage.data.toString())
//        Log.d("fcm", "remote message received" + remoteMessage.notification!!.body)
//        if (remoteMessage.notification != null) {
//            Log.d("fcm", "remote message received" + remoteMessage.notification!!.body)
//        }

        Log.e("remoteMessageFrom ", "From: " + remoteMessage.from)
        val type = remoteMessage.data.get("type")
        if (type != null) {
            if (type.equals("invitation")) {
                val intent = Intent(applicationContext, IncomingActivity::class.java)
                intent.putExtra(
                    "meetingType",
                    remoteMessage.data["meetingType"]
                )
                intent.putExtra(
                    "name",
                    remoteMessage.data["name"]
                )
                intent.putExtra(
                    "email",
                    remoteMessage.data["email"]
                )
                intent.putExtra(
                    "inviterToken",
                    remoteMessage.data["inviterToken"]
                )
                intent.putExtra(
                    "meetingRoom",
                    remoteMessage.data["meetingRoom"]
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                toast(remoteMessage.data["type"]!!, applicationContext)
            } else if (type == "invitation_response") {
                val intent = Intent("invitation_response")
                intent.putExtra(
                    "invitation_response",
                    remoteMessage.data["invitation_response"]
                )
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
            } else {
                startActivity(Intent(applicationContext, IncomingActivity::class.java))
            }
        }
    }
}