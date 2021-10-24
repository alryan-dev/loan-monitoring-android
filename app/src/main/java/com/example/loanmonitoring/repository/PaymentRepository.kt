package com.example.loanmonitoring.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun fetchAll(): Task<QuerySnapshot> {
        return db.collection("payments").get()
    }

    fun save(loan: HashMap<String, Any>): Task<DocumentReference> {
        return db.collection("payments").add(loan)
    }
}