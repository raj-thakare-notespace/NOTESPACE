package com.example.notespace.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.notespace.MyLibrary1
import com.example.notespace.R
import com.example.notespace.models.MyLibraryModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

// Adapter to show folders in my library (first library page)
class MyLibraryAdapter(val context: Context, var arrayList: ArrayList<MyLibraryModel>) :
    RecyclerView.Adapter<MyLibraryAdapter.ViewHolder>() {


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

        holder.folderName.text = arrayList[holder.adapterPosition].folderName1

        val reference = FirebaseStorage.getInstance()
        var uidArrayList = ArrayList<String>()
        if (!arrayList[holder.adapterPosition].folderName1.isNullOrEmpty()) {

            try {
                Firebase.database.reference.child("libraryOfPdfUrls")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child(arrayList[holder.adapterPosition].folderName1)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                uidArrayList.clear()
                                for (item in snapshot.children) {
                                    var count = 0
                                    for (i in item.children) {
                                        Log.i("abcdefgh", i.value.toString())
                                        count++
                                    }
                                    if (count > 0 || item.value.toString() == "folderTrue") {
                                        continue
                                    } else {
                                        uidArrayList.add(item.value.toString())
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

        holder.threeDotsDelete.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.threeDotsDelete)
            popupMenu.menuInflater.inflate(R.menu.three_dots_menu_folder, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {

                    R.id.deleteIcon -> {

                        val builder = MaterialAlertDialogBuilder(context)
                        builder.setTitle("Are you sure you want to Delete?")
                        builder.setCancelable(false)
                            .setPositiveButton("Yes") { dialog, id ->

                                try {
                                    Firebase.database.reference.child("Library")
                                        .child(Firebase.auth.currentUser!!.uid)
                                        .child(arrayList[holder.adapterPosition].folderName1)
                                        .removeValue().addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    arrayList[holder.adapterPosition].folderName1 + " deleted.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                try {
                                                    for (item in uidArrayList) {
                                                        val ref = reference.getReferenceFromUrl(item)
                                                        ref.delete()
                                                    }
                                                    Firebase.database.reference.child("libraryOfPdfUrls")
                                                        .child(Firebase.auth.currentUser!!.uid)
                                                        .child(arrayList[holder.adapterPosition].folderName1)
                                                        .removeValue()
                                                } catch (e: Exception) {
                                                }
                                            }
                                        }
                                } catch (e: Exception) {
                                }
                            }
                            .setNegativeButton("No") { dialog, id ->
                                dialog.dismiss()
                            }
                        val alert = builder.create()
                        alert.show()

                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MyLibrary1::class.java)
            intent.putExtra("folderName", arrayList[holder.adapterPosition].folderName1)
            intent.putExtra("uid", Firebase.auth.currentUser!!.uid)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}