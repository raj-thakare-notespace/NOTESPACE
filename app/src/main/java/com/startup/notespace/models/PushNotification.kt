package com.startup.notespace.models

data class PushNotification(
    val data: NotificationData,
    val to: String
)