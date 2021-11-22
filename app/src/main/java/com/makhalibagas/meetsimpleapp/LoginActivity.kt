package com.makhalibagas.meetsimpleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.makhalibagas.meetsimpleapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            btLogin.setOnClickListener {
                login(
                    etEmail.text.toString(),
                    etPassword.text.toString()
                )
            }
            tvDaftar.setOnClickListener {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        RegisterActivity::class.java
                    )
                )
            }
        }
    }

    private fun login(email: String, password: String) {
        loading(true)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    main()
                    loading(false)
                }
            }.addOnFailureListener {
                Log.d("errorlogin", it.message.toString())
                toast(getString(R.string.error), this@LoginActivity)
                loading(false)
            }
    }

    private fun loading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                btLogin.visibility = View.GONE
            } else {
                btLogin.visibility = View.VISIBLE
            }
        }
    }

    private fun main() {
        startActivity(Intent(this, MeetActivity::class.java))
        finish()
    }

}