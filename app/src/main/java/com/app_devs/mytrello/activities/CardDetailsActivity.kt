package com.app_devs.mytrello.activities

import android.app.Activity
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.app_devs.mytrello.R
import com.app_devs.mytrello.firebase.FireStoreClass
import com.app_devs.mytrello.models.Board
import com.app_devs.mytrello.models.Card
import com.app_devs.mytrello.models.Task
import com.app_devs.mytrello.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*
import kotlinx.android.synthetic.main.activity_create_board.*

class CardDetailsActivity : BaseActivity() {
    private lateinit var mBoardDetails:Board
    private var mTaskListPos=-1
    private var mCardPos=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        getIntentData()
        setUpActionBar()
        et_name_card_details.setText(mBoardDetails.taskList[mTaskListPos].cards[mCardPos].title)
        //focus gets on last character
        et_name_card_details.setSelection(et_name_card_details.text.toString().length)

        btn_update_card_details.setOnClickListener {
            if(!TextUtils.isEmpty(et_name_card_details.text.toString()))
            {
                updateCardDetails()
            }
            else{
                et_name_card_details.error="Can't be empty"
                et_name_card_details.requestFocus()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_Delete_card->
            {
                showAlertDialogForDelete(mBoardDetails.taskList[mTaskListPos].cards[mCardPos].title)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getIntentData()
    {
        if(intent.hasExtra(Constants.BOARD_DETAIL))
        {
            mBoardDetails=intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }
        if(intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION))
        {
            mTaskListPos=intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION,-1)
        }
        if(intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION))
        {
            mCardPos=intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION,-1)
        }

    }

    private fun setUpActionBar()
    {
        setSupportActionBar(toolbar_card_details_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        supportActionBar?.title=mBoardDetails.taskList[mTaskListPos].cards[mCardPos].title
        toolbar_card_details_activity.setNavigationOnClickListener {
            onBackPressed()
        }

    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateCardDetails(){
        val card=Card(et_name_card_details.text.toString(),
                mBoardDetails.taskList[mTaskListPos].cards[mCardPos].createdBy,
                mBoardDetails.taskList[mTaskListPos].cards[mCardPos].assignedTo)

        mBoardDetails.taskList[mTaskListPos].cards[mCardPos]=card

        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)
    }
    private fun showAlertDialogForDelete(cardName:String)
    {
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $cardName ?")
        builder.setIcon(R.drawable.ic_alert)

        builder.setPositiveButton("Yes"){
            dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()

        }
        builder.setNegativeButton("No"){
            dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog=builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()

    }

    private fun deleteCard()
    {
        val cardList=mBoardDetails.taskList[mTaskListPos].cards
        cardList.removeAt(mCardPos)

        val taskList =mBoardDetails.taskList
        taskList.removeAt(taskList.size-1)
        taskList[mTaskListPos].cards=cardList
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this,mBoardDetails)

    }
}