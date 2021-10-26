package com.example.loanmonitoring.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loanmonitoring.R
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.models.Payment
import java.text.DecimalFormat

class PaymentsRvAdapter(
    private var paymentsList: List<Payment>,
    private val onItemSelectListener: OnItemSelectListener,
) : RecyclerView.Adapter<PaymentsRvAdapter.ViewHolder>() {

    class ViewHolder(view: View, onItemSelectListener: OnItemSelectListener) :
        RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)

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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment, parent, false)
        return ViewHolder(view, onItemSelectListener)
    }

    override fun onBindViewHolder(holder: PaymentsRvAdapter.ViewHolder, position: Int) {
        // Set amount field
        val formatter = DecimalFormat("#,###.00")
        val amount = "₱" + formatter.format(paymentsList[position].amount)
        holder.tvAmount.text = amount

        // Set date field
        holder.tvDate.text = Utils.calendarToString(paymentsList[position].date)

        // Set status field
        val status: String = if (paymentsList[position].lenderConfirmed) {
            holder.tvStatus.setTextColor(Color.parseColor("#00C853"))
            "CONFIRMED"
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#FF0000"))
            "PENDING"
        }
        holder.tvStatus.text = status
    }

    override fun getItemCount(): Int {
        return paymentsList.size
    }
}