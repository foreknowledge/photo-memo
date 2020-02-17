package com.foreknowledge.photomemo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object RequestCode {
    const val PERMISSION_REQUEST_CODE = 100
    const val CHOOSE_CAMERA_IMAGE = 101
    const val CHOOSE_GALLERY_IMAGE = 102
}

fun switchTo(context: Context, activity: Class<*>, bundle: Bundle? = null) {
    val intent = Intent(context, activity)
    bundle?.let{ intent.putExtras(bundle) }

    context.startActivity(intent)
}

object FileHelper {
    fun deleteFile(filePath: String) = File(filePath).delete()

    fun deleteFiles(filePaths: List<String>) {
        for (filePath in filePaths)
            deleteFile(filePath)
    }
}

object BitmapHelper {
    fun bitmapToImageFile(context: Context, bitmap: Bitmap?, filePath: String = ""): String {
        if (bitmap == null) return ""

        val imageFile = if (filePath.isNotBlank())
            File(filePath)
        else {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        }

        FileOutputStream(imageFile)
            .use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                it.flush()

                return imageFile.absolutePath
            }
    }
}