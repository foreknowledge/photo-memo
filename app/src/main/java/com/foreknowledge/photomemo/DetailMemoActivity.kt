package com.foreknowledge.photomemo

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_detail_memo.*

class DetailMemoActivity : AppCompatActivity() {
    private val context = this@DetailMemoActivity

    private var memoId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_memo)

        btn_edit.setOnClickListener {
            val bundle = Bundle()
            bundle.putLong(KeyName.MEMO_ID, memoId)

            switchTo(context, CreateMemoActivity::class.java, bundle)
        }
        btn_delete.setOnClickListener { showAlertDialog() }
        btn_go_before.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()

        fillContent()
    }

    private fun fillContent() {
        memoId = intent.getLongExtra(KeyName.MEMO_ID, 0)

        val memo = MemoDbTable(this).readMemo(memoId)

        if (memo.title.isBlank())
            text_memo_title.visibility = View.GONE
        else {
            text_memo_title.visibility = View.VISIBLE
            text_memo_title.text = memo.title
        }

        text_memo_content.text = memo.content

        image_list.layoutManager = LinearLayoutManager(this)
        image_list.adapter = DetailImageListAdapter(this, memo.imagePaths, memoId)
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(context)
            .setMessage(getString(R.string.delete_message))
            .setPositiveButton( getString(R.string.btn_ok_text) ) { _, _ -> deleteMemo() }
            .setNegativeButton( getString(R.string.btn_cancel_text) ) { dialog, _ -> dialog.dismiss() }.show()
    }

    private fun deleteMemo() {
        MemoDbTable(this).remove(memoId)
        finish()
    }
}
