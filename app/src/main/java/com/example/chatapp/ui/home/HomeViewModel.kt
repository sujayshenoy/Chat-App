package com.example.chatapp.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.Repository
import com.example.chatapp.data.wrappers.Group
import com.example.chatapp.data.wrappers.User
import com.example.chatapp.firebase.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class HomeViewModel(uid: String) : ViewModel() {
    private val repository = Repository()
    var currentUser : User ?= null
    var userList: ArrayList<User> = ArrayList()
    var groupList: ArrayList<Group> = ArrayList()
    private val logger: Logger = LoggerImpl("HomeViewModel")

    private val _logoutStatus = MutableLiveData<Boolean>()
    val logoutStatus = _logoutStatus as LiveData<Boolean>

    private val _getUserFromDbStatus = MutableLiveData<User?>()
    val getUserFromDbStatus = _getUserFromDbStatus as LiveData<User?>

    private val _groupListChanged = MutableLiveData<Boolean>()
    val groupListChanged = _groupListChanged as LiveData<Boolean>

    private val _userListChanged = MutableLiveData<Boolean>()
    val userListChanged = _userListChanged as LiveData<Boolean>

    init {
        getUserFromDB(uid)
        getGroupList(uid)
        getUserList(uid)
    }

    fun logout() {
        FirebaseAuth.logout()
        _logoutStatus.value = true
    }

    private fun getUserFromDB(uid: String) {
        viewModelScope.launch {
            repository.getUserFromDB(uid)?.let {
                currentUser = it
                _getUserFromDbStatus.postValue(it)
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun getUserList(userId: String) {
        viewModelScope.launch {
            repository.getAllUsers(userId).collect {
                userList.clear()
                userList.addAll(it)
                _userListChanged.value = true
            }
        }
    }

    @ExperimentalCoroutinesApi
    private fun getGroupList(userId: String) {
        viewModelScope.launch {
            repository.getAllGroups(userId).collect {
                groupList.clear()
                groupList.addAll(it)
                _groupListChanged.value = true
            }
        }
    }
}