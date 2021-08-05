package com.app_devs.mytrello.firebase

import com.app_devs.mytrello.activities.SignInActivity
import com.app_devs.mytrello.activities.SignUpActivity
import com.app_devs.mytrello.models.User
import com.app_devs.mytrello.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {
    private val mFireStore=FirebaseFirestore.getInstance()

    fun registerUser(activity:SignUpActivity,userInfo:User)
    {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()

            }

    }
    fun signInUser(activity: SignInActivity)
    {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get()
            .addOnSuccessListener {
                document->
                val loggedInUser=document.toObject(User::class.java)!!
                activity.signInSuccess(loggedInUser)

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