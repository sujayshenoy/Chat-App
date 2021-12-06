package com.example.chatapp.firebase

import android.content.Context
import com.example.chatapp.data.models.DbUser
import com.example.chatapp.data.wrappers.User
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.suspendCoroutine

class FirebaseDb(private val context: Context) {
    private val fireStore = Firebase.firestore

    companion object {
        private val INSTANCE: FirebaseDb? by lazy { null }
        private const val USER_COLLECTION = "users"

        fun getInstance(context: Context) = INSTANCE ?: FirebaseDb(context)
    }

    suspend fun addUserToDatabase(user: User): Boolean {
        return suspendCoroutine {
            val dbUser = DbUser(user.name, user.phone)
            fireStore.collection(USER_COLLECTION).document(user.id).set(dbUser)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.resumeWith(Result.success(true))
                    } else {
                        it.resumeWith(
                            Result.failure(
                                task.exception
                                    ?: FirebaseException("Unknown Error!! Something went wrong")
                            )
                        )
                    }
                }
        }
    }

    suspend fun getUserFromDatabase(uid: String): User? {
        return suspendCoroutine { continuation ->
            fireStore.collection(USER_COLLECTION).document(uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { snapshot ->
                            if (!snapshot.exists()) {
                                continuation.resumeWith(Result.success(null))
                            } else {
                                snapshot.data as HashMap
                                val user = User(
                                    uid,
                                    snapshot["name"].toString(),
                                    snapshot["phone"].toString()
                                )
                                continuation.resumeWith(Result.success(user))
                            }
                        }
                    } else {
                        continuation.resumeWith(
                            Result.failure(
                                task.exception
                                    ?: FirebaseException("Unknown Error!! Something went wrong")
                            )
                        )
                    }
                }
        }
    }
}