package com.example.chatapp.data.repo.repoInterfaces

import com.example.chatapp.data.wrappers.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun addUserToDB(user: User): Boolean
    suspend fun getUserFromDB(userId: String): User?
    suspend fun getUsersFromUserIds(userIds: ArrayList<String>): ArrayList<User>
    fun getAllUsers(userId: String): Flow<ArrayList<User>>
}