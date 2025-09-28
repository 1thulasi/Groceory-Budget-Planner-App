package com.example.smartgrocery.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.smartgrocery.admin.ManageRecipeActivity
import com.example.smartgrocery.R
import com.example.smartgrocery.admin.AddGroceryActivity
import com.example.smartgrocery.admin.AddRecipeActivity
import com.example.smartgrocery.admin.ManageGroceryActivity

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        val btnAddGrocery = findViewById<Button>(R.id.btnAddGrocery)
        val btnAddRecipe = findViewById<Button>(R.id.btnAddRecipe)
        val btnManageGroceries = findViewById<Button>(R.id.btnManageGroceries)
        val btnManageRecipes = findViewById<Button>(R.id.btnManageRecipes)

        // Add Grocery
        btnAddGrocery.setOnClickListener {
            val intent = Intent(this, AddGroceryActivity::class.java)
            startActivity(intent)
        }

        // ✅ Add Recipe
        btnAddRecipe.setOnClickListener {
            val intent = Intent(this, AddRecipeActivity::class.java)
            startActivity(intent)
        }

        // Manage Groceries
        btnManageGroceries.setOnClickListener {
            val intent = Intent(this, ManageGroceryActivity::class.java)
            startActivity(intent)
        }

        // Manage Recipes
        btnManageRecipes.setOnClickListener {
            val intent = Intent(this, ManageRecipeActivity::class.java)
            startActivity(intent)
        }
    }
}
