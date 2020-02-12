package com.foreknowledge.photomemo

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_create_memo.*

class CreateMemoActivity : AppCompatActivity() {
    private val context = this@CreateMemoActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_memo)

        images_grid.setHasFixedSize(true)
        images_grid.layoutManager = GridLayoutManager(context, 4)
        images_grid.adapter = ImageListAdapter(context, getSampleImages())

        btn_cancel.setOnClickListener { finish() }
        btn_add_image.setOnClickListener { showMenu() }
    }

    fun saveMemo(v: View) {
        if (edit_memo_title.text.toString().isBlank())
            Snackbar.make(v, "제목은 필수 입력 사항입니다.", Snackbar.LENGTH_SHORT).show()

    }

    private fun showMenu() {
        val options = resources.getStringArray(R.array.option_add_image)

        AlertDialog.Builder(context)
            .setTitle(resources.getString(R.string.text_add_image))
            .setItems(options){ _, i ->
                when (i) {
                    0 -> Toast.makeText(context, "사진촬영", Toast.LENGTH_SHORT).show()
                    1 -> Toast.makeText(context, "갤러리", Toast.LENGTH_SHORT).show()
                    2 -> Toast.makeText(context, "이미지 url", Toast.LENGTH_SHORT).show()
                }
            }.show()
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