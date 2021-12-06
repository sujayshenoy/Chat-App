package com.example.chatapp.data

import android.content.Context
import com.example.chatapp.common.Logger
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.firebase.FirebaseDb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class Repository(private val context: Context) : UserRepository {
    private val firebaseDatabase = FirebaseDb.getInstance(context)
    private val logger = Logger.getInstance(context)

    companion object {
        private val INSTANCE: Repository? by lazy { null }

        fun getInstance(context: Context) = INSTANCE ?: Repository(context)
    }

    override suspend fun addUserToDB(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                firebaseDatabase.addUserToDatabase(user)
            } catch (ex: Exception) {
                logger.logInfo("Repository: FireStore Exception")
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
                logger.logInfo("Repository: FireStore Exception")
                ex.printStackTrace()
                null
            }
        }
    }
}