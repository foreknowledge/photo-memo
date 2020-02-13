package com.foreknowledge.photomemo

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.GridLayoutManager
import com.foreknowledge.photomemo.ImageListAdapter.Companion.MAX_IMAGE_COUNT
import com.foreknowledge.photomemo.RequestCode.CHOOSE_CAMERA_IMAGE
import com.foreknowledge.photomemo.RequestCode.CHOOSE_GALLERY_IMAGE
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_memo.*
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class CreateMemoActivity : AppCompatActivity() {
    private val context = this@CreateMemoActivity
    private val imagesAdapter = ImageListAdapter(context, mutableListOf())
    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_memo)

        images_grid.setHasFixedSize(true)
        images_grid.layoutManager = GridLayoutManager(context, 4)
        images_grid.adapter = imagesAdapter

        btn_cancel.setOnClickListener { finish() }
        btn_add_image.setOnClickListener { showMenu() }
    }

    fun saveMemo(v: View) {
        if (edit_memo_title.text.toString().isBlank())
            Snackbar.make(v, "제목은 필수 입력 사항입니다.", Snackbar.LENGTH_SHORT).show()

    }

    private fun showMenu() {
        val options = resources.getStringArray(R.array.option_add_image)

        AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.text_add_image))
            .setItems(options){ _, i ->
                when (i) {
                    0 -> setPicturedImage()
                    1 -> setGalleryImage()
                    2 -> Toast.makeText(context, "이미지 url", Toast.LENGTH_SHORT).show()
                }
            }.show()
    }

    private fun setPicturedImage() {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)

        val uri: Uri = FileProvider.getUriForFile(this, "com.foreknowledge.photomemo.fileprovider", file)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        startActivityForResult(intent, CHOOSE_CAMERA_IMAGE)
    }

    private fun setGalleryImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE

        startActivityForResult(intent, CHOOSE_GALLERY_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var imageBitmap: Bitmap? = null

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_GALLERY_IMAGE && data != null && data.data != null)
                imageBitmap = tryReadBitmap(data.data!!)
            else if (requestCode == CHOOSE_CAMERA_IMAGE)
                imageBitmap = BitmapFactory.decodeFile(file.absolutePath)?.getRotateBitmap(file.absolutePath)
        }

        imageBitmap?.let {
            if (imagesAdapter.addImage(imageBitmap))
                imagesAdapter.notifyDataSetChanged()
            else
                Toast.makeText(context, "이미지 첨부는 ${MAX_IMAGE_COUNT}개까지만 가능합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun tryReadBitmap(data: Uri): Bitmap? {
        val inputStream: InputStream? = contentResolver.openInputStream(data)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        return bitmap
    }

    private fun Bitmap.getRotateBitmap(photoPath: String): Bitmap {
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