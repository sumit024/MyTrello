package com.app_devs.mytrello.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app_devs.mytrello.R
import com.app_devs.mytrello.firebase.FireStoreClass
import com.app_devs.mytrello.models.User
import com.app_devs.mytrello.utils.Constants
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import java.io.IOException
import java.util.jar.Manifest

class MyProfile : BaseActivity() {

    companion object{
        private const val READ_EXTERNAL_STORAGE_CODE=1
        private const val IMAGE_PICK_REQUEST_CODE =2
    }
    private var mSelectedImageUri:Uri?=null
    private var mProfileImageURL:String=""

    private lateinit var mUserDetails:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_profile)
        setUpActionBar()
        FireStoreClass().loadUserData(this)
        iv_profile_user_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                showImageChooser()
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_EXTERNAL_STORAGE_CODE)

            }
        }

        btn_update.setOnClickListener {
            if(mSelectedImageUri!=null) {
                uploadUserImage()
            }
            else {

                showProgressDialog(resources.getString(R.string.please_wait))
                // Call a function to update user details in the database.
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== READ_EXTERNAL_STORAGE_CODE)
        {
            if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                showImageChooser()
            }
            else
            {
                Toast.makeText(this,"You've denied permission. You can enable it in app's settings.",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showImageChooser()
    {
        val intent=Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode== IMAGE_PICK_REQUEST_CODE && data!!.data!=null)
        {
            mSelectedImageUri=data.data
            try {
                Glide.with(this).load(mSelectedImageUri)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(iv_profile_user_image)
            }catch (e:IOException){
                e.printStackTrace()
            }

        }
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
        mUserDetails=user


    }

    private fun getFileExtension(uri:Uri?):String?
    {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
    private fun uploadUserImage()
    {
        showProgressDialog(resources.getString(R.string.please_wait))
        if(mSelectedImageUri!=null)
        {
            val storageReference=FirebaseStorage.getInstance()
                    .reference
                    .child("User Image"+ System.currentTimeMillis()+"."+getFileExtension(mSelectedImageUri))
            storageReference.putFile(mSelectedImageUri!!).addOnSuccessListener {

                taskSnapShot->
                hideProgressDialog()
                Log.e("Firebase Image Url",taskSnapShot.metadata!!.reference!!.downloadUrl.toString())
                // Get the downloadable url from the task snapshot
                taskSnapShot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()

                            // Call a function to update user details in the database.
                            updateUserProfileData()

                        }

            }.addOnFailureListener{
                exception->
                hideProgressDialog()
                Toast.makeText(this,exception.message,Toast.LENGTH_LONG).show()
            }


        }
    }

    fun profileUpdateSuccess()
    {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateUserProfileData()
    {
        val userHashMap=HashMap<String,Any>()
        if(mProfileImageURL.isNotEmpty() && mProfileImageURL!=mUserDetails.image)
        {
            userHashMap[Constants.IMAGE]=mProfileImageURL
        }
        if (et_name.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = et_name.text.toString()
        }

        if (et_mobile.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = et_mobile.text.toString().toLong()
        }

        // Update the data in the database.
        FireStoreClass().updateUserProfileData(this, userHashMap)
    }
}