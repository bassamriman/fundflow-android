package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rimanware.fundflow_android.databinding.FragmentRecurrentTransactionListBinding
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment


class RecurrentTransactionListFragment :
    ViewBindingFragment<FragmentRecurrentTransactionListBinding>() {

    private lateinit var recurrentTransactionListViewModel: RecurrentTransactionListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bindView(
            FragmentRecurrentTransactionListBinding.inflate(
                inflater,
                container,
                false
            )
        )

        //Inflate View
        val rootView: View = viewBinding.root

        //Get the recurrentTransactionListViewModel
        recurrentTransactionListViewModel = activity?.run {
            ViewModelProvider(this).get(RecurrentTransactionListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        //Setup recycler view
        val recyclerView = viewBinding.recurrentTransactionListItems

        val recurrentTransactionListAdapter = RecurrentTransactionListAdapter()

        recurrentTransactionListViewModel.recurrentTransactions.observeForever {
            recurrentTransactionListAdapter.submitList(it)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = recurrentTransactionListAdapter

        val fab: FloatingActionButton = viewBinding.recurrentTransactionFab

        fab.setOnClickListener {
            val action =
                RecurrentTransactionListFragmentDirections.actionNavRecurrentTransactionListToNavRecurrentTransactionEdit(
                    ""
                )
            it.findNavController().navigate(action)
        }

        return rootView
    }


}