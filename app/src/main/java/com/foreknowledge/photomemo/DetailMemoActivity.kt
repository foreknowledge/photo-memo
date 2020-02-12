package com.foreknowledge.photomemo

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail_memo.*

class DetailMemoActivity : AppCompatActivity() {
    private val context = this@DetailMemoActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_memo)

        fillContent()

        btn_edit.setOnClickListener { switchTo(context, CreateMemoActivity::class.java) }
        btn_delete.setOnClickListener { showAlertDialog() }
        btn_go_before.setOnClickListener { finish() }
    }

    private fun fillContent() {

    }

    private fun showAlertDialog() {
        AlertDialog.Builder(context)
            .setMessage(getString(R.string.delete_message))
            .setPositiveButton( getString(R.string.btn_ok_text) ) { _, _ -> deleteMemo() }
            .setNegativeButton( getString(R.string.btn_cancel_text) ) { dialog, _ -> dialog.dismiss() }.show()
    }

    private fun deleteMemo() {
        finish()
    }
}
