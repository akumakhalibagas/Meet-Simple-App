package com.makhalibagas.meetsimpleapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.makhalibagas.meetsimpleapp.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private val binding: ActivityWelcomeBinding by lazy {
        ActivityWelcomeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        binding.apply {
            btNext.setOnClickListener {
                startActivity(
                    Intent(
                        this@WelcomeActivity,
                        LoginActivity::class.java
                    )
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, MeetActivity::class.java))
            finish()
        }
    }
}