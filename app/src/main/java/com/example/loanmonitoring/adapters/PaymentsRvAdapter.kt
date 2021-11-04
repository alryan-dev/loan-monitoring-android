package com.example.loanmonitoring.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.example.loanmonitoring.R
import com.example.loanmonitoring.databinding.ItemPaymentBinding
import com.example.loanmonitoring.models.Payment
import com.example.loanmonitoring.models.UserModel

class PaymentsRvAdapter(
    private var paymentsList: List<Payment>,
    private val onItemSelectListener: OnItemSelectListener,
    private val onConfirmListener: OnItemSelectListener,
    private val borrower: UserModel?
) : RecyclerView.Adapter<PaymentsRvAdapter.ViewHolder>() {

    class ViewHolder(
        val binding: ItemPaymentBinding,
        onItemSelectListener: OnItemSelectListener,
        onConfirmListener: OnItemSelectListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onItemSelectListener.onItemClick(currentPosition)
                }
            }

            binding.btnConfirm.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onConfirmListener.onItemClick(currentPosition)
                }
            }
        }
    }

    interface OnItemSelectListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemPaymentBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_payment,
            parent,
            false
        )
        return ViewHolder(binding, onItemSelectListener, onConfirmListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.setVariable(BR.payment, paymentsList[position])
        holder.binding.setVariable(BR.borrower, borrower)
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int {
        return paymentsList.size
    }
}