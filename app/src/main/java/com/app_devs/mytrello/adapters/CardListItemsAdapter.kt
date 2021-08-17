package com.app_devs.mytrello.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app_devs.mytrello.R
import com.app_devs.mytrello.activities.TaskListActivity
import com.app_devs.mytrello.models.Card
import com.app_devs.mytrello.models.SelectedMembers
import kotlinx.android.synthetic.main.activity_card_details.view.*
import kotlinx.android.synthetic.main.item_card.view.*

open class CardListItemsAdapter(private val context: Context,private val list:ArrayList<Card>) :RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private var onClickListener:OnClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]
        if(holder is MyViewHolder)
        {
            if(model.labelColor.isNotEmpty())
            {
                holder.itemView.view_label_color.visibility=View.VISIBLE
                holder.itemView.view_label_color.setBackgroundColor(Color.parseColor(model.labelColor))
            }
            else
            {
                holder.itemView.view_label_color.visibility=View.GONE
            }
            holder.itemView.tv_card_name.text=model.title

            if((context as TaskListActivity).mAssignedMemberDetailsList.size>0)
            {
                val selectedMembersList:ArrayList<SelectedMembers> = ArrayList()
                for(i in context.mAssignedMemberDetailsList.indices)
                {
                    for(j in model.assignedTo){
                        if(context.mAssignedMemberDetailsList[i].id==j)
                        {
                            val selectedMembers=SelectedMembers(context.mAssignedMemberDetailsList[i].id,
                            context.mAssignedMemberDetailsList[i].image)
                            selectedMembersList.add(selectedMembers)
                        }
                    }
                }
                if(selectedMembersList.size>0){
                    if(selectedMembersList.size==1 && selectedMembersList[0].id==model.createdBy){
                        holder.itemView.rv_card_selected_members_list.visibility=View.GONE
                    }
                    else
                    {
                        holder.itemView.rv_card_selected_members_list.visibility=View.VISIBLE
                        holder.itemView.rv_card_selected_members_list.layoutManager=GridLayoutManager(context,4)
                        val adapter=CardMembersListAdapter(context,selectedMembersList,false)
                        holder.itemView.rv_card_selected_members_list.adapter=adapter
                        adapter.setOnClickListener(object :CardMembersListAdapter.OnClickListener{
                            override fun onClick() {
                                if(onClickListener!=null)
                                {
                                    onClickListener!!.onClick(position)
                                }
                            }
                        })

                    }
                }
                else{
                    holder.itemView.rv_card_selected_members_list.visibility=View.GONE
                }

            }
            holder.itemView.setOnClickListener {
                if(onClickListener!=null){
                    onClickListener!!.onClick(position)
                }

            }

        }
    }

    override fun getItemCount(): Int {
       return list.size
    }
    class MyViewHolder(view: View):RecyclerView.ViewHolder(view)

    interface OnClickListener {
        fun onClick(position: Int)
    }
    /**
     * A function for OnClickListener where the Interface is the expected parameter..
     */
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

}