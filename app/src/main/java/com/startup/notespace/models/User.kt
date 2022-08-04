package com.startup.notespace.models

// Model for user
data class User(
    var uid: String = "",
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var displayName: String = "",
    var imageUrl: String? = "",
    var profilePicture: String? = "",
    var bio: String = "",
    var profession: String = "",
    var accountPrivate : Boolean = false
)
