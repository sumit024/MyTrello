package com.app_devs.mytrello.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.app_devs.mytrello.activities.*
import com.app_devs.mytrello.models.Board
import com.app_devs.mytrello.models.Card
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
    fun loadUserData(activity: Activity, readBoardsList:Boolean=false)
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
                        activity.updateNavigationUserDetails(loggedInUser,readBoardsList)
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
                    is MyProfile->
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

    fun getBoardDetails(activity: TaskListActivity,documentId:String)
    {
        mFireStore.collection(Constants.BOARDS).document(documentId).get().addOnSuccessListener {
            document->
            Log.e(activity.javaClass.simpleName,document.toString())
            val board=document.toObject(Board::class.java)!!
            board.documentId=document.id
            activity.boardDetails(board)

        }.addOnFailureListener {
            e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error in creating",e)
            // Toast.makeText(activity,"Error in creating!",Toast.LENGTH_SHORT).show()
        }
    }

    fun addUpdateTaskList(activity: Activity,board: Board)
    {
        val taskListHashMap= HashMap<String, Any> ()
        taskListHashMap[Constants.TASK_LIST]=board.taskList

        mFireStore.collection(Constants.BOARDS)
                .document(board.documentId)
                .update(taskListHashMap)
                .addOnSuccessListener {
                    Log.e(activity.javaClass.simpleName,"TASK LIST UPDATED SUCCESSFULLY")
                    if(activity is TaskListActivity)
                        activity.addUpdateTaskListSuccess()
                    if(activity is CardDetailsActivity)
                        activity.addUpdateTaskListSuccess()
                }.addOnFailureListener{
                    exception->
                    if(activity is TaskListActivity )
                        activity.hideProgressDialog()
                    else if(activity is CardDetailsActivity)
                        activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error in creating",exception)
                }

    }

    fun getBoardsList(activity: MainActivity){
        mFireStore.collection(Constants.BOARDS)
                .whereArrayContains(Constants.ASSIGNED_TO,getCurrentUserId())
                .get()
                .addOnSuccessListener {
            document->
            Log.e(activity.javaClass.simpleName,document.documents.toString())

            val boardsList: ArrayList<Board> = ArrayList()
            // A for loop as per the list of documents to convert them into Boards ArrayList.
            for (i in document.documents) {
                val board = i.toObject(Board::class.java)!!
                board.documentId = i.id
                boardsList.add(board)
            }

            // Here pass the result to the base activity.
            activity.populateBoardsListToUi(boardsList)

        }.addOnFailureListener {
            e->
            activity.hideProgressDialog()
            Log.e(activity.javaClass.simpleName,"Error in creating",e)
           // Toast.makeText(activity,"Error in creating!",Toast.LENGTH_SHORT).show()
        }
    }

    fun getAssignedMembersListDetail(activity: Activity,assignedTo:ArrayList<String>)
    {
        mFireStore.collection(Constants.USERS)
            .whereIn(Constants.ID,assignedTo)
            .get()
            .addOnSuccessListener {
                document->
                Log.e(activity.javaClass.simpleName,document.documents.toString())
                val userList:ArrayList<User> = ArrayList()
                for(i in document.documents){
                    val user= i.toObject(User::class.java)!!
                    userList.add(user)
                }
                if(activity is MembersActivity)
                    activity.setUpMembersList(userList)
                if (activity is TaskListActivity)
                    activity.boardMemberDetailsList(userList)
            }.addOnFailureListener{
                    e->
                    if(activity is MembersActivity)
                        activity.hideProgressDialog()
                    else if(activity is TaskListActivity)
                        activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error in fetching",e)
            }

    }

    fun getMembersDetails(activity: MembersActivity, email:String){
        mFireStore.collection(Constants.USERS)
                .whereEqualTo(Constants.EMAIL,email)
                .get()
                .addOnSuccessListener {
                    document->
                    if(document.documents.size>0)
                    {
                        val user=document.documents[0].toObject(User::class.java)!!
                        activity.memberDetails(user)
                    }
                    else
                    {
                        activity.hideProgressDialog()
                        activity.showErrorSnackBar("No such member found!")
                    }
                }

    }

    fun assignMemberToBoard(activity: MembersActivity, board: Board, user: User){
        val assignMemberHashMap= HashMap<String,Any>()
        assignMemberHashMap[Constants.ASSIGNED_TO]=board.assignedTo

        mFireStore.collection(Constants.BOARDS)
                .document(board.documentId)
                .update(assignMemberHashMap)
                .addOnSuccessListener {
                    activity.memberAssignSuccess(user)
                }.addOnFailureListener {
                    e->
                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error in updating",e)
                }

    }
}