package com.example.notespace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notespace.PostDetailActivity
import com.example.notespace.R
import com.example.notespace.models.Post
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

// Adapter to load posts in profile
class ProfilePostsAdapter(val context: Context, var arrayList: ArrayList<Post>) :
    RecyclerView.Adapter<ProfilePostsAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postImageView: ImageView = itemView.findViewById(R.id.postIVRVITEM)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.profile_posts_rv_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(arrayList[holder.adapterPosition].postImage)
            .into(holder.postImageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context,PostDetailActivity::class.java)
            intent.putExtra("uri",arrayList[holder.adapterPosition].postImage)
            intent.putExtra("postedAt",arrayList[holder.adapterPosition].postedAt.toString())
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
                                            val uri = post!!.postImage
                                            val currentUid = item.key
                                            if (uri == arrayList[holder.adapterPosition].postImage) {
                                                FirebaseDatabase.getInstance().reference.child("posts")
                                                    .child(currentUid.toString())
                                                    .removeValue().addOnCompleteListener {
                                                        if (it.isSuccessful) {
                                                            var reference = FirebaseStorage.getInstance()
                                                                .getReferenceFromUrl(post.postImage)
                                                            reference.delete().addOnSuccessListener {
                                                                Toast.makeText(
                                                                    context,
                                                                    "Post deleted.",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
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
                            .child("my_posts")
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        for (item in snapshot.children) {
                                            val post = item.getValue(Post::class.java)
                                            val uri = post!!.postImage
                                            val currentUid = item.key
                                            if (uri == arrayList[holder.adapterPosition].postImage) {
                                                FirebaseDatabase.getInstance().reference.child("users")
                                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                    .child("my_posts")
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