package com.startup.notespace.models

// Model for group
data class Group(
    var uid : String = "",
    var createdBy : String = "",
    var username: String = "",
    var displayName: String = "",
    var profilePicture: String? = "",
    var bio: String = "",
    var joinCode : String = ""
)