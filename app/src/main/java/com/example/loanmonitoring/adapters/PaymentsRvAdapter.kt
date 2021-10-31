package com.example.loanmonitoring.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loanmonitoring.R
import com.example.loanmonitoring.Utils
import com.example.loanmonitoring.Utils.toUserModel
import com.example.loanmonitoring.models.Payment
import com.example.loanmonitoring.models.UserModel
import com.google.firebase.auth.FirebaseAuth

class PaymentsRvAdapter(
    private var paymentsList: List<Payment>,
    private val onItemSelectListener: OnItemSelectListener,
    private val onConfirmListener: OnItemSelectListener,
    private val borrower: UserModel?
) : RecyclerView.Adapter<PaymentsRvAdapter.ViewHolder>() {

    class ViewHolder(
        view: View,
        onItemSelectListener: OnItemSelectListener,
        onConfirmListener: OnItemSelectListener
    ) :
        RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val btnConfirm: ImageButton = view.findViewById(R.id.btnConfirm)

        init {
            view.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    onItemSelectListener.onItemClick(currentPosition)
                }
            }

            btnConfirm.setOnClickListener {
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
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment, parent, false)
        return ViewHolder(view, onItemSelectListener, onConfirmListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvAmount.text = Utils.toCurrencyFormat(paymentsList[position].amount)
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

        // Show/Hide confirm button
        if (
            paymentsList[position].lenderConfirmed ||
            borrower?.uid == FirebaseAuth.getInstance().currentUser.toUserModel().uid
        )
            holder.btnConfirm.visibility = View.GONE
        else
            holder.btnConfirm.visibility = View.VISIBLE
    }

    override fun getItemCount(): Int {
        return paymentsList.size
    }
}