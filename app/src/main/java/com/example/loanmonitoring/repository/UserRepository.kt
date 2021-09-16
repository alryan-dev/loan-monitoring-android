package com.example.loanmonitoring.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun save(user: HashMap<String, Any>): Task<DocumentReference> {
        return db.collection("user").add(user)
    }
}