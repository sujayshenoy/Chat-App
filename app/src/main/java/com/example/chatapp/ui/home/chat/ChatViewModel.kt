package com.example.chatapp.ui.home.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.Repository
import com.example.chatapp.data.wrappers.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class ChatViewModel(userId: String) : ViewModel() {
    var userList: ArrayList<User> = ArrayList()

    init {
        getUserList(userId)
    }

    private val _userListChanged = MutableLiveData<Boolean>()
    val userListChanged = _userListChanged as LiveData<Boolean>

    @ExperimentalCoroutinesApi
    private fun getUserList(userId: String) {
        viewModelScope.launch {
            Repository().getAllUsers(userId).collect {
                userList.clear()
                userList.addAll(it)
                _userListChanged.value = true
            }
        }
    }
}