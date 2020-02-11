package com.foreknowledge.photomemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class CreateMemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_memo)
    }

    fun saveMemo(v: View) {

    }

    fun cancel(v: View) {
        finish()
    }
}
