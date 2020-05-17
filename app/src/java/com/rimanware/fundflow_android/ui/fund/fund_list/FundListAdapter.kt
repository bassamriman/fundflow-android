package com.rimanware.fundflow_android.ui.fund.fund_list

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
import fundflow.Fund

class FundListAdapter : ListAdapter<Fund, FundListViewHolder>(
    AsyncDifferConfig.Builder(object : DiffUtil.ItemCallback<Fund>() {
        override fun areItemsTheSame(oldItem: Fund, newItem: Fund): Boolean {
            return oldItem.reference == newItem.reference
        }

        override fun areContentsTheSame(oldItem: Fund, newItem: Fund): Boolean {
            return oldItem == newItem
        }
    })
        .build()
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FundListViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val itemView: View = layoutInflater.inflate(R.layout.item_fund_list, parent, false)
        return FundListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FundListViewHolder, position: Int) {
        val fund = getItem(position)
        holder.textTitle?.text = fund.name
        holder.textDesciption?.text = fund.description
    }
}

class FundListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textTitle = itemView.findViewById<TextView?>(R.id.testTitle)
    val textDesciption = itemView.findViewById<TextView?>(R.id.textDescription)

    init {
        itemView.setOnClickListener {
            val action =
                FundListFragmentDirections.actionNavFundListToNavFundView(DataManager.loadAllFunds()[adapterPosition].reference.id)
            itemView.findNavController().navigate(action)
        }
    }
}
