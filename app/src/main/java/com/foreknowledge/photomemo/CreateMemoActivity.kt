package com.foreknowledge.photomemo

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.recyclerview.widget.GridLayoutManager
import com.foreknowledge.photomemo.RequestCode.CHOOSE_CAMERA_IMAGE
import com.foreknowledge.photomemo.RequestCode.CHOOSE_GALLERY_IMAGE
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_memo.*
import kotlinx.android.synthetic.main.url_input_box.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CreateMemoActivity : AppCompatActivity() {
    private val context = this@CreateMemoActivity
    private val imagesAdapter = PreviewImageListAdapter(context, mutableListOf())
    private lateinit var file: File

    private var memoId: Long = 0

    private lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_memo)

        fillContentIfExists()

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        images_grid.setHasFixedSize(true)
        images_grid.layoutManager = GridLayoutManager(context, 4)
        images_grid.adapter = imagesAdapter

        btn_cancel.setOnClickListener { finish() }
        btn_add_image.setOnClickListener { showMenu() }

        setUrlInputBox()
    }

    private fun fillContentIfExists() {
        memoId = intent.getLongExtra("memoId", 0)

        if (memoId != 0L) {
            val memo = MemoDbTable(this).readMemo(memoId)

            edit_memo_title.setText(memo.title)
            edit_memo_content.setText(memo.content)

            imagesAdapter.addImagePaths(memo.imagePaths)
        }
    }

    fun saveMemo(v: View) {
        hideKeyboard()

        if (edit_memo_title.text.toString().trim().isBlank()) {
            Snackbar.make(v, "제목은 필수 입력 사항입니다.", Snackbar.LENGTH_SHORT).show()
            return
        }

        val title = edit_memo_title.text.toString()
        val content = edit_memo_content.text.toString()
        val images = imagesAdapter.getAllItems()

        if (memoId != 0L) {
            MemoDbTable(this).update(Memo(memoId, title, content, images))
            Toast.makeText(this, "편집되었습니다.", Toast.LENGTH_SHORT).show()
        }
        else {
            MemoDbTable(this).store(title, content, images)
            Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    private fun showMenu() {
        hideKeyboard()

        val options = resources.getStringArray(R.array.option_add_image)

        AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.text_add_image))
            .setItems(options){ _, i ->
                when (i) {
                    0 -> switchToAlbum()
                    1 -> switchToCamera()
                    2 -> url_input_box.visibility = View.VISIBLE
                }
            }.show()
    }

    private fun switchToAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE

        startActivityForResult(intent, CHOOSE_GALLERY_IMAGE)
    }

    private fun switchToCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, createUri())

        startActivityForResult(intent, CHOOSE_CAMERA_IMAGE)
    }

    private fun createUri(): Uri {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        file = File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)

        return FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
    }

    private fun setUrlInputBox() {
        btn_hide.setOnClickListener {
            url_input_box.visibility = View.GONE
            et_url.text.clear()

            hideKeyboard()
        }
        btn_clear.setOnClickListener { et_url.text.clear() }

        btn_adjust.setOnClickListener {
            if (et_url.text.toString().isBlank())
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            else
                ImageLoadTask(this, et_url.text.toString(), imagesAdapter).execute()
            et_url.text.clear()

            hideKeyboard()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (url_input_box.visibility == View.VISIBLE) url_input_box.visibility = View.GONE

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CHOOSE_GALLERY_IMAGE && data != null && data.data != null) {
                imagesAdapter.addImagePath(getImageFilePath(data.data!!))
            } else if (requestCode == CHOOSE_CAMERA_IMAGE) {
                imagesAdapter.addImagePath(getImageFilePath(file.absolutePath))
            }
        }
    }

    private fun hideKeyboard() = inputMethodManager.hideSoftInputFromWindow(et_url.windowToken, 0)

    private fun getImageFilePath(filePath: String): String {
        val bitmap = BitmapFactory.decodeFile(filePath)?.getRotateBitmap(filePath)
        return bitmapToImageFile(context, bitmap, filePath)
    }

    private fun getImageFilePath(data: Uri): String {
        data.path?.let {
            contentResolver.query(data, null, null, null, null)
                .use {
                    it?.let{
                        it.moveToNext()
                        return it.getString(it.getColumnIndex("_data"))
                    }
                }
        }
        return ""
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