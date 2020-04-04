package com.rimanware.fundflow_android.ui.fund_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rimanware.fundflow_android.R


class FundListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflate View
        val rootView: View = inflater.inflate(R.layout.fragment_fund_list, container, false)

        //Get the fundListViewModel
        val fundListViewModel =
            ViewModelProvider(this).get(FundListViewModel::class.java)

        //Setup recycler view
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.listItems)

        val fundListAdapter = FundListAdapter()

        fundListViewModel.funds.observe(viewLifecycleOwner, Observer {
            fundListAdapter.submitList(it)
        })

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = fundListAdapter

        return rootView
    }


}