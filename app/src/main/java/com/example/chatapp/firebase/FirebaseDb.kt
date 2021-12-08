package com.example.chatapp.firebase

import android.util.Log
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.models.DbMessage
import com.example.chatapp.data.models.DbMessage.Companion.CONTENT
import com.example.chatapp.data.models.DbMessage.Companion.CONTENT_TYPE
import com.example.chatapp.data.models.DbMessage.Companion.RECEIVER_ID
import com.example.chatapp.data.models.DbMessage.Companion.SENDER_ID
import com.example.chatapp.data.models.DbMessage.Companion.TIMESTAMP
import com.example.chatapp.data.models.DbUser
import com.example.chatapp.data.models.DbUser.Companion.USER_NAME
import com.example.chatapp.data.models.DbUser.Companion.USER_PHONE
import com.example.chatapp.data.wrappers.Message
import com.example.chatapp.data.wrappers.User
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.lang.Error
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.coroutines.suspendCoroutine

class FirebaseDb {
    private val fireStore = Firebase.firestore
    private val logger: Logger = LoggerImpl("FirebaseDb")

    companion object {
        private const val USER_COLLECTION = "users"
        private const val CHANNEL_COLLECTION = "channels"
        private const val MESSAGE_COLLECTION = "messages"
        private const val CONTENT_TEXT = "text/plain"
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

    @ExperimentalCoroutinesApi
    fun getAllMessages(
        senderId: String,
        receiverId: String,
    ): Flow<Message?> {
        return callbackFlow {
            val channelId = getChannelId(senderId, receiverId)
            val ref = fireStore.collection(CHANNEL_COLLECTION).document(channelId)
                .collection(MESSAGE_COLLECTION).orderBy(TIMESTAMP).addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        this.trySend(null).isFailure
                        e.printStackTrace()
                        logger.logError("Error attaching snapshot listener")
                    } else {
                        snapshot?.let {
                            for (i in it.documentChanges) {
                                if (i.type == DocumentChange.Type.ADDED) {
                                    i.document.data.let { data ->
                                        val message = Message(
                                            data[SENDER_ID].toString(),
                                            data[RECEIVER_ID].toString(),
                                            data[CONTENT].toString(),
                                            data[CONTENT_TYPE].toString(),
                                            data[TIMESTAMP] as Long
                                        )
                                        this.trySend(message).isSuccess
                                    }
                                }
                            }
                        }
                    }
                }
            awaitClose {
                ref.remove()
            }
        }
    }

    suspend fun sendTextMessage(senderId: String, receiverId: String, message: String): String {
        val channelId = getChannelId(senderId, receiverId)
        val messageData = DbMessage(
            senderId,
            receiverId,
            message,
            CONTENT_TEXT,
            System.currentTimeMillis()
        )
        return suspendCoroutine {
            fireStore.collection(CHANNEL_COLLECTION).document(channelId).collection(
                MESSAGE_COLLECTION
            )
                .add(messageData).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.let { ref ->
                            it.resumeWith(Result.success(ref.id))
                        }
                    } else {
                        it.resumeWith(
                            Result.failure(
                                task.exception ?: FirebaseException("Something Went Wrong")
                            )
                        )
                    }
                }
        }
    }

    private fun getChannelId(senderId: String, receiverId: String): String {
        return if (senderId < receiverId) {
            senderId + "_" + receiverId
        } else {
            receiverId + "_" + senderId
        }
    }

    @ExperimentalCoroutinesApi
    fun getAllUsersFromDatabase(userId: String): Flow<ArrayList<User>> = callbackFlow {
        val ref = fireStore.collection(USER_COLLECTION).addSnapshotListener { snapshot, error ->
            if (error != null) {
                this.trySend(ArrayList()).isFailure
                error.printStackTrace()
            } else {
                val userList = ArrayList<User>()
                snapshot?.let {
                    for (i in it.documents) {
                        if (i.id == userId) {
                            continue
                        } else {
                            i.data?.let { data ->
                                val user = User(
                                    i.id,
                                    data[USER_NAME].toString(),
                                    data[USER_PHONE].toString()
                                )
                                userList.add(user)
                            }
                        }
                    }
                    this.trySend(userList).isSuccess
                }
            }
        }
        awaitClose {
            ref.remove()
        }
    }
}