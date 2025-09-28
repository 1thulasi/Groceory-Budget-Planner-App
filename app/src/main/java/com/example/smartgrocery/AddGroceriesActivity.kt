package com.example.smartgrocery.ui.theme

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.adapter.UnselectedGroceryAdapter
import com.example.smartgrocery.model.GroceryItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddGroceriesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UnselectedGroceryAdapter
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val unselectedGroceries = mutableListOf<GroceryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_groceries)

        recyclerView = findViewById(R.id.recyclerViewUnselectedGroceries)
        recyclerView.layoutManager = LinearLayoutManager(this)

        database = FirebaseDatabase.getInstance().getReference("Groceries")
        auth = FirebaseAuth.getInstance()

        fetchUnselectedGroceries()

        findViewById<Button>(R.id.saveSelectedGroceriesButton).setOnClickListener {
            saveSelectedGroceries()
        }
    }

    private fun fetchUnselectedGroceries() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                unselectedGroceries.clear()
                for (grocerySnapshot in snapshot.children) {
                    val grocery = grocerySnapshot.getValue(GroceryItem::class.java)
                    if (grocery != null) {
                        unselectedGroceries.add(grocery)
                    }
                }
                adapter = UnselectedGroceryAdapter(unselectedGroceries)
                recyclerView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddGroceriesActivity, "Failed to load groceries", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveSelectedGroceries() {
        val selectedGroceries = adapter.getSelectedGroceries()
        if (selectedGroceries.isNotEmpty()) {
            val userId = auth.currentUser?.uid ?: return
            val userRef = FirebaseDatabase.getInstance().getReference("UserGroceryLists").child(userId)
            for (grocery in selectedGroceries) {
                userRef.child(grocery.id).setValue(grocery)
            }
            Toast.makeText(this, "Groceries added!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "No groceries selected", Toast.LENGTH_SHORT).show()
        }
    }
}
