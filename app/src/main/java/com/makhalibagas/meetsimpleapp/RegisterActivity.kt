package com.makhalibagas.meetsimpleapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.makhalibagas.meetsimpleapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.apply {
            btRegister.setOnClickListener {
                if (etEmail.text.toString().isNotEmpty() && etName.text.toString()
                        .isNotEmpty() && etPassword.text.toString().isNotEmpty()
                ) {
                    register(
                        etEmail.text.toString(),
                        "token",
                        etName.text.toString(),
                        etPassword.text.toString(),
                    )
                } else {
                    toast(getString(R.string.error), this@RegisterActivity)
                }
            }

            tvLogin.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }
        }
    }

    private fun create(id: String, email: String, token: String, name: String) {
        val user = User(id, email, token, name)
        FirebaseDatabase.getInstance().getReference("User")
            .child(id).setValue(user).addOnCompleteListener {
                toast(getString(R.string.succes), this@RegisterActivity)
                startActivity(
                    Intent(
                        this@RegisterActivity,
                        LoginActivity::class.java
                    )
                )
            }
    }

    private fun register(email: String, token: String, name: String, password: String) {
        loading(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val id = FirebaseAuth.getInstance().currentUser!!.uid
                    create(id, email, token, name)
                    loading(false)
                } else {
                    toast(getString(R.string.error), this@RegisterActivity)
                }
            }.addOnFailureListener {
                toast(getString(R.string.error), this@RegisterActivity)
                loading(false)
                Log.d("register", it.message.toString())
            }
    }

    private fun loading(isLoading: Boolean) {
        binding.apply {
            if (isLoading) {
                btRegister.visibility = View.GONE
            } else {
                btRegister.visibility = View.VISIBLE
            }
        }
    }
}