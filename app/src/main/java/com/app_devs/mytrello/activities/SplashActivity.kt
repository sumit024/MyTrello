package com.app_devs.mytrello.activities

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.app_devs.mytrello.R
import com.app_devs.mytrello.firebase.FireStoreClass
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val typeface= Typeface.createFromAsset(assets,"carbon bl.ttf")
        tv_app_name.typeface=typeface

        Handler(Looper.getMainLooper()).postDelayed({
            val currentUserId=FireStoreClass().getCurrentUserId()
            if(currentUserId.isNotEmpty())
                startActivity(Intent(this, MainActivity::class.java))
            else
                startActivity(Intent(this, IntroActivity::class.java))

            //we do finish so nothing happens once user press back btn
            finish()
        }, 1500)

    }
}