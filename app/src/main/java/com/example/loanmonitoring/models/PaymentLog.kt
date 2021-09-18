package com.example.loanmonitoring.models

import java.util.*

class PaymentLog(
    var id: Int = 0,
    var amount: Double = 0.0,
    var date: Calendar = Calendar.getInstance(),
    var description: String = "",
    var createdBy: User? = null,
    var lenderConfirmed: Boolean = false,
) {
}