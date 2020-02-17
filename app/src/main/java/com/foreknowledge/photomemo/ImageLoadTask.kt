package com.foreknowledge.photomemo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import java.net.URL

class ImageLoadTask(private val context: Context, private val urlStr: String, private val adapter: PreviewImageListAdapter) : AsyncTask<Void, Void, Bitmap>() {
    companion object {
        private val bitmapHash = mutableMapOf<String, Bitmap>()
    }

    override fun doInBackground(vararg p0: Void): Bitmap? =
        if (bitmapHash.containsKey(urlStr)) bitmapHash[urlStr]
        else {
            try {
                val bitmap = BitmapFactory.decodeStream(URL(urlStr).openConnection().getInputStream())
                bitmap?.let {
                    bitmapHash[urlStr] = it
                    it
                }
            } catch (e: Exception) { null }
        }

    override fun onPostExecute(bitmap: Bitmap?) {
        super.onPostExecute(bitmap)

        if (bitmap != null)
            adapter.addImagePath(BitmapHelper.bitmapToImageFile(context, bitmap))
        else
            adapter.showErrorMessage("이미지 URL이 잘못 되었습니다.")
    }

}