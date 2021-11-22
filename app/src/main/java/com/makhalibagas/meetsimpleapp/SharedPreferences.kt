package com.makhalibagas.meetsimpleapp

import android.content.Context

class SharedPreferences(context: Context) {


    private val key_preferences = "meetappsimple"
    private val key_id = "id"
    private val key_email = "email"
    private val key_token = "token"
    private val key_name = "name"
    private val codeRoom = "codeRoom"

    val preferences = context.getSharedPreferences(key_preferences, Context.MODE_PRIVATE)

    fun save(user: User) {
        val editor = preferences.edit()
        editor.apply {
            user.apply {
                putString(key_id, id)
                putString(key_email, email)
                putString(key_token, token)
                putString(key_name, name)
            }
        }
        editor.apply()
    }

    fun saveCodeRoom(codeRoom: String) {
        val editor = preferences.edit()
        editor.apply {
            putString(codeRoom, codeRoom)
        }
        editor.apply()
    }

    fun getCodeRoom(): String {
        return preferences.getString(codeRoom, "").toString()
    }

    fun get(): User {
        val user = User()
        user.apply {
            preferences.apply {
                id = getString(key_id, "")
                email = getString(key_email, "")
                token = getString(key_token, "")
                name = getString(key_name, "")
            }
        }

        return user
    }

}