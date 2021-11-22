package com.makhalibagas.meetsimpleapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.makhalibagas.meetsimpleapp.databinding.ActivityJoinMeetBinding
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import timber.log.Timber
import java.net.URL

class JoinMeetActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences
    private val binding: ActivityJoinMeetBinding by lazy {
        ActivityJoinMeetBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferences = SharedPreferences(this)
        Timber.d("codeRoomInJoinMeet", preferences.getCodeRoom())
        binding.apply {
            tvJoin.setOnClickListener {
                if (etCoderoom.text.toString().isEmpty()) {
                    toast("Masukan kode room", this@JoinMeetActivity)
                } else {
                    startActivity(Intent(this@JoinMeetActivity, PreviewActivity::class.java))
                }
            }
            back.setOnClickListener { onBackPressed() }
        }
    }

    private fun startMeet(codeRoom: String) {
        try {
            val user = preferences.get()
            val userInfo = JitsiMeetUserInfo()
            val serverURL = URL("https://meet.jit.si")
            val builder = JitsiMeetConferenceOptions.Builder()
            userInfo.displayName = user.name
            userInfo.email = user.email
            builder.setServerURL(serverURL)
            builder.setWelcomePageEnabled(false)
            builder.setRoom(codeRoom)
            builder.setUserInfo(userInfo)
            JitsiMeetActivity.launch(this, builder.build())
            finish()
        } catch (e: Exception) {
            toast(e.message.toString(), this)
            finish()
        }
    }
}