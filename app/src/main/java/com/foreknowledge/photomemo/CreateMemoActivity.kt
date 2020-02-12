package com.foreknowledge.photomemo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_create_memo.*


class CreateMemoActivity : AppCompatActivity() {
    private val context = this@CreateMemoActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_memo)

        images_grid.setHasFixedSize(true)
        images_grid.layoutManager = GridLayoutManager(context, 4)
        images_grid.adapter = ImageListAdapter(context, getSampleImages())

        btn_save_memo.setOnClickListener {  }
        btn_cancel.setOnClickListener { finish() }
    }

    private fun getSampleImages(): List<Bitmap> {
        return listOf(
            BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_01),
            BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_02),
            BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_03),
            BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_04),
            BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_05),
            BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_06),
            BitmapFactory.decodeResource(context.resources, R.drawable.travel_sample_07)
        )
    }
}