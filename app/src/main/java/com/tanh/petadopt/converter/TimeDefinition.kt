package com.tanh.petadopt.converter

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

object TimeDefinition {

    fun formatDate(timestamp: Timestamp): String {
        val dateTime = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val currentDateTime = LocalDateTime.now()
        return if (dateTime.year == currentDateTime.year &&
            dateTime.month == currentDateTime.month &&
            dateTime.dayOfMonth == currentDateTime.dayOfMonth) {
            "${dateTime.hour}:${dateTime.minute.toString().padStart(2, '0')}"
        } else {
            "${dateTime.dayOfMonth}/${dateTime.monthValue}"
        }
    }

}