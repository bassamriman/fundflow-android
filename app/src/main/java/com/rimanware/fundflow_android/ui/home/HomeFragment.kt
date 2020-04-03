package com.rimanware.fundflow_android.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.FundRecyclerAdapter
import com.rimanware.fundflow_android.R


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root: View = inflater.inflate(R.layout.fragment_home, container, false)
        // 1. get a reference to recyclerView

        // 1. get a reference to recyclerView
        val recyclerView = root.findViewById<RecyclerView>(R.id.listItems)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = context?.let { FundRecyclerAdapter(it) { -> DataManager.funds() } }

        return root
    }


}