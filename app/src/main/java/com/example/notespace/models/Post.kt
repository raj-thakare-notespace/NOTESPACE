package com.example.notespace.models

// Model for Post
data class Post(
    var postId: String = "",
    var postImage: String = "",
    var postedBy: String? = "",
    var postDescription: String? = "",
    var postedAt: Long = 0L,
    var postLike: Long = 0,
    var docName: String = "",
    var docUrl: String = "",
    var docThumbnail : String = ""
)

