package com.app_devs.mytrello.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app_devs.mytrello.R
import com.app_devs.mytrello.models.SelectedMembers
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_card_selected_member.view.*

open class CardMembersListAdapter(private val context: Context,
                                  private val list:ArrayList<SelectedMembers>,
                                  private var assignMember:Boolean
                                  )
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onCLickListener:OnClickListener?=null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_card_selected_member,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model=list[position]
        if(holder is MyViewHolder){
            if(position==list.size-1 && assignMember)
            {
                holder.itemView.iv_add_member.visibility=View.VISIBLE
                holder.itemView.iv_selected_member_image.visibility=View.GONE
            }
            else
            {
                holder.itemView.iv_add_member.visibility=View.GONE
                holder.itemView.iv_selected_member_image.visibility=View.VISIBLE
                Glide
                        .with(context)
                        .load(model.image)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(holder.itemView.iv_selected_member_image)

            }
            holder.itemView.setOnClickListener {
                if (onCLickListener != null) {
                    onCLickListener!!.onClick()
                }
            }
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    class MyViewHolder(view:View):RecyclerView.ViewHolder(view)
    interface  OnClickListener{
        fun onClick()
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onCLickListener=onClickListener
    }
}