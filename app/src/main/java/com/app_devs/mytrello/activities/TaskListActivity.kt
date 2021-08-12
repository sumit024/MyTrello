package com.app_devs.mytrello.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_devs.mytrello.R
import com.app_devs.mytrello.adapters.TaskListItemsAdapter
import com.app_devs.mytrello.firebase.FireStoreClass
import com.app_devs.mytrello.models.Board
import com.app_devs.mytrello.models.Task
import com.app_devs.mytrello.utils.Constants
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_task_list.*

class TaskListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentId=""
        if(intent.hasExtra(Constants.DOCUMENT_ID))
        {
            boardDocumentId=intent.getStringExtra(Constants.DOCUMENT_ID)!!
        }
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().getBoardDetails(this,boardDocumentId)
    }
    private fun setUpActionBar(title:String)
    {
        setSupportActionBar(toolbar_task_list_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title=title
        toolbar_task_list_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }
    fun boardDetails(board: Board)
    {
        hideProgressDialog()
        val addTaskList=Task(resources.getString(R.string.add_list))
        board.taskList.add(addTaskList)
        rv_task_list.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        rv_task_list.setHasFixedSize(true)
        val adapter=TaskListItemsAdapter(this,board.taskList)
        rv_task_list.adapter=adapter

        setUpActionBar(board.name)
    }
}