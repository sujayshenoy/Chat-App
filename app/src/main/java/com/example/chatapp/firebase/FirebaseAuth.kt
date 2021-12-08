package com.example.chatapp.firebase

import android.app.Activity
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.wrappers.User
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.lang.Exception
import java.util.concurrent.TimeUnit

object FirebaseAuth {
    private val auth = Firebase.auth
    private val logger: Logger = LoggerImpl("FirebaseAuthentication")
    const val PHONE_VERIFY_COMPLETE = 1
    const val PHONE_VERIFY_FAILED = 2
    const val PHONE_OTP_SENT = 3

    private fun getAuthenticatedUser(): User? {
        return auth.currentUser?.let {
            User(it.uid, it.displayName.toString(), it.phoneNumber.toString().substring(3))
        }
    }

    fun sendOtp(
        phone: String,
        activity: Activity,
        resendToken: PhoneAuthProvider.ForceResendingToken? = null,
        callback: (Int, User?, String, PhoneAuthProvider.ForceResendingToken?) -> Unit
    ) {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                logger.logInfo("OnVerificationCompleted: Login Success")
                signInWithCredential(p0) {
                    callback(PHONE_VERIFY_COMPLETE, it, "", null)
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                logger.logInfo("OnVerificationFailed: Login Failed")
                p0.printStackTrace()
                when (p0) {
                    is FirebaseTooManyRequestsException -> {
                        logger.logError("Too many Requests exception")
                    }
                    is FirebaseNetworkException -> {
                        logger.logError("No Internet Connection")
                    }
                }
                callback(PHONE_VERIFY_FAILED, null, "", null)
            }

            override fun onCodeSent(
                verificationId: String,
                resendToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(verificationId, resendToken)
                logger.logInfo("OnCodeSent: true")
                callback(PHONE_OTP_SENT, null, verificationId, resendToken)
            }
        }

        val options = resendToken?.let {
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .setForceResendingToken(it)
                .build()
        } ?: PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOTP(verificationID: String, otp: String, callback: (User?) -> Unit) {
        val credential = PhoneAuthProvider.getCredential(verificationID, otp)
        signInWithCredential(credential) {
            callback(it)
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential, callback: (User?) -> Unit) {
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = getAuthenticatedUser()
                user?.let {
                    it.isNewUser = task.result?.additionalUserInfo?.isNewUser ?: false
                }
                callback(user)
            } else {
                try {
                    throw  task.exception ?: Exception("Something Went Wrong")
                } catch (ex: FirebaseException) {
                    if (ex is FirebaseAuthInvalidCredentialsException) {
                        logger.logInfo("Invalid OTP")
                    } else {
                        ex.printStackTrace()
                        logger.logError("Unknown Error")
                    }
                } finally {
                    callback(null)
                }
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
}