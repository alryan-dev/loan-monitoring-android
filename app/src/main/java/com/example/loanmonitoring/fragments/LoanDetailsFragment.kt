package com.example.loanmonitoring.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.loanmonitoring.R
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.viewmodels.LoanViewModel

class LoanDetailsFragment : Fragment() {
    private val loanViewModel: LoanViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set amount field
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        tvAmount.text = Utils.toCurrencyFormat(loanViewModel.selectedLoan.value?.amount ?: 0.0)

        // Set lender field
        val tvLender: TextView = view.findViewById(R.id.tvLender)
        loanViewModel.selectedLoan.value?.lender?.let { lender ->
            tvLender.text = Utils.checkCurrentUser(lender)
        }

        // Set borrower field
        val tvBorrower: TextView = view.findViewById(R.id.tvBorrower)
        loanViewModel.selectedLoan.value?.borrower?.let { borrower ->
            tvBorrower.text = Utils.checkCurrentUser(borrower)
        }

        // Set description field
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        tvDescription.text = loanViewModel.selectedLoan.value?.description

        // Set createdBy field
        val tvCreatedBy: TextView = view.findViewById(R.id.tvCreatedBy)
        loanViewModel.selectedLoan.value?.createdBy?.let { createdBy ->
            tvCreatedBy.text = Utils.checkCurrentUser(createdBy)
        }

        // Set createdOn field
        val tvCreatedOn: TextView = view.findViewById(R.id.tvCreatedOn)
        loanViewModel.selectedLoan.value?.createdOn?.let { createdOn ->
            tvCreatedOn.text = Utils.calendarToString(createdOn)
        }
    }
}