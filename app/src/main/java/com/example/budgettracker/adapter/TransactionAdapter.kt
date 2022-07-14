package com.example.budgettracker.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.budgettracker.ui.AddTransactionActivity
import com.example.budgettracker.R
import com.example.budgettracker.databinding.TransactionLayoutBinding
import com.example.budgettracker.db.Transaction
import com.example.budgettracker.ui.DetailedActivity
import com.example.budgettracker.utils.Constants.BUNDLE_TRANSACTION_ID
import kotlin.math.abs

class TransactionAdapter: RecyclerView.Adapter<TransactionAdapter.TransactionHolder>() {

    private lateinit var binding: TransactionLayoutBinding
    private lateinit var context: Context

    inner class TransactionHolder: RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Transaction) {
            binding.apply {
                tvLabel.text = item.label

                /* Change the number color based on the amount */
                if (item.amount >= 0) {
                    tvAmount.text = "+ $%.2f".format(item.amount)
                    tvAmount.setTextColor(ContextCompat.getColor(context, R.color.green))
                } else {
                    tvAmount.text = "- $%.2f".format(abs(item.amount))
                    tvAmount.setTextColor(ContextCompat.getColor(context, R.color.red))
                }

                root.setOnClickListener {
                    val intent = Intent(context, DetailedActivity::class.java)
                    intent.putExtra(BUNDLE_TRANSACTION_ID, item.id)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = TransactionLayoutBinding.inflate(layoutInflater, parent, false)
        context = parent.context
        return TransactionHolder()
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int = differ.currentList.size

    /* Using DiffUtil to improve recycler view performance */
    private val differCallback = object: DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
}







