package com.example.smartgrocery.user

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.adapter.AlternativeSuggestionAdapter
import com.example.smartgrocery.model.GroceryItem
import com.google.firebase.database.*

class AlternativeActivity : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTitle: TextView
    private val alternativeList = mutableListOf<GroceryItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alternative)

        recyclerView = findViewById(R.id.rvAlternatives)
        tvTitle = findViewById(R.id.tvAltTitle)

        // Get the original item passed via Intent
        val originalItem = intent.getSerializableExtra("originalItem") as? GroceryItem

        if (originalItem == null) {
            Toast.makeText(this, "No item data found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        tvTitle.text = "Alternatives for ${originalItem.name}"

        databaseRef = FirebaseDatabase.getInstance().getReference("groceryItems")
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadAlternatives(originalItem)
    }

    private fun loadAlternatives(originalItem: GroceryItem) {
        databaseRef.orderByChild("category").equalTo(originalItem.category)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    alternativeList.clear()

                    for (itemSnapshot in snapshot.children) {
                        val item = itemSnapshot.getValue(GroceryItem::class.java)

                        if (item != null &&
                            item.name != originalItem.name &&
                            item.getFinalPrice() < originalItem.getFinalPrice()
                        ) {
                            item.quantity = originalItem.quantity
                            alternativeList.add(item)
                        }
                    }

                    if (alternativeList.isEmpty()) {
                        Toast.makeText(
                            this@AlternativeActivity,
                            "No cheaper alternatives found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    val adapter = AlternativeSuggestionAdapter(alternativeList) { selectedItem ->
                        Toast.makeText(
                            this@AlternativeActivity,
                            "${selectedItem.name} selected!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // TODO: Send back to previous activity if needed
                        finish()
                    }

                    recyclerView.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@AlternativeActivity,
                        "Failed to load alternatives",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
