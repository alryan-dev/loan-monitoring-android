package com.example.loanmonitoring.models

import com.google.firebase.auth.FirebaseUser

class Loan(
    var id: Int = 0,
    var totalAmount: Double = 0.0,
    var lender: FirebaseUser? = null,
    var borrower: FirebaseUser? = null,
    var description: String = "",
    var status: String = "PENDING"
) {
}