package com.example.loanmonitoring.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.models.Loan
import com.example.loanmonitoring.models.Payment
import com.example.loanmonitoring.models.UserModel
import com.example.loanmonitoring.repository.LoanRepository
import com.example.loanmonitoring.repository.PaymentRepository
import com.example.loanmonitoring.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LoanViewModel @Inject constructor(
    private val loanRepository: LoanRepository,
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
) : ViewModel() {
    val usersLiveData = MutableLiveData<MutableList<UserModel>>()
    val loansLiveData = MutableLiveData<MutableList<Loan>>()
    val loanPayments = MutableLiveData<MutableList<Payment>>()
    val loanSavedLiveData = MutableLiveData(false)
    val paymentSavedLiveData = MutableLiveData(false)
    val selectedLoan = MutableLiveData<Loan>()
    val selectedPayment = MutableLiveData<Payment>()

    fun fetchLoans() {
        val task = loanRepository.fetchAll()
        task.addOnSuccessListener { result ->
            val loans = mutableListOf<Loan>()

            for (document in result)
                Loan(document.data as HashMap<*, *>).let(loans::add)

            loansLiveData.value = loans
        }.addOnFailureListener { e ->
            Utils.print("Error getting documents: " + e.message)
        }
    }

    fun fetchLoanPayments(uid: String) {
        val task = paymentRepository.fetchLoanPayments(uid)
        task.addOnSuccessListener { result ->
            val payments = mutableListOf<Payment>()

            for (document in result)
                Payment(document.data as HashMap<*, *>).let(payments::add)

            loanPayments.value = payments
        }.addOnFailureListener { e ->
            Utils.print("Error getting documents: " + e.message)
        }
    }

    fun fetchUsers() {
        val task = userRepository.fetchAll()
        task.addOnSuccessListener { result ->
            val users = mutableListOf<UserModel>()

            for (document in result)
                UserModel(document.data as HashMap<*, *>).let(users::add)

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

    fun savePayment(payment: Payment) {
        val task = paymentRepository.save(payment.toMap())
        task.addOnSuccessListener {
            paymentSavedLiveData.value = true
        }.addOnFailureListener { e ->
            Utils.print("Error adding document: " + e.message)
        }
    }
}