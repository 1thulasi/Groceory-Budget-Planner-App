package com.example.smartgrocery.user

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.adapter.SavedGroceryAdapter
import com.example.smartgrocery.model.GroceryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SavedGroceryListActivity : AppCompatActivity() {

    private lateinit var savedGroceryRecyclerView: RecyclerView
    private lateinit var totalCostTextView: TextView
    private lateinit var saveGroceryListButton: Button
    private lateinit var adapter: SavedGroceryAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val savedGroceryList = mutableListOf<GroceryItem>()
    private var totalCost = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_grocery_list)

        // Initialize UI components
        totalCostTextView = findViewById(R.id.totalCostTextView)
        saveGroceryListButton = findViewById(R.id.saveGroceryListButton)
        savedGroceryRecyclerView = findViewById(R.id.savedGroceryRecyclerView)

        savedGroceryRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().getReference("UserGroceryLists")

        // Load saved grocery list from Firebase
        loadSavedGroceryList()

        // Save updated grocery list on button click
        saveGroceryListButton.setOnClickListener {
            saveUpdatedGroceryList()
        }
    }

    private fun loadSavedGroceryList() {
        val userId = auth.currentUser?.uid ?: return
        val userGroceryRef = database.child(userId)

        userGroceryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                savedGroceryList.clear()
                if (!snapshot.exists()) {
                    Toast.makeText(this@SavedGroceryListActivity, "No saved groceries found", Toast.LENGTH_SHORT).show()
                    return
                }

                for (data in snapshot.children) {
                    val item = data.getValue(GroceryItem::class.java)
                    item?.let { savedGroceryList.add(it) }
                }

                setupRecyclerView()
                calculateTotal()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SavedGroceryListActivity, "Failed to load saved groceries", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = SavedGroceryAdapter(
            savedGroceryList,
            onQuantityChanged = {
                calculateTotal()
            },
            onItemRemoved = {
                saveUpdatedGroceryList()
            }
        )
        savedGroceryRecyclerView.adapter = adapter
    }

    private fun calculateTotal() {
        totalCost = savedGroceryList.sumOf { it.getFinalPrice() }
        totalCostTextView.text = "Total: ₹%.2f".format(totalCost)
    }

    private fun saveUpdatedGroceryList() {
        val userId = auth.currentUser?.uid ?: return
        val userGroceryRef = database.child(userId)

        if (savedGroceryList.isEmpty()) {
            userGroceryRef.removeValue()
                .addOnSuccessListener {
                    Toast.makeText(this, "Grocery List Cleared", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to clear grocery list", Toast.LENGTH_SHORT).show()
                }
        } else {
            val groceryMap = savedGroceryList.associateBy { it.name }
            userGroceryRef.setValue(groceryMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Grocery List Updated!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update grocery list", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
