package com.foreknowledge.photomemo

import android.graphics.Bitmap

data class Memo(val id: Long, val title: String, val content: String, val images: List<Bitmap> = listOf())