package com.example.loanmonitoring.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class LoanRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun save(loan: HashMap<String, Any>): Task<DocumentReference> {
        return db.collection("loan").add(loan)
    }
}