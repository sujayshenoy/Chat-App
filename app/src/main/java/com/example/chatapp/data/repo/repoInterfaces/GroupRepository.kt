package com.example.chatapp.data.repo.repoInterfaces

import com.example.chatapp.data.wrappers.Group
import com.example.chatapp.data.wrappers.User
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun createGroup(groupName: String, members: ArrayList<User>): String
    fun getAllGroups(userId: String): Flow<ArrayList<Group>>
}