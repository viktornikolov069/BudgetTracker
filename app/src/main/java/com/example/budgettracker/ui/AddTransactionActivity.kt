package com.example.budgettracker.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.room.Entity
import androidx.room.Room
import com.example.budgettracker.R
import com.example.budgettracker.databinding.ActivityAddTransactionBinding
import com.example.budgettracker.db.AppDatabase
import com.example.budgettracker.db.Transaction
import com.example.budgettracker.utils.Constants.APP_DATABASE

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val appDB: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, APP_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }
    private lateinit var transaction: Transaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* Removes error message */
        binding.etLabelInput.addTextChangedListener {
            if (it!!.isNotEmpty()) {
                binding.tilLabelLayout.error = null
            }
        }
        /* Removes error message */
        binding.etAmountInput.addTextChangedListener {
            if (it!!.isNotEmpty()) {
                binding.tilAmountLayout.error = null
            }
        }

        /* Inserts transaction (label, amount, description) in to data base */
        binding.apply {
            btnAddTransaction.setOnClickListener {
                val label = etLabelInput.text.toString()
                val amount = etAmountInput.text.toString().toDoubleOrNull()//ToDoubleOrNUll
                val description  = etDescriptionInput.text.toString()

                if (label.isEmpty()){
                    tilLabelLayout.error = getString(R.string.error_empty_label)
                }
                else if (amount == null) {
                    tilAmountLayout.error = getString(R.string.error_empty_amount)
                } else {
                    transaction = Transaction(0, label, amount, description)
                    appDB.transactionDao().insertAll(transaction)
                    finish()
                }
            }

            btnClose.setOnClickListener {
                finish()
            }

            /* Hides keyboard after tapping on the main layout */
            rootView.setOnClickListener {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }

    }
}