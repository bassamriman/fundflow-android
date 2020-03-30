package com.rimanware.fundflow_android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fundflow.Fund

class FundRecyclerAdapter(
    private val context: Context,
    private val funds: List<Fund>
) :
    RecyclerView.Adapter<FundRecyclerAdapter.ViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = layoutInflater.inflate(R.layout.item_fund_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = funds.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val fund = funds[position]
        holder.textTitle?.text = fund.name
        holder.textDesciption?.text = fund.description
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textTitle = itemView.findViewById<TextView?>(R.id.testTitle)
        val textDesciption = itemView.findViewById<TextView?>(R.id.textDescription)
    }
}