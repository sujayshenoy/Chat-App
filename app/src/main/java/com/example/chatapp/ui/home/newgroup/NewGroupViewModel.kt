package com.example.chatapp.ui.home.newgroup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.Repository
import com.example.chatapp.data.wrappers.User
import kotlinx.coroutines.launch

class NewGroupViewModel : ViewModel() {
    private val repository = Repository()

    private val _createGroupStatus = MutableLiveData<Boolean>()
    val createGroupStatus = _createGroupStatus as LiveData<Boolean>

    fun createGroup(groupName: String, members: ArrayList<User>) {
        viewModelScope.launch {
            repository.createGroup(groupName, members).let {
                _createGroupStatus.value = true
            }
        }
    }
}