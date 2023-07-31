package com.zakharov.lessonsqlkotlin.db

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zakharov.lessonsqlkotlin.EditActivity
import com.zakharov.lessonsqlkotlin.R

class MyAdapter(var listMain: ArrayList<ListItem>, var contextM: Context) :
    RecyclerView.Adapter<MyAdapter.MyHolder>() {


    class MyHolder(itemView: View, var context: Context) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvTime = itemView.findViewById<TextView>(R.id.tvTime)

        fun setData(item: ListItem) {
            tvTitle.text = item.title
            tvTime.text = item.time
            itemView.setOnClickListener {
                val intent = Intent(context, EditActivity::class.java).apply {
                    putExtra(MyIntentConstants.I_TITLE_KEY, item.title)
                    putExtra(MyIntentConstants.I_DESC_KEY, item.desc)
                    putExtra(MyIntentConstants.I_URI_KEY, item.uri)
                    putExtra(MyIntentConstants.I_ID_KEY, item.id)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyHolder(inflater.inflate(R.layout.rc_item, parent, false), contextM)
    }

    override fun getItemCount(): Int {
        return listMain.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listMain[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(listItems: List<ListItem>) {
        listMain.clear()
        listMain.addAll(listItems)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int, dbManger: MyDbManager) {
        dbManger.removeItemFromDb(listMain[position].id.toString())
        listMain.removeAt(position)
        notifyItemRangeChanged(0, listMain.size)
        notifyItemRemoved(position)


    }
}