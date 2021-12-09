package com.example.chatapp.ui.groupChat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.data.Repository
import com.example.chatapp.data.wrappers.Group
import com.example.chatapp.data.wrappers.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class GroupChatViewModel(private val group: Group): ViewModel() {
    val messageList = ArrayList<Message>()

    private val _sendMessageStatus = MutableLiveData<String>()
    val sendMessageStatus = _sendMessageStatus as LiveData<String>

    private val _newMessageStatus = MutableLiveData<Boolean>()
    val newMessageStatus = _newMessageStatus as LiveData<Boolean>

    init {
        getMessage(group.channelId)
    }

    fun sendMessage(senderId: String, message: String) {
        viewModelScope.launch {
            Repository().sendGroupMessage(senderId, group.channelId, message).let {
                if(it.isNotEmpty()) {
                    _sendMessageStatus.postValue(it)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getMessage(channelId: String) {
        viewModelScope.launch {
            Repository().getGroupMessages(channelId).collect {
                it?.let {
                    messageList.add(it)
                    _newMessageStatus.postValue(true)
                }
            }
        }
    }
}