package com.makhalibagas.meetsimpleapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.makhalibagas.meetsimpleapp.databinding.ActivityIncomingBinding
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL

class IncomingActivity : AppCompatActivity() {

    private val binding: ActivityIncomingBinding by lazy {
        ActivityIncomingBinding.inflate(layoutInflater)
    }

    private val sendViewModel: SendViewModel by inject()
    private lateinit var meetingType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        meetingType = intent.getStringExtra("type")!!
        val name = intent.getStringExtra("name")

        binding.apply {
            if (meetingType == "video") {
                typecall.text = getString(R.string.videocall)
            } else {
                typecall.text = getString(R.string.audiocall)
            }

            tvName.text = name
            tvChar.text = name!!.substring(0, 1)
            accept.setOnClickListener {
                sendInvitationResponse("accept", intent.getStringExtra("inviterToken")!!)
            }
            reject.setOnClickListener {
                sendInvitationResponse("rejected", intent.getStringExtra("inviterToken")!!)
            }
        }


    }

    private fun sendInvitationResponse(type: String, receiverToken: String) {
        try {
            val tokens = JSONArray()
            tokens.put(receiverToken)
            val body = JSONObject()
            val data = JSONObject()
            data.put("type", "invitation_response")
            data.put("invitation_response", type)
            body.put("data", data)
            body.put("registration_ids", tokens)
            sendRemoteMessage(body.toString(), type)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun sendRemoteMessage(remoteMsgBody: String, type: String) {
        ApiClient.client?.create(FirebaseService::class.java)?.sendRemoteMessage(
            getRemoteMessageHeaders(), remoteMsgBody
        )?.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    if (type == "accept") {
                        try {
                            val serverURL = URL("https://meet.jit.si")
                            val builder = JitsiMeetConferenceOptions.Builder()
                            builder.setServerURL(serverURL)
                            builder.setWelcomePageEnabled(false)
                            builder.setRoom(intent.getStringExtra("meetingRoom"))

                            Log.d("meetingRoom", intent.getStringExtra("meetingRoom")!!)
                            if (meetingType == "audio") {
                                builder.setVideoMuted(true)
                            }
                            JitsiMeetActivity.launch(this@IncomingActivity, builder.build())
                            finish()
                        } catch (e: Exception) {
                            Toast.makeText(this@IncomingActivity, e.message, Toast.LENGTH_SHORT)
                                .show()
                            finish()
                        }
                    } else {
                        Toast.makeText(
                            this@IncomingActivity,
                            "Invitation Rejected",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                }
            }
        })
//        sendViewModel.sendRemoteMessage(remoteMsgBody).observe(this, {
//            Log.d("dataremotemessage", it)
//            if (it!=null){
//                if (type == "accept") {
//                    try {
//                        val serverURL = URL("https://meet.jit.si")
//                        val builder = JitsiMeetConferenceOptions.Builder()
//                        builder.setServerURL(serverURL)
//                        builder.setWelcomePageEnabled(false)
//                        builder.setRoom(intent.getStringExtra("meetingRoom"))
//
//                        Log.d("meetingRoom", intent.getStringExtra("meetingRoom")!!)
//                        if (meetingType == "audio") {
//                            builder.setVideoMuted(true)
//                        }
//                        JitsiMeetActivity.launch(this@IncomingActivity, builder.build())
//                        finish()
//                    } catch (e: Exception) {
//                        Toast.makeText(this@IncomingActivity, e.message, Toast.LENGTH_SHORT)
//                            .show()
//                        finish()
//                    }
//                } else {
//                    Toast.makeText(
//                        this@IncomingActivity,
//                        "Invitation Rejected",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                    finish()
//                }
//            }else{
//                Toast.makeText(
//                    this@IncomingActivity,
//                    "itnull",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }
//        })
    }

    private val invitationResponseReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val type = intent.getStringExtra("invitation_response")
            if (type != null) {
                if (type == "rejected") {
                    Toast.makeText(context, "Invitation Cancelled", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(
            invitationResponseReceiver,
            IntentFilter("invitation_response")
        )
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(
            invitationResponseReceiver
        )
    }
}