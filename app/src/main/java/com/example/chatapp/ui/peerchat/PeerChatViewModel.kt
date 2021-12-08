package com.example.chatapp.ui.peerchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.Repository
import com.example.chatapp.data.wrappers.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class PeerChatViewModel(senderId: String, receiverId: String): ViewModel() {
    val messageList = ArrayList<Message>()

    private val _sendMessageStatus = MutableLiveData<String>()
    val sendMessageStatus = _sendMessageStatus as LiveData<String>

    private val _newMessageStatus = MutableLiveData<Boolean>()
    val newMessageStatus = _newMessageStatus as LiveData<Boolean>

    init {
        getMessage(senderId, receiverId)
    }

    fun sendMessage(senderId: String, receiverId: String, message: String) {
        viewModelScope.launch {
            Repository().sendMessage(senderId, receiverId, message).let {
                if(it.isNotEmpty()) {
                    _sendMessageStatus.postValue(it)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getMessage(senderId: String, receiverId: String) {
        viewModelScope.launch {
            Repository().getMessage(senderId, receiverId).collect {
                it?.let {
                    messageList.add(it)
                    _newMessageStatus.postValue(true)
                }
            }
        }
    }
}