package com.example.smartgrocery.admin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.example.smartgrocery.R
import com.example.smartgrocery.model.GroceryItem

class AddGroceryActivity : AppCompatActivity() {

    private lateinit var etGroceryName: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDiscount: EditText
    private lateinit var spCategory: Spinner
    private lateinit var spUnits: Spinner
    private lateinit var btnSaveGrocery: Button
    private lateinit var database: DatabaseReference

    private val categories = arrayOf(
        "Rice/Grains/Pulses", "Salt/Sugar/Spices", "Oil/Ghee", "Flours",
        "Dals & Lentils", "Masalas & Spices", "Beverages", "Dry Fruits & Nuts",
        "Vegetables", "Fruits", "Dairy Products", "Bakery Products", "Snacks & Ready-to-Eat",
        "Frozen Foods", "Personal Care", "Household Items", "Cleaning Supplies",
        "Baby Care", "Pet Care"
    )

    private val units = arrayOf("Kg", "Gram", "Litre", "ml", "Piece", "Dozen", "Packet")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_grocery)

        // Initialize UI elements
        etGroceryName = findViewById(R.id.etGroceryName)
        etPrice = findViewById(R.id.etPrice)
        etDiscount = findViewById(R.id.etDiscount)
        spCategory = findViewById(R.id.spCategory)
        spUnits = findViewById(R.id.spUnits)
        btnSaveGrocery = findViewById(R.id.btnSaveGrocery)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance().reference.child("Groceries")

        // Set up Spinners
        spCategory.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spUnits.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, units)

        // Save Grocery Button Click
        btnSaveGrocery.setOnClickListener {
            saveGroceryToFirebase()
        }
    }

    private fun saveGroceryToFirebase() {
        val name = etGroceryName.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val discount = etDiscount.text.toString().trim()
        val category = spCategory.selectedItem.toString()
        val unit = spUnits.selectedItem.toString()

        if (name.isEmpty() || price.isEmpty() || discount.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val groceryId = database.push().key ?: return
        val groceryItem = GroceryItem(groceryId, name, price.toDouble(), discount.toDouble(), category, unit)

        database.child(groceryId).setValue(groceryItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Grocery added successfully", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add grocery", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearFields() {
        etGroceryName.text.clear()
        etPrice.text.clear()
        etDiscount.text.clear()
        spCategory.setSelection(0)
        spUnits.setSelection(0)
    }
}
