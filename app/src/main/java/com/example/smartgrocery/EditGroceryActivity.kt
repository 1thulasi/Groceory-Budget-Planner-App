package com.example.smartgrocery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

class EditGroceryActivity : AppCompatActivity() {

    private lateinit var etGroceryName: EditText
    private lateinit var etQuantity: EditText
    private lateinit var etPrice: EditText
    private lateinit var etDiscount: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_grocery)

        etGroceryName = findViewById(R.id.etGroceryName)
        etQuantity = findViewById(R.id.etQuantity)
        etPrice = findViewById(R.id.etPrice)
        etDiscount = findViewById(R.id.etDiscount)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        // Get data from Intent
        val groceryId = intent.getStringExtra("groceryId") ?: ""
        val groceryName = intent.getStringExtra("groceryName") ?: ""
        val quantity = intent.getStringExtra("quantity") ?: ""
        val price = intent.getStringExtra("price") ?: ""
        val discount = intent.getStringExtra("discount") ?: ""

        // Set data to input fields
        etGroceryName.setText(groceryName)
        etQuantity.setText(quantity)
        etPrice.setText(price)
        etDiscount.setText(discount)

        btnSave.setOnClickListener {
            saveGrocery(groceryId)
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveGrocery(groceryId: String) {
        val name = etGroceryName.text.toString().trim()
        val quantity = etQuantity.text.toString().trim()
        val price = etPrice.text.toString().trim()
        val discount = etDiscount.text.toString().trim()

        if (name.isEmpty() || quantity.isEmpty() || price.isEmpty() || discount.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance().getReference("Groceries")
        val groceryMap = mapOf(
            "groceryName" to name,
            "quantity" to quantity,
            "price" to price,
            "discount" to discount
        )

        database.child(groceryId).updateChildren(groceryMap).addOnSuccessListener {
            Toast.makeText(this, "Grocery updated successfully", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to update grocery", Toast.LENGTH_SHORT).show()
        }
    }
}
