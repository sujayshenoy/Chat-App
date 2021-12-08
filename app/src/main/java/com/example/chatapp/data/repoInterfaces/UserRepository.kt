package com.example.chatapp.data.repoInterfaces

import com.example.chatapp.data.wrappers.User

interface UserRepository {
    suspend fun addUserToDB(user: User): Boolean
    suspend fun getUserFromDB(userId: String): User?
}