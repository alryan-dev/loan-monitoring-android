package com.example.loanmonitoring.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loanmonitoring.R
import com.example.loanmonitoring.models.Loan

class LoansRvAdapter(
    private var loanList: List<Loan>,
    private val onItemSelectListener: OnItemSelectListener,
) :
    RecyclerView.Adapter<LoansRvAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvLoanName: TextView

        init {
            tvLoanName = view.findViewById(R.id.tvLoanName)
        }
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
        holder.tvLoanName.text = loanList[position].totalAmount.toString()
    }

    override fun getItemCount(): Int {
        return loanList.size
    }
}