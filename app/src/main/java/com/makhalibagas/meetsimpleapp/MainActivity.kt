package com.makhalibagas.meetsimpleapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.makhalibagas.meetsimpleapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), Listerners {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        users()

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful && it.result != null) {
                updateToken(it.result.toString())
            }
        }

        binding.apply {
            signout.setOnClickListener {
                updateToken("token")
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }
        checkForBatteryOptimizations()
    }

    private fun users() {
        val list = ArrayList<User>()
        val id = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().getReference("User")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    list.clear()
                    for (datasnapshot in snapshot.children) {
                        val user = datasnapshot.getValue(User::class.java)
                        if (id == user!!.id) saveToPreferences(user) else list.add(user)

                    }

                    binding.apply {
                        rvUser.layoutManager = LinearLayoutManager(this@MainActivity)
                        rvUser.adapter = UserAdapter(list, this@MainActivity)
                    }
                }
            })
    }

    private fun updateToken(token: String) {
        val db = FirebaseDatabase.getInstance()
            .getReference("User")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["token"] = token
        db.updateChildren(hashMap)
    }

    override fun audioMeet(user: User) {
        if (user.token.equals("token")) {
            toast("Terjadi kesalahan", this)
        } else {
            val intent = Intent(this, OutgoingActivity::class.java)
            intent.putExtra("type", "audio")
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    override fun videoMeet(user: User) {
        if (user.token.equals("token")) {
            toast("Terjadi kesalahan", this)
        } else {
            val intent = Intent(this, OutgoingActivity::class.java)
            intent.putExtra("type", "video")
            intent.putExtra("user", user)
            startActivity(intent)
        }
    }

    private fun saveToPreferences(user: User) {
        val preferences = SharedPreferences(this)
        preferences.save(user)
    }

    private fun checkForBatteryOptimizations() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("Warning")
                builder.setMessage("Battery optimization is enabled. It can interrupt running background services.")
                builder.setPositiveButton("Disable") { dialogInterface, i ->
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                    startActivityForResult(intent, 123)
                }
                builder.setNegativeButton("Cancel") { dialogInterface, i -> dialogInterface.dismiss() }
                builder.create().show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123) {
            checkForBatteryOptimizations()
        }
    }
}