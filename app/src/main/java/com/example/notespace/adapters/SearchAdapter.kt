package com.example.notespace.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.notespace.*
import com.example.notespace.models.UserSearch
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

// Adapter to load search results
class SearchAdapter(options: FirebaseRecyclerOptions<UserSearch>) :
    FirebaseRecyclerAdapter<UserSearch, SearchAdapter.ViewHolder>(
        options
    ) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username: TextView = itemView.findViewById(R.id.usernameSearch)
        var displayName: TextView = itemView.findViewById(R.id.displayNameSearch)
        var profilePicture: CircleImageView = itemView.findViewById(R.id.profilePictureSearch)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_search_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: UserSearch) {
        holder.username.text = model.username
        holder.displayName.text = model.displayName
        Glide.with(holder.profilePicture.context)
            .load(model.profilePicture)
            .placeholder(R.drawable.profile_placeholder)
            .into(holder.profilePicture)

        var arrayListMyCreatedGroups : ArrayList<String> = ArrayList()

        try {
            Firebase.database.reference.child("users").child(Firebase.auth.currentUser!!.uid).child("my_created_groups")
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayListMyCreatedGroups.clear()
                            for(item in snapshot.children){
                                arrayListMyCreatedGroups.add(item.key.toString())
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        } catch (e: Exception) {
        }

        holder.itemView.setOnClickListener {
            if(model.uid == Firebase.auth.currentUser!!.uid){
                it.context.startActivity(Intent(it.context,ProfileActivity::class.java))
            }
            else if (model.uid.toString().length < 16) {
                if(arrayListMyCreatedGroups.contains(model.uid)){
                    val intent = Intent(it.context, GroupProfileActivity::class.java)
                    intent.putExtra("usernameGroup", model.username)
                    intent.putExtra("uid", model.uid)
                    it.context.startActivity(intent)
                }
                else{
                    val intent = Intent(it.context, GroupProfileOther::class.java)
                    intent.putExtra("usernameGroup", model.username)
                    intent.putExtra("uid", model.uid)
                    it.context.startActivity(intent)
                }

            } else {
                val intent = Intent(it.context, OtherUsersProfileActivity::class.java)
                intent.putExtra("uid", model.uid)
                intent.putExtra("username", model.username)
                intent.putExtra("profilePicture", model.profilePicture)
                it.context.startActivity(intent)
            }

        }


    }
}