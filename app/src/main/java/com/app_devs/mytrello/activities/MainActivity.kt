package com.app_devs.mytrello.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_devs.mytrello.R
import com.app_devs.mytrello.adapters.BoardItemsAdapter
import com.app_devs.mytrello.firebase.FireStoreClass
import com.app_devs.mytrello.models.Board
import com.app_devs.mytrello.models.User
import com.app_devs.mytrello.utils.Constants
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object{
        const val MY_PROFILE_REQUEST_CODE=11
        const val CREATE_BOARD_REQUEST_CODE=12
    }
    private lateinit var mUserName:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpActionBar()
        nav_view.setNavigationItemSelectedListener(this)
        FireStoreClass().loadUserData(this,true)

        fab_create_board.setOnClickListener {

            val intent=Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)

        }

    }

    fun populateBoardsListToUi(list:ArrayList<Board>)
    {
        hideProgressDialog()
        if(list.size>0)
        {
            rv_boards_list.visibility=View.VISIBLE
            tv_no_boards_available.visibility=View.GONE
            rv_boards_list.layoutManager=LinearLayoutManager(this)
            rv_boards_list.setHasFixedSize(true)

            val adapter=BoardItemsAdapter(this,list)
            rv_boards_list.adapter=adapter
            adapter.onClickListener(object :BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, board: Board) {
                    val intent=Intent(this@MainActivity,TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID,board.documentId)
                    startActivity(intent)
                    
                }
            })

        }
        else{
            rv_boards_list.visibility=View.GONE
            tv_no_boards_available.visibility=View.VISIBLE
        }
    }

    private fun setUpActionBar()
    {
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }

    }
    private fun toggleDrawer()
    {
        if(drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else
            drawer_layout.openDrawer(GravityCompat.START)
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START))
            drawer_layout.closeDrawer(GravityCompat.START)
        else
            doubleBackToExit()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK && requestCode== MY_PROFILE_REQUEST_CODE)
        {
            FireStoreClass().loadUserData(this)
        }
        else if(resultCode==Activity.RESULT_OK && requestCode== CREATE_BOARD_REQUEST_CODE)
        {
            FireStoreClass().getBoardsList(this)
        }
        else
        {
            Log.e("Cancelled", "Cancelled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
       when(item.itemId)
       {
           R.id.nav_my_profile -> {
               val intent = Intent(this, MyProfile::class.java)
               startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)

           }
           R.id.nav_sign_out -> {
               FirebaseAuth.getInstance().signOut()
               val intent = Intent(this, IntroActivity::class.java)
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
               startActivity(intent)
               finish()
           }
           R.id.iv_user_image->{

           }
       }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user: User,readBoardsToList:Boolean) {
        mUserName=user.name
        Glide.with(this).load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(iv_user_image)

        tv_username.text=user.name
        if(readBoardsToList)
        {
            showProgressDialog(resources.getString(R.string.please_wait))
            FireStoreClass().getBoardsList(this)
        }

    }

}