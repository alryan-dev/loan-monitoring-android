package com.example.loanmonitoring.fragments

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.loanmonitoring.R
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.Utils.toUserModel
import com.example.loanmonitoring.adapters.ViewPagerAdapter
import com.example.loanmonitoring.viewmodels.LoanViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class LoanFragment : Fragment() {
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var materialToolbar: MaterialToolbar
    private lateinit var navController: NavController
    private val loanViewModel: LoanViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_loan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set observers
        loanViewModel.loanPayments.observe(viewLifecycleOwner, { payments ->
            loanViewModel.selectedLoan.value?.let { loan ->
                // Compute loan remaining balance
                var remainingBalance = loan.amount
                payments.forEach { payment ->
                    if (payment.lenderConfirmed) remainingBalance -= payment.amount
                }

                setUpRemainingBalance(view, remainingBalance)

                // Check if loan is fully paid
                if (
                    loan.lender?.uid == FirebaseAuth.getInstance().currentUser.toUserModel().uid
                    && remainingBalance <= 0.0
                    && loan.status == "ACTIVE"
                )
                    showCloseLoanDialog()
            }
        })

        loanViewModel.loanSavedLiveData.value = false
        loanViewModel.loanSavedLiveData.observe(viewLifecycleOwner, {
            loanViewModel.selectedLoan.value?.let { loan ->
                if (loan.status == "FULLY PAID") {
                    setUpLoanStatus(view)
                    requireActivity().findViewById<FloatingActionButton>(R.id.fabAdd).hide()
                }
            }
        })

        // Set up views
        setUpToolbar(view)
        setUpLoanStatus(view)
        setUpViewPager(view)
    }

    private fun setUpToolbar(view: View) {
        materialToolbar = view.findViewById(R.id.materialToolbar)
        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        collapsingToolbarLayout = view.findViewById(R.id.collapsingToolbarLayout)
        collapsingToolbarLayout.setupWithNavController(
            materialToolbar,
            navController,
            appBarConfiguration
        )
    }

    private fun setUpRemainingBalance(view: View, remainingBalance: Double) {
        val tvRemainingBalance: TextView = view.findViewById(R.id.tvRemainingBalance)
        tvRemainingBalance.text = Utils.toCurrencyFormat(remainingBalance)
    }

    private fun setUpLoanStatus(view: View) {
        val chipStatus: Chip = view.findViewById(R.id.chipStatus)
        val status: String = loanViewModel.selectedLoan.value?.status ?: ""
        chipStatus.text = status.lowercase().capitalize()

        if (status == "ACTIVE") {
            chipStatus.setTextColor(Color.parseColor("#FFA500"))
        } else {
            chipStatus.setTextColor(Color.parseColor("#00C853"))
        }
    }

    private fun setUpViewPager(view: View) {
        // Set up ViewPager
        val vpLoanDetails: ViewPager2 = view.findViewById(R.id.vpLoanDetails)
        vpLoanDetails.adapter = ViewPagerAdapter(
            activity as FragmentActivity, listOf(LoanPaymentsFragment(), LoanDetailsFragment())
        )

        // Show/Hide add payment fab
        vpLoanDetails.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                loanViewModel.selectedLoan.value?.let { loan ->
                    val fabAdd = requireActivity().findViewById<FloatingActionButton>(R.id.fabAdd)
                    if (position == 0 && loan.status == "ACTIVE") fabAdd.show()
                    else fabAdd.hide()
                }
            }
        })

        // Link the TabLayout with the ViewPager
        val tlLoanDetails: TabLayout = view.findViewById(R.id.tlLoanDetails)
        TabLayoutMediator(tlLoanDetails, vpLoanDetails) { tab, position ->
            if (position == 0) tab.text = resources.getString(R.string.label_expenses)
            else tab.text = resources.getString(R.string.label_details)
        }.attach()
    }

    private fun showCloseLoanDialog() {
        val builder = AlertDialog.Builder(activity)

        builder.setMessage("Loan is now fully paid. Do you want to close this loan?")
            .setPositiveButton("Yes") { _, _ ->
                loanViewModel.selectedLoan.value?.let {
                    it.status = "FULLY PAID"
                    loanViewModel.saveLoan(it)
                }
            }
            .setNegativeButton("No", null)

        builder.create().show()
    }
}