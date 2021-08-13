package com.app_devs.mytrello.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app_devs.mytrello.R
import com.app_devs.mytrello.activities.TaskListActivity
import com.app_devs.mytrello.models.Task
import kotlinx.android.synthetic.main.item_task.view.*

open class TaskListItemsAdapter(private val context: Context,private val list:ArrayList<Task>)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.item_task,parent,false)
        // Here the layout params are converted dynamically according to the screen size
        // as width is 70% and height is wrap_content.
        val layoutParams= LinearLayout.LayoutParams(
                (parent.width*0.7).toInt(),LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins( (15.toDp().toPx()),0, (40.toDp().toPx()),0 )
        view.layoutParams=layoutParams
        return MyViewHolder(view)
    }



    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]
        if(holder is MyViewHolder)
        {
            if(position==list.size-1) // it means if position < 0
            {
                holder.itemView.tv_add_task_list.visibility=View.VISIBLE
                holder.itemView.ll_task_item.visibility=View.GONE
            }else{
                holder.itemView.tv_add_task_list.visibility=View.GONE
                holder.itemView.ll_task_item.visibility=View.VISIBLE
            }
            holder.itemView.tv_task_list_title.text=model.title

            holder.itemView.tv_add_task_list.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility=View.GONE
                holder.itemView.cv_add_task_list_name.visibility=View.VISIBLE
            }

            holder.itemView.ib_close_list_name.setOnClickListener {
                holder.itemView.tv_add_task_list.visibility=View.VISIBLE
                holder.itemView.cv_add_task_list_name.visibility=View.GONE
            }

            holder.itemView.ib_done_list_name.setOnClickListener {
                val listName = holder.itemView.et_task_list_name.text.toString()
                if(listName.isNotEmpty())
                {
                    if(context is TaskListActivity)
                    {
                        Log.i("check",listName)
                        context.createTaskList(listName)
                    }
                }
                else
                {
                    holder.itemView.et_task_list_name.error="Can't be empty"
                    holder.itemView.et_task_list_name.requestFocus()
                }
            }

            holder.itemView.ib_edit_list_name.setOnClickListener {
                holder.itemView.et_edit_task_list_name.setText(model.title)
                holder.itemView.ll_title_view.visibility=View.GONE
                holder.itemView.cv_edit_task_list_name.visibility=View.VISIBLE
            }

            holder.itemView.ib_close_editable_view.setOnClickListener {
                holder.itemView.ll_title_view.visibility=View.VISIBLE
                holder.itemView.cv_edit_task_list_name.visibility=View.GONE
            }

            holder.itemView.ib_done_edit_list_name.setOnClickListener {
                val listName=holder.itemView.et_edit_task_list_name.text.toString()
                if(listName.isNotEmpty())
                {
                    if(context is TaskListActivity)
                    {
                        Log.i("check",listName)
                        context.updateTaskList(position, listName, model)
                    }
                }
                else
                {
                    holder.itemView.et_task_list_name.error="Can't be empty"
                    holder.itemView.et_task_list_name.requestFocus()
                }

            }
            holder.itemView.ib_delete_list.setOnClickListener {
                showAlertDialogForDelete(position,model.title)

            }

            holder.itemView.tv_add_card.setOnClickListener {
                holder.itemView.tv_add_card.visibility=View.GONE
                holder.itemView.cv_add_card.visibility=View.VISIBLE
            }
            holder.itemView.ib_close_card_name.setOnClickListener {
                holder.itemView.tv_add_card.visibility=View.VISIBLE
                holder.itemView.cv_add_card.visibility=View.GONE
            }
            holder.itemView.ib_done_card_name.setOnClickListener {
                val cardName=holder.itemView.et_card_name.text.toString()
                if(cardName.isNotEmpty())
                {
                    if(context is TaskListActivity)
                    {
                        context.addCardToTaskList(position, cardName)
                    }
                }
                else
                {
                    holder.itemView.et_card_name.error="Can't be empty"
                    holder.itemView.et_card_name.requestFocus()
                }
            }

            holder.itemView.rv_card_list.layoutManager=LinearLayoutManager(context)
            holder.itemView.rv_card_list.setHasFixedSize(true)
            val adapter=CardListItemsAdapter(context,model.cards)
            holder.itemView.rv_card_list.adapter=adapter



        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDp():Int=(this/ Resources.getSystem().displayMetrics.density).toInt()
    private fun Int.toPx():Int =(this* Resources.getSystem().displayMetrics.density).toInt()

    private fun showAlertDialogForDelete(position: Int,title:String)
    {
        val builder=AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title ?")
        builder.setIcon(R.drawable.ic_alert)

        builder.setPositiveButton("Yes"){
            dialogInterface, _ ->
            dialogInterface.dismiss()
            if(context is TaskListActivity)
                context.deleteTaskList(position)

        }
        builder.setNegativeButton("No"){
            dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog=builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()


    }



    class MyViewHolder(view:View): RecyclerView.ViewHolder(view)


}