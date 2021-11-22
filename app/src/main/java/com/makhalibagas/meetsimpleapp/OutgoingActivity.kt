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
import com.makhalibagas.meetsimpleapp.databinding.ActivityOutgoingBinding
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.json.JSONArray
import org.json.JSONObject
import org.koin.android.ext.android.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.URL
import java.util.*


class OutgoingActivity : AppCompatActivity() {

    private val sendViewModel: SendViewModel by inject()
    private lateinit var meetingRoom: String
    private lateinit var meetingType: String
    private val binding: ActivityOutgoingBinding by lazy {
        ActivityOutgoingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        meetingType = intent.getStringExtra("type")!!

        binding.apply {
            if (meetingType == "video") {
                typeCall.text = getString(R.string.videocall)
            } else {
                typeCall.text = getString(R.string.audiocall)
            }
        }


        val user = intent.getParcelableExtra<User>("user")
        binding.apply {
            user?.apply {
                tvName.text = name
                tvChar.text = name!!.substring(0, 1)
            }

            cancel.setOnClickListener {
                cancelInvitation(user!!.token)
            }
        }

        if (user != null) {
            initMeeting(meetingType, user.token!!)
        }

    }

    private fun initMeeting(meetingType: String, receiverToken: String) {
        try {

            val tokens = JSONArray()
            tokens.put(receiverToken)

            val body = JSONObject()
            val data = JSONObject()

            val preferences = SharedPreferences(this)
            val user = preferences.get()
            data.put("type", "invitation")
            data.put("meetingType", meetingType)
            data.put("name", user.name)
            data.put("name", user.name)
            data.put("email", user.email)
            data.put("inviterToken", user.token)
            meetingRoom = user.id!! + "-" +
                    UUID.randomUUID().toString().substring(0, 8)
            data.put("meetingRoom", meetingRoom)

            body.put("data", data)
            body.put("registration_ids", tokens)

            Log.d("invitation", body.toString())

            sendRemoteMessage(body.toString(), "invitation")

        } catch (e: Exception) {
            toast("error", this)
        }
    }

    private fun cancelInvitation(receiverToken: String?) {
        try {
            val tokens = JSONArray()
            if (receiverToken != null) {
                tokens.put(receiverToken)
            }

            val body = JSONObject()
            val data = JSONObject()
            data.put("type", "invitation_response")
            data.put(
                "invitation_response",
                "rejected"
            )

            body.put("data", data)
            body.put("registration_ids", tokens)
            sendRemoteMessage(body.toString(), "invitation_response")
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun sendRemoteMessage(remoteMsgBody: String, type: String) {
//        sendViewModel.sendRemoteMessage(remoteMsgBody).observe(this, {
//            if (it != null) {
//                Log.d("errormeeting", it.toString())
//                if (type == "invitation") {
//                    toast("Undangan dikirim", this)
//                } else if (type == "invitation_response") {
//                    toast("Undangan gagal", this)
//                    finish()
//                }
//            } else {
//                toast("Undangan gagal", this)
//                finish()
//            }
//        })

        ApiClient.client?.create(FirebaseService::class.java)?.sendRemoteMessage(
            getRemoteMessageHeaders(), remoteMsgBody
        )?.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {

                Log.d("statusResponse", response.isSuccessful.toString())
                Log.d("dataResponse", response.body().toString())
                if (response.isSuccessful) {
                    if (type == "invitation") {
                        toast("Undangan dikirim", this@OutgoingActivity)
                    } else if (type == "invitation_response") {
                        toast("Undangan gagal", this@OutgoingActivity)
                        finish()
                    }
                }
            }
        })
    }

    private val invitationResponseReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val type = intent.getStringExtra("invitation_response")
            if (type != null) {
                if (type == "accept") {
                    try {
                        val serverURL = URL("https://meet.jit.si")
                        val builder = JitsiMeetConferenceOptions.Builder()
                        builder.setServerURL(serverURL)
                        builder.setWelcomePageEnabled(false)
                        builder.setRoom(meetingRoom)
                        Log.d("meetingRoom", meetingRoom)
                        if (meetingType == "audio") {
                            builder.setVideoMuted(true)
                        }
                        JitsiMeetActivity.launch(this@OutgoingActivity, builder.build())
                        finish()
                    } catch (e: java.lang.Exception) {
                        toast(e.message.toString(), this@OutgoingActivity)
                        finish()
                    }
                } else if (type == "rejected") {
                    toast("Undangan gagal", this@OutgoingActivity)
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