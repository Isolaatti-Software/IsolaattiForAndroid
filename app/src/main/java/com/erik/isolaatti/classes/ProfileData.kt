package com.erik.isolaatti.classes

data class ProfileData(
    val username: String,
    val email: String?,
    val description: String?,
    val color: String,
    val audioUrl: String?,
    val numberOfPosts: Int,
    val profilePictureUrl: String,
    val numberOfFollowers: Int,
    val numberOfFollowings: Int,
    val numberOfLikes: Int
)
