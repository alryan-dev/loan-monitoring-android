package com.example.loanmonitoring.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.example.loanmonitoring.R
import com.example.loanmonitoring.databinding.ItemLoanBinding
import com.example.loanmonitoring.models.Loan

class LoansRvAdapter(
    private var loansList: List<Loan>,
    private val onItemSelectListener: OnItemSelectListener,
) :
    RecyclerView.Adapter<LoansRvAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemLoanBinding, onItemSelectListener: OnItemSelectListener) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
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
        val binding = DataBindingUtil.inflate<ItemLoanBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_loan,
            parent,
            false
        )

        return ViewHolder(binding, onItemSelectListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setVariable(BR.loan, loansList[position])
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return loansList.size
    }
}