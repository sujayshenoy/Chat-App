package com.example.chatapp.ui.home

interface ChatFragmentHostListener<T> {
    fun onChatItemClicked(selectedEntity: T)
}