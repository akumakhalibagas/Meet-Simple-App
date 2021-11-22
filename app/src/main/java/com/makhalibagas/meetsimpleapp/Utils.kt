package com.makhalibagas.meetsimpleapp

import android.content.Context
import android.widget.Toast

const val REMOTE_MSG_AUTHORIZATION = "Authorization"
const val REMOTE_MSG_CONTENT_TYPE = "Content-Type"
const val SERVER_KEY =
    "key=AAAA1qPTwUE:APA91bG7v5TK0secX8fnyodixQsevPMh2C-2Tn0hA_K71v5Wsqm8PwQjHvvPGGCuEWDtIhwAgGCUsPoKenMC0Fl65uBWB0UX-TI6-G8iMj1KHz6PspxzxP1Kk2ZtM_wRiIQGYBdcWmwQ"
const val REMOTE_VALUE_CONTENT_TYPE = "application/json"

fun getRemoteMessageHeaders(): HashMap<String, String> {
    val headers: HashMap<String, String> = HashMap()
    headers[REMOTE_MSG_AUTHORIZATION] = SERVER_KEY
    headers[REMOTE_MSG_CONTENT_TYPE] = REMOTE_VALUE_CONTENT_TYPE
    return headers
}

fun toast(msg: String, context: Context) = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
    .show()