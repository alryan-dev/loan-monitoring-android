package com.example.loanmonitoring.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.loanmonitoring.R
import com.example.loanmonitoring.Utils.toUserModel
import com.example.loanmonitoring.activities.MainActivity
import com.example.loanmonitoring.databinding.FragmentLoanFormBinding
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
    private var users = mutableListOf<String>()
    private lateinit var tilBorrower: TextInputLayout
    private lateinit var tilLender: TextInputLayout
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var materialToolbar: MaterialToolbar
    private var _binding: FragmentLoanFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoanFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Set observers
        loanViewModel.usersLiveData.observe(viewLifecycleOwner, {
            users.clear()
            for (user in it) users.add(user.displayName)
        })

        loanViewModel.loanSavedLiveData.value = false
        loanViewModel.loanSavedLiveData.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().popBackStack()
                Snackbar.make(coordinatorLayout, "Saved!", Snackbar.LENGTH_LONG).show()
                loanViewModel.fetchLoans()
            }
        })

        loanViewModel.fetchUsers()

        // Set up views
        binding.btnSave.setOnClickListener { if (validateForm()) saveLoan() }
        coordinatorLayout = (requireActivity() as MainActivity).binding.coordinatorLayout
        setUpToolbar()
        setUpBorrowerLenderFields()
    }

    private fun setUpToolbar() {
        materialToolbar = binding.materialToolbar
        val appBarConfiguration = AppBarConfiguration(findNavController().graph)
        materialToolbar.setupWithNavController(findNavController(), appBarConfiguration)
        (activity as AppCompatActivity).setSupportActionBar(materialToolbar)
    }

    private fun setUpBorrowerLenderFields() {
        val actBorrower = binding.actBorrower
        tilBorrower = binding.tilBorrower
        val actLender = binding.actLender
        tilLender = binding.tilLender

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, users
        )
        actLender.setAdapter(adapter)
        actBorrower.setAdapter(adapter)
    }

    private fun validateForm(): Boolean {
        var isValid = true
        binding.tilAmount.error = null
        tilBorrower.error = null
        tilLender.error = null

        if (binding.tilAmount.editText!!.text.isEmpty()) {
            binding.tilAmount.error = getString(R.string.errortext_required)
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
        loan.amount = binding.tilAmount.editText!!.text.toString().toDouble()
        loan.description =
            if (binding.tilDescription.editText!!.text.isNotEmpty()) binding.tilDescription.editText!!.text.toString()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}