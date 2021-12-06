package com.example.chatapp.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.common.Logger
import com.example.chatapp.common.SharedPrefUtil
import com.example.chatapp.common.SharedPrefUtil.Companion.USER_ID
import com.example.chatapp.data.Repository
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.firebase.FirebaseAuth
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var currentUser : User ?= null

    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus = _logoutStatus as LiveData<Boolean>

    private val _goToLoginScreen = MutableLiveData<Boolean>()
    val goToLoginScreen = _goToLoginScreen as LiveData<Boolean>

    private val _getUserFromDbStatus = MutableLiveData<User?>()
    val getUserFromDbStatus = _getUserFromDbStatus as LiveData<User?>

    fun logout(context: Context) {
        FirebaseAuth.getInstance(context).logout()
        _logoutStatus.value = true
    }

    fun goToLoginScreen() {
        _goToLoginScreen.value = true
    }

    private fun getUserFromDB(context: Context, uid: String) {
        viewModelScope.launch {
            Repository.getInstance(context).getUserFromDB(uid).let {
                currentUser = it
                _getUserFromDbStatus.postValue(it)
            }
        }
    }

    fun fetchCurrentUserData(context: Context) {
        SharedPrefUtil.getInstance(context).getString(USER_ID)?.let {
            getUserFromDB(context, it)
        } ?: Logger.getInstance(context).logError("Null User Data")
    }
}