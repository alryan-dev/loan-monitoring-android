package com.example.loanmonitoring.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.loanmonitoring.R
import com.example.loanmonitoring.adapters.ViewPagerAdapter
import com.example.loanmonitoring.databinding.FragmentLoanBinding
import com.example.loanmonitoring.viewmodels.LoanViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class LoanFragment : Fragment() {
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var materialToolbar: MaterialToolbar
    private lateinit var navController: NavController
    private val loanViewModel: LoanViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Create binding
        val fragmentLoanBinding = DataBindingUtil.inflate<FragmentLoanBinding>(
            inflater,
            R.layout.fragment_loan,
            container,
            false
        )
        fragmentLoanBinding.loanViewModel = loanViewModel
        fragmentLoanBinding.lifecycleOwner = viewLifecycleOwner

        return fragmentLoanBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set observers
        loanViewModel.loanPayments.value = mutableListOf()
        loanViewModel.loanPayments.observe(viewLifecycleOwner, {
            loanViewModel.computeRemainingBalance()
            if (loanViewModel.loanFullyPaid()) showCloseLoanDialog()
        })

        loanViewModel.selectedLoan.observe(viewLifecycleOwner, {
            if (it.status == "FULLY PAID")
                requireActivity().findViewById<FloatingActionButton>(R.id.fabAdd).hide()
        })

        loanViewModel.fetchLoanPayments()

        // Set up other views
        setUpToolbar(view)
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

    private fun setUpViewPager(view: View) {
        // Set up ViewPager2
        val vpLoanDetails: ViewPager2 = view.findViewById(R.id.vpLoanDetails)
        vpLoanDetails.adapter = ViewPagerAdapter(
            this,
            listOf(LoanPaymentsFragment(), LoanDetailsFragment())
        )

        // Show or hide add payment fab
        vpLoanDetails.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                loanViewModel.selectedLoan.value?.let { loan ->
                    val fabAdd = requireActivity().findViewById<FloatingActionButton>(R.id.fabAdd)
                    if (position == 0 && loan.status == "ACTIVE") fabAdd.show()
                    else fabAdd.hide()
                }
            }
        })

        // Link the TabLayout with the ViewPager2
        val tlLoanDetails: TabLayout = view.findViewById(R.id.tlLoanDetails)
        TabLayoutMediator(tlLoanDetails, vpLoanDetails) { tab, position ->
            if (position == 0) tab.text = resources.getString(R.string.label_expenses)
            else tab.text = resources.getString(R.string.label_details)
        }.attach()
    }

    private fun showCloseLoanDialog() {
        val builder = AlertDialog.Builder(activity)

        builder.setMessage("Loan is now fully paid. Do you want to close this loan?")
            .setPositiveButton("Yes") { _, _ -> loanViewModel.closeLoan() }
            .setNegativeButton("No", null)

        builder.create().show()
    }
}