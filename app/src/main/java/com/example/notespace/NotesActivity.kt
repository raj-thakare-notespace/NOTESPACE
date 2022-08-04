package com.example.notespace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notespace.adapters.OnlineNoteAdapter
import com.example.notespace.models.NoteModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NotesActivity : AppCompatActivity() {

    lateinit var fab : FloatingActionButton

    lateinit var recyclerView : RecyclerView
    lateinit var arrayList : ArrayList<NoteModel>
    lateinit var adapter : OnlineNoteAdapter
//    lateinit var backArrow : ImageView

    lateinit var searchView : SearchView

    lateinit var materialToolbar: MaterialToolbar

    lateinit var noItemFoundTextView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        materialToolbar = findViewById(R.id.toolBarNotes)

        noItemFoundTextView = findViewById(R.id.noItemFoundTVNotes)

        materialToolbar.setNavigationOnClickListener {
            finish()
        }

        val userId = intent.getStringExtra("uid")



        recyclerView  = findViewById(R.id.recycler_view_notes)
        arrayList = ArrayList()
        adapter = OnlineNoteAdapter(this,arrayList)
        recyclerView.layoutManager =  StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = adapter

        fab = findViewById(R.id.fab_notes)
        searchView = findViewById(R.id.searchViewNoteActivity)


        fab.setOnClickListener {
            val intent = Intent(this,CreateOnlineNoteActivity::class.java)
            startActivity(intent)
        }



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
                .child(FirebaseAuth.getInstance().currentUser!!.uid)
                .addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            arrayList.clear()
                            for(item in snapshot.children){
                                val model = item.getValue(NoteModel::class.java)
                                arrayList.add(model!!)
                            }
                            adapter.notifyDataSetChanged()
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
                    recyclerView.visibility = View.VISIBLE
                    noItemFoundTextView.visibility = View.GONE
                }
            }
        }

        if(filteredList.isEmpty()){
//            noItemFoundTV.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            noItemFoundTextView.visibility = View.VISIBLE
        }
        else{
            recyclerView.visibility = View.VISIBLE
            noItemFoundTextView.visibility = View.GONE
            adapter.setFilteredList(filteredList)
        }
    }

}