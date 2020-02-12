package com.foreknowledge.photomemo

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val context = this@MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        memo_list.setHasFixedSize(true)
        memo_list.layoutManager = LinearLayoutManager(context)

        memo_list.adapter = MemoListAdapter(getSampleMemo(context), object: ItemClickListener{
            override fun onClick(view: View, position: Int) {
                switchTo(context, DetailMemoActivity::class.java)
            }
        })

        btn_create_memo.setOnClickListener { switchTo(context, CreateMemoActivity::class.java) }
    }
}

fun switchTo(context: Context, activity: Class<*>) {
    val intent = Intent(context, activity)
    context.startActivity(intent)
}
