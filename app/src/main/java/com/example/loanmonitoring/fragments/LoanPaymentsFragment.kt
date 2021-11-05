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
import com.example.loanmonitoring.activities.MainActivity
import com.example.loanmonitoring.adapters.PaymentsRvAdapter
import com.example.loanmonitoring.databinding.FragmentLoanPaymentsBinding
import com.example.loanmonitoring.models.Payment
import com.example.loanmonitoring.viewmodels.LoanViewModel

class LoanPaymentsFragment : Fragment() {
    private lateinit var paymentsRvAdapter: PaymentsRvAdapter
    private val paymentsList = mutableListOf<Payment>()
    private val loanViewModel: LoanViewModel by activityViewModels()
    private var _binding: FragmentLoanPaymentsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoanPaymentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set observers
        loanViewModel.paymentSavedLiveData.value = false
        loanViewModel.paymentSavedLiveData.observe(viewLifecycleOwner, {
            if (it) loanViewModel.fetchLoanPayments()
        })

        loanViewModel.loanPayments.observe(viewLifecycleOwner, { payments ->
            paymentsList.clear()
            paymentsList.addAll(payments)
            paymentsRvAdapter.notifyDataSetChanged()
        })

        // Set up other views
        setUpAddPaymentBtn()
        setUpRecyclerView()
    }

    private fun setUpAddPaymentBtn() {
        val fabAddPayment = (requireActivity() as MainActivity).binding.fabAdd
        fabAddPayment.setOnClickListener(fun(_: View) {
            val action = LoanFragmentDirections.actionLoanFragmentToPaymentFormFragment()
            findNavController().navigate(action)
        })
    }

    private fun setUpRecyclerView() {
        // Set up recyclerview
        val rvPayments = binding.rvPayments
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
            .setPositiveButton("Confirm") { _, _ -> loanViewModel.confirmPayment(position) }
            .setNegativeButton("Cancel", null)

        builder.create().show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}