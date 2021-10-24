package com.example.loanmonitoring.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.loanmonitoring.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LoanPaymentsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_payments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpAddPaymentBtn()
    }

    private fun setUpAddPaymentBtn() {
        val fabAddPayment: FloatingActionButton = requireActivity().findViewById(R.id.fabAddPayment)
        fabAddPayment.setOnClickListener {
            val action = LoanFragmentDirections.actionLoanFragmentToPaymentFormFragment()
            findNavController().navigate(action)
        }
    }
}