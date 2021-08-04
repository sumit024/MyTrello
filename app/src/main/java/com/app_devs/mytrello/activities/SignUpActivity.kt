package com.app_devs.mytrello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app_devs.mytrello.R
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        setSupportActionBar(toolbar_sign_up_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        toolbar_sign_up_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}