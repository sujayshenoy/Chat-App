package com.example.chatapp.ui.home.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.common.logger.Logger
import com.example.chatapp.common.logger.LoggerImpl
import com.example.chatapp.data.repo.Repository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = Repository()
    private val logger: Logger = LoggerImpl(this::class.java.simpleName)

    private val _setUserAvatarStatus = MutableLiveData<String>()
    val setUserAvatarStatus = _setUserAvatarStatus as LiveData<String>

    fun updateUserName(userId: String, newName: String) {
        viewModelScope.launch {
            repository.updateUserName(userId, newName)
        }
    }

    fun setUserAvatar(userId: String, imgByteArray: ByteArray) {
        viewModelScope.launch {
            repository.setUserAvatar(userId, imgByteArray).let {
                logger.logInfo("Avatar url = $it")
                _setUserAvatarStatus.postValue(it)
            }
        }
    }
}