package com.example.notespace.models

// Model for folder or pdf in library
data class DocModel2(
    var folderName: String = "",
    var name: String = "",
    var url: String = "",
    var prePreviousFolderName: String = "",
    var previousFolderName: String = "",
    var uid: String = ""
)