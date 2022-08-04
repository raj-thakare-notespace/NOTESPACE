package com.startup.notespace

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.startup.notespace.adapters.OnlineNoteAdapterOther
import com.startup.notespace.models.NoteModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NotesActivityOther : AppCompatActivity() {


    lateinit var recyclerView : RecyclerView
    lateinit var arrayList : ArrayList<NoteModel>
    lateinit var adapter : OnlineNoteAdapterOther

    lateinit var searchView : SearchView

    lateinit var searchLayoutNotesOther : TextInputLayout

    private lateinit var toolbar: MaterialToolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes_other)

        val userId = intent.getStringExtra("uid")

        toolbar  = findViewById(R.id.toolBarNotesOther)

        toolbar.setNavigationOnClickListener {
            finish()
        }



        recyclerView  = findViewById(R.id.recycler_view_notesOther)
        arrayList = ArrayList()
        adapter = OnlineNoteAdapterOther(this,arrayList)
        recyclerView.layoutManager =  StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter

        searchView = findViewById(R.id.searchViewNotesOther)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })


        try {
            FirebaseDatabase.getInstance().reference.child("notes")
                .child(userId.toString())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayList.clear()
                            for(item in snapshot.children){
                                val model = item.getValue(NoteModel::class.java)
                                if(model!!.isPrivate){
                                    continue
                                }
                                arrayList.add(model!!)
                                adapter.notifyDataSetChanged()
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

    private fun filterList(text: String?) {
        var filteredList = java.util.ArrayList<NoteModel>()
        for(item in arrayList){
            if (text != null) {
                if(item.title.toLowerCase().contains(text.toLowerCase())){
                    filteredList.add(item)
//                    noItemFoundTV.visibility = View.GONE
                }
            }
        }

        if(filteredList.isEmpty()){
//            noItemFoundTV.visibility = View.VISIBLE
            searchLayoutNotesOther.error = "No data found"
        }
        else{
            adapter.setFilteredList(filteredList)
        }
    }

}