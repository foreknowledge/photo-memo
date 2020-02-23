package com.foreknowledge.photomemo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
import com.foreknowledge.photomemo.PreviewImageListAdapter.Companion.MAX_IMAGE_COUNT
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object RequestCode {
    const val PERMISSION_REQUEST_CODE = 100
    const val CHOOSE_CAMERA_IMAGE = 101
    const val CHOOSE_GALLERY_IMAGE = 102
}

object KeyName {
    const val MEMO_ID = "memoId"
    const val POSITION = "position"
}

object Message {
    const val IMAGE_FULL = "이미지 첨부는 ${MAX_IMAGE_COUNT}개까지만 가능합니다."
    const val VACANT_URL = "Url 주소를 입력해 주세요."
    const val NETWORK_DISCONNECT = "네트워크 연결 상태를 확인 해 주세요."
    const val VACANT_CONTENT = "입력한 내용이 없어 메모를 저장하지 않았어요."
}

fun switchTo(context: Context, activity: Class<*>, extras: Bundle? = null) {
    val intent = Intent(context, activity)
    extras?.let{ intent.putExtras(it) }

    context.startActivity(intent)
}

@Suppress("DEPRECATION")
object NetworkHelper {
    fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= 23) {
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            capabilities?.let {
                return it.hasTransport(TRANSPORT_WIFI) || it.hasTransport(TRANSPORT_CELLULAR)
            }
        }
        else {
            connectivityManager.activeNetworkInfo?.let {
                return it.isConnectedOrConnecting
            }
        }

        return false
    }
}

object FileHelper {
    fun createJpgFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    fun deleteFile(filePath: String) = File(filePath).delete()

    fun deleteFiles(filePaths: List<String>) {
        for (filePath in filePaths)
            deleteFile(filePath)
    }
}

object BitmapHelper {
    fun bitmapToImageFile(context: Context, bitmap: Bitmap): String {
        val imageFile = FileHelper.createJpgFile(context)

        return compressBitmapToImageFile(imageFile.absolutePath, bitmap)
    }

    fun rotateAndCompressImage(filePath: String): String {
        val options = BitmapFactory.Options()
        if (File(filePath).length() > 1000000)
            options.inSampleSize = 2

        val rotatedBitmap = BitmapFactory.decodeFile(filePath, options).getRotatedBitmap(filePath)

        return compressBitmapToImageFile(filePath, rotatedBitmap)
    }

    private fun compressBitmapToImageFile(imagePath: String, bitmap: Bitmap): String {
        FileOutputStream(imagePath)
            .use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it)
                it.flush()

                return imagePath
            }
    }

    private fun Bitmap.getRotatedBitmap(photoPath: String): Bitmap {
        val exifInterface = ExifInterface(photoPath)
        val orientation: Int = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> this.rotateImage(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> this.rotateImage(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> this.rotateImage(270f)
            else -> this
        }
    }

    private fun Bitmap.rotateImage(angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    }
}