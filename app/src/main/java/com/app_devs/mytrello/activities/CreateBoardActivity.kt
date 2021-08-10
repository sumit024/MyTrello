package com.app_devs.mytrello.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app_devs.mytrello.R
import com.app_devs.mytrello.utils.Constants
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_create_board.*
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException

class CreateBoardActivity : AppCompatActivity() {
    private var mSelectedImageUri:Uri?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_board)
        setUpActionBar()
        iv_board_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                Constants.showImageChooser(this)
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        Constants.READ_EXTERNAL_STORAGE_CODE)

            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== Constants.READ_EXTERNAL_STORAGE_CODE)
        {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Constants.showImageChooser(this)
            }
            else
            {
                Toast.makeText(this,"You've denied permission. You can enable it in app's settings.", Toast.LENGTH_LONG).show()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK && requestCode== Constants.IMAGE_PICK_REQUEST_CODE && data!!.data!=null)
        {
            mSelectedImageUri=data.data
            try {
                Glide.with(this).load(mSelectedImageUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_board_place_holder)
                        .into(iv_board_image)
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
    }
    private fun setUpActionBar()
    {
        setSupportActionBar(toolbar_create_board_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title=resources.getString(R.string.create_board_title)
        toolbar_create_board_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

}