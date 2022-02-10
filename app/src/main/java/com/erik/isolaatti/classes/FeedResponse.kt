package com.erik.isolaatti.classes

data class FeedResponse(
    val lastPostId: Long,
    val moreContent: Boolean,
    val posts: ArrayList<Post>
)