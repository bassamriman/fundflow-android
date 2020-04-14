package com.rimanware.fundflow_android.ui.fund.fund_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rimanware.fundflow_android.R


class FundListFragment : Fragment() {

    private lateinit var fundListViewModel: FundListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflate View
        val rootView: View = inflater.inflate(R.layout.fragment_fund_list, container, false)

        //Get the fundListViewModel
        fundListViewModel = activity?.run {
            ViewModelProvider(this).get(FundListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        //Setup recycler view
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.listItems)

        val fundListAdapter = FundListAdapter()

        fundListViewModel.funds.observeForever(Observer {
            fundListAdapter.submitList(it)
        })

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = fundListAdapter

        val fab: FloatingActionButton = rootView.findViewById(R.id.fab)

        fab.setOnClickListener {
            val action =
                FundListFragmentDirections.actionNavFundListToNavFundEdit("")
            it.findNavController().navigate(action)
        }

        return rootView
    }


}