package com.example.smartgrocery.user

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.adapter.SelectedGroceriesAdapter
import com.example.smartgrocery.model.GroceryItem
import com.example.smartgrocery.ui.theme.AddGroceriesActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ViewSelectedGroceriesActivity : AppCompatActivity() {

    private lateinit var totalCostTextView: TextView
    private lateinit var selectedGroceryRecyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var editButton: Button
    private lateinit var removeButton: Button
    private lateinit var saveButton: Button
    private lateinit var adapter: SelectedGroceriesAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val selectedGroceryList = mutableListOf<GroceryItem>()
    private var isEditing = false
    private var isRemoving = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_selected_groceries)

        totalCostTextView = findViewById(R.id.totalCostTextView)
        selectedGroceryRecyclerView = findViewById(R.id.selectedGroceryRecyclerView)
        addButton = findViewById(R.id.addGroceriesButton)
        editButton = findViewById(R.id.editGroceriesButton)
        removeButton = findViewById(R.id.removeGroceriesButton)
        saveButton = findViewById(R.id.saveGroceriesButton)

        selectedGroceryRecyclerView.layoutManager = LinearLayoutManager(this)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        loadSelectedGroceries()

        addButton.setOnClickListener {
            val intent = Intent(this, AddGroceriesActivity::class.java)
            startActivity(intent)
        }

        editButton.setOnClickListener {
            isEditing = true
            isRemoving = false
            adapter.enableEditing(true)
            saveButton.visibility = View.VISIBLE
        }

        removeButton.setOnClickListener {
            isRemoving = true
            isEditing = false
            adapter.enableRemoving(true)
            saveButton.visibility = View.VISIBLE
        }

        saveButton.setOnClickListener {
            saveUpdatedGroceries()
        }
    }

    private fun loadSelectedGroceries() {
        val userId = auth.currentUser?.uid ?: return
        val userGroceryRef = database.child("UserGroceryLists").child(userId)

        userGroceryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                selectedGroceryList.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(GroceryItem::class.java)
                    if (item != null) selectedGroceryList.add(item)
                }

                if (selectedGroceryList.isEmpty()) {
                    Toast.makeText(this@ViewSelectedGroceriesActivity, "No groceries selected", Toast.LENGTH_SHORT).show()
                }

                adapter = SelectedGroceriesAdapter(selectedGroceryList, ::onQuantityChanged, ::onRemoveItem)
                selectedGroceryRecyclerView.adapter = adapter
                calculateTotal()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewSelectedGroceriesActivity, "Failed to load selected groceries", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onQuantityChanged(updatedItem: GroceryItem) {
        calculateTotal()
    }

    private fun onRemoveItem(removedItem: GroceryItem) {
        selectedGroceryList.remove(removedItem)
        adapter.notifyDataSetChanged()
        calculateTotal()
    }

    private fun calculateTotal() {
        val totalCost = selectedGroceryList.sumOf { it.getFinalPrice() }
        totalCostTextView.text = "Total Cost: ₹%.2f".format(totalCost)
    }

    private fun saveUpdatedGroceries() {
        val userId = auth.currentUser?.uid ?: return
        val userGroceryRef = database.child("UserGroceryLists").child(userId)

        userGroceryRef.setValue(selectedGroceryList).addOnSuccessListener {
            Toast.makeText(this, "Grocery list updated successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update grocery list", Toast.LENGTH_SHORT).show()
        }
    }
}