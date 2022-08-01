package com.example.notespace.models

data class DocPostModel(
    var docName: String = "",
    var docUrl: String = "",
    var postDescription: String? = "",
    var postId: String = "",
    var postedAt: Long = 0L,
    var postedBy: String? = "",
)
