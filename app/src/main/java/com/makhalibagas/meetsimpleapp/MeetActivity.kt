package com.makhalibagas.meetsimpleapp

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.makhalibagas.meetsimpleapp.databinding.ActivityMeetBinding
import com.makhalibagas.meetsimpleapp.databinding.DialogBottomBinding
import com.makhalibagas.meetsimpleapp.databinding.DialogLinkmeetBinding
import org.jitsi.meet.sdk.JitsiMeetActivity
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions
import org.jitsi.meet.sdk.JitsiMeetUserInfo
import timber.log.Timber
import java.net.URL
import java.util.*

class MeetActivity : AppCompatActivity() {

    private val binding: ActivityMeetBinding by lazy {
        ActivityMeetBinding.inflate(layoutInflater)
    }

    private lateinit var preferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        preferences = SharedPreferences(this)

        binding.apply {
            btMeetCode.setOnClickListener {
                startActivity(
                    Intent(
                        this@MeetActivity,
                        JoinMeetActivity::class.java
                    )
                )
            }
            btMeet.setOnClickListener { bottomDialog() }
        }

        profile()
    }

    private fun profile() {
        val id = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().getReference("User").child(id)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    preferences.save(user!!)
                    user.apply {
                        binding.apply {
                            tvName.text = name!!.substring(0, 1)
                        }
                    }
                }
            })
    }

    private fun bottomDialog() {
        val dialog = BottomSheetDialog(this)
        val dialogView = DialogBottomBinding.inflate(layoutInflater)
        dialog.setContentView(dialogView.root)
        dialogView.apply {
            linkMeet.setOnClickListener {
                linkMeet()
                dialog.dismiss()
            }
            startMeet.setOnClickListener { startMeet() }
            close.setOnClickListener { dialog.dismiss() }
        }

        dialog.show()
    }

    private fun linkMeet() {
        val dialog = Dialog(this)
        val dialogView = DialogLinkmeetBinding.inflate(layoutInflater)
        val codeRoom = "Kom- " + UUID.randomUUID().toString().substring(0, 7)
        preferences.saveCodeRoom(codeRoom)
        Timber.d("CodeRoom", codeRoom)
        dialog.setContentView(dialogView.root)
        dialogView.apply {
            etCoderoom.setText("https://meet.jit.si/$codeRoom")
            close.setOnClickListener { dialog.dismiss() }
            copy.setOnClickListener { copyCodeRoom(codeRoom) }
        }
        dialog.show()
    }

    private fun startMeet() {
        try {
            val serverURL = URL("https://meet.jit.si")
            val builder = JitsiMeetConferenceOptions.Builder()
            val userInfo = JitsiMeetUserInfo()
            val user = preferences.get()
            val codeRoom = "Kom- " + UUID.randomUUID().toString().substring(0, 7)
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

    private fun copyCodeRoom(codeRoom: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", codeRoom)
        clipboard.setPrimaryClip(clip)
        toast("berhasil disalin", this)
    }

}