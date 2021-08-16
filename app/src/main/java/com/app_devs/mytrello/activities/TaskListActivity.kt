package com.app_devs.mytrello.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_devs.mytrello.R
import com.app_devs.mytrello.adapters.TaskListItemsAdapter
import com.app_devs.mytrello.firebase.FireStoreClass
import com.app_devs.mytrello.models.Board
import com.app_devs.mytrello.models.Card
import com.app_devs.mytrello.models.Task
import com.app_devs.mytrello.utils.Constants
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_task_list.*
import java.text.FieldPosition

//commit check
class TaskListActivity : BaseActivity() {
    private lateinit var mBoardDetails:Board
    private lateinit var mBoardDocumentId:String

    companion object{
        const val MEMBER_REQUEST_CODE=13
        const val CARD_DETAIL_REQUEST_CODE=14
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)


        if(intent.hasExtra(Constants.DOCUMENT_ID))
        {
            mBoardDocumentId=intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this,mBoardDocumentId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_members,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_members-> {
                val intent=Intent(this, MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
                startActivityForResult(intent, MEMBER_REQUEST_CODE)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode== MEMBER_REQUEST_CODE || requestCode== CARD_DETAIL_REQUEST_CODE)
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardDetails(this,mBoardDocumentId)
        }
        else
        {
            Log.e("Cancelled","Cancelled")
        }
    }
    private fun setUpActionBar()
    {
        setSupportActionBar(toolbar_task_list_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title=mBoardDetails.name
        toolbar_task_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }
    fun boardDetails(board: Board)
    {
        mBoardDetails=board
        hideProgressDialog()
        val addTaskList=Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        rv_task_list.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_task_list.setHasFixedSize(true)
        val adapter=TaskListItemsAdapter(this,board.taskList)
        rv_task_list.adapter=adapter
        setUpActionBar()
    }

    fun addUpdateTaskListSuccess()
    {
        hideProgressDialog()
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this,mBoardDetails.documentId)
    }

    //creating a task
    fun createTaskList(taskListName:String)
    {
        Log.e("Task List Name", taskListName)
        val task=Task(taskListName,FireStoreClass().getCurrentUserId())
        mBoardDetails.taskList.add(0,task)
        // Remove the last position as we have added the item manually for adding the TaskList.
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun updateTaskList(position: Int, listName:String, model:Task)
    {
        val task=Task(listName,model.createdBy)
        mBoardDetails.taskList[position]=task
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)

    }

    fun deleteTaskList(position: Int)
    {
        mBoardDetails.taskList.removeAt(position)
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }

    fun addCardToTaskList(position: Int,cardName:String)
    {
        mBoardDetails.taskList.removeAt(mBoardDetails.taskList.size-1)
        val cardAssignedUsers:ArrayList<String> = ArrayList()
        cardAssignedUsers.add(FireStoreClass().getCurrentUserId())

        val card=Card(cardName,FireStoreClass().getCurrentUserId(),cardAssignedUsers)
        // boards k andar tasklist hai task k andar cards hai

        val cardList = mBoardDetails.taskList[position].cards
        cardList.add(card)
        val task=Task(mBoardDetails.taskList[position].title, mBoardDetails.taskList[position].createdBy, cardList)


        mBoardDetails.taskList[position]=task
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)

    }

    fun cardDetails(taskListPosition:Int, cardPosition:Int)
    {
        val intent=Intent(this,CardDetailsActivity::class.java)
        intent.putExtra(Constants.BOARD_DETAIL,mBoardDetails)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        startActivityForResult(intent, CARD_DETAIL_REQUEST_CODE)
    }
}