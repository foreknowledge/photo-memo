package com.foreknowledge.photomemo

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.foreknowledge.photomemo.RequestCode.CHOOSE_CAMERA_IMAGE
import com.foreknowledge.photomemo.RequestCode.CHOOSE_GALLERY_IMAGE
import kotlinx.android.synthetic.main.activity_create_memo.*
import kotlinx.android.synthetic.main.url_input_box.*

class CreateMemoActivity : AppCompatActivity() {
    private val context = this@CreateMemoActivity

    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var previewManager: PreviewImageManager

    private val cameraImageImporter = CameraImageImporter(this)
    private val galleryImageImporter = GalleryImageImporter(this)
    private val urlImageImporter = UrlImageImporter()

    private var memoId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_memo)

        init()
        setUrlInputBox()

        btn_save.setOnClickListener { saveMemo() }
        btn_cancel.setOnClickListener { goBack() }
        btn_add_image.setOnClickListener { showMenu() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goBack()
    }

    private fun goBack() {
        previewManager.revertImages()
        finish()
    }

    private fun init() {
        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        memoId = intent.getLongExtra(KeyName.MEMO_ID, 0)

        val imagePaths =
            if (memoId != 0L) {
                val memo = MemoDbTable(context).readMemo(memoId)

                if (memo.title.isNotBlank())
                    edit_memo_title.setText(memo.title)
                edit_memo_content.setText(memo.content)
                memo.imagePaths
            }
            else null

        previewManager = PreviewImageManager(this, imagePaths)

        previewManager.setPreviewItemTouchCallbackAt(preview_recycler)
        previewManager.setAdapterTo(preview_recycler)
    }

    private fun setUrlInputBox() {
        btn_hide.setOnClickListener {
            urlImageImporter.fadeOut(url_input_box)
            et_url.text.clear()

            hideKeyboard()
        }
        btn_clear.setOnClickListener { et_url.text.clear() }

        btn_adjust.setOnClickListener {
            if (previewManager.isImageFull())
                showToastMessage(Message.IMAGE_FULL)
            else if (et_url.text.toString().isBlank())
                showToastMessage(Message.VACANT_URL)
            else if (!NetworkHelper.isConnected(this))
                showToastMessage(Message.NETWORK_DISCONNECT)
            else {
                previewManager.importImageFrom(et_url.text.toString())
                loading_panel.visibility = View.VISIBLE
                et_url.text.clear()
            }

            hideKeyboard()
        }
    }

    private fun showToastMessage(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    private fun hideKeyboard() = inputMethodManager.hideSoftInputFromWindow(et_url.windowToken, 0)

    private fun showMenu() {
        hideKeyboard()

        if (previewManager.isImageFull()) {
            showToastMessage(Message.IMAGE_FULL)
            return
        }

        val options = resources.getStringArray(R.array.option_add_image)

        AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.text_add_image))
            .setItems(options){ _, i ->
                when (i) {
                    0 -> galleryImageImporter.switchToAlbum()
                    1 -> cameraImageImporter.switchToCamera()
                    2 -> urlImageImporter.fadeIn(url_input_box)
                }
            }.show()
    }

    private fun saveMemo() {
        if (edit_memo_title.isBlank() && edit_memo_content.isBlank() && previewManager.isImageEmpty() ) {
            showToastMessage(Message.VACANT_CONTENT)
        }
        else {
            val title = edit_memo_title.text.toString().trim()
            val content = edit_memo_content.text.toString()
            val images = previewManager.getImages()

            if (memoId != 0L)
                MemoDbTable(this).update(Memo(memoId, title, content, images))
            else
                MemoDbTable(this).store(title, content, images)
        }

        finish()
    }

    private fun EditText.isBlank() = this.text.toString().trim().isBlank()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (url_input_box.visibility == View.VISIBLE) url_input_box.visibility = View.GONE

        if (resultCode == Activity.RESULT_OK) {
            val resultImgPath =
                if (requestCode == CHOOSE_GALLERY_IMAGE && data != null && data.data != null)
                    galleryImageImporter.getImageFilePath(data.data!!)
                else if (requestCode == CHOOSE_CAMERA_IMAGE)
                    cameraImageImporter.getFilePath()
                else null

            resultImgPath?.let{
                previewManager.addImageToAdapter(it)
            }
        }
    }

    fun quitLoading() { loading_panel.visibility = View.INVISIBLE }
    fun focusToBottom() { scroll_view.post { scroll_view.fullScroll(ScrollView.FOCUS_DOWN) } }
}