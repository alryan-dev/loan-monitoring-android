package com.example.loanmonitoring.models

import java.util.*

class Loan(
    var uid: String = "",
    var amount: Double = 0.0,
    var lender: UserModel? = null,
    var borrower: UserModel? = null,
    var description: String = "",
    var status: String = "ACTIVE",
    var createdBy: UserModel? = null,
    var createdOn: Calendar? = null,
) {
    constructor(map: HashMap<*, *>) : this(
        uid = map["uid"].toString(),
        amount = map["amount"].toString().toDouble(),
        lender = UserModel(map["lender"] as HashMap<*, *>),
        borrower = UserModel(map["borrower"] as HashMap<*, *>),
        description = map["description"].toString(),
        status = map["status"].toString(),
        createdBy = UserModel(map["createdBy"] as HashMap<*, *>),
    ) {
        // Convert createdOn to a Calendar object
        Calendar.getInstance().let { calendar ->
            calendar.timeInMillis = map["createdOn"] as Long
            this.createdOn = calendar
        }
    }

    fun toMap(): HashMap<String, Any> {
        val loan = HashMap<String, Any>()
        loan["uid"] = this.uid
        loan["amount"] = this.amount
        loan["lender"] = this.lender?.toMap() as Any
        loan["borrower"] = this.borrower?.toMap() as Any
        loan["description"] = this.description
        loan["status"] = this.status
        loan["createdBy"] = this.createdBy?.toMap() as Any
        loan["createdOn"] = this.createdOn?.timeInMillis as Any
        return loan
    }
}