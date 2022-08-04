package com.startup.notespace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.R
import com.startup.notespace.ViewDocumentActivityPost
import com.startup.notespace.models.DocPostModel
import com.startup.notespace.models.Post
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class DocPostAdapter(val context: Context, var arrayList: ArrayList<DocPostModel>) :
    RecyclerView.Adapter<DocPostAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var docName: TextView = itemView.findViewById(R.id.nameDocPostTV)
    }

    fun setFilteredList(filteredList: ArrayList<DocPostModel>) {
        this.arrayList = filteredList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return DocPostAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.document_post_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.docName.text = arrayList[holder.adapterPosition].docName

        val ref = FirebaseStorage.getInstance()

        holder.itemView.setOnClickListener {
            val pdfPath = arrayList[holder.adapterPosition].docUrl
            val intent = Intent(context, ViewDocumentActivityPost::class.java)
            intent.putExtra("path", pdfPath)
            intent.putExtra("pdfName", arrayList[holder.adapterPosition].docName)
            context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Are you sure you want to Delete?")
            builder.setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->

                    try {
                        FirebaseDatabase.getInstance().reference.child("posts")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for (item in snapshot.children) {
                                            val post = item.getValue(Post::class.java)
                                            val uri = post!!.docUrl
                                            val currentUid = item.key
                                            if (uri == arrayList[holder.adapterPosition].docUrl) {
                                                FirebaseDatabase.getInstance().reference.child("posts")
                                                    .child(currentUid.toString())
                                                    .removeValue().addOnCompleteListener {
                                                        if (it.isSuccessful) {

                                                            try {
                                                                var reference = ref.getReferenceFromUrl(post.docUrl)
                                                                reference.delete().addOnSuccessListener {
                                                                    Toast.makeText(context, "Post deleted.", Toast.LENGTH_SHORT).show()
                                                                }
                                                            } catch (e: Exception) {
                                                            }

                                                        }
                                                    }
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

                    try {
                        // to delete from my_posts
                        FirebaseDatabase.getInstance().reference.child("users")
                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                            .child("my_document_posts")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for (item in snapshot.children) {
                                            val post = item.getValue(Post::class.java)
                                            val uri = post!!.docUrl
                                            val currentUid = item.key
                                            if (uri == arrayList[holder.adapterPosition].docUrl) {
                                                FirebaseDatabase.getInstance().reference.child("users")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child("my_document_posts")
                                                    .child(currentUid.toString())
                                                    .removeValue()
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
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

            false
        }


    }


    override fun getItemCount(): Int {
        return arrayList.size
    }
}