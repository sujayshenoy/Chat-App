package com.example.chatapp.data

import com.example.chatapp.data.wrappers.User

interface UserRepository {
    suspend fun addUserToDB(user: User): Boolean
    suspend fun getUserFromDB(userId: String): User?
}