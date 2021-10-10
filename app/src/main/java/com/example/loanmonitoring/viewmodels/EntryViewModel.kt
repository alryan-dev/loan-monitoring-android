package com.example.loanmonitoring.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    var loginSuccessLiveData = MutableLiveData<Boolean>()

    init {
        loginSuccessLiveData.value = false
    }

    fun save(user: HashMap<String, Any>) {
        val task = userRepository.save(user)
        task.addOnSuccessListener {
            loginSuccessLiveData.value = true
        }.addOnFailureListener { e ->
            Utils.print("Error adding document: " + e.message)
        }
    }
}