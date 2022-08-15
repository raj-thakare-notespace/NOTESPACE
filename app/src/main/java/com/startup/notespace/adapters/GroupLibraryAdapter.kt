package com.startup.notespace.adapters

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
import com.startup.notespace.MyLibrary1
import com.startup.notespace.R
import com.startup.notespace.models.MyLibraryModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class GroupLibraryAdapter(val context: Context, var arrayList: ArrayList<MyLibraryModel>) :
    RecyclerView.Adapter<GroupLibraryAdapter.ViewHolder>() {


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

        holder.folderName.text = model.folderName1


        val reference = FirebaseStorage.getInstance()
        var uidArrayList = ArrayList<String>()
        if (!model.folderName1.isNullOrEmpty()) {

            try {
                Firebase.database.reference.child("libraryOfPdfUrls")
                    .child(model.uid)
                    .child(model.folderName1)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                uidArrayList.clear()
                                for (item in snapshot.children) {
                                    var count = 0
                                    for (i in item.children) {

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
                                        .child(model.uid)
                                        .child(model.folderName1)
                                        .removeValue().addOnCompleteListener {
                                            if (it.isSuccessful) {
                                                Toast.makeText(
                                                    context,
                                                    model.folderName1 + " deleted.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                try {
                                                    for (item in uidArrayList) {
                                                        val ref = reference.getReferenceFromUrl(item)
                                                        ref.delete()
                                                    }
                                                    Firebase.database.reference.child("libraryOfPdfUrls")
                                                        .child(model.uid)
                                                        .child(model.folderName1)
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
            intent.putExtra("folderName", model.folderName1)
            intent.putExtra("uid", model.uid)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

}