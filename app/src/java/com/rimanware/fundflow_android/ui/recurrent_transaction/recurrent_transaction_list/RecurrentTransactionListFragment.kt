package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rimanware.fundflow_android.databinding.FragmentRecurrentTransactionListBinding
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment
import com.rimanware.fundflow_android.ui.common.viewModels

class RecurrentTransactionListFragment :
    ViewBindingFragment<FragmentRecurrentTransactionListBinding>() {

    private val recurrentTransactionListViewModel: RecurrentTransactionListViewModel by viewModels()

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

        // Inflate View
        val rootView: View = viewBinding.root

        // Setup recycler view
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
