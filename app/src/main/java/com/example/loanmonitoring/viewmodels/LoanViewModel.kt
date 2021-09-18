package com.example.loanmonitoring.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.models.User
import com.example.loanmonitoring.repository.LoanRepository
import com.example.loanmonitoring.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val usersLiveData = MutableLiveData<MutableList<User>>()
    val userSavedLiveData = MutableLiveData(false)

    fun fetchUsers() {
        val task = userRepository.fetchAll()

        task.addOnSuccessListener { result ->
            val users = mutableListOf<User>()

            for (document in result) {
                User(
                    uid = document.data["uid"].toString(),
                    name = document.data["name"].toString(),
                    email = document.data["email"].toString()
                ).let {
                    users.add(it)
                }
            }

            usersLiveData.value = users
        }.addOnFailureListener { e ->
            Utils.print("Error getting documents: " + e.message)
        }
    }

    fun saveLoan(loan: HashMap<String, Any>) {
        val task = loanRepository.save(loan)

        task.addOnSuccessListener {
            userSavedLiveData.value = true
        }.addOnFailureListener { e ->
            Utils.print("Error adding document: " + e.message)
        }
    }
}