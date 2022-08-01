package com.example.notespace.models

// Model for user in search
data class UserSearch(
    var uid : String = "",
    var username: String = "",
    var displayName: String = "",
    var profilePicture: String? = ""
)