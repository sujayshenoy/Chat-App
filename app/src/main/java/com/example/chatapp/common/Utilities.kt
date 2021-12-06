package com.example.chatapp.common

import android.content.Context
import android.widget.Toast

object Utilities {
    fun displayLongToast(context: Context, message: String) =
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    fun displayShortToast(context: Context, message: String) =
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}