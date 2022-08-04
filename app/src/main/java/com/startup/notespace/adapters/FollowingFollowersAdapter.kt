package com.startup.notespace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.startup.notespace.OtherUsersProfileActivity
import com.startup.notespace.ProfileActivity
import com.startup.notespace.R
import com.startup.notespace.models.AllChatModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView

// Adapter to list  down following users or followers of any profile
class FollowingFollowersAdapter(val context: Context, val arrayList: ArrayList<AllChatModel>) :
    RecyclerView.Adapter<FollowingFollowersAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val displayName: TextView = itemView.findViewById(R.id.displayNameAllChat)
        val username: TextView = itemView.findViewById(R.id.usernameAllChat)
        val profilePicture: CircleImageView = itemView.findViewById(R.id.profilePictureAllChat)
        val deleteIcon : ImageView = itemView.findViewById(R.id.deleteIconThreeDots)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.all_chat_item_rv, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = arrayList[position]
        holder.displayName.text = model.displayName
        holder.username.text = model.username
        Glide.with(context)
            .load(model.profilePicture)
            .placeholder(R.drawable.profile_placeholder)
            .into(holder.profilePicture)

        holder.deleteIcon.visibility = View.INVISIBLE

        holder.itemView.setOnClickListener {
            if(model.uid == Firebase.auth.currentUser!!.uid){
                val intent = Intent(it.context, ProfileActivity::class.java)
                intent.putExtra("uid", model.uid)
                intent.putExtra("username", model.username)
                intent.putExtra("profilePicture", model.profilePicture)
                context.startActivity(intent)
            }
            else{
                val intent = Intent(it.context, OtherUsersProfileActivity::class.java)
                intent.putExtra("uid", model.uid)
                intent.putExtra("username", model.username)
                intent.putExtra("profilePicture", model.profilePicture)
                context.startActivity(intent)
            }

        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}