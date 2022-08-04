package com.startup.notespace.ui.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.startup.notespace.R
import com.startup.notespace.adapters.SearchAdapter
import com.startup.notespace.models.UserSearch
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SearchFragment : Fragment() {

    companion object {
        fun newInstance() = SearchFragment()
    }

    private lateinit var viewModel: SearchViewModel

    private lateinit var mSearchField : EditText
    private lateinit var searchRV : RecyclerView
    private lateinit var mUserDatabase : DatabaseReference

    private lateinit var searchAdapter: SearchAdapter

    override fun onStart() {
        super.onStart()
        searchAdapter.startListening()
        searchAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        searchAdapter.stopListening()
        searchAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        searchAdapter.startListening()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users")


        mSearchField = view.findViewById(R.id.search_field)

        searchRV = view.findViewById(R.id.result_list)

        searchRV.setHasFixedSize(false)

        searchRV.layoutManager = LinearLayoutManager(view.context)


        val options: FirebaseRecyclerOptions<UserSearch> = FirebaseRecyclerOptions.Builder<UserSearch>()
            .setQuery(FirebaseDatabase.getInstance().reference.child("users").limitToFirst(5), UserSearch::class.java)
            .build()

        searchAdapter = SearchAdapter(options)

        searchRV.adapter = searchAdapter



        mSearchField.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                txtSearch(s.toString())
                searchAdapter.startListening()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                txtSearch(s.toString())
                searchAdapter.startListening()
            }
        })



    }

    fun txtSearch(str : String){

        try {
            val options: FirebaseRecyclerOptions<UserSearch> = FirebaseRecyclerOptions.Builder<UserSearch>()
                .setQuery(FirebaseDatabase.getInstance().reference.child("users").orderByChild("username")
                    .startAt(str).endAt(str+"~").limitToFirst(5), UserSearch::class.java)
                .build()
            searchAdapter = SearchAdapter(options)
            searchAdapter.startListening()
            searchRV.adapter = searchAdapter
        } catch (e: Exception) {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_fragment, container, false)
    }

}