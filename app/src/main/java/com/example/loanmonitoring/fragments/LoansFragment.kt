package com.example.loanmonitoring.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loanmonitoring.R
import com.example.loanmonitoring.adapters.LoansRvAdapter
import com.example.loanmonitoring.models.Loan
import com.example.loanmonitoring.viewmodels.LoanViewModel

class LoansFragment : Fragment() {
    private val loanViewModel: LoanViewModel by activityViewModels()
    private val loansList = mutableListOf<Loan>()
    private lateinit var loansRvAdapter: LoansRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loans, container, false)
        setUpRecyclerView(view)
        setHasOptionsMenu(true)

        loanViewModel.loansLiveData.observe(viewLifecycleOwner, {
            loansList.clear()
            loansList.addAll(it)
            loansRvAdapter.notifyDataSetChanged()
        })

        return view
    }

    private fun setUpRecyclerView(view: View) {
        // Set up recyclerview
        val rvAlarms = view.findViewById<RecyclerView>(R.id.rvLoans)
        rvAlarms.layoutManager = LinearLayoutManager(context)
        rvAlarms.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))

        // Set up adapter
        loansRvAdapter = LoansRvAdapter(loansList, object : LoansRvAdapter.OnItemSelectListener {
            override fun onItemClick(position: Int) {
                // loanViewModel.alarmFormLiveData.value = alarmsList[position]
                // val action = AlarmsFragmentDirections.actionAlarmsFragmentToAlarmFormFragment()
                // findNavController().navigate(action)
            }
        })
        rvAlarms.adapter = loansRvAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_loans, menu)
    }
}