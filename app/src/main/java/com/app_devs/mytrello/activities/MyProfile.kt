package com.app_devs.mytrello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app_devs.mytrello.R
import com.app_devs.mytrello.firebase.FireStoreClass
import com.app_devs.mytrello.models.User
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MyProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setUpActionBar()
        FireStoreClass().loadUserData(this)
    }
    private fun setUpActionBar()
    {
        setSupportActionBar(toolbar_my_profile_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title=resources.getString(R.string.my_profile)
        toolbar_my_profile_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }
    fun setProfileUI(user: User)
    {
        Glide.with(this).load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_profile_user_image)
        et_name.setText(user.name)
        et_email.setText(user.email)
        if(user.mobile!=0L)
            et_mobile.setText(user.mobile.toString())

    }
}