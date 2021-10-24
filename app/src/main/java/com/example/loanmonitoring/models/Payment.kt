package com.example.loanmonitoring.models

import java.util.*

class Payment(
    var uid: String = "",
    var loan: Loan? = null,
    var amount: Double = 0.0,
    var date: Calendar = Calendar.getInstance(),
    var description: String = "",
    var lenderConfirmed: Boolean = false,
    var createdBy: UserModel? = null,
    var createdOn: Calendar? = null,
) {
    fun toMap(includeUid: Boolean = false): HashMap<String, Any> {
        val payment = HashMap<String, Any>()

        if (includeUid) payment["uid"] = this.uid
        payment["loan"] = this.loan?.toMap() as Any
        payment["amount"] = this.amount
        payment["date"] = this.date.timeInMillis as Any
        payment["description"] = this.description
        payment["lenderConfirmed"] = this.lenderConfirmed
        payment["createdBy"] = this.createdBy?.toMap() as Any
        payment["createdOn"] = this.createdOn?.timeInMillis as Any

        return payment
    }
}