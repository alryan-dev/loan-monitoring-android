package com.example.loanmonitoring.models

import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.*

class Loan(
    var uid: String = "",
    var amount: Double = 0.0,
    var lender: UserModel? = null,
    var borrower: UserModel? = null,
    var description: String = "",
    var status: String = "PENDING",
    var createdBy: UserModel? = null,
    var createdOn: Calendar? = null,
) {
    constructor(queryDocumentSnapshot: QueryDocumentSnapshot) : this(
        uid = queryDocumentSnapshot.id,
        amount = queryDocumentSnapshot.data["amount"].toString().toDouble(),
        lender = UserModel(queryDocumentSnapshot.data["lender"] as HashMap<*, *>),
        borrower = UserModel(queryDocumentSnapshot.data["borrower"] as HashMap<*, *>),
        description = queryDocumentSnapshot.data["description"].toString(),
        status = queryDocumentSnapshot.data["status"].toString(),
        createdBy = UserModel(queryDocumentSnapshot.data["createdBy"] as HashMap<*, *>),
    ) {
        // Convert createdOn to a Calendar object
        Calendar.getInstance().let { calendar ->
            calendar.timeInMillis = queryDocumentSnapshot["createdOn"] as Long
            this.createdOn = calendar
        }
    }

    fun toMap(includeUid: Boolean = false): HashMap<String, Any> {
        val loan = HashMap<String, Any>()

        if (includeUid) loan["uid"] = this.uid
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