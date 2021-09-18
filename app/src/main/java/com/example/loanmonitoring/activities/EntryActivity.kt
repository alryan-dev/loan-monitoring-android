package com.example.loanmonitoring.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.loanmonitoring.R
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.viewmodels.EntryViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

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

        // Check if there is a user logged in
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navigateMainActivity()
            return
        }

        entryViewModel.userSavedLiveData.observe(this, {
            if (it) navigateMainActivity()
        })

        // Choose authentication providers
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse

        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            saveUserDetails()
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            Utils.print("Error signing-in: " + response?.error?.errorCode.toString())
        }
    }

    private fun saveUserDetails() {
        FirebaseAuth.getInstance().currentUser?.let  { user ->
            val userMap = HashMap<String, Any>()
            userMap["uid"] = user.uid
            user.email?.also { userMap["email"] = it }
            user.displayName?.also { userMap["name"] = it }
            entryViewModel.save(userMap)
        }
    }

    private fun navigateMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}