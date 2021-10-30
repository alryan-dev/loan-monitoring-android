package com.example.loanmonitoring.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.Button
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
    private lateinit var tilAmount: TextInputLayout
    private lateinit var tilDate: TextInputLayout
    private lateinit var tilDescription: TextInputLayout
    private var payment: Payment = Payment()
    private val loanViewModel: LoanViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_payment_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loanViewModel.selectedPayment.value?.let {
            if (it.uid.isNotEmpty()) payment = it
        }

        // Set observer when payment is saved
        val coordinatorLayout: CoordinatorLayout =
            requireActivity().findViewById(R.id.coordinatorLayout)
        loanViewModel.paymentSavedLiveData.value = false
        loanViewModel.paymentSavedLiveData.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().popBackStack()
                Snackbar.make(coordinatorLayout, "Saved!", Snackbar.LENGTH_LONG).show()
            }
        })

        setUpToolbar(view)
        setUpDateField(view)

        // Set up fields
        tilAmount = view.findViewById(R.id.tilAmount)
        tilDescription = view.findViewById(R.id.tilDescription)

        // Set up save button
        val btnSave = view.findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener { if (validateForm()) savePayment() }
    }

    private fun setUpToolbar(view: View) {
        navController = findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)

        materialToolbar = view.findViewById(R.id.materialToolbar)
        materialToolbar.setupWithNavController(navController, appBarConfiguration)

        (activity as AppCompatActivity).setSupportActionBar(materialToolbar)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpDateField(view: View) {
        tilDate = view.findViewById(R.id.tilDate)
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
        tilAmount.error = null
        tilDate.error = null

        if (tilAmount.editText!!.text.isEmpty()) {
            tilAmount.error = getString(R.string.errortext_required)
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
        payment.amount = tilAmount.editText!!.text.toString().toDouble()
        payment.createdOn = Calendar.getInstance()

        if (tilDescription.editText!!.text.isNotEmpty())
            payment.description = tilDescription.editText!!.text.toString()

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
}