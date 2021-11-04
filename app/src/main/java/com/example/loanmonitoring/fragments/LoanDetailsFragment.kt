package com.example.loanmonitoring.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.loanmonitoring.R
import com.example.loanmonitoring.databinding.FragmentLoanDetailsBinding
import com.example.loanmonitoring.viewmodels.LoanViewModel

class LoanDetailsFragment : Fragment() {
    private val loanViewModel: LoanViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Create binding
        val fragmentLoanDetailsBinding = DataBindingUtil.inflate<FragmentLoanDetailsBinding>(
            inflater,
            R.layout.fragment_loan_details,
            container,
            false
        )
        fragmentLoanDetailsBinding.loanViewModel = loanViewModel
        fragmentLoanDetailsBinding.lifecycleOwner = viewLifecycleOwner

        return fragmentLoanDetailsBinding.root
    }
}