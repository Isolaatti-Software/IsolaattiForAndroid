package com.erik.isolaatti.classes

data class SessionToken(
    val expires: String,
    val created: String,
    val token: String
)
