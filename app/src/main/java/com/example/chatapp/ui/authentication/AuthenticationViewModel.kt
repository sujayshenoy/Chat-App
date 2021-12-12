package com.example.chatapp.ui.authentication

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.repo.Repository
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.firebase.FirebaseAuth
import com.example.chatapp.firebase.FirebaseAuth.PHONE_OTP_SENT
import com.example.chatapp.firebase.FirebaseAuth.PHONE_VERIFY_COMPLETE
import com.example.chatapp.firebase.FirebaseAuth.PHONE_VERIFY_FAILED
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch

class AuthenticationViewModel : ViewModel() {
    private var lastUsedPhone = ""
    private var storedVerificationId = ""
    private var storedRetryToken: PhoneAuthProvider.ForceResendingToken? = null
    private val repository = Repository()
    var loggedInUser: User? = null

    private val _sendOtpStatus = MutableLiveData<Boolean>()
    val sendOtpStatus = _sendOtpStatus as LiveData<Boolean>

    private val _resendOtpStatus = MutableLiveData<Boolean>()
    val resendOtpStatus = _resendOtpStatus as LiveData<Boolean>

    private val _verifyUserStatus = MutableLiveData<User?>()
    val verifyUserStatus = _verifyUserStatus as LiveData<User?>

    private val _getUserFromDbStatus = MutableLiveData<User?>()
    val getUserFromDbStatus = _getUserFromDbStatus as LiveData<User?>

    fun sendOtp(
        activity: Activity,
        phone: String,
        resendToken: PhoneAuthProvider.ForceResendingToken? = null
    ) {
        FirebaseAuth.sendOtp(phone, activity, resendToken) { status, user, verificationID, token ->
            when (status) {
                PHONE_OTP_SENT -> {
                    storedVerificationId = verificationID
                    storedRetryToken = token
                    lastUsedPhone = phone
                    if (resendToken == null) {
                        _sendOtpStatus.value = true
                    } else {
                        _resendOtpStatus.value = true
                    }
                }

                PHONE_VERIFY_COMPLETE, PHONE_VERIFY_FAILED -> {
                    loggedInUser = user
                    _verifyUserStatus.value = user
                }
            }
        }
    }

    fun resendOtp(activity: Activity) {
        sendOtp(activity, lastUsedPhone, storedRetryToken)
    }

    fun verifyOtp(otp: String) {
        FirebaseAuth.verifyOTP(storedVerificationId, otp) {
            loggedInUser = it
            _verifyUserStatus.value = it
        }
    }

    fun addUserToDb(user: User) {
        viewModelScope.launch {
            repository.addUserToDB(user)
        }
    }

    fun getUserFromDB(uid: String, token: String) {
        viewModelScope.launch {
            repository.getUserFromDB(uid).let {
                if (it != null) {
                    it.messageToken = token
                    _getUserFromDbStatus.postValue(it)
                    repository.attachMessageTokenToUser(uid, token)
                }
            }
        }
    }
}