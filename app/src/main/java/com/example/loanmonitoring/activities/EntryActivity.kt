package com.example.loanmonitoring.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.loanmonitoring.BuildConfig
import com.example.loanmonitoring.R
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.viewmodels.EntryViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class EntryActivity : AppCompatActivity() {
    private val entryViewModel: EntryViewModel by viewModels()
    private val signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
            onSignInResult(res)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        // Check if a user is logged in
        if (FirebaseAuth.getInstance().currentUser != null) {
            navigateToMainActivity()
            return
        }

        entryViewModel.loginSuccessLiveData.observe(this, {
            if (it) navigateToMainActivity()
        })

        // Create and launch sign-in intent
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setIsSmartLockEnabled(!BuildConfig.DEBUG, true)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            if (response?.isNewUser == true) saveUser()
            else entryViewModel.loginSuccessLiveData.value = true
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Utils.print("Error signing-in: " + response?.error?.errorCode.toString())
        }
    }

    private fun saveUser() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            val userMap = HashMap<String, Any>()

            userMap["uid"] = user.uid
            user.email?.also { userMap["email"] = it }
            user.displayName?.also { userMap["displayName"] = it }
            userMap["createdOn"] = Calendar.getInstance().timeInMillis

            entryViewModel.save(userMap)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}