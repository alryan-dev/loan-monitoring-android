package com.example.loanmonitoring.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class LoanRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun fetchActive(): Task<QuerySnapshot> {
        return db.collection("loans").whereEqualTo("status", "ACTIVE").get()
    }

    fun fetchAll(): Task<QuerySnapshot> {
        return db.collection("loans")
            .orderBy("createdOn", Query.Direction.DESCENDING).get()
    }

    fun add(loan: HashMap<String, Any>): Task<DocumentReference> {
        return db.collection("loans").add(loan)
    }

    fun update(loan: HashMap<String, Any>): Task<Void> {
        return db.collection("loans").document(loan["uid"].toString()).update(loan)
    }
}