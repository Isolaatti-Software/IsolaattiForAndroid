package com.erik.isolaatti.classes

import java.util.Date

data class Post(
    val id: Long,
    val username: String,
    val userId: Int,
    val liked: Boolean,
    val content: String,
    val numberOfLikes: Int,
    val numberOfComments: Int,
    val privacy: Int,
    val audioUrl: String?,
    val timeStamp: Date,
    val title: String?,
    val description: String?,
    val language: String?
)
