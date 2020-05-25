package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rimanware.fundflow_android.databinding.FragmentRecurrentTransactionListBinding
import com.rimanware.fundflow_android.ui.common.ClassArg
import com.rimanware.fundflow_android.ui.common.ViewBindingFragment
import com.rimanware.fundflow_android.ui.common.viewModels
import com.rimanware.fundflow_android.ui.fund.fund_list.FundListViewModel
import kotlinx.android.synthetic.main.fragment_recurrent_transaction_list.view.*

class RecurrentTransactionListFragment :
    ViewBindingFragment<FragmentRecurrentTransactionListBinding>() {

    private val recurrentTransactionListViewModel: RecurrentTransactionListViewModel by viewModels()

    private val recurrentTransactionListViewModelClassArg =
        ClassArg(RecurrentTransactionListViewModel::class.java)

    private val fundListViewModelClassArg = ClassArg(FundListViewModel::class.java)

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
        val coordinatorLayout = viewBinding.coordinatorLayout

        val filterIcon = coordinatorLayout.filterIcon
        val contentLayout: LinearLayout = coordinatorLayout.contentLayout

        val sheetBehavior = BottomSheetBehavior.from(contentLayout)
        sheetBehavior.isFitToContents = false
        sheetBehavior.isHideable =
            false //prevents the boottom sheet from completely hiding off the screen

        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED //initially state to fully expanded

        filterIcon.setOnClickListener { toggleFilters() }

        // Inflate View
        val rootView: View = viewBinding.root

        // Setup recycler view
        val recyclerView = viewBinding.recurrentTransactionListItems

        val recurrentTransactionListAdapter = RecurrentTransactionListAdapter(
            recurrentTransactionListViewModelClassArg,
            fundListViewModelClassArg
        )

        recurrentTransactionListViewModel.recurrentTransactions.observeForever {
            recurrentTransactionListAdapter.submitList(it)
        }

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = recurrentTransactionListAdapter

        val fab: FloatingActionButton = viewBinding.recurrentTransactionFab

        fab.setOnClickListener {
            val action =
                RecurrentTransactionListFragmentDirections.actionNavRecurrentTransactionListToNavRecurrentTransactionEdit(
                    "",
                    recurrentTransactionListViewModelClassArg,
                    fundListViewModelClassArg
                )
            it.findNavController().navigate(action)
        }

        return rootView
    }

    private fun toggleFilters() {
        val sheetBehavior: BottomSheetBehavior<LinearLayout> =
            BottomSheetBehavior.from(viewBinding.coordinatorLayout.contentLayout)
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        } else {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}
