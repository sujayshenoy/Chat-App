package com.example.chatapp.common

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

object Utilities {
    fun displayLongToast(context: Context, message: String) =
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()

    fun displayShortToast(context: Context, message: String) =
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

    private fun getDateFormatter() = SimpleDateFormat("dd-MMM-yy; HH:mm:ss; z",Locale.getDefault())

    fun dateToString(date: Date) = getDateFormatter().format(date)

    fun stringToDate(string: String) = getDateFormatter().parse(string)?: Date()
}