package com.erik.isolaatti.classes

data class MakePostData(
    val privacy: Int,
    val content: String,
    val theme: PostTheme,
    val audioUrl: String?
)
