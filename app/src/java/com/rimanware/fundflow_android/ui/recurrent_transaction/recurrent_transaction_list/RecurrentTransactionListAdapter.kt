package com.rimanware.fundflow_android.ui.recurrent_transaction.recurrent_transaction_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rimanware.fundflow_android.DataManager
import com.rimanware.fundflow_android.R
import com.rimanware.fundflow_android.ui.common.ClassArg
import fundflow.ledgers.RecurrentTransaction

class RecurrentTransactionListAdapter(
    private val recurrentTransactionListViewModelClassArg: ClassArg,
    private val fundListViewModelClassArg: ClassArg
) :
    ListAdapter<RecurrentTransaction, RecurrentTransactionListViewHolder>(
        AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<RecurrentTransaction>() {
            override fun areItemsTheSame(
                oldItem: RecurrentTransaction,
                newItem: RecurrentTransaction
            ): Boolean {
                return oldItem.reference == newItem.reference
            }

            override fun areContentsTheSame(
                oldItem: RecurrentTransaction,
                newItem: RecurrentTransaction
            ): Boolean {
                return oldItem == newItem
            }
        })
            .build()
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecurrentTransactionListViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView: View =
            layoutInflater.inflate(R.layout.item_recurrent_transaction_list, parent, false)
        return RecurrentTransactionListViewHolder(
            itemView,
            recurrentTransactionListViewModelClassArg,
            fundListViewModelClassArg
        )
    }

    override fun onBindViewHolder(holder: RecurrentTransactionListViewHolder, position: Int) {
        val recurrentTransaction = getItem(position)
        DataManager.loadFundUsingRef(recurrentTransaction.transactionCoordinates.source)
            .map { holder.textFromValue?.text = it.name }
        DataManager.loadFundUsingRef(recurrentTransaction.transactionCoordinates.destination)
            .map { holder.textToValue?.text = it.name }

        val flow = recurrentTransaction.quantification.flow
        val fundFlowAsString = "$${flow.value} ${flow.unit.perAlias}"
        holder.textFundFlowValue?.text = fundFlowAsString
    }
}

class RecurrentTransactionListViewHolder(
    itemView: View,
    private val recurrentTransactionListViewModelClassArg: ClassArg,
    private val fundListViewModelClassArg: ClassArg
) : RecyclerView.ViewHolder(itemView) {
    val textFromValue = itemView.findViewById<TextView?>(R.id.textFromValue)
    val textToValue = itemView.findViewById<TextView?>(R.id.textToValue)
    val textFundFlowValue = itemView.findViewById<TextView?>(R.id.textFundFlowValue)

    init {
        itemView.setOnClickListener {
            val action =
                RecurrentTransactionListFragmentDirections.actionNavRecurrentTransactionListToNavRecurrentTransactionEdit(
                    DataManager.loadAllRecurrentTransactions()[adapterPosition].reference.id,
                    recurrentTransactionListViewModelClassArg,
                    fundListViewModelClassArg
                )
            itemView.findNavController().navigate(action)
        }
    }
}
