package com.example.loanmonitoring.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loanmonitoring.R
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.models.Loan

class LoansRvAdapter(
    private var loansList: List<Loan>,
    private val onItemSelectListener: OnItemSelectListener,
) :
    RecyclerView.Adapter<LoansRvAdapter.ViewHolder>() {

    class ViewHolder(view: View, onItemSelectListener: OnItemSelectListener) :
        RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvBorrower: TextView = view.findViewById(R.id.tvBorrower)
        val tvLender: TextView = view.findViewById(R.id.tvLender)

        init {
            view.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onItemSelectListener.onItemClick(currentPosition)
                }
            }
        }
    }

    interface OnItemSelectListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_loan, parent, false)
        return ViewHolder(view, onItemSelectListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAmount.text = Utils.toCurrencyFormat(loansList[position].amount)

        loansList[position].borrower?.let { borrower ->
            val borrowerName = holder.itemView.context.getString(R.string.label_borrower) +
                    ": " + Utils.checkCurrentUser(borrower)
            holder.tvBorrower.text = borrowerName
        }

        loansList[position].lender?.let { lender ->
            val lenderName = holder.itemView.context.getString(R.string.label_lender) +
                    ": " + Utils.checkCurrentUser(lender)
            holder.tvLender.text = lenderName
        }
    }

    override fun getItemCount(): Int {
        return loansList.size
    }
}