package com.example.notespace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notespace.*
import com.example.notespace.R
import com.example.notespace.models.Post
import com.example.notespace.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase


// Adapter to load posts
class PostAdapter(var context: Context, private val arrayList: ArrayList<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: ImageView = itemView.findViewById(R.id.userImagePost)
        var username: TextView = itemView.findViewById(R.id.username)
        var postImage: ImageView = itemView.findViewById(R.id.postImage)
        var likeButton: ImageView = itemView.findViewById(R.id.likeButton)
        var postMenu: ImageView = itemView.findViewById(R.id.postMenu)
        var likeCount: TextView = itemView.findViewById(R.id.likeCount)
        var postDescription: TextView = itemView.findViewById(R.id.postCaption)
        var pdfName: TextView = itemView.findViewById(R.id.pdfName)
        var createdAtTimeTV: TextView = itemView.findViewById(R.id.createAtTimeTV)
        var postDoc: LinearLayout = itemView.findViewById(R.id.postDoc)
        var savePost: ImageView = itemView.findViewById(R.id.savePostButton)
        var pdfBlackNameLL : LinearLayout = itemView.findViewById(R.id.pdfBlackNameLL)
        val pdfImageView : ImageView = itemView.findViewById(R.id.postPdfImageView)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_post_rv, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        try {
            var isSaved = false

            val model = arrayList[holder.adapterPosition]

            Glide.with(context)
                .load(model.postImage)
                .placeholder(R.drawable.image_placeholder)
                .into(holder.postImage)

            if(model.postDescription.isNullOrEmpty()){
                holder.postDescription.visibility = View.GONE
            }
            holder.postDescription.text = model.postDescription
            holder.likeCount.text = model.postLike.toString()
            holder.createdAtTimeTV.text = Utils.getTimeAgo(model.postedAt)

            if (model.docName.isNotEmpty())
            {
                holder.postImage.visibility = View.GONE
                holder.postDoc.visibility = View.VISIBLE
                holder.pdfBlackNameLL.background.alpha = 180
                holder.pdfName.text = model.docName

                holder.pdfImageView.visibility = View.VISIBLE

                Glide.with(context)
                    .load(model.docThumbnail)
                    .into(holder.pdfImageView)


                holder.pdfBlackNameLL.setOnClickListener {
                    val intent = Intent(context,ViewDocumentActivityPost::class.java)
                    intent.putExtra("path",model.docUrl)
                    intent.putExtra("pdfName", arrayList[holder.adapterPosition].docName)
                    context.startActivity(intent)
                }
            }

            holder.postImage.setOnClickListener {
                val intent = Intent(context,PostDetailActivity::class.java)
                intent.putExtra("uri",model.postImage)
                intent.putExtra("postedAt",model.postedAt)
                intent.putExtra("code","other")
                context.startActivity(intent)
            }

            holder.username.setOnClickListener {
                if(Firebase.auth.currentUser!!.uid == model.postedBy){
                    context.startActivity(Intent(context,ProfileActivity::class.java))
                }
                else{
                    val intent = Intent(context,OtherUsersProfileActivity::class.java)
                    intent.putExtra("uid",model.postedBy)
                    context.startActivity(intent)
                }
            }

            // Download Post
            holder.postMenu.setOnClickListener {
                val popupMenu: PopupMenu = PopupMenu(context, it)
                popupMenu.menuInflater.inflate(R.menu.post_menu, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.download_post -> {

                            if(holder.postImage.visibility == View.VISIBLE){

                                val intent = Intent(context,PostDetailActivity::class.java)
                                intent.putExtra("uri",model.postImage)
                                intent.putExtra("postedAt",model.postedAt)
                                intent.putExtra("code","other")
                                context.startActivity(intent)

                            }
                            else{

                                val intent = Intent(context,ViewDocumentActivityPost::class.java)
                                intent.putExtra("path",model.docUrl)
                                intent.putExtra("pdfName", arrayList[holder.adapterPosition].docName)
                                context.startActivity(intent)
                            }


                            true
                        }
                        else -> true
                    }
                }
                popupMenu.show()
            }

            try {
                // To check post is saved or not
                FirebaseDatabase.getInstance().reference
                    .child("users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("saved")
                    .addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                for(item in snapshot.children){
                                    if(item.key == model.postId){
                                        isSaved = true
                                        holder.savePost.setImageDrawable(ContextCompat.getDrawable(holder.savePost.context,R.drawable.ic_bookmark_filled))
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

            // To save post
            try {
                holder.savePost.setOnClickListener {
                    holder.savePost.isEnabled = false
                    if(isSaved){
                        FirebaseDatabase.getInstance().reference
                            .child("users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .child("saved")
                            .child(model.postId)
                            .removeValue().addOnCompleteListener {
                                if(it.isSuccessful){
                                    isSaved = false
                                    holder.savePost.setImageResource(R.drawable.ic_bookmark)
                                    holder.savePost.isEnabled = true
                                }
                            }
                    }
                    else{
                        FirebaseDatabase.getInstance().reference
                            .child("users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .child("saved")
                            .child(model.postId)
                            .setValue(model)
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    isSaved = true
                                    holder.savePost.setImageResource(R.drawable.ic_bookmark_filled)
                                    holder.savePost.isEnabled = true
                                }
                            }
                    }

                }
            } catch (e: Exception) {
            }

            // To keep the like data updated
            try {
                FirebaseDatabase.getInstance().reference
                    .child("posts")
                    .child(model.postId)
                    .child("likes")
                    .child(Firebase.auth.currentUser!!.uid)
                    .addValueEventListener(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(!snapshot.exists()){
//                                holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_unliked))
                                holder.likeButton.setImageResource(R.drawable.ic_unliked)
                                holder.likeCount.text =(model.postLike).toString()
                            }
                            else{
                                if(snapshot.value == true){
//                                    holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_liked))
                                    holder.likeButton.setImageResource(R.drawable.ic_liked)
                                    holder.likeCount.text =(model.postLike).toString()
                                }
                                else if(snapshot.value == false){
//                                    holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_unliked))
                                    holder.likeButton.setImageResource(R.drawable.ic_unliked)
                                    holder.likeCount.text =(model.postLike).toString()
                                }
                            }
//                            notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            } catch (e: Exception) {
            }

            holder.likeButton.setOnClickListener {

                holder.likeButton.isEnabled = false
                try {
                    FirebaseDatabase.getInstance().reference
                        .child("posts")
                        .child(model.postId)
                        .child("likes")
                        .child(Firebase.auth.currentUser!!.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (!snapshot.exists()) {
                                        FirebaseDatabase.getInstance().reference
                                            .child("posts")
                                            .child(model.postId)
                                            .child("likes")
                                            .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                            .setValue(true).addOnCompleteListener {
                                                if (it.isSuccessful) {
                                                    holder.likeCount.text =(model.postLike+1).toString()
                                                    FirebaseDatabase.getInstance().reference
                                                        .child("posts")
                                                        .child(model.postId)
                                                        .child("postLike")
                                                        .setValue(model.postLike + 1)
                                                        .addOnCompleteListener {               // holder.likeCount.text.toString().toInt()
                                                            if (it.isSuccessful) {
                                                                holder.likeButton.isEnabled = true
    //                                                            holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_liked))
                                                                holder.likeButton.setImageResource(R.drawable.ic_liked)
                                                            }
                                                        }
                                                }
                                            }

                                }
                                else {
                                    if (snapshot.value == true) {
                                            FirebaseDatabase.getInstance().reference
                                                .child("posts")
                                                .child(model.postId)
                                                .child("likes")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .setValue(false).addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        if((model.postLike-1)>=0){
                                                            holder.likeCount.text =(model.postLike-1).toString()
                                                            FirebaseDatabase.getInstance().reference
                                                                .child("posts")
                                                                .child(model.postId)
                                                                .child("postLike")
                                                                .setValue(model.postLike - 1)
                                                                .addOnCompleteListener {               // holder.likeCount.text.toString().toInt()
                                                                    if (it.isSuccessful) {
                                                                        holder.likeButton.isEnabled = true
                                                                        holder.likeButton.setImageResource(R.drawable.ic_unliked)
    //                                                                    holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_unliked))
                                                                    }
                                                                }
                                                        }
                                                    }
                                                }

                                    }
                                    else if (snapshot.value == false) {
                                            FirebaseDatabase.getInstance().reference
                                                .child("posts")
                                                .child(model.postId)
                                                .child("likes")
                                                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                                .setValue(true).addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        holder.likeCount.text =(model.postLike+1).toString()
                                                        FirebaseDatabase.getInstance().reference
                                                            .child("posts")
                                                            .child(model.postId)
                                                            .child("postLike")
                                                            .setValue(model.postLike + 1)
                                                            .addOnCompleteListener {               // holder.likeCount.text.toString().toInt()
                                                                if (it.isSuccessful) {
                                                                    holder.likeButton.isEnabled = true
                                                                    holder.likeButton.setImageResource(R.drawable.ic_liked)
    //                                                                holder.likeButton.setImageDrawable(ContextCompat.getDrawable(holder.likeButton.context,R.drawable.ic_liked))
                                                                }
                                                            }
                                                    }
                                                }

                                    }

                                }
                                notifyDataSetChanged()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                } catch (e: Exception) {
                }


            }


            try {
                FirebaseDatabase.getInstance().reference.child("users")
                    .child(model.postedBy!!).addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                val user = snapshot.getValue(User::class.java)
                                Glide.with(context)
                                    .load(user?.profilePicture)
                                    .placeholder(R.drawable.profile_placeholder)
                                    .into(holder.profileImage)
                                holder.username.text = user?.username
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            } catch (e: Exception) {
            }

        } catch (e: Exception) {
        }

    }


    override fun getItemCount(): Int {
        return arrayList.size
    }
}