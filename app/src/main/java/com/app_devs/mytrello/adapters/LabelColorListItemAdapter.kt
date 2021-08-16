package com.app_devs.mytrello.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app_devs.mytrello.R
import kotlinx.android.synthetic.main.item_label_color.view.*

class LabelColorListItemAdapter(private val context: Context,
                                private val list:ArrayList<String>,
                                private val mSelectedColor:String)
    :RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    var mOnclickListener:OnClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.item_label_color,parent,false))

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item=list[position]
        if(holder is MyViewHolder){
            holder.itemView.view_main.setBackgroundColor(Color.parseColor(item))

            if(item==mSelectedColor){
                holder.itemView.iv_selected_color.visibility=View.VISIBLE
            }
            else{
                holder.itemView.iv_selected_color.visibility=View.GONE
            }

            holder.itemView.setOnClickListener{
                if (mOnclickListener!=null){
                    mOnclickListener!!.onClick(position,item)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size

    }

    class MyViewHolder(view:View):RecyclerView.ViewHolder(view)

    interface OnClickListener{
        fun onClick(pos:Int, color:String)
    }

    fun setOnClickListener(onClickListener: CardListItemsAdapter.OnClickListener) {
        this.mOnclickListener = onClickListener
    }


}

