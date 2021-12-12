package com.example.chatapp.ui.home.peerchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.common.CONTENT_TYPE_TEXT
import com.example.chatapp.data.repo.Repository
import com.example.chatapp.data.wrappers.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class PeerChatViewModel(senderId: String, receiverId: String) : ViewModel() {
    val messageList = ArrayList<Message>()
    private val repository = Repository()

    private val _sendMessageStatus = MutableLiveData<Message>()
    val sendMessageStatus = _sendMessageStatus as LiveData<Message>

    private val _newMessageStatus = MutableLiveData<Boolean>()
    val newMessageStatus = _newMessageStatus as LiveData<Boolean>

    init {
        getMessage(senderId, receiverId)
    }

    fun sendMessage(senderId: String, receiverId: String, message: String) {
        viewModelScope.launch {
            repository.sendTextMessage(senderId, receiverId, "", message).let {
                if (it != null) {
                    _sendMessageStatus.postValue(it)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getMessage(senderId: String, receiverId: String) {
        viewModelScope.launch {
            repository.getMessages(senderId, receiverId).collect {
                it?.let {
                    messageList.add(it)
                    _newMessageStatus.postValue(true)
                }
            }
        }
    }

    fun sendImageMessage(senderId: String, receiverId: String, imgByteArray: ByteArray) {
        viewModelScope.launch {
            repository.sendImageMessage(senderId, receiverId, "", imgByteArray).let {
                if (it != null) {
                    _sendMessageStatus.postValue(it)
                }
            }
        }
    }

    fun sendPushNotification(to: String, title: String, message: Message) {
        viewModelScope.launch {
            if (message.contentType == CONTENT_TYPE_TEXT) {
                repository.sendPushNotificationToUser(to, title, message.content, "")
            } else {
                repository.sendPushNotificationToUser(to, title, "", message.content)
            }
        }
    }
}