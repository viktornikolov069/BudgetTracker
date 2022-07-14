package com.example.budgettracker.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.room.Room
import com.example.budgettracker.databinding.ActivityDetailedBinding
import com.example.budgettracker.db.AppDatabase
import com.example.budgettracker.db.Transaction
import com.example.budgettracker.utils.Constants.APP_DATABASE
import com.example.budgettracker.utils.Constants.BUNDLE_TRANSACTION_ID
import com.google.android.material.snackbar.Snackbar

class DetailedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailedBinding
    private val appDB: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, APP_DATABASE)
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    private lateinit var transaction: Transaction
    private var id = 0
    private var label = ""
    private var amount = 0.0
    private var description = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* This intent is being sent from the TransactionAdapter class */
        intent.extras?.let {
            id = it.getInt(BUNDLE_TRANSACTION_ID)
        }

        binding.apply {
            /* appDB.transactionDao().getAll().find {}
               extracts only a single element (Transaction) from the database which holds a
            *  List<Transaction> */
            label = appDB.transactionDao().getAll().find { it.id == id }!!.label
            amount = appDB.transactionDao().getAll().find { it.id == id }!!.amount
            description = appDB.transactionDao().getAll().find { it.id == id }!!.description

            etLabelInput.setText(label)
            etAmountInput.setText(amount.toString())
            etDescriptionInput.setText(description)

            /* Hides keyboard after tapping on the main layout */
            rootView.setOnClickListener {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(it.windowToken, 0)
            }

            /* Button btnUpdate is set to visible after an edit has been made */
            etLabelInput.addTextChangedListener {
                btnUpdate.visibility = View.VISIBLE
            }

            etAmountInput.addTextChangedListener {
                btnUpdate.visibility = View.VISIBLE
            }

            etDescriptionInput.addTextChangedListener {
                btnUpdate.visibility = View.VISIBLE
            }

            /* On pressing btnUpdate either there is an update of the data or an error
               message is shown */
            btnUpdate.setOnClickListener {
                val label = etLabelInput.text.toString()
                val amount = etAmountInput.text.toString().toDoubleOrNull()
                val description = etDescriptionInput.text.toString()

                if (label.isNotEmpty() && (amount != null)) {
                    transaction = Transaction(id, label, amount, description)
                    appDB.transactionDao().update(transaction)
                    finish()
                } else if (label.isEmpty()) {
                    tilLabelLayout.error = "Please enter a valid label"
                    Snackbar.make(
                        it,
                        "Label and Amount cannot be Empty", Snackbar.LENGTH_LONG
                    ).show()
                } else if (amount == null) {
                    tilAmountLayout.error = "Please enter a valid amount"
                    Snackbar.make(
                        it,
                        "Label and Amount cannot be Empty", Snackbar.LENGTH_LONG
                    ).show()
                }
            }

            /* Removes error message */
            etLabelInput.addTextChangedListener {
                if (it!!.isNotEmpty()) {
                    binding.tilLabelLayout.error = null
                }
            }

            /* Removes error message */
            etAmountInput.addTextChangedListener {
                if (it!!.isNotEmpty()) {
                    binding.tilAmountLayout.error = null
                }
            }

            btnClose.setOnClickListener {
                finish()
            }
        }

    }
}