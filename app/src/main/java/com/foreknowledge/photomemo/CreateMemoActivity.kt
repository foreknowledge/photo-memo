package com.foreknowledge.photomemo

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.foreknowledge.photomemo.ImageListAdapter.Companion.MAX_IMAGE_COUNT
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_memo.*
import java.io.InputStream

class CreateMemoActivity : AppCompatActivity() {
    private val context = this@CreateMemoActivity

    companion object {
        const val CHOOSE_IMAGE_FROM_CAMERA = 101
        const val CHOOSE_IMAGE_FROM_GALLERY = 102
    }

    private val imagesAdapter = ImageListAdapter(context, mutableListOf())

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
                    0 -> Toast.makeText(context, "사진촬영", Toast.LENGTH_SHORT).show()
                    1 -> chooseImageFromGallery()
                    2 -> Toast.makeText(context, "이미지 url", Toast.LENGTH_SHORT).show()
                }
            }.show()
    }

    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE

        startActivityForResult(intent, CHOOSE_IMAGE_FROM_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            if (requestCode == CHOOSE_IMAGE_FROM_GALLERY) {
                val imageBitmap = tryReadBitmap(data.data!!)

                imageBitmap?.let {
                    if (imagesAdapter.addImage(imageBitmap))
                        imagesAdapter.notifyDataSetChanged()
                    else
                        Toast.makeText(context, "이미지 첨부는 ${MAX_IMAGE_COUNT}개까지만 가능합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun tryReadBitmap(data: Uri): Bitmap? {
        val inputStream: InputStream? = contentResolver.openInputStream(data)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        return bitmap
    }
}