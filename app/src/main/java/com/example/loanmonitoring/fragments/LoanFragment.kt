package com.example.loanmonitoring.fragments

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
import com.example.loanmonitoring.adapters.ViewPagerAdapter
import com.example.loanmonitoring.viewmodels.LoanViewModel
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.text.DecimalFormat

class LoanFragment : Fragment() {
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    private lateinit var materialToolbar: MaterialToolbar
    private lateinit var navController: NavController
    private val loanViewModel: LoanViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loan, container, false)
        setUpAmount(view)
        setUpStatus(view)
        return view
    }

    private fun setUpAmount(view: View) {
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val formatter = DecimalFormat("#,###.00")
        val amount = "₱" + formatter.format(loanViewModel.selectedLoan.value?.amount)
        tvAmount.text = amount
    }

    private fun setUpStatus(view: View) {
        val chipStatus: Chip = view.findViewById(R.id.chipStatus)
        val status: String = loanViewModel.selectedLoan.value?.status ?: ""
        chipStatus.text = status.lowercase().capitalize()

        if (status == "ACTIVE") {
            chipStatus.setTextColor(Color.parseColor("#FFA500"))
        } else {
            chipStatus.setTextColor(Color.parseColor("#00C853"))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
        // Setup ViewPager
        val vpLoanDetails: ViewPager2 = view.findViewById(R.id.vpLoanDetails)
        vpLoanDetails.adapter = ViewPagerAdapter(
            activity as FragmentActivity, listOf(LoanPaymentsFragment(), LoanDetailsFragment())
        )

        // Show/Hide add payment fab
        vpLoanDetails.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val fabAddPayment =
                    requireActivity().findViewById<FloatingActionButton>(R.id.fabAddPayment)

                if (position == 0) fabAddPayment.show()
                else fabAddPayment.hide()
            }
        })

        // Link the TabLayout with the ViewPager
        val tlLoanDetails: TabLayout = view.findViewById(R.id.tlLoanDetails)
        TabLayoutMediator(tlLoanDetails, vpLoanDetails) { tab, position ->
            if (position == 0) tab.text = resources.getString(R.string.label_expenses)
            else tab.text = resources.getString(R.string.label_details)
        }.attach()
    }
}