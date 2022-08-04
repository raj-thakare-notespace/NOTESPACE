package com.startup.notespace.models

// Model for chat Message
data class MessageModel(
    var message: String? = null,
    var senderId: String? = null,
    var receiverId: String? = null,
    var time: Long? = 0L,
    var groupUsername: String? = null,
    var groupUid : String? = null,
    var createdBy : String? = null
)
