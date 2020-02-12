package com.foreknowledge.photomemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        memo_list.setHasFixedSize(true)
        memo_list.layoutManager = LinearLayoutManager(this)

        memo_list.adapter = MemoListAdapter(getSampleMemo(this@MainActivity), object: ItemClickListener{
            override fun onClick(view: View, position: Int) {
                switchTo(DetailMemoActivity::class.java)
            }
        })

        btn_create_memo.setOnClickListener { switchTo(CreateMemoActivity::class.java) }
    }

    private fun switchTo(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }
}
