package com.example.chatapp.ui.authentication

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.common.SharedPrefUtil
import com.example.chatapp.common.Utilities
import com.example.chatapp.data.Repository
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.firebase.FirebaseAuth
import com.example.chatapp.firebase.FirebaseAuth.Companion.PHONE_OTP_SENT
import com.example.chatapp.firebase.FirebaseAuth.Companion.PHONE_VERIFY_COMPLETE
import com.example.chatapp.firebase.FirebaseAuth.Companion.PHONE_VERIFY_FAILED
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch

class AuthenticationViewModel : ViewModel() {
    private var lastUsedPhone = ""
    private var storedVerificationId = ""
    private var storedRetryToken: PhoneAuthProvider.ForceResendingToken? = null
    var loggedInUser: User? = null

    private val _sendOtpStatus = MutableLiveData<Boolean>()
    val sendOtpStatus = _sendOtpStatus as LiveData<Boolean>

    private val _resendOtpStatus = MutableLiveData<Boolean>()
    val resendOtpStatus = _resendOtpStatus as LiveData<Boolean>

    private val _verifyUserStatus = MutableLiveData<User?>()
    val verifyUserStatus = _verifyUserStatus as LiveData<User?>

    private val _goToLoginFragment = MutableLiveData<Boolean>()
    val goToLoginFragment = _goToLoginFragment as LiveData<Boolean>

    private val _goToOtpFragment = MutableLiveData<Boolean>()
    val goToOtpFragment = _goToOtpFragment as LiveData<Boolean>

    private val _goToHomeScreen = MutableLiveData<Boolean>()
    val goToHomeScreen = _goToHomeScreen as LiveData<Boolean>

    private val _goToNewUserFragment = MutableLiveData<User>()
    val goToNewUserFragment = _goToNewUserFragment as LiveData<User>

    private val _getUserFromDbStatus = MutableLiveData<User?>()
    val getUserFromDbStatus = _getUserFromDbStatus as LiveData<User?>

    fun sendOtp(
        context: Context,
        activity: Activity,
        phone: String,
        resendToken: PhoneAuthProvider.ForceResendingToken? = null
    ) {
        FirebaseAuth.getInstance(context)
            .sendOtp(phone, activity, resendToken) { status, user, verificationID, token ->
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
                        Utilities.displayShortToast(context, "Otp Sent to $phone")
                    }

                    PHONE_VERIFY_COMPLETE, PHONE_VERIFY_FAILED -> {
                        loggedInUser = user
                        _verifyUserStatus.value = user
                    }
                }
            }
    }

    fun resendOtp(context: Context, activity: Activity) {
        sendOtp(context, activity, lastUsedPhone, storedRetryToken)
    }

    fun verifyOtp(context: Context, otp: String) {
        FirebaseAuth.getInstance(context).verifyOTP(storedVerificationId, otp) {
            loggedInUser = it
            _verifyUserStatus.value = it
        }
    }

    fun addUserToDb(context: Context, user: User) {
        viewModelScope.launch {
            Repository.getInstance(context).addUserToDB(user)
        }
    }

    fun getUserFromDB(context: Context, uid: String) {
        viewModelScope.launch {
            Repository.getInstance(context).getUserFromDB(uid).let {
                SharedPrefUtil.getInstance(context).addString(SharedPrefUtil.USER_ID, uid)
                _getUserFromDbStatus.postValue(it)
            }
        }
    }

    fun goToLoginFragment() {
        _goToLoginFragment.value = true
    }

    fun goToOtpFragment() {
        _goToOtpFragment.value = true
    }

    fun goToNewUserFragment() {
        _goToNewUserFragment.value = loggedInUser
    }

    fun goToHomeScreen() {
        _goToHomeScreen.value = true
    }
}