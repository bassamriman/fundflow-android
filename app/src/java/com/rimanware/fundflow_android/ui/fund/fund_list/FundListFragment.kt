package com.rimanware.fundflow_android.ui.fund.fund_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rimanware.fundflow_android.databinding.FragmentFundListBinding
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment


class FundListFragment : ViewBindingFragment<FragmentFundListBinding>() {

    private lateinit var fundListViewModel: FundListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindView(
            FragmentFundListBinding.inflate(
                inflater,
                container,
                false
            )
        )

        val root = viewBinding.root

        //Get the fundListViewModel
        fundListViewModel = activity?.run {
            ViewModelProvider(this).get(FundListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        //Setup recycler view
        val recyclerView = viewBinding.listItems

        val fundListAdapter = FundListAdapter()

        fundListViewModel.funds.observeForever(Observer {
            fundListAdapter.submitList(it)
        })

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = fundListAdapter

        val fab: FloatingActionButton = viewBinding.fab

        fab.setOnClickListener {
            val action =
                FundListFragmentDirections.actionNavFundListToNavFundEdit("")
            it.findNavController().navigate(action)
        }

        return root
    }


}