package com.example.budgettracker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.budgettracker.R
import com.example.budgettracker.adapter.TransactionAdapter
import com.example.budgettracker.databinding.ActivityMainBinding
import com.example.budgettracker.db.AppDatabase
import com.example.budgettracker.db.Transaction
import com.example.budgettracker.utils.Constants.APP_DATABASE
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val transactionAdapter by lazy { TransactionAdapter() }
    private val appDB: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, APP_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    /* deletedTransaction and oldTransactionList are used when the user uses the UNDO option after
       deleting a row in the recycler view */
    private lateinit var deletedTransaction: Transaction
    private lateinit var oldTransactionList: List<Transaction>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }

        // Enables swiping of a row in recycler view
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                deleteTransaction(transactionAdapter.differ.currentList[viewHolder.adapterPosition])
            }
        }

        val swipeHelper = ItemTouchHelper(itemTouchHelper)
        swipeHelper.attachToRecyclerView(binding.rvRecyclerView)
    }



    private fun deleteTransaction(transaction: Transaction) {
        deletedTransaction = transaction
        oldTransactionList = transactionAdapter.differ.currentList
        appDB.transactionDao().delete(transaction)

        //Creates a new list of transactions excluding the deleted transaction
        transactionAdapter.differ.submitList(appDB.transactionDao().getAll().filter { it.id != transaction.id })

        runOnUiThread {
            updateDashBoard()
            showSnackbar()
        }
    }

    // Snackbar is shown temporarily after a row is deleted and provides an UNDO option
    private fun showSnackbar() {
        val snackbar = Snackbar.make(binding.coordLayoutMain, "Transaction deleted!", Snackbar.LENGTH_LONG)
        snackbar.setAction("Undo") {
            undoDelete()
        }
            .setActionTextColor(ContextCompat.getColor(this, R.color.red))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()

    }

    private fun undoDelete() {
        appDB.transactionDao().insertAll(deletedTransaction)
        transactionAdapter.differ.submitList(oldTransactionList)

        runOnUiThread {
            setupRecyclerView()
            updateDashBoard()
        }

    }

    override fun onResume() {
        super.onResume()
        checkItem()
    }

    private fun checkItem() {
        binding.apply {
            if (appDB.transactionDao().getAll().isNotEmpty()) {
                transactionAdapter.differ.submitList(appDB.transactionDao().getAll())
                setupRecyclerView()
                updateDashBoard()
            }
        }
    }

    /* */
    private fun updateDashBoard() {
        val transactions = appDB.transactionDao().getAll()
        val totalAmount = transactions.sumOf { it.amount }
        val budgetAmount = transactions.filter { it.amount > 0 }.sumOf { it.amount }
        val expenseAmount = totalAmount - budgetAmount

        binding.tvBalance.text = "$ %.2f".format(totalAmount)
        binding.tvBudget.text = "$ %.2f".format(budgetAmount)
        binding.tvExpense.text = "$ %.2f".format(expenseAmount)
    }

    /* */
    private fun setupRecyclerView() {
        binding.rvRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = transactionAdapter
        }
    }
}














