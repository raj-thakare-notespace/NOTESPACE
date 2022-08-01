package com.example.notespace.models

data class GroupMemberModel(
    var groupUid : String = "",
    var uid: String = "",
    var createdBy : String = "",
    var username: String = "",
    var displayName: String = "",
    var profilePicture: String = ""
)