package com.example.loanmonitoring.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loanmonitoring.R
import com.example.loanmonitoring.adapters.LoansRvAdapter
import com.example.loanmonitoring.models.Loan
import com.example.loanmonitoring.viewmodels.LoanViewModel
import com.google.android.material.appbar.MaterialToolbar

class LoansFragment : Fragment() {
    private val loanViewModel: LoanViewModel by activityViewModels()
    private val loansList = mutableListOf<Loan>()
    private lateinit var loansRvAdapter: LoansRvAdapter
    private lateinit var materialToolbar: MaterialToolbar;
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loans, container, false)
        setUpRecyclerView(view)
        setHasOptionsMenu(true)

        // Set loans listener
        loanViewModel.loansLiveData.observe(viewLifecycleOwner, {
            loansList.clear()
            loansList.addAll(it)
            loansRvAdapter.notifyDataSetChanged()
        })

        return view
    }

    private fun setUpRecyclerView(view: View) {
        // Set up recyclerview
        val rvLoans = view.findViewById<RecyclerView>(R.id.rvLoans)
        rvLoans.layoutManager = LinearLayoutManager(context)
        rvLoans.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Set up adapter
        loansRvAdapter = LoansRvAdapter(loansList, object : LoansRvAdapter.OnItemSelectListener {
            override fun onItemClick(position: Int) {
                loanViewModel.selectedLoan.value = loansList[position]
                val action = LoansFragmentDirections.actionLoansFragmentToLoanDetailsFragment()
                findNavController().navigate(action)
            }
        })
        rvLoans.adapter = loansRvAdapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        materialToolbar = view.findViewById(R.id.materialToolbar)
        materialToolbar.setupWithNavController(navController, appBarConfiguration)
        (activity as AppCompatActivity).setSupportActionBar(materialToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_loans, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }
}