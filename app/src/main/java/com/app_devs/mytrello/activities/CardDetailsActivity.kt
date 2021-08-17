package com.app_devs.mytrello.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.app_devs.mytrello.R
import com.app_devs.mytrello.adapters.CardMembersListAdapter
import com.app_devs.mytrello.dialogs.LabelColorListDialog
import com.app_devs.mytrello.dialogs.MembersListDialog
import com.app_devs.mytrello.firebase.FireStoreClass
import com.app_devs.mytrello.models.*
import com.app_devs.mytrello.utils.Constants
import kotlinx.android.synthetic.main.activity_card_details.*
import kotlinx.android.synthetic.main.activity_create_board.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CardDetailsActivity : BaseActivity() {

    // A global variable for board details
    private lateinit var mBoardDetails: Board
    // A global variable for task item position
    private var mTaskListPosition: Int = -1
    // A global variable for card item position
    private var mCardPosition: Int = -1
    // A global variable for selected label color
    private var mSelectedColor: String = ""

    // A global variable for Assigned Members List.
    private lateinit var mMembersDetailList: ArrayList<User>

    private var mSelectedDueDateMilliSecs:Long=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)

        getIntentData()

        setupActionBar()

        et_name_card_details.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].title)
        et_name_card_details.setSelection(et_name_card_details.text.toString().length) // The cursor after the string length

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        mSelectedDueDateMilliSecs=mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].dueDate
        if (mSelectedColor.isNotEmpty()) {
            setColor()
        }
        if(mSelectedDueDateMilliSecs>0)
        {
            val sdf=SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH)
            val selectedDate=sdf.format(Date(mSelectedDueDateMilliSecs))
            tv_select_due_date.text=selectedDate
        }

        tv_select_label_color.setOnClickListener {
            labelColorsListDialog()
        }

        // TODO (Step 4: Call the function to setup the recyclerView for assigned members.)
        // START
        setupSelectedMembersList()
        // END

        tv_select_members.setOnClickListener {
            membersListDialog()
        }

        btn_update_card_details.setOnClickListener {
            if (et_name_card_details.text.toString().isNotEmpty()) {
                updateCardDetails()
            } else {
                Toast.makeText(this@CardDetailsActivity, "Enter card name.", Toast.LENGTH_SHORT)
                        .show()
            }
        }

        tv_select_due_date.setOnClickListener {
            showDataPicker()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu to use in the action bar
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_Delete_card -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].title)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * A function to setup action bar
     */
    private fun setupActionBar() {

        setSupportActionBar(toolbar_card_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].title
        }

        toolbar_card_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    // A function to get all the data that is sent through intent.
    private fun getIntentData() {

        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, -1)
        }
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    /**
     * A function to get the result of add or updating the task list.
     */
    fun addUpdateTaskListSuccess() {

        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    /**
     * A function to update card details.
     */
    private fun updateCardDetails() {

        // Here we have updated the card name using the data model class.
        val card = Card(
                et_name_card_details.text.toString(),
                mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
                mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
                mSelectedColor,mSelectedDueDateMilliSecs
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        // Here we have assigned the update card details to the task list using the card position.
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    /**
     * A function to show an alert dialog for the confirmation to delete the card.
     */
    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle(resources.getString(R.string.alert))
        //set message for alert dialog
        builder.setMessage(
                resources.getString(
                        R.string.confirmation_message_to_delete_card,
                        cardName
                )
        )
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
            deleteCard()
        }
        //performing negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    /**
     * A function to delete the card from the task list.
     */
    private fun deleteCard() {

        // Here we have got the cards list from the task item list using the task list position.
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        // Here we will remove the item from cards list using the card position.
        cardsList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)

        taskList[mTaskListPosition].cards = cardsList

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))
        FireStoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    /**
     * A function to remove the text and set the label color to the TextView.
     */
    private fun setColor() {
        tv_select_label_color.text = ""
        tv_select_label_color.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    /**
     * A function to add some static label colors in the list.
     */
    private fun colorsList(): ArrayList<String> {

        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C86F")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")

        return colorsList
    }

    /**
     * A function to launch the label color list dialog.
     */
    private fun labelColorsListDialog() {

        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object : LabelColorListDialog(
                this@CardDetailsActivity,
                colorsList,
                resources.getString(R.string.str_select_label_color),
                mSelectedColor
        ) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }
        listDialog.show()
    }

    /**
     * A function to launch and setup assigned members detail list into recyclerview.
     */
    private fun membersListDialog() {

        // Here we get the updated assigned members list
        val cardAssignedMembersList =
                mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        if (cardAssignedMembersList.size > 0) {
            // Here we got the details of assigned members list from the global members list which is passed from the Task List screen.
            for (i in mMembersDetailList.indices) {
                for (j in cardAssignedMembersList) {
                    if (mMembersDetailList[i].id == j) {
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        } else {
            for (i in mMembersDetailList.indices) {
                mMembersDetailList[i].selected = false
            }
        }

        val listDialog = object : MembersListDialog(
                this@CardDetailsActivity,
                mMembersDetailList,
                resources.getString(R.string.str_select_member)
        ) {
            override fun onItemSelected(user: User, action: String) {

                // TODO (Step 5: Here based on the action in the members list dialog update the list.)
                // START
                if (action == Constants.SELECT) {
                    if (!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(
                                    user.id
                            )
                    ) {
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.add(
                                user.id
                        )
                    }
                } else {
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.remove(
                            user.id
                    )

                    for (i in mMembersDetailList.indices) {
                        if (mMembersDetailList[i].id == user.id) {
                            mMembersDetailList[i].selected = false
                        }
                    }
                }

                setupSelectedMembersList()
                // END
            }
        }
        listDialog.show()
    }

// TODO (Step 3: Create a function to setup the recyclerView for card assigned members.)
// START
    /**
     * A function to setup the recyclerView for card assigned members.
     */
    private fun setupSelectedMembersList() {

        // Assigned members of the Card.
        val cardAssignedMembersList =
                mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        // A instance of selected members list.
        val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

        // Here we got the detail list of members and add it to the selected members list as required.
        for (i in mMembersDetailList.indices) {
            for (j in cardAssignedMembersList) {
                if (mMembersDetailList[i].id == j) {
                    val selectedMember = SelectedMembers(
                            mMembersDetailList[i].id,
                            mMembersDetailList[i].image
                    )

                    selectedMembersList.add(selectedMember)
                }
            }
        }

        if (selectedMembersList.size > 0) {

            // This is for the last item to show.
            selectedMembersList.add(SelectedMembers("", ""))

            tv_select_members.visibility = View.GONE
            rv_selected_members_list.visibility = View.VISIBLE

            rv_selected_members_list.layoutManager = GridLayoutManager(this@CardDetailsActivity, 6)
            val adapter = CardMembersListAdapter(this@CardDetailsActivity, selectedMembersList,true)
            rv_selected_members_list.adapter = adapter
            adapter.setOnClickListener(object :
                    CardMembersListAdapter.OnClickListener {
                override fun onClick() {
                    membersListDialog()
                }
            })
        } else {
            tv_select_members.visibility = View.VISIBLE
            rv_selected_members_list.visibility = View.GONE
        }
    }
    private fun showDataPicker() {
        /**
         * This Gets a calendar using the default time zone and locale.
         * The calender returned is based on the current time
         * in the default time zone with the default.
         */
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR) // Returns the value of the given calendar field. This indicates YEAR
        val month = c.get(Calendar.MONTH) // This indicates the Month
        val day = c.get(Calendar.DAY_OF_MONTH) // This indicates the Day

        /**
         * Creates a new date picker dialog for the specified date using the parent
         * context's default date picker dialog theme.
         */
        val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                    /*
                      The listener used to indicate the user has finished selecting a date.
                     Here the selected date is set into format i.e : day/Month/Year
                      And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.

                     Here the selected date is set into format i.e : day/Month/Year
                      And the month is counted in java is 0 to 11 so we need to add +1 so it can be as selected.*/

                    // Here we have appended 0 if the selected day is smaller than 10 to make it double digit value.
                    val sDayOfMonth = if (dayOfMonth < 10) "0$dayOfMonth" else "$dayOfMonth"
                    // Here we have appended 0 if the selected month is smaller than 10 to make it double digit value.
                    val sMonthOfYear = if ((monthOfYear + 1) < 10) "0${monthOfYear + 1}" else "${monthOfYear + 1}"

                    val selectedDate = "$sDayOfMonth/$sMonthOfYear/$year"
                    // Selected date it set to the TextView to make it visible to user.
                    tv_select_due_date.text = selectedDate

                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */

                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */
                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */
                    /**
                     * Here we have taken an instance of Date Formatter as it will format our
                     * selected date in the format which we pass it as an parameter and Locale.
                     * Here I have passed the format as dd/MM/yyyy.
                     */
                    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)

                    // The formatter will parse the selected date in to Date object
                    // so we can simply get date in to milliseconds.
                    val theDate = sdf.parse(selectedDate)

                    /** Here we have get the time in milliSeconds from Date object
                     */

                    /** Here we have get the time in milliSeconds from Date object
                     */

                    /** Here we have get the time in milliSeconds from Date object
                     */

                    /** Here we have get the time in milliSeconds from Date object
                     */
                    mSelectedDueDateMilliSecs = theDate!!.time
                },
                year,
                month,
                day
        )
        dpd.show() // It is used to show the datePicker Dialog.
    }
// END
}