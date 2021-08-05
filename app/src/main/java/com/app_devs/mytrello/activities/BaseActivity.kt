package com.app_devs.mytrello.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.app_devs.mytrello.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_progress.*

open class BaseActivity : AppCompatActivity() {
    private var doubleBackToExitPressedOnce=false
    private lateinit var mProgressDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun showProgressDialog(text:String)
    {
        mProgressDialog= Dialog(this)

        mProgressDialog.setContentView(R.layout.dialog_progress)
        mProgressDialog.tv_progress_text.text=text
        mProgressDialog.show()

    }
    fun hideProgressDialog()
    {
        mProgressDialog.dismiss()
    }
    fun getCurrentUserId():String
    {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
    fun doubleBackToExit()
    {
        if(doubleBackToExitPressedOnce)
        {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce=true
        Toast.makeText(this,"Please press back once more to exit",Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({doubleBackToExitPressedOnce=false},
                2000
        )
    }

    fun showErrorSnackBar(message:String)
    {
        val snackbar=Snackbar.make(findViewById(android.R.id.content),
                message,
                Snackbar.LENGTH_LONG)
        val snackbarView = snackbar.view
        snackbarView.setBackgroundColor(ContextCompat.getColor(this,R.color.snackbar_error_color))
        snackbar.show()

    }
}