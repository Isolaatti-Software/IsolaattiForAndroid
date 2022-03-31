package com.erik.isolaatti.classes

data class SessionTokenValidated(
    val isValid: Boolean,
    val userId: Int
)
