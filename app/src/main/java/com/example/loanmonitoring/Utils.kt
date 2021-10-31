package com.example.loanmonitoring

import android.util.Log
import com.example.loanmonitoring.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

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

    fun calendarToString(calendar: Calendar): String =
        SimpleDateFormat("MMM. dd, yyyy", Locale.getDefault()).format(calendar.time)

    fun toCurrencyFormat(amount: Double) : String {
        val formatter = DecimalFormat("#,##0.00")
        return  "₱" + formatter.format(amount)
    }

    fun checkCurrentUser(user: UserModel) : String {
        FirebaseAuth.getInstance().currentUser?.let { currentUser ->
            if (currentUser.uid == user.uid) return "You"
        }
        return user.displayName
    }
}