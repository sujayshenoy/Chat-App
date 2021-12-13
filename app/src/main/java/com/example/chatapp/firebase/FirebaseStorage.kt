package com.example.chatapp.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*
import kotlin.coroutines.suspendCoroutine

class FirebaseStorage {
    private val storage = Firebase.storage.reference

    companion object {
        private const val CHANNEL_REF = "channel"
        private const val USER_REF = "user"
    }

    suspend fun uploadChannelImage(channelId: String, imgByteArray: ByteArray): String {
        val uid = UUID.randomUUID().toString()
        val ref = storage.child(CHANNEL_REF).child(channelId).child(uid)
        return suspendCoroutine { continuation ->
            ref.putBytes(imgByteArray)
                .addOnFailureListener {
                    continuation.resumeWith(Result.failure(it))
                }.addOnSuccessListener { task ->
                    task.metadata?.let {
                        ref.downloadUrl.addOnSuccessListener {
                            continuation.resumeWith(Result.success(it.toString()))
                        }
                    }
                }
        }
    }

    suspend fun uploadUserAvatar(userId: String, imgByteArray: ByteArray): String {
        val ref = storage.child(USER_REF).child(userId)
        return suspendCoroutine { continuation ->
            ref.putBytes(imgByteArray)
                .addOnFailureListener {
                    continuation.resumeWith(Result.failure(it))
                }.addOnSuccessListener { task ->
                    task.metadata?.let {
                        ref.downloadUrl.addOnSuccessListener {
                            continuation.resumeWith(Result.success(it.toString()))
                        }
                    }
                }
        }
    }
}