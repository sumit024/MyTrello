package com.app_devs.mytrello.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.app_devs.mytrello.R
import com.app_devs.mytrello.firebase.FireStoreClass
import com.app_devs.mytrello.models.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_in.et_email
import kotlinx.android.synthetic.main.activity_sign_in.et_password

class SignInActivity : BaseActivity() {
    //commit check
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        auth= FirebaseAuth.getInstance()

        setSupportActionBar(toolbar_sign_in_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        toolbar_sign_in_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        btn_sign_in.setOnClickListener {
            signInUser()
        }
    }

    private fun signInUser()
    {
        val email :String=et_email.text.toString().trim{it<=' '}
        val pwd:String=et_password.text.toString().trim{it<=' '}
        if(validateForm(email,pwd))
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            auth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(this){

                task->
                if(task.isSuccessful)
                {

                   // startActivity(Intent(this,MainActivity::class.java))
                    FireStoreClass().loadUserData(this)
                }
                else
                {
                    Toast.makeText(this,"SignIn Failed", Toast.LENGTH_SHORT).show()
                    hideProgressDialog()
                }
            }

        }
    }
    private fun validateForm(email:String, pwd:String):Boolean
    {
        return when{
            TextUtils.isEmpty(email)->{
                showErrorSnackBar("Enter email")
                false
            }
            TextUtils.isEmpty(pwd)->{
                showErrorSnackBar("Enter password")
                false
            }
            else->
            {
                true
            }

        }

    }
    fun signInSuccess(user:User)
    {
        hideProgressDialog()
        Toast.makeText(this,"SignIn Success", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

}