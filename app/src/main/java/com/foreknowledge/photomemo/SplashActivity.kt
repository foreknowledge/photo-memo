package com.foreknowledge.photomemo

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

/*
[사용한 외부 라이브러리]
'com.github.pedroSG94:AutoPermissions:1.0.3'
'com.github.chrisbanes:PhotoView:2.1.3'
*/

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            switchTo(this, MainActivity::class.java)
            finish()
        }, 800)

    }
}
