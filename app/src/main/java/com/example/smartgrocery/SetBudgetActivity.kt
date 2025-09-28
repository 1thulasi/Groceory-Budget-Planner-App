package com.example.smartgrocery.user

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.smartgrocery.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SetBudgetActivity : AppCompatActivity() {

    private lateinit var budgetInput: EditText
    private lateinit var btnSaveBudget: Button
    private lateinit var currentBudgetText: TextView
    private lateinit var btnNext: Button
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_budget)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Initialize Views
        budgetInput = findViewById(R.id.budgetInput)
        btnSaveBudget = findViewById(R.id.btnSaveBudget)
        currentBudgetText = findViewById(R.id.currentBudgetText)
        btnNext = findViewById(R.id.btnNext)

        // Load Existing Budget (if available)
        loadBudget()

        // Save Budget Button Click
        btnSaveBudget.setOnClickListener {
            val budget = budgetInput.text.toString()
            if (budget.isNotEmpty()) {
                saveBudget(budget)
            } else {
                Toast.makeText(this, "Please enter a valid budget!", Toast.LENGTH_SHORT).show()
            }
        }

        // Next Button Click (Navigate to User Dashboard)
        btnNext.setOnClickListener {
            startActivity(Intent(this, UserDashboardActivity::class.java))
            finish()
        }
    }

    private fun saveBudget(budget: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).child("budget").setValue(budget)
                .addOnSuccessListener {
                    Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show()
                    currentBudgetText.text = "Current Budget: $budget"
                    currentBudgetText.visibility = TextView.VISIBLE
                    btnNext.visibility = Button.VISIBLE // Show Next Button

                    // ✅ Clear input field after saving
                    budgetInput.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save budget!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadBudget() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            database.child("users").child(userId).child("budget").get()
                .addOnSuccessListener { snapshot ->
                    val budget = snapshot.value?.toString()
                    if (!budget.isNullOrEmpty()) {
                        currentBudgetText.text = "Current Budget: $budget"
                        currentBudgetText.visibility = TextView.VISIBLE
                        btnNext.visibility = Button.VISIBLE // Show Next Button if budget exists
                    }
                }
        }
    }
}
