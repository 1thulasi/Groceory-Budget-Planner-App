package com.example.smartgrocery.user

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.adapter.GroceryAdapter
import com.example.smartgrocery.model.GroceryItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserGroceryListActivity : AppCompatActivity() {

    private lateinit var budgetTextView: TextView
    private lateinit var groceryRecyclerView: RecyclerView
    private lateinit var totalCostTextView: TextView
    private lateinit var budgetWarningTextView: TextView
    private lateinit var saveGroceryListButton: Button
    private lateinit var adapter: GroceryAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val groceryList = mutableListOf<GroceryItem>()
    private var totalCost = 0.0
    private var userBudget = 0.0
    private var dialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_grocery_list)

        budgetTextView = findViewById(R.id.budgetTextView)
        totalCostTextView = findViewById(R.id.totalCostTextView)
        budgetWarningTextView = findViewById(R.id.budgetWarningTextView)
        saveGroceryListButton = findViewById(R.id.saveGroceryListButton)
        groceryRecyclerView = findViewById(R.id.groceryRecyclerView)
        groceryRecyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("Groceries")

        fetchUserBudget()
        loadGroceryItems()

        saveGroceryListButton.setOnClickListener {
            saveSelectedGroceries()
        }
    }

    private fun fetchUserBudget() {
        val userId = auth.currentUser?.uid ?: return
        val budgetRef = FirebaseDatabase.getInstance().getReference("users")
            .child(userId).child("budget")

        budgetRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userBudget = snapshot.getValue(String::class.java)?.toDoubleOrNull() ?: 0.0
                budgetTextView.text = "Your Budget: ₹%.2f".format(userBudget)
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load budget")
            }
        })
    }

    private fun loadGroceryItems() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                groceryList.clear()
                for (data in snapshot.children) {
                    data.getValue(GroceryItem::class.java)?.let { groceryList.add(it) }
                }
                setupRecyclerView()
                loadSavedGroceryList()
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load groceries")
            }
        })
    }

    private fun loadSavedGroceryList() {
        val userId = auth.currentUser?.uid ?: return
        val userGroceryRef = FirebaseDatabase.getInstance().getReference("UserGroceryLists").child(userId)

        userGroceryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    data.getValue(GroceryItem::class.java)?.let { savedItem ->
                        groceryList.find { it.name == savedItem.name }?.apply {
                            isSelected = true
                            quantity = savedItem.quantity
                        }
                    }
                }
                adapter.notifyDataSetChanged()
                calculateTotal()
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load saved groceries")
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = GroceryAdapter(
            groceryList,
            onItemChecked = { _, _ -> calculateTotal() },
            onQuantityChanged = { calculateTotal() }
        )
        groceryRecyclerView.adapter = adapter
    }

    private fun calculateTotal() {
        totalCost = groceryList.filter { it.isSelected }.sumOf { it.getFinalPrice() }
        totalCostTextView.text = "Total: ₹%.2f".format(totalCost)

        if (totalCost > userBudget) {
            totalCostTextView.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))
            budgetWarningTextView.visibility = TextView.VISIBLE

            if (!dialogShown) {
                dialogShown = true
                showBudgetExceededDialog()
            }
        } else {
            totalCostTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black))
            budgetWarningTextView.visibility = TextView.GONE
            dialogShown = false
        }
    }

    private fun saveSelectedGroceries() {
        val selectedGroceries = groceryList.filter { it.isSelected }
        val userId = auth.currentUser?.uid ?: return
        val userGroceryRef = FirebaseDatabase.getInstance().getReference("UserGroceryLists").child(userId)

        if (selectedGroceries.isEmpty()) {
            userGroceryRef.removeValue()
        } else {
            val groceryMap = selectedGroceries.associateBy { it.name }
            userGroceryRef.setValue(groceryMap)
                .addOnSuccessListener { showToast("Grocery List Saved!") }
                .addOnFailureListener { showToast("Failed to save grocery list") }
        }
    }

    private fun showBudgetExceededDialog() {
        val selectedItems = groceryList.filter { it.isSelected }
        val mostExpensiveItem = selectedItems.maxByOrNull { it.getFinalPrice() }

        MaterialAlertDialogBuilder(this)
            .setTitle("Budget Exceeded!")
            .setMessage("Your total cost ₹%.2f exceeds your budget of ₹%.2f.\nDo you want to see the alternative or still want to proceed?"
                .format(totalCost, userBudget))
            .setNegativeButton("See Alternatives") { dialog, _ ->
                dialog.dismiss()
                if (mostExpensiveItem != null) {
                    val intent = Intent(this, AlternativeActivity::class.java)
                    intent.putExtra("originalItem", mostExpensiveItem)
                    startActivity(intent)
                } else {
                    showToast("No item selected for alternative suggestion.")
                }
            }
            .setPositiveButton("Proceed") { dialog, _ -> dialog.dismiss() }
            .setIcon(R.drawable.ic_info)
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
