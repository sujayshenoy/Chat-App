package com.example.chatapp.firebase

import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.models.DbGroup
import com.example.chatapp.data.models.DbGroup.Companion.GROUP_CHANNEL
import com.example.chatapp.data.models.DbGroup.Companion.GROUP_MEM
import com.example.chatapp.data.models.DbGroup.Companion.GROUP_NAME
import com.example.chatapp.data.models.DbMessage
import com.example.chatapp.data.models.DbMessage.Companion.CONTENT
import com.example.chatapp.data.models.DbMessage.Companion.CONTENT_TYPE
import com.example.chatapp.data.models.DbMessage.Companion.SENDER_ID
import com.example.chatapp.data.models.DbMessage.Companion.TIMESTAMP
import com.example.chatapp.data.models.DbUser
import com.example.chatapp.data.models.DbUser.Companion.USER_NAME
import com.example.chatapp.data.models.DbUser.Companion.USER_PHONE
import com.example.chatapp.data.wrappers.Group
import com.example.chatapp.data.wrappers.Message
import com.example.chatapp.data.wrappers.User
import com.google.firebase.FirebaseException
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.coroutines.suspendCoroutine

class FirebaseDb {
    private val fireStore = Firebase.firestore
    private val logger: Logger = LoggerImpl("FirebaseDb")

    companion object {
        const val USER_COLLECTION = "users"
        const val CHANNEL_COLLECTION = "channels"
        const val MESSAGE_COLLECTION = "messages"
        const val GROUP_COLLECTION = "groups"
        const val CONTENT_TEXT = "text/plain"
    }

    suspend fun addUserToDatabase(user: User): Boolean {
        return suspendCoroutine {
            val dbUser = DbUser(user.name, user.phone, user.messageToken)
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
            val channelId = if(receiverId.isEmpty()) senderId else getChannelId(senderId, receiverId)
            val ref = fireStore.collection(CHANNEL_COLLECTION).document(channelId)
                .collection(MESSAGE_COLLECTION).orderBy(TIMESTAMP)
                .addSnapshotListener { snapshot, e ->
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

    suspend fun sendTextMessage(
        channelId: String,
        message: DbMessage,
    ): String {
        return suspendCoroutine {
            fireStore.collection(CHANNEL_COLLECTION).document(channelId).collection(
                MESSAGE_COLLECTION
            )
                .add(message).addOnCompleteListener { task ->
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

    fun getChannelId(senderId: String, receiverId: String): String {
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

    suspend fun createGroup(groupName: String, members: ArrayList<User>): String {
        return suspendCoroutine {
            val channelId = fireStore.collection(CHANNEL_COLLECTION).document().id
            val memberIds = ArrayList<String>()
            members.forEach {
                memberIds.add(it.id)
            }
            val dbGroup = DbGroup(groupName, channelId, memberIds)
            fireStore.collection(GROUP_COLLECTION).add(dbGroup).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let { ref ->
                        it.resumeWith(Result.success(ref.id))
                    }
                } else {
                    it.resumeWith(
                        Result.failure(
                            task.exception ?: Exception("Something went wrong")
                        )
                    )
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getAllGroups(userId: String): Flow<ArrayList<Group>> {
        return callbackFlow {
            val ref = fireStore.collection(GROUP_COLLECTION).whereArrayContains(GROUP_MEM, userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        logger.logError("Listener attach failed")
                    } else {
                        val groups = ArrayList<Group>()
                        snapshot?.let {
                            for (i in it.documents) {
                                i.data?.let { data ->
                                    val group = Group(
                                        i.id,
                                        data[GROUP_NAME].toString(),
                                        data[GROUP_CHANNEL].toString(),
                                        data[GROUP_MEM] as ArrayList<String>
                                    )
                                    groups.add(group)
                                }
                            }
                            this.trySend(groups).isSuccess
                        }
                    }
                }
            awaitClose {
                ref.remove()
            }
        }
    }
}