package com.app_devs.mytrello.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.app_devs.mytrello.activities.*
import com.app_devs.mytrello.models.Board
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

    fun updateUserProfileData(activity: MyProfile,userHashMap:HashMap<String,Any>)
    {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).update(userHashMap).addOnSuccessListener {
                Log.e(activity.javaClass.simpleName,"Profile Data Updated")
                Toast.makeText(activity,"Updated Successfully!",Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Profile Data not Updated",e)
                Toast.makeText(activity,"Error in updating!",Toast.LENGTH_SHORT).show()
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

    fun createBoard(activity: CreateBoardActivity, board: Board)
    {
        mFireStore.collection(Constants.BOARDS)
                .document()
                .set(board, SetOptions.merge())
                .addOnSuccessListener {
                    Log.e(activity.javaClass.simpleName,"Profile Data Updated")
                    Toast.makeText(activity,"Board Created Successfully!",Toast.LENGTH_SHORT).show()
                    activity.boardCreatedSuccessfully()
                }.addOnFailureListener{
                    e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Profile Data not Updated",e)
                    Toast.makeText(activity,"Error in creating!",Toast.LENGTH_SHORT).show()
                }
    }
}