package com.example.loanmonitoring.models

class Loan(
    var id: Int = 0,
    var totalAmount: Double = 0.0,
    var lender: User? = null,
    var borrower: User? = null,
    var description: String = "",
    var status: String = "PENDING"
) {
}