package com.example.chatapp.data

import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.models.DbMessage
import com.example.chatapp.data.repoInterfaces.MessageRepository
import com.example.chatapp.data.repoInterfaces.UserRepository
import com.example.chatapp.data.wrappers.Group
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
                logger.logError("FireStore Exception")
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
                logger.logError("FireStore Exception")
                ex.printStackTrace()
                null
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getAllUsers(userId: String): Flow<ArrayList<User>> {
        return firebaseDatabase.getAllUsersFromDatabase(userId)
    }

    suspend fun sendMessage(
        senderId: String,
        receiverId: String,
        channelId: String = "",
        message: String
    ): String {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val messageData = DbMessage(
                    senderId,
                    message,
                    FirebaseDb.CONTENT_TEXT,
                    System.currentTimeMillis()
                )
                val channel = if(channelId.isEmpty()) {
                    firebaseDatabase.getChannelId(senderId, receiverId)
                } else channelId
                firebaseDatabase.sendTextMessage(channel, messageData)
            } catch (ex: Exception) {
                logger.logError("FireStore Exception")
                ex.printStackTrace()
                ""
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getMessages(senderId: String, receiverId: String): Flow<Message?> {
        return firebaseDatabase.getAllMessages(senderId, receiverId)
    }

    suspend fun sendGroupMessage(senderId: String, channelId: String, message: String): String {
        return sendMessage(senderId, "" , channelId, message)
    }

    @ExperimentalCoroutinesApi
    fun getGroupMessages(channelId: String): Flow<Message?> {
        return getMessages(channelId, "")
    }

    suspend fun createGroup(groupName: String, members: ArrayList<User>): String {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                firebaseDatabase.createGroup(groupName, members)
            } catch (ex: Exception) {
                logger.logError("firestore exception")
                ex.printStackTrace()
                ""
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getAllGroups(userId: String): Flow<ArrayList<Group>> {
        return firebaseDatabase.getAllGroups(userId)
    }
}