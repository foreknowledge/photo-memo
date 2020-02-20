package com.foreknowledge.photomemo

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.foreknowledge.photomemo.RequestCode.CHOOSE_CAMERA_IMAGE
import com.foreknowledge.photomemo.RequestCode.CHOOSE_GALLERY_IMAGE
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_memo.*
import kotlinx.android.synthetic.main.url_input_box.*
import java.io.File

class CreateMemoActivity : AppCompatActivity(), PreviewImageListAdapter.OnStartDragListener {
    private val context = this@CreateMemoActivity

    private lateinit var previewAdapter: PreviewImageListAdapter
    private lateinit var inputMethodManager: InputMethodManager
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var file: File

    private var memoId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_memo)

        setDefaultContentIfExists()
        setPreviewItemTouchCallback()

        inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        preview_recycler.setHasFixedSize(true)
        preview_recycler.layoutManager = GridLayoutManager(context, 4)
        preview_recycler.adapter = previewAdapter

        btn_cancel.setOnClickListener { goBack() }
        btn_add_image.setOnClickListener { showMenu() }

        setUrlInputBox()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        goBack()
    }

    private fun goBack() {
        previewAdapter.undo()
        finish()
    }

    fun quitLoading() { loadingPanel.visibility = View.INVISIBLE }
    fun focusToBottom() { scroll_view.post { scroll_view.fullScroll(ScrollView.FOCUS_DOWN) } }

    fun saveMemo(v: View) {
        hideKeyboard()

        if (edit_memo_title.text.toString().trim().isBlank()) {
            Snackbar.make(v, Message.VACANT_TITLE, Snackbar.LENGTH_SHORT).show()
            return
        }

        val title = edit_memo_title.text.toString()
        val content = edit_memo_content.text.toString()
        val images = previewAdapter.reflect()

        if (memoId != 0L)
            MemoDbTable(this).update(Memo(memoId, title, content, images))
        else
            MemoDbTable(this).store(title, content, images)

        finish()
    }

    private fun setDefaultContentIfExists() {
        memoId = intent.getLongExtra(KeyName.MEMO_ID, 0)

        previewAdapter =
            if (memoId != 0L) {
                val memo = MemoDbTable(this).readMemo(memoId)

                edit_memo_title.setText(memo.title)
                edit_memo_content.setText(memo.content)

                PreviewImageListAdapter(context, memo.imagePaths.toMutableList(), this)
            } else
                PreviewImageListAdapter(context, mutableListOf(), this)
    }

    private fun setPreviewItemTouchCallback() {
        val callback = PreviewItemTouchCallback(previewAdapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(preview_recycler)
    }

    private fun setUrlInputBox() {
        btn_hide.setOnClickListener {
            fadeOutUrlInputBox()
            et_url.text.clear()

            hideKeyboard()
        }
        btn_clear.setOnClickListener { et_url.text.clear() }

        btn_adjust.setOnClickListener {
            if (previewAdapter.isFull())
                showToastMessage(Message.IMAGE_FULL)
            else if (et_url.text.toString().isBlank())
                showToastMessage(Message.VACANT_URL)
            else if (!NetworkHelper.isConnected(this))
                showToastMessage(Message.NETWORK_DISCONNECT)
            else {
                ImageLoadTask(this, et_url.text.toString(), previewAdapter).execute()
                loadingPanel.visibility = View.VISIBLE
                et_url.text.clear()
            }

            hideKeyboard()
        }
    }

    private fun showToastMessage(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun hideKeyboard() = inputMethodManager.hideSoftInputFromWindow(et_url.windowToken, 0)

    private fun showMenu() {
        hideKeyboard()

        if (previewAdapter.isFull()) {
            showToastMessage(Message.IMAGE_FULL)
            return
        }

        val options = resources.getStringArray(R.array.option_add_image)

        AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.text_add_image))
            .setItems(options){ _, i ->
                when (i) {
                    0 -> switchToAlbum()
                    1 -> switchToCamera()
                    2 -> fadeInUrlInputBox()
                }
            }.show()
    }

    private fun switchToAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE

        startActivityForResult(intent, CHOOSE_GALLERY_IMAGE)
    }

    private fun switchToCamera() {
        file = FileHelper.createJpgFile(this)
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

        startActivityForResult(intent, CHOOSE_CAMERA_IMAGE)
    }

    private fun fadeInUrlInputBox() {
        url_input_box.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(300L)
                .setListener(null)
        }
    }

    private fun fadeOutUrlInputBox() {
        url_input_box.animate()
            .alpha(0f)
            .setDuration(300L)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    url_input_box.visibility = View.GONE
                }
            })
    }

    private fun getImageFilePath(data: Uri): String {
        data.path?.let {
            contentResolver.query(data, null, null, null, null)
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
        val newFile = FileHelper.createJpgFile(this)
        File(originalFilePath).copyTo(newFile, true)

        return newFile.absolutePath
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (url_input_box.visibility == View.VISIBLE) url_input_box.visibility = View.GONE

        if (resultCode == Activity.RESULT_OK) {
            var resultImgPath = ""

            if (requestCode == CHOOSE_GALLERY_IMAGE && data != null && data.data != null)
                resultImgPath = getImageFilePath(data.data!!)
            else if (requestCode == CHOOSE_CAMERA_IMAGE)
                resultImgPath = file.absolutePath

            previewAdapter.addImagePath(BitmapHelper.rotateAndCompressImage(resultImgPath))
        }
    }

    override fun onStartDrag(viewHolder: ImageListAdapter.ImageViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }
}