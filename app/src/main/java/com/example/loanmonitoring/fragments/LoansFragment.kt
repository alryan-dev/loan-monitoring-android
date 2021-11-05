package com.example.loanmonitoring.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loanmonitoring.R
import com.example.loanmonitoring.activities.EntryActivity
import com.example.loanmonitoring.activities.MainActivity
import com.example.loanmonitoring.adapters.LoansRvAdapter
import com.example.loanmonitoring.databinding.FragmentLoansBinding
import com.example.loanmonitoring.models.Loan
import com.example.loanmonitoring.viewmodels.LoanViewModel
import com.firebase.ui.auth.AuthUI
import com.google.android.material.appbar.MaterialToolbar

class LoansFragment : Fragment() {
    private val loanViewModel: LoanViewModel by activityViewModels()
    private val loansList = mutableListOf<Loan>()
    private lateinit var loansRvAdapter: LoansRvAdapter
    private lateinit var materialToolbar: MaterialToolbar
    private var _binding: FragmentLoansBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoansBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set observers
        loanViewModel.loansLiveData.observe(viewLifecycleOwner, {
            loansList.clear()
            loansList.addAll(it)
            loansRvAdapter.notifyDataSetChanged()
        })

        // Set up other views
        setUpToolbar()
        setUpRecyclerView()
        setUpAddLoanBtn()
    }

    private fun setUpToolbar() {
        materialToolbar = binding.materialToolbar
        val appBarConfiguration = AppBarConfiguration(findNavController().graph)
        materialToolbar.setupWithNavController(findNavController(), appBarConfiguration)
        (activity as AppCompatActivity).setSupportActionBar(materialToolbar)
    }

    private fun setUpRecyclerView() {
        // Set up recyclerview
        val rvLoans = binding.rvLoans
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

    private fun setUpAddLoanBtn() {
        val fabAddLoan = (requireActivity() as MainActivity).binding.fabAdd
        fabAddLoan.setOnClickListener {
            val action = LoansFragmentDirections.actionLoansFragmentToLoanFormFragment()
            findNavController().navigate(action)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_loans, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_signout -> {
            AuthUI.getInstance()
                .signOut((activity as AppCompatActivity))
                .addOnCompleteListener {
                    val intent = Intent(context, EntryActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                }
            true
        }
        else -> {
            item.onNavDestinationSelected(findNavController()) || super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}