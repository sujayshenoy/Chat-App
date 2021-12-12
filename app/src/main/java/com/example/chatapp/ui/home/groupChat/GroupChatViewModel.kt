package com.example.chatapp.ui.home.groupChat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.common.CONTENT_TYPE_TEXT
import com.example.chatapp.data.repo.Repository
import com.example.chatapp.data.wrappers.Group
import com.example.chatapp.data.wrappers.Message
import com.example.chatapp.data.wrappers.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class GroupChatViewModel(private val group: Group) : ViewModel() {
    private val repository = Repository()
    val messageList = ArrayList<Message>()
    val memberList = ArrayList<User>()

    private val _sendMessageStatus = MutableLiveData<Message>()
    val sendMessageStatus = _sendMessageStatus as LiveData<Message>

    private val _newMessageStatus = MutableLiveData<Boolean>()
    val newMessageStatus = _newMessageStatus as LiveData<Boolean>

    private val _fetchMemberListStatus = MutableLiveData<Boolean>()
    val fetchMembeListStatus = _fetchMemberListStatus as LiveData<Boolean>

    init {
        getMessage(group.channelId)
        getUsersFromUserIds()
    }

    fun sendMessage(senderId: String, message: String) {
        viewModelScope.launch {
            repository.sendGroupTextMessage(senderId, group.channelId, message).let {
                if (it != null) {
                    _sendMessageStatus.postValue(it)
                }
            }
        }
    }

    fun sendImageMessage(senderId: String, imgByteArray: ByteArray) {
        viewModelScope.launch {
            repository.sendGroupImageMessage(senderId, group.channelId, imgByteArray).let {
                if (it != null) {
                    _sendMessageStatus.postValue(it)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    fun getMessage(channelId: String) {
        viewModelScope.launch {
            repository.getGroupMessages(channelId).collect {
                it?.let {
                    messageList.add(it)
                    _newMessageStatus.postValue(true)
                }
            }
        }
    }

    private fun getUsersFromUserIds() {
        viewModelScope.launch {
            repository.getUsersFromUserIds(group.members).let {
                memberList.addAll(it)
                _fetchMemberListStatus.postValue(true)
            }
        }
    }

    fun sendPushNotification(title: String, message: Message) {
        val memberTokens = ArrayList<String>()
        memberList.forEach {
            memberTokens.add(it.messageToken)
        }
        viewModelScope.launch {
            if (message.contentType == CONTENT_TYPE_TEXT) {
                repository.sendPushNotificationToGroup(memberTokens, title, message.content, "")
            } else {
                repository.sendPushNotificationToGroup(memberTokens, title, "", message.content)
            }
        }
    }
}