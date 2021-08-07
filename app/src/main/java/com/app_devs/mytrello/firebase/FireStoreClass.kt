package com.app_devs.mytrello.firebase

import android.app.Activity
import android.util.Log
import com.app_devs.mytrello.activities.MainActivity
import com.app_devs.mytrello.activities.MyProfile
import com.app_devs.mytrello.activities.SignInActivity
import com.app_devs.mytrello.activities.SignUpActivity
import com.app_devs.mytrello.models.User
import com.app_devs.mytrello.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {
    private val mFireStore=FirebaseFirestore.getInstance()

    //sign up
    fun registerUser(activity:SignUpActivity,userInfo:User)
    {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()

            }

    }
    //signIn
    fun loadUserData(activity: Activity)
    {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener {
                document->
                val loggedInUser=document.toObject(User::class.java)!!
                when(activity)
                {
                    is SignInActivity->
                        activity.signInSuccess(loggedInUser)
                    is MainActivity->
                        activity.updateNavigationUserDetails(loggedInUser)
                    is MyProfile->
                        activity.setProfileUI(loggedInUser)

                }

            }.addOnFailureListener {
                e->
                when(activity)
                {
                    is SignInActivity->
                        activity.hideProgressDialog()
                    is MainActivity->
                        activity.hideProgressDialog()

                }
                Log.e("SignInUser","Error writing document",e)

            }
    }
    fun getCurrentUserId():String
    {
        val currentUser=FirebaseAuth.getInstance().currentUser
        var currentUserId=""
        if(currentUser!=null)
        {
            currentUserId=currentUser.uid
        }
        return currentUserId

    }
}