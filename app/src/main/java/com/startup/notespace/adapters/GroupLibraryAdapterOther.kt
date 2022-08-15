package com.startup.notespace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.Library1
import com.startup.notespace.R
import com.startup.notespace.models.MyLibraryModel

class GroupLibraryAdapterOther(val context: Context, var arrayList: ArrayList<MyLibraryModel>) :
    RecyclerView.Adapter<GroupLibraryAdapterOther.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var folderName: TextView = itemView.findViewById(R.id.folderName)
        var threeDotsDelete: ImageView = itemView.findViewById(R.id.deleteThreeDots)
    }

    fun setFilteredList(filteredList: ArrayList<MyLibraryModel>) {
        this.arrayList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_rv, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = arrayList[holder.adapterPosition]

        holder.threeDotsDelete.visibility = View.GONE

        holder.folderName.text = model.folderName1




        holder.itemView.setOnClickListener {
            val intent = Intent(context, Library1::class.java)
            intent.putExtra("folderName", model.folderName1)
            intent.putExtra("uid", model.uid)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}