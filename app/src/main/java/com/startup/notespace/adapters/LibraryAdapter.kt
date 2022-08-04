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
import com.startup.notespace.MyLibrary1
import com.startup.notespace.R
import com.startup.notespace.models.MyLibraryModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

// Adapter to show folders in others library (first library page)
class LibraryAdapter(val context: Context, var arrayList: ArrayList<MyLibraryModel>) :
    RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var folderName: TextView = itemView.findViewById(R.id.folderName)
        var delete: ImageView = itemView.findViewById(R.id.deleteThreeDots)
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

        holder.folderName.text = arrayList[holder.adapterPosition].folderName1

        holder.delete.visibility = View.GONE

        holder.itemView.setOnClickListener {

            try {
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(arrayList[holder.adapterPosition].uid).addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                if (snapshot.key!!.length < 25) {
                                    FirebaseDatabase.getInstance().reference.child("users")
                                        .child(arrayList[holder.adapterPosition].uid)
                                        .child("createdBy")
                                        .addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if(snapshot.exists()){
                                                    var ans = snapshot.value!!.toString() == Firebase.auth.currentUser!!.uid
                                                    if (ans) {
                                                        val intent = Intent(context, MyLibrary1::class.java)
                                                        intent.putExtra(
                                                            "folderName",
                                                            arrayList[holder.adapterPosition].folderName1
                                                        )
                                                        intent.putExtra(
                                                            "uid",
                                                            arrayList[holder.adapterPosition].uid
                                                        )
                                                        context.startActivity(intent)
                                                    }
                                                    else {
                                                        val intent = Intent(context, Library1::class.java)
                                                        intent.putExtra(
                                                            "folderName",
                                                            arrayList[holder.adapterPosition].folderName1
                                                        )
                                                        intent.putExtra(
                                                            "uid",
                                                            arrayList[holder.adapterPosition].uid
                                                        )
                                                        context.startActivity(intent)
                                                    }
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                TODO("Not yet implemented")
                                            }

                                        })
                                }
                                else {
                                    try {
                                        FirebaseDatabase.getInstance().reference.child("users")
                                            .child(arrayList[holder.adapterPosition].uid)
                                            .child("uid")
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if(snapshot.exists()){
                                                        var ans = snapshot.value!!.toString() == Firebase.auth.currentUser!!.uid
                                                        if (ans) {
                                                            val intent =
                                                                Intent(context, MyLibrary1::class.java)
                                                            intent.putExtra(
                                                                "folderName",
                                                                arrayList[holder.adapterPosition].folderName1
                                                            )
                                                            intent.putExtra(
                                                                "uid",
                                                                arrayList[holder.adapterPosition].uid
                                                            )
                                                            context.startActivity(intent)
                                                        }
                                                        else {
                                                            val intent =
                                                                Intent(context, Library1::class.java)
                                                            intent.putExtra(
                                                                "folderName",
                                                                arrayList[holder.adapterPosition].folderName1
                                                            )
                                                            intent.putExtra(
                                                                "uid",
                                                                arrayList[holder.adapterPosition].uid
                                                            )
                                                            context.startActivity(intent)
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }

                                            })
                                    } catch (e: Exception) {
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            } catch (e: Exception) {
            }


        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }


}