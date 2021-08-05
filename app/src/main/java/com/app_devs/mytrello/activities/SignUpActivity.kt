package com.app_devs.mytrello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.app_devs.mytrello.R
import com.app_devs.mytrello.firebase.FireStoreClass
import com.app_devs.mytrello.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setSupportActionBar(toolbar_sign_up_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        toolbar_sign_up_activity.setNavigationOnClickListener {
            onBackPressed()
        }

        btn_sign_up.setOnClickListener {
            registerUser()
        }

    }
    private fun registerUser()
    {
        val name:String=et_name.text.toString().trim{it<=' '}
        val email :String=et_email.text.toString().trim{it<=' '}
        val pwd:String=et_password.text.toString().trim{it<=' '}
        if(validateForm(name,email,pwd))
        {
            //Toast.makeText(this,"Registered",Toast.LENGTH_SHORT).show()
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(OnCompleteListener {
                task ->
                if(task.isSuccessful)
                {
                    val firebaseUser:FirebaseUser=task.result!!.user!!
                    val registeredEmail=firebaseUser.email!!
                    val user= User(firebaseUser.uid,name,registeredEmail)
                    FireStoreClass().registerUser(this,user)

                }
                else
                    Toast.makeText(this,task.exception!!.message,Toast.LENGTH_SHORT).show()


            })
        }

    }
    private fun validateForm(name:String, email:String, pwd:String):Boolean
    {
        return when{

            TextUtils.isEmpty(name)->{
                showErrorSnackBar("Enter name")
                false
            }
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
    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
     fun userRegisteredSuccess()
    {
        Toast.makeText(this,"Successfully Registered",Toast.LENGTH_SHORT).show()
        hideProgressDialog()
        /**
        * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
        * and send him to Intro Screen for Sign-In
        */
        FirebaseAuth.getInstance().signOut()
        finish()
    }
}