package com.foreknowledge.photomemo

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_detail_memo.*

class DetailMemoActivity : AppCompatActivity() {
    private val context = this@DetailMemoActivity

    private var memoId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_memo)

        fillContent()

        btn_edit.setOnClickListener { switchTo(context, CreateMemoActivity::class.java) }
        btn_delete.setOnClickListener { showAlertDialog() }
        btn_go_before.setOnClickListener { finish() }
    }

    private fun fillContent() {
        memoId = intent.getLongExtra("memoId", 0)

        val memoList = MemoDbTable(this).readMemo(memoId)

        text_memo_title.text = memoList.title
        text_memo_content.text = memoList.content

        image_list.setHasFixedSize(true)
        image_list.layoutManager = LinearLayoutManager(this)
        image_list.adapter = DetailImageListAdapter(this, memoList.images)
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(context)
            .setMessage(getString(R.string.delete_message))
            .setNegativeButton( getString(R.string.btn_ok_text) ) { _, _ -> deleteMemo() }
            .setPositiveButton( getString(R.string.btn_cancel_text) ) { dialog, _ -> dialog.dismiss() }.show()
    }

    private fun deleteMemo() {
        MemoDbTable(this).remove(memoId)
        finish()
    }
}
