package com.example.loanmonitoring.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.loanmonitoring.R
import com.example.loanmonitoring.viewmodels.LoanViewModel
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoanFormFragment : Fragment() {
    private val loanViewModel: LoanViewModel by activityViewModels()
    private lateinit var fragmentView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView = inflater.inflate(R.layout.fragment_loan_form, container, false)

        // val tilAmount = fragmentView.findViewById<TextInputLayout>(R.id.tilAmount)
        // val tilAmount = fragmentView.findViewById<TextInputLayout>(R.id.tilAmount)
        // val tilAmount = fragmentView.findViewById<TextInputLayout>(R.id.tilAmount)
        // val tilAmount = fragmentView.findViewById<TextInputLayout>(R.id.tilAmount)

        val btnSave = fragmentView.findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            val loan = HashMap<String, Any>()
            loan["amount"] = 1000
            loanViewModel.saveLoan(loan)
        }

        // Firebase.firestore.

        return fragmentView
    }

    private fun setupAmountField() {
        val tilAmount = fragmentView.findViewById<TextInputLayout>(R.id.tilAmount)
    }
}