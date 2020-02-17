package com.foreknowledge.photomemo

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            switchTo(this, MainActivity::class.java)
            finish()
        }, 1000)

    }
}
