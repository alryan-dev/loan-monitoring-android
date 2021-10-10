package com.example.loanmonitoring.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loanmonitoring.R
import com.example.loanmonitoring.models.Loan

class LoansRvAdapter(
    private var loansList: List<Loan>,
    private val onItemSelectListener: OnItemSelectListener,
) :
    RecyclerView.Adapter<LoansRvAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvBorrower: TextView = view.findViewById(R.id.tvBorrower)
        val tvLender: TextView = view.findViewById(R.id.tvLender)
    }

    interface OnItemSelectListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_loan, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val amount = "₱" + String.format("%.2f", loansList[position].amount)
        holder.tvAmount.text = amount

        val borrower =
            holder.itemView.context.getString(R.string.label_borrower) + ": " + loansList[position].borrower?.displayName
        holder.tvBorrower.text = borrower

        val lender =
            holder.itemView.context.getString(R.string.label_lender) + ": " + loansList[position].lender?.displayName
        holder.tvLender.text = lender
    }

    override fun getItemCount(): Int {
        return loansList.size
    }
}