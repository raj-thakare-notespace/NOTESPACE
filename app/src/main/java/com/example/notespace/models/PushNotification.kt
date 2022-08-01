package com.example.notespace.models

data class PushNotification(
    val data: NotificationData,
    val to: String
)