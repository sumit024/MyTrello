package com.app_devs.mytrello.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_devs.mytrello.R
import com.app_devs.mytrello.adapters.MembersAdapter
import com.app_devs.mytrello.firebase.FireStoreClass
import com.app_devs.mytrello.models.Board
import com.app_devs.mytrello.models.User
import com.app_devs.mytrello.utils.Constants
import kotlinx.android.synthetic.main.activity_members.*
import kotlinx.android.synthetic.main.activity_task_list.*
import kotlinx.android.synthetic.main.dialog_search_member.*

class MembersActivity : BaseActivity() {

    private lateinit var mBoardDetails:Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)
        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetails= intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getAssignedMembersListDetail(this,mBoardDetails.assignedTo)
        }
        setUpActionBar()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_member->
            {
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember()
    {
        val dialog=Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.tv_add.setOnClickListener {
            val email=dialog.et_email_search_member.text.toString()
            if(email.isNotEmpty()){
                Toast.makeText(this,"Added",Toast.LENGTH_SHORT).show()
                dialog.dismiss()

            }
            else{
                dialog.et_email_search_member.error = "Please enter email"
                dialog.et_email_search_member.requestFocus()
            }

        }
        dialog.tv_cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setUpActionBar()
    {
        setSupportActionBar(toolbar_members_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title=resources.getString(R.string.members)
        toolbar_members_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

     fun setUpMembersList(list:ArrayList<User>){
        hideProgressDialog()
        rv_members_list.layoutManager=LinearLayoutManager(this)
        rv_members_list.setHasFixedSize(true)
        val adapter=MembersAdapter(this,list)
        rv_members_list.adapter=adapter
    }
}