package com.foreknowledge.photomemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class ImageLoadTask(private val context: Context, private val urlStr: String, private val adapter: PreviewImageListAdapter) : AsyncTask<Void, Void, Bitmap>() {
    companion object {
        private val bitmapHash = mutableMapOf<String, Bitmap>()
    }

    override fun doInBackground(vararg p0: Void): Bitmap? =
        if (bitmapHash.containsKey(urlStr)) bitmapHash[urlStr]
        else {
            val bitmap = BitmapFactory.decodeStream(URL(urlStr).openConnection().getInputStream())
            bitmapHash[urlStr] = bitmap
            bitmap
        }

    override fun onPostExecute(bitmap: Bitmap?) {
        super.onPostExecute(bitmap)

        adapter.addImagePath(bitmapToImageFile(context, bitmap))
    }

}

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