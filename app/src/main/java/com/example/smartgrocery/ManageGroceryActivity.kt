package com.example.smartgrocery.admin

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smartgrocery.R
import com.example.smartgrocery.adapter.GrocerySectionAdapter
import com.example.smartgrocery.model.GroceryItem
import com.google.firebase.database.*

class ManageGroceryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GrocerySectionAdapter
    private val allGroceries = mutableListOf<GroceryItem>()
    private var groupedGroceries: Map<String, List<GroceryItem>> = mapOf()

    private lateinit var searchInput: EditText
    private lateinit var categorySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_grocery)

        recyclerView = findViewById(R.id.recyclerViewManageGroceries)
        searchInput = findViewById(R.id.searchInput)
        categorySpinner = findViewById(R.id.categorySpinner)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GrocerySectionAdapter(this, groupedGroceries)
        recyclerView.adapter = adapter

        setupSearchAndFilter()
        fetchGroceriesFromFirebase()
    }

    private fun setupSearchAndFilter() {
        val categories = listOf(
            "All", "Rice/Grains/Pulses", "Salt/Sugar/Spices", "Oil/Ghee", "Flours",
            "Dals & Lentils", "Masalas & Spices", "Beverages", "Dry Fruits & Nuts",
            "Vegetables", "Fruits", "Dairy Products", "Bakery Products", "Snacks & Ready-to-Eat",
            "Frozen Foods", "Personal Care", "Household Items", "Cleaning Supplies", "Baby Care", "Pet Care"
        )

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                filterAndGroupGroceries()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterAndGroupGroceries()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun fetchGroceriesFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Groceries")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allGroceries.clear()
                for (child in snapshot.children) {
                    val item = child.getValue(GroceryItem::class.java)
                    item?.id = child.key ?: ""
                    if (item != null) {
                        allGroceries.add(item)
                    }
                }
                filterAndGroupGroceries()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageGroceryActivity, "Failed to load groceries", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun filterAndGroupGroceries() {
        val query = searchInput.text.toString().trim().lowercase()
        val selectedCategory = categorySpinner.selectedItem.toString()

        val filteredGroceries = allGroceries.filter {
            (selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)) &&
                    (it.name.lowercase().contains(query) || it.category.lowercase().contains(query))
        }

        groupedGroceries = filteredGroceries.groupBy { it.category }

        adapter.updateData(groupedGroceries)
    }
}
