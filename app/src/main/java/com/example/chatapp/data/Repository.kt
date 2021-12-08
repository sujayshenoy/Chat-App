package com.example.chatapp.data

import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.repoInterfaces.MessageRepository
import com.example.chatapp.data.repoInterfaces.UserRepository
import com.example.chatapp.data.wrappers.Message
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.firebase.FirebaseDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.lang.Exception

class Repository : UserRepository, MessageRepository {
    private val firebaseDatabase = FirebaseDb()
    private val logger: Logger = LoggerImpl("Repository")

    override suspend fun addUserToDB(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                firebaseDatabase.addUserToDatabase(user)
            } catch (ex: Exception) {
                logger.logInfo("FireStore Exception")
                ex.printStackTrace()
                false
            }
        }
    }

    override suspend fun getUserFromDB(userId: String): User? {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                firebaseDatabase.getUserFromDatabase(userId)
            } catch (ex: Exception) {
                logger.logInfo("FireStore Exception")
                ex.printStackTrace()
                null
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getAllUsers(userId: String ): Flow<ArrayList<User>> {
        return firebaseDatabase.getAllUsersFromDatabase(userId)
    }

    override suspend fun sendMessage(senderId: String, receiverId: String, message: String): String {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                firebaseDatabase.sendTextMessage(senderId, receiverId, message)
            } catch (ex: Exception) {
                logger.logInfo("FireStore Exception")
                ex.printStackTrace()
                ""
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getMessage(senderId: String, receiverId: String): Flow<Message?> {
        return firebaseDatabase.getAllMessages(senderId, receiverId)
    }
}