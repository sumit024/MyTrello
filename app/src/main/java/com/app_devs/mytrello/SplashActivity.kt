package com.app_devs.mytrello

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val typeface= Typeface.createFromAsset(assets,"carbon bl.ttf")
        tv_app_name.typeface=typeface

    }
}