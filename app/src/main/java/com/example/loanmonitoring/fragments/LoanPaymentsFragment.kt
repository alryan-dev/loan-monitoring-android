package com.example.loanmonitoring.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loanmonitoring.R
import com.example.loanmonitoring.adapters.PaymentsRvAdapter
import com.example.loanmonitoring.models.Payment
import com.example.loanmonitoring.viewmodels.LoanViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LoanPaymentsFragment : Fragment() {
    private lateinit var paymentsRvAdapter: PaymentsRvAdapter
    private val paymentsList = mutableListOf<Payment>()
    private val loanViewModel: LoanViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan_payments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set observers
        loanViewModel.loanPayments.observe(viewLifecycleOwner, {
            paymentsList.clear()
            paymentsList.addAll(it)
            paymentsRvAdapter.notifyDataSetChanged()
        })

        loanViewModel.paymentSavedLiveData.observe(viewLifecycleOwner, {
            loanViewModel.selectedLoan.value?.let { loan ->
                loanViewModel.fetchLoanPayments(loan.uid)
            }
        })

        // Set up views
        setUpAddPaymentBtn()
        setUpRecyclerView(view)
    }

    private fun setUpAddPaymentBtn() {
        val fabAddPayment: FloatingActionButton = requireActivity().findViewById(R.id.fabAdd)
        fabAddPayment.setOnClickListener(fun(_: View) {
            val action = LoanFragmentDirections.actionLoanFragmentToPaymentFormFragment()
            findNavController().navigate(action)
        })
    }

    private fun setUpRecyclerView(view: View) {
        // Set up recyclerview
        val rvPayments = view.findViewById<RecyclerView>(R.id.rvPayments)
        rvPayments.layoutManager = LinearLayoutManager(context)
        rvPayments.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Set up adapter
        paymentsRvAdapter =
            PaymentsRvAdapter(paymentsList, object : PaymentsRvAdapter.OnItemSelectListener {
                override fun onItemClick(position: Int) {
                    if (loanViewModel.selectedLoan.value?.status == "FULLY PAID") return
                    loanViewModel.selectedPayment.value = paymentsList[position]
                    loanViewModel.paymentFormMode.value = "VIEW"
                    val action = LoanFragmentDirections.actionLoanFragmentToPaymentFormFragment()
                    findNavController().navigate(action)
                }
            }, object : PaymentsRvAdapter.OnItemSelectListener {
                override fun onItemClick(position: Int) {
                    if (loanViewModel.selectedLoan.value?.status == "FULLY PAID") return
                    showConfirmationDialog(position)
                }
            }, loanViewModel.selectedLoan.value?.borrower)
        rvPayments.adapter = paymentsRvAdapter
    }

    private fun showConfirmationDialog(position: Int) {
        val builder = AlertDialog.Builder(activity)

        builder.setMessage("Confirm Payment?")
            .setPositiveButton("Confirm") { _, _ ->
                paymentsList[position].lenderConfirmed = true
                loanViewModel.savePayment(paymentsList[position])
            }
            .setNegativeButton("Cancel", null)

        builder.create().show()
    }
}