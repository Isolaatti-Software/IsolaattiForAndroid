package com.erik.isolaatti.classes

import java.util.Date

data class Comment(
    val id: Long,
    val content: String,
    val authorId: Int,
    val authorName: String,
    val postId: Long,
    val targetUserId: Int,
    val privacy: Int,
    val audioUrl: String?,
    val timeStamp: Date
)
