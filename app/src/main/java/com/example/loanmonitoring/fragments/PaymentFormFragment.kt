package com.example.loanmonitoring.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.example.loanmonitoring.R
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.Utils.toUserModel
import com.example.loanmonitoring.activities.MainActivity
import com.example.loanmonitoring.databinding.FragmentPaymentFormBinding
import com.example.loanmonitoring.models.Payment
import com.example.loanmonitoring.viewmodels.LoanViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class PaymentFormFragment : Fragment() {
    private lateinit var materialToolbar: MaterialToolbar
    private lateinit var navController: NavController
    private lateinit var tilDate: TextInputLayout
    private var payment: Payment = Payment()
    private val loanViewModel: LoanViewModel by activityViewModels()
    private var _binding: FragmentPaymentFormBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Init data and set observers
        loanViewModel.selectedPayment.value?.let { selectedPayment ->
            if (selectedPayment.uid.isNotEmpty()) payment = selectedPayment
        }

        val coordinatorLayout: CoordinatorLayout =
            (requireActivity() as MainActivity).binding.coordinatorLayout

        loanViewModel.paymentSavedLiveData.value = false
        loanViewModel.paymentSavedLiveData.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().popBackStack()
                Snackbar.make(coordinatorLayout, "Saved!", Snackbar.LENGTH_LONG).show()
            }
        })

        // Set up views
        setUpToolbar()
        setUpDateField()
        binding.btnSave.setOnClickListener { if (validateForm()) savePayment() }
    }

    private fun setUpToolbar() {
        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        materialToolbar = binding.materialToolbar
        materialToolbar.setupWithNavController(navController, appBarConfiguration)

        (activity as AppCompatActivity).setSupportActionBar(materialToolbar)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpDateField() {
        tilDate = binding.tilDate
        tilDate.editText?.inputType = InputType.TYPE_NULL
        tilDate.editText?.setText(Utils.calendarToString(payment.date))

        tilDate.editText?.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val datePicker =
                    MaterialDatePicker
                        .Builder
                        .datePicker()
                        .setTitleText("Select date")
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()

                datePicker.addOnPositiveButtonClickListener {
                    payment.date = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    payment.date.timeInMillis = it
                    tilDate.editText?.setText(Utils.calendarToString(payment.date))
                }

                activity?.let { activity ->
                    datePicker.show(activity.supportFragmentManager, "DATE_DIALOG")
                }
            }

            false
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        binding.tilAmount.error = null
        tilDate.error = null

        if (binding.tilAmount.editText!!.text.isEmpty()) {
            binding.tilAmount.error = getString(R.string.errortext_required)
            isValid = false
        }

        if (tilDate.editText!!.text.isEmpty()) {
            tilDate.error = getString(R.string.errortext_required)
            isValid = false
        }

        return isValid
    }

    private fun savePayment() {
        payment.loan = loanViewModel.selectedLoan.value
        payment.amount = binding.tilAmount.editText!!.text.toString().toDouble()
        payment.createdOn = Calendar.getInstance()

        if (binding.tilDescription.editText!!.text.isNotEmpty())
            payment.description = binding.tilDescription.editText!!.text.toString()

        val userModel = FirebaseAuth.getInstance().currentUser.toUserModel()
        payment.createdBy = userModel

        if (userModel.uid == loanViewModel.selectedLoan.value?.lender?.uid) {
            payment.lenderConfirmed = true
        }

        loanViewModel.savePayment(payment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}