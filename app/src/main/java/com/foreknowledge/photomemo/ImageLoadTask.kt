package com.foreknowledge.photomemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.net.URL

class ImageLoadTask(private val urlStr: String, private val adapter: ImageListAdapter) : AsyncTask<Void, Void, Bitmap>() {
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
        adapter.addImage(bitmap)
    }

}