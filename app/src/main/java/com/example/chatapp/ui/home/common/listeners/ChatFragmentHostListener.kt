package com.example.chatapp.ui.home.common.listeners

interface ChatFragmentHostListener<T> {
    fun onChatItemClicked(selectedEntity: T)
}