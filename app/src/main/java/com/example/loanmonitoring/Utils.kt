package com.example.loanmonitoring

import android.util.Log
import com.example.loanmonitoring.models.UserModel
import com.google.firebase.auth.FirebaseUser

object Utils {
    fun print(text: String) {
        Log.d("LOAN-MONITORING", text)
    }

    fun FirebaseUser?.toUserModel(): UserModel {
        val user = UserModel()

        this?.uid?.let { user.uid = it }
        this?.displayName?.let { user.displayName = it }
        this?.email?.let { user.email = it }

        return user
    }
}