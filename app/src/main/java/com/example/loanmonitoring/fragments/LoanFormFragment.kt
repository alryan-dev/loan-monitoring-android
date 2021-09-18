package com.example.loanmonitoring.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.loanmonitoring.R
import com.example.loanmonitoring.viewmodels.LoanViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentView = inflater.inflate(R.layout.fragment_loan_form, container, false)

        // Init data and set observers
        loanViewModel.usersLiveData.observe(viewLifecycleOwner, {
            users.clear()
            for (user in it) users.add(user.name)
        })
        loanViewModel.fetchUsers()

        loanViewModel.userSavedLiveData.value = false
        loanViewModel.userSavedLiveData.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().popBackStack()
                Snackbar.make(coordinatorLayout, "Saved!", Snackbar.LENGTH_LONG).show()
            }
        })

        // init fields
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
        val loan = HashMap<String, Any>()
        loan["amount"] = tilAmount.editText!!.text.toString().toDouble()

        for (user in loanViewModel.usersLiveData.value!!) {
            if (user.name == tilBorrower.editText!!.text.toString())
                loan["borrower"] = user

            if (user.name == tilLender.editText!!.text.toString())
                loan["lender"] = user
        }

        loan["description"] =
            if (tilDescription.editText!!.text.isNotEmpty()) tilDescription.editText!!.text.toString() else ""

        loanViewModel.saveLoan(loan)
    }
}