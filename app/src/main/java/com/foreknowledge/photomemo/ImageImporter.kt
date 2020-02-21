package com.foreknowledge.photomemo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import java.io.File

class GalleryImageImporter(private val activity: Activity) {
    fun switchToAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE

        activity.startActivityForResult(intent, RequestCode.CHOOSE_GALLERY_IMAGE)
    }

    fun getImageFilePath(data: Uri): String {
        data.path?.let {
            activity.contentResolver.query(data, null, null, null, null)
                .use {
                    it?.let{
                        it.moveToNext()

                        return copyImage(it.getString(it.getColumnIndex("_data")))
                    }
                }
        }
        return ""
    }

    private fun copyImage(originalFilePath: String): String {
        val newFile = FileHelper.createJpgFile(activity)
        File(originalFilePath).copyTo(newFile, true)

        return newFile.absolutePath
    }
}

class CameraImageImporter(private val activity: Activity) {
    private lateinit var file: File

    fun switchToCamera() {
        file = FileHelper.createJpgFile(activity)
        val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", file)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        activity.startActivityForResult(intent, RequestCode.CHOOSE_CAMERA_IMAGE)
    }

    fun getFilePath() = file.absolutePath ?: ""
}

class UrlImageImporter {
    fun fadeIn(view: View) {
        view.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(300L)
                .setListener(null)
        }
    }

    fun fadeOut(view: View) {
        view.animate()
            .alpha(0f)
            .setDuration(300L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.visibility = View.GONE
                }
            })
    }
}