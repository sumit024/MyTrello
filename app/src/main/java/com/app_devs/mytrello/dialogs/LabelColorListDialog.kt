package com.app_devs.mytrello.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_devs.mytrello.R
import com.app_devs.mytrello.adapters.LabelColorListItemAdapter
import kotlinx.android.synthetic.main.dialog_list.view.*

abstract class LabelColorListDialog(
        context: Context,
        private var list:ArrayList<String>,
        private val title:String="",
        private var mSelectedColor:String=""):Dialog(context)
{
    private var adapter:LabelColorListItemAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view=LayoutInflater.from(context).inflate(R.layout.dialog_list,null)
        setContentView(view)
        setCanceledOnTouchOutside(true)
        setCancelable(true)
        setUpRecyclerView(view)
    }
    private fun setUpRecyclerView(view:View){
        view.tvTitle.text=title
        view.rvList.layoutManager=LinearLayoutManager(context)
        adapter= LabelColorListItemAdapter(context,list,mSelectedColor)
        view.rvList.adapter=adapter
        adapter!!.mOnclickListener=object :LabelColorListItemAdapter.OnClickListener{
            override fun onClick(pos: Int, color: String) {
                dismiss()
                onItemSelected(color)
            }
        }

    }

    protected abstract fun onItemSelected(color:String)
}