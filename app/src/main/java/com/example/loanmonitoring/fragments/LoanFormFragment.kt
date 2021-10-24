package com.example.loanmonitoring.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.loanmonitoring.R
import com.example.loanmonitoring.Utils.toUserModel
import com.example.loanmonitoring.models.Loan
import com.example.loanmonitoring.viewmodels.LoanViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class LoanFormFragment : Fragment() {
    private val loanViewModel: LoanViewModel by activityViewModels()
    private lateinit var fragmentView: View
    private var users = mutableListOf<String>()
    private lateinit var tilAmount: TextInputLayout
    private lateinit var tilBorrower: TextInputLayout
    private lateinit var tilLender: TextInputLayout
    private lateinit var tilDescription: TextInputLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var materialToolbar: MaterialToolbar
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView = inflater.inflate(R.layout.fragment_loan_form, container, false)
        initData()

        // Initialize fields
        tilAmount = fragmentView.findViewById(R.id.tilAmount)
        tilDescription = fragmentView.findViewById(R.id.tilDescription)
        coordinatorLayout = requireActivity().findViewById(R.id.coordinatorLayout)
        initBorrowerLenderFields()

        val btnSave = fragmentView.findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            if (validateForm()) saveLoan()
        }

        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        materialToolbar = view.findViewById(R.id.materialToolbar)
        materialToolbar.setupWithNavController(navController, appBarConfiguration)
        (activity as AppCompatActivity).setSupportActionBar(materialToolbar)
    }

    private fun initData() {
        // Fetch users and set an observer for it
        loanViewModel.usersLiveData.observe(viewLifecycleOwner, {
            users.clear()
            for (user in it) users.add(user.displayName)
        })
        loanViewModel.fetchUsers()

        // Set observer when loan is saved
        loanViewModel.loanSavedLiveData.value = false
        loanViewModel.loanSavedLiveData.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().popBackStack()
                Snackbar.make(coordinatorLayout, "Saved!", Snackbar.LENGTH_LONG).show()
                loanViewModel.fetchLoans()
            }
        })
    }

    private fun initBorrowerLenderFields() {
        val actBorrower = fragmentView.findViewById<AutoCompleteTextView>(R.id.actBorrower)
        tilBorrower = fragmentView.findViewById(R.id.tilBorrower)
        val actLender = fragmentView.findViewById<AutoCompleteTextView>(R.id.actLender)
        tilLender = fragmentView.findViewById(R.id.tilLender)

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, users
        )
        actLender.setAdapter(adapter)
        actBorrower.setAdapter(adapter)
    }

    private fun validateForm(): Boolean {
        var isValid = true
        tilAmount.error = null
        tilBorrower.error = null
        tilLender.error = null

        if (tilAmount.editText!!.text.isEmpty()) {
            tilAmount.error = getString(R.string.errortext_required)
            isValid = false
        }

        if (tilBorrower.editText!!.text.isEmpty()) {
            tilBorrower.error = getString(R.string.errortext_required)
            isValid = false
        } else if (!users.contains(tilBorrower.editText!!.text.toString())) {
            tilBorrower.error = getString(R.string.errortext_select_borrower)
            isValid = false
        }

        if (tilLender.editText!!.text.isEmpty()) {
            tilLender.error = getString(R.string.errortext_required)
            isValid = false
        } else if (!users.contains(tilLender.editText!!.text.toString())) {
            tilLender.error = getString(R.string.errortext_select_lender)
            isValid = false
        }

        return isValid
    }

    private fun saveLoan() {
        val loan = Loan()
        loan.amount = tilAmount.editText!!.text.toString().toDouble()
        loan.description =
            if (tilDescription.editText!!.text.isNotEmpty()) tilDescription.editText!!.text.toString()
            else ""
        loan.status = "ACTIVE"
        loan.createdBy = FirebaseAuth.getInstance().currentUser.toUserModel()
        loan.createdOn = Calendar.getInstance()

        // Set borrower and lender
        for (user in loanViewModel.usersLiveData.value!!) {
            if (user.displayName == tilBorrower.editText!!.text.toString())
                loan.borrower = user

            if (user.displayName == tilLender.editText!!.text.toString())
                loan.lender = user
        }

        loanViewModel.saveLoan(loan)
    }
}