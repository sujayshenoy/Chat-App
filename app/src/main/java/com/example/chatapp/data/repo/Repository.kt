package com.example.chatapp.data.repo

import com.example.chatapp.common.CONTENT_TYPE_IMG
import com.example.chatapp.common.CONTENT_TYPE_TEXT
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.models.DbMessage
import com.example.chatapp.data.models.DbUser
import com.example.chatapp.data.models.DbUser.Companion.FIREBASE_MESSAGE_TOKEN
import com.example.chatapp.data.network.retrofit.PushContent
import com.example.chatapp.data.network.retrofit.PushMessage
import com.example.chatapp.data.network.retrofit.PushNotificationSenderService
import com.example.chatapp.data.repo.repoInterfaces.GroupRepository
import com.example.chatapp.data.repo.repoInterfaces.MessageRepository
import com.example.chatapp.data.repo.repoInterfaces.UserRepository
import com.example.chatapp.data.wrappers.Group
import com.example.chatapp.data.wrappers.Message
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.firebase.FirebaseAuth
import com.example.chatapp.firebase.FirebaseDb
import com.example.chatapp.firebase.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class Repository : UserRepository, MessageRepository, GroupRepository {
    private val firebaseDatabase = FirebaseDb()
    private val firebaseStorage = FirebaseStorage()
    private val logger: Logger = LoggerImpl("Repository")
    private val pushNotificationSenderService = PushNotificationSenderService()

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

    suspend fun setUserAvatar(userId: String, imgByteArray: ByteArray): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = firebaseStorage.uploadUserAvatar(userId, imgByteArray)
                logger.logInfo("Avatar url = $url")
                updateUserAvatar(userId, url)
                return@withContext url
            } catch (ex: Exception) {
                logger.logError("FireStorage Exception")
                ex.printStackTrace()
                ""
            }
        }
    }

    suspend fun attachMessageTokenToUser(userId: String, token: String) {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                firebaseDatabase.attachMessageTokenToUser(userId, token)
            } catch (ex: Exception) {
                logger.logError("FireStore Exception")
                ex.printStackTrace()
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

    override fun getAllUsers(userId: String): Flow<ArrayList<User>> {
        return firebaseDatabase.getAllUsersFromDatabase(userId)
    }

    override suspend fun getUsersFromUserIds(userIds: ArrayList<String>): ArrayList<User> {
        return withContext(Dispatchers.IO) {
            val userList = ArrayList<User>()
            for (id in userIds) {
                try {
                    firebaseDatabase.getUserFromDatabase(id).let {
                        it?.let {
                            userList.add(it)
                        }
                    }
                } catch (ex: java.lang.Exception) {
                    logger.logError("Unable to fetch user")
                    ex.printStackTrace()
                }
            }
            return@withContext userList
        }
    }

    suspend fun updateUserName(userId: String, newName: String) {
        val updates = mapOf(
            DbUser.USER_NAME to newName
        )
        withContext(Dispatchers.IO) {
            firebaseDatabase.updateUser(userId, updates)
        }
    }

    private suspend fun updateUserAvatar(userId: String, imageUrl: String) =
        coroutineScope {
            val updates = mapOf(
                DbUser.USER_AVATAR to imageUrl
            )
            firebaseDatabase.updateUser(userId, updates)
        }

    override suspend fun sendTextMessage(
        senderId: String,
        receiverId: String,
        channelId: String,
        message: String
    ): Message? {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val messageData = DbMessage(
                    senderId,
                    message,
                    CONTENT_TYPE_TEXT,
                    System.currentTimeMillis()
                )
                val channel = if (channelId.isEmpty()) {
                    firebaseDatabase.getChannelId(senderId, receiverId)
                } else channelId
                firebaseDatabase.sendMessage(channel, messageData)
            } catch (ex: Exception) {
                logger.logError("FireStore Exception")
                ex.printStackTrace()
                null
            }
        }
    }

    override fun getMessages(senderId: String, receiverId: String): Flow<Message?> {
        return firebaseDatabase.getAllMessages(senderId, receiverId)
    }

    suspend fun getMessagesBefore(
        senderId: String,
        receiverId: String,
        timeStamp: Long,
    ): ArrayList<Message> {
        return firebaseDatabase.getMessagesBefore(senderId, receiverId, timeStamp)
    }

    override suspend fun sendGroupTextMessage(
        senderId: String,
        channelId: String,
        message: String
    ): Message? {
        return sendTextMessage(senderId, "", channelId, message)
    }

    override fun getGroupMessages(channelId: String): Flow<Message?> {
        return getMessages(channelId, "")
    }

    suspend fun getGroupMessageBefore(
        channelId: String,
        timeStamp: Long,
    ): ArrayList<Message> {
        return getMessagesBefore(channelId, "", timeStamp)
    }

    override suspend fun createGroup(groupName: String, members: ArrayList<User>): String {
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

    override fun getAllGroups(userId: String): Flow<ArrayList<Group>> {
        return firebaseDatabase.getAllGroups(userId)
    }

    override suspend fun sendImageMessage(
        senderId: String,
        receiverId: String,
        channelId: String,
        imgByteArray: ByteArray
    ): Message? {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val channel = if (channelId.isEmpty()) {
                    firebaseDatabase.getChannelId(senderId, receiverId)
                } else channelId
                val imgUrl = firebaseStorage.uploadChannelImage(channel, imgByteArray)
                val messageData = DbMessage(
                    senderId,
                    imgUrl,
                    CONTENT_TYPE_IMG,
                    System.currentTimeMillis()
                )
                firebaseDatabase.sendMessage(channel, messageData)
            } catch (ex: Exception) {
                logger.logError("firestore exception")
                ex.printStackTrace()
                null
            }
        }
    }

    override suspend fun sendGroupImageMessage(
        senderId: String,
        channelId: String,
        imgByteArray: ByteArray
    ): Message? {
        return sendImageMessage(senderId, "", channelId, imgByteArray)
    }

    override suspend fun sendPushNotificationToUser(
        userToken: String,
        title: String,
        message: String,
        imageUrl: String
    ) {
        return withContext(Dispatchers.IO) {
            val pushContent = PushContent(message, title, imageUrl)
            val pushMessage = PushMessage(userToken, pushContent)
            logger.logInfo(
                pushNotificationSenderService.sendPushNotification(pushMessage).toString()
            )
        }
    }

    override suspend fun sendPushNotificationToGroup(
        members: ArrayList<String>,
        title: String,
        message: String,
        imageUrl: String
    ) {
        return withContext(Dispatchers.IO) {
            for (i in members) {
                sendPushNotificationToUser(i, title, message, imageUrl)
            }
        }
    }

    suspend fun logout(userId: String) {
        return withContext(Dispatchers.IO) {
            val update = mapOf(
                FIREBASE_MESSAGE_TOKEN to ""
            )
            firebaseDatabase.updateUser(userId, update)
            FirebaseAuth.logout()
        }
    }
}