package com.example.loanmonitoring.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.models.Loan
import com.example.loanmonitoring.models.UserModel
import com.example.loanmonitoring.repository.LoanRepository
import com.example.loanmonitoring.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val usersLiveData = MutableLiveData<MutableList<UserModel>>()
    val loansLiveData = MutableLiveData<MutableList<Loan>>()
    val loanSavedLiveData = MutableLiveData(false)

    fun fetchLoans() {
        val task = loanRepository.fetchAll()

        task.addOnSuccessListener { result ->
            val loans = mutableListOf<Loan>()
            for (document in result)
                Loan(document).let(loans::add)
            loansLiveData.value = loans
        }.addOnFailureListener { e ->
            Utils.print("Error getting documents: " + e.message)
        }
    }

    fun fetchUsers() {
        val task = userRepository.fetchAll()

        task.addOnSuccessListener { result ->
            val users = mutableListOf<UserModel>()
            for (document in result)
                UserModel(document.data as java.util.HashMap<*, *>).let(users::add)
            usersLiveData.value = users
        }.addOnFailureListener { e ->
            Utils.print("Error getting documents: " + e.message)
        }
    }

    fun saveLoan(loan: Loan) {
        val task = loanRepository.save(loan.toMap())
        task.addOnSuccessListener {
            loanSavedLiveData.value = true
        }.addOnFailureListener { e ->
            Utils.print("Error adding document: " + e.message)
        }
    }
}