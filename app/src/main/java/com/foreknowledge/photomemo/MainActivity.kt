package com.foreknowledge.photomemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        memo_list.setHasFixedSize(true)
        memo_list.layoutManager = LinearLayoutManager(this)

    }

    fun createMemo(v: View) {
        val intent = Intent(this, CreateMemoActivity::class.java)
        startActivity(intent)
    }
}
