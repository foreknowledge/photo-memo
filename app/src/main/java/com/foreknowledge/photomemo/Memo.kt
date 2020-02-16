package com.foreknowledge.photomemo

data class Memo(val id: Long, val title: String, val content: String, val imagePaths: List<String> = listOf())