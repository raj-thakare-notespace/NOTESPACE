package com.startup.notespace.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.startup.notespace.PostDetailActivity
import com.startup.notespace.R

// Adapter to load posts in profile
class ProfilePostAdapterOthers(val context: Context, var arrayList: ArrayList<String>) :
    RecyclerView.Adapter<ProfilePostAdapterOthers.ViewHolder>() {

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
            .load(arrayList[holder.adapterPosition])
            .into(holder.postImageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context,PostDetailActivity::class.java)
            intent.putExtra("uri",arrayList[holder.adapterPosition])
            intent.putExtra("code","other")
            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}