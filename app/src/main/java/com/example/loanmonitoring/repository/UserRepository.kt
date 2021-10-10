package com.example.loanmonitoring.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val db: FirebaseFirestore
) {
    fun fetchAll(): Task<QuerySnapshot> {
        return db.collection("users").get()
    }

    fun fetchExcludingCurrentUser(): Task<QuerySnapshot> {
        return db.collection("users")
            .whereNotEqualTo("uid", FirebaseAuth.getInstance().currentUser?.uid).get()
    }

    fun save(user: HashMap<String, Any>): Task<DocumentReference> {
        return db.collection("users").add(user)
    }
}