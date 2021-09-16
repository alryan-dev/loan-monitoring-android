package com.example.loanmonitoring.viewmodels

import androidx.lifecycle.ViewModel
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.repository.LoanRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val loanRepository: LoanRepository
) : ViewModel() {

    fun saveLoan(loan: HashMap<String, Any>) {
        val task = loanRepository.save(loan)
        task.addOnSuccessListener { documentReference ->
            Utils.print("DocumentSnapshot added with ID: ${documentReference.id}")
        }.addOnFailureListener { e ->
            Utils.print("Error adding document: " + e.message)
        }
    }
}