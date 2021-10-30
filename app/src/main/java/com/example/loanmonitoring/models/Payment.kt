package com.example.loanmonitoring.models

import com.google.firebase.firestore.QueryDocumentSnapshot
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
    constructor(queryDocumentSnapshot: QueryDocumentSnapshot) : this(
        uid = queryDocumentSnapshot.id,
        loan = Loan(queryDocumentSnapshot.data["loan"] as HashMap<*, *>),
        amount = queryDocumentSnapshot.data["amount"].toString().toDouble(),
        description = queryDocumentSnapshot.data["description"].toString(),
        lenderConfirmed = queryDocumentSnapshot.data["lenderConfirmed"].toString().toBoolean(),
        createdBy = UserModel(queryDocumentSnapshot.data["createdBy"] as HashMap<*, *>),
    ) {
        // Convert createdOn and date fields to a Calendar object
        Calendar.getInstance().let { calendar ->
            calendar.timeInMillis = queryDocumentSnapshot.data["createdOn"] as Long
            this.createdOn = calendar
        }

        Calendar.getInstance().let { calendar ->
            calendar.timeInMillis = queryDocumentSnapshot.data["createdOn"] as Long
            this.createdOn = calendar
        }
    }

    fun toMap(): HashMap<String, Any> {
        val payment = HashMap<String, Any>()
        payment["uid"] = this.uid
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