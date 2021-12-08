package com.example.chatapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.Repository
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.firebase.FirebaseAuth
import kotlinx.coroutines.launch

class HomeViewModel(uid: String) : ViewModel() {
    var currentUser : User ?= null

    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus = _logoutStatus as LiveData<Boolean>

    private val _getUserFromDbStatus = MutableLiveData<User?>()
    val getUserFromDbStatus = _getUserFromDbStatus as LiveData<User?>

    init {
        getUserFromDB(uid)
    }

    fun logout() {
        FirebaseAuth.logout()
        _logoutStatus.value = true
    }

    private fun getUserFromDB(uid: String) {
        viewModelScope.launch {
            Repository().getUserFromDB(uid)?.let {
                currentUser = it
                _getUserFromDbStatus.postValue(it)
            }
        }
    }
}