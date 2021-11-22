package com.makhalibagas.meetsimpleapp

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
data class User(
    var id: String? = null,
    var email: String? = null,
    var token: String? = null,
    var name: String? = null
) : Parcelable