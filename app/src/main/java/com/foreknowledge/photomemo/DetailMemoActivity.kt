package com.foreknowledge.photomemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_detail_memo.*

class DetailMemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_memo)

        fillContent()

        btn_menu.setOnClickListener {  }
        btn_go_before.setOnClickListener { finish() }
    }

    private fun fillContent() {

    }
}
