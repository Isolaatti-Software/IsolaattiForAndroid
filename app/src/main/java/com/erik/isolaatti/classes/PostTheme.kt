package com.erik.isolaatti.classes

data class Border(
    val color: String,
    val type: String,
    val size: Int,
    val radius: Int
)

data class Background(
    val type: String,
    val colors: ArrayList<String>,
    val direction: Int
)


data class PostTheme(
    val existing: Boolean,
    val fontColor: String,
    val backgroundColor: String,
    val gradient: String,
    val border: Border,
    val background: Background
)
