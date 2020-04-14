package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_list

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


class RecurrentTransactionListFragment : Fragment() {

    private lateinit var recurrentTransactionListViewModel: RecurrentTransactionListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Inflate View
        val rootView: View =
            inflater.inflate(R.layout.fragment_recurrent_transaction_list, container, false)

        //Get the recurrentTransactionListViewModel
        recurrentTransactionListViewModel = activity?.run {
            ViewModelProvider(this).get(RecurrentTransactionListViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        //Setup recycler view
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.recurrentTransactionListItems)

        val recurrentTransactionListAdapter = RecurrentTransactionListAdapter()

        recurrentTransactionListViewModel.recurrentTransactions.observeForever(Observer {
            recurrentTransactionListAdapter.submitList(it)
        })

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = recurrentTransactionListAdapter

        val fab: FloatingActionButton = rootView.findViewById(R.id.recurrentTransactionFab)

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