package com.example.chatapp.ui.home.chat

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.data.Repository
import com.example.chatapp.data.wrappers.User

class ChatViewModel : ViewModel() {
    var userList: ArrayList<User>? = null

    private val _getUserListStatus = MutableLiveData<Boolean>()
    val getUserListStatus = _getUserListStatus as LiveData<Boolean>

    fun getUserList(context: Context) {
        if(userList != null) {
            _getUserListStatus.value = true
        } else {
            Repository.getInstance(context).getAllUsers {
                userList = it
                _getUserListStatus.value = true
            }
        }
    }
}